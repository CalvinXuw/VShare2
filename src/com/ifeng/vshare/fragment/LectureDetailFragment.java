package com.ifeng.vshare.fragment;

import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ifeng.util.logging.Log;
import com.ifeng.util.model.AbstractModel;
import com.ifeng.util.net.requestor.IRequestModelErrorCode;
import com.ifeng.util.ui.OnSingleClickListener;
import com.ifeng.util.ui.StateView;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareFragment;
import com.ifeng.vshare.activity.LectureCommentActivity;
import com.ifeng.vshare.activity.LecturePhotoActivity;
import com.ifeng.vshare.activity.LectureVideoActivity;
import com.ifeng.vshare.database.dao.LectureInterestDao;
import com.ifeng.vshare.model.LectureDetailItem;
import com.ifeng.vshare.model.LectureDetailItem.LectureDetailPhotoItem;
import com.ifeng.vshare.requestor.BaseVShareRequestor;
import com.ifeng.vshare.requestor.BaseVShareRequestor.VShareRequestResult;
import com.ifeng.vshare.requestor.LectureDetailRequestor;
import com.ifeng.vshare.requestor.LectureInterestRequestor;
import com.ifeng.vshare.ui.state.ErrorRequestState;

/**
 * 大讲堂详情fragment
 * 
 * @author Calvin
 * 
 */
public class LectureDetailFragment extends VShareFragment {

	/** key id */
	private static final String KEY_ID = "id";
	/** key model requestor of detail */
	private static final String KEY_MODEL_REQUESTOR_DETAIL = "requestor_detail";
	/** key model requestor of interest */
	private static final String KEY_MODEL_REQUESTOR_INTEREST = "requestor_interest";

	/**
	 * 获取实例
	 * 
	 * @param id
	 *            大讲堂id
	 * @return
	 */
	public static LectureDetailFragment getInstance(int id) {
		LectureDetailFragment detailFragment = new LectureDetailFragment();
		Bundle arg = new Bundle();
		arg.putInt(KEY_ID, id);
		detailFragment.setArguments(arg);
		return detailFragment;
	}

	/** state view 当前状态 */
	private StateView mStateView;
	/** view holder */
	private LectureDetailViewHolder mHolder;

	/** 讲堂详情requestor */
	private LectureDetailRequestor mDetailRequestor;
	/** 用于提交感兴趣的requestor */
	private LectureInterestRequestor mInterestRequestor;

	/**
	 * 构造
	 */
	public LectureDetailFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(new Bundle());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		/*
		 * 设置默认加载图片
		 */
		mImageFetcher.setLoadingImage(R.drawable.default_image_little);

		View layout = inflater.inflate(R.layout.lecture_detail, container,
				false);

		mStateView = (StateView) layout.findViewById(R.id.stateview);

		mStateView.setState(mProcessingRequestState);

		mDetailRequestor.request();

		return layout;
	}

	/*
	 * 处理网络请求成功的数据，其中需要根据RefreshListView的状态进行变更
	 * 
	 * @see
	 * com.ifeng.util.net.requestor.AbstractRequestor.OnRequestorListener#onSuccess
	 * (com.ifeng.util.net.requestor.AbstractRequestor)
	 */

	@Override
	public void onSuccess(AbstractModel requestor) {
		// 筛除服务器返回错误
		if (((BaseVShareRequestor) requestor).getLastRequestResult() != VShareRequestResult.SUCCESS) {
			onFailed(requestor, IRequestModelErrorCode.ERROR_CODE_SERVICE_ERROR);
			return;
		}

		if (requestor instanceof LectureDetailRequestor) {
			// 请求完成，结束等待页面
			mStateView.dismiss();
			updateDatas((LectureDetailRequestor) requestor);
		} else if (requestor instanceof LectureInterestRequestor) {
			LectureInterestDao.getInstance(getActivity())
					.insertLectureInterestRecord(getArguments().getInt(KEY_ID));
			mDetailRequestor.getDetailItem().mJoinNum++;
			updateDatasByNoticeState();
			Toast.makeText(
					getActivity(),
					String.format(getString(R.string.lecture_interest_success),
							mDetailRequestor.getDetailItem().mTitle),
					Toast.LENGTH_LONG).show();
		}

	}

	/**
	 * 更新数据
	 * 
	 * @param requestor
	 */
	private synchronized void updateDatas(LectureDetailRequestor requestor) {
		if (mHolder == null) {
			mHolder = new LectureDetailViewHolder();

			mHolder.mImage = (ImageView) getView().findViewById(
					R.id.image_lecture_detail);
			// 适配计算，原图为250*175尺寸
			View imageContainerView = getView().findViewById(
					R.id.layout_lecture_detail_image);
			imageContainerView.setLayoutParams(new LinearLayout.LayoutParams(
					(int) (mWidth * mDensity / 720 * 250), (int) (mWidth
							* mDensity / 720 * 175)));

			mHolder.mName = (TextView) getView().findViewById(
					R.id.text_lecture_title);
			mHolder.mLeftBtn = (Button) getView().findViewById(
					R.id.btn_lecture_top_left);
			mHolder.mRightBtn = (Button) getView().findViewById(
					R.id.btn_lecture_top_right);
			mHolder.mTime = (TextView) getView().findViewById(
					R.id.text_lecture_time);
			mHolder.mLocation = (TextView) getView().findViewById(
					R.id.text_lecture_location);
			mHolder.mActivityIntroduce = (TextView) getView().findViewById(
					R.id.text_lecture_introduce_activity);
			mHolder.mGuestIntroduce = (TextView) getView().findViewById(
					R.id.text_lecture_introduce_guest);
			mHolder.mActivityGroup = (ViewGroup) getView().findViewById(
					R.id.layout_lecture_activity);
			LinearLayout.LayoutParams oldGroupParams = (LinearLayout.LayoutParams) mHolder.mActivityGroup
					.getLayoutParams();
			// 屏幕适配调整，原设计图比例为720*140 + 50 section高度 10 margin
			oldGroupParams.height = (int) ((int) (mWidth * mDensity / 720 * 140) + (50 + 10 + 5)
					* mDensity);
			mHolder.mActivityGroup.setLayoutParams(oldGroupParams);
			mHolder.mActivityLeft = (ImageView) getView().findViewById(
					R.id.image_lecture_activity_left);
			mHolder.mActivityRight = (ImageView) getView().findViewById(
					R.id.image_lecture_activity_right);
			mHolder.mActivityMiddle = (ImageView) getView().findViewById(
					R.id.image_lecture_activity_middle);
		}

		final LectureDetailItem item = requestor.getDetailItem();

		mHolder.mName.setText(item.mTitle);
		mImageFetcher.loadImage(item.mImg, mHolder.mImage);
		mHolder.mTime.setText(item.mTime);
		mHolder.mLocation.setText(item.mAddr);
		mHolder.mActivityIntroduce.setText(item.mSummary);
		mHolder.mGuestIntroduce.setText(item.mMessage);

		switch (item.getLectureState()) {
		case NOTICE:
			mInterestRequestor = new LectureInterestRequestor(getActivity(),
					getArguments().getInt(KEY_ID), this);
			updateDatasByNoticeState();
			break;
		case GOING:
			updateDatasByOnGoingState(item);
			break;
		case HISTORY:
			updateDatasByHistoryState(item);
			break;
		}

		List<LectureDetailPhotoItem> photos = item.mActivityPhotos;
		if (photos == null || photos.size() == 0) {
			mHolder.mActivityGroup.setVisibility(View.GONE);
			return;
		} else if (photos != null && photos.size() == 1) {
			((View) mHolder.mActivityMiddle.getParent())
					.setVisibility(View.INVISIBLE);
			((View) mHolder.mActivityRight.getParent())
					.setVisibility(View.INVISIBLE);
		} else if (photos != null && photos.size() == 2) {
			((View) mHolder.mActivityRight.getParent())
					.setVisibility(View.INVISIBLE);
		}

		mImageFetcher.loadImage(photos.get(0).mImage, mHolder.mActivityLeft);

		if (photos.size() > 1) {
			mImageFetcher.loadImage(photos.get(1).mImage,
					mHolder.mActivityMiddle);
		}

		if (photos.size() > 2) {
			mImageFetcher.loadImage(photos.get(2).mImage,
					mHolder.mActivityRight);
		}

		// 查看图片详情
		mHolder.mActivityGroup.setOnClickListener(new OnSingleClickListener() {

			@Override
			public void onSingleClick(View v) {
				getActivity().startActivity(
						LecturePhotoActivity.getIntent(getActivity(),
								item.mTitle, item));
			}
		});

	}

	/**
	 * 设置预告类型的视图
	 * 
	 * @param item
	 */
	private void updateDatasByNoticeState() {
		mHolder.mActivityGroup.setVisibility(View.GONE);

		final LectureDetailItem item = mDetailRequestor.getDetailItem();

		mHolder.mLeftBtn.setText(String.format(
				getString(R.string.btn_lecture_interest), item.mJoinNum));
		mHolder.mRightBtn.setText(R.string.btn_lecture_signup);

		// 如果没有提交过感兴趣请求，则可关注此活动，发起感兴趣请求
		if (!LectureInterestDao.getInstance(getActivity())
				.queryLectureInterestRecordExistByLectureId(
						getArguments().getInt(KEY_ID))) {
			mHolder.mLeftBtn.setOnClickListener(new OnSingleClickListener() {

				@Override
				public void onSingleClick(View v) {
					mInterestRequestor.request();
					mHolder.mLeftBtn.setText(R.string.btn_lecture_requesting);
					v.setOnClickListener(null);
				}
			});
		} else {
			mHolder.mLeftBtn.setBackgroundColor(getResources().getColor(
					R.color.btn_disable));
		}

		// 如果没有下发短信号码和短信内容，则隐藏发送短信报名按钮
		if (TextUtils.isEmpty(item.mSmsText) || TextUtils.isEmpty(item.mSmsNum)) {
			mHolder.mRightBtn.setVisibility(View.GONE);
			return;
		}
		mHolder.mRightBtn.setOnClickListener(new OnSingleClickListener() {

			@Override
			public void onSingleClick(View v) {
				try {
					Uri uri = Uri.parse("smsto:" + item.mSmsNum);
					Intent it = new Intent(Intent.ACTION_SENDTO, uri);
					it.putExtra("sms_body", item.mSmsText);
					getActivity().startActivity(it);
				} catch (Exception e) {
					if (DEBUG) {
						Log.e(TAG, e);
					}
				}
			}
		});
	}

	/**
	 * 设置现场类型的视图
	 * 
	 * @param item
	 */
	private void updateDatasByOnGoingState(final LectureDetailItem item) {
		mHolder.mLeftBtn.setText(R.string.btn_lecture_usercomment);
		mHolder.mRightBtn.setVisibility(View.GONE);

		mHolder.mLeftBtn.setOnClickListener(new OnSingleClickListener() {

			@Override
			public void onSingleClick(View v) {
				getActivity().startActivity(
						LectureCommentActivity.getIntent(getActivity(),
								item.mTitle, item.mCommentTag));
			}
		});
	}

	/**
	 * 设置回顾类型的视图
	 * 
	 * @param item
	 */
	private void updateDatasByHistoryState(final LectureDetailItem item) {
		mHolder.mLeftBtn.setText(R.string.btn_lecture_readcomment);
		mHolder.mRightBtn.setText(R.string.btn_lecture_videoreview);

		// 不直接播放视频
		mHolder.mLeftBtn.setOnClickListener(new OnSingleClickListener() {

			@Override
			public void onSingleClick(View v) {
				getActivity().startActivity(
						LectureVideoActivity.getIntent(getActivity(), item,
								false));
			}
		});
		// 直接播放视频
		mHolder.mRightBtn.setOnClickListener(new OnSingleClickListener() {

			@Override
			public void onSingleClick(View v) {
				getActivity().startActivity(
						LectureVideoActivity.getIntent(getActivity(), item,
								true));
			}
		});
	}

	/*
	 * 处理网络请求失败的数据，其中需要根据RefreshListView的状态进行变更
	 * 
	 * @see
	 * com.ifeng.util.net.requestor.AbstractRequestor.OnRequestorListener#onFailed
	 * (com.ifeng.util.net.requestor.AbstractRequestor,
	 * com.ifeng.util.net.requestor.IRequestErrorCode)
	 */

	@Override
	public void onFailed(AbstractModel requestor, int errorCode) {
		if (requestor instanceof LectureDetailRequestor) {
			mStateView.setState(mErrorRequestState);
		} else if (requestor instanceof LectureInterestRequestor) {
			updateDatasByNoticeState();
		}
	}

	/*
	 * 接收自StateView的状态信息，其中状态id为子类指定id，处理页面整体的显示状态，错误、无数据、正常等
	 * 
	 * @see
	 * com.ifeng.util.ui.StateView.State.OnStateActionListener#onActionTrigger
	 * (int)
	 */

	@Override
	public void onActionTrigger(int actionId) {
		if (actionId == ErrorRequestState.STATE_ACTION_ERROR_RETRY) {
			mStateView.setState(mProcessingRequestState);
			mDetailRequestor.request();
		}
	}

	@Override
	protected void setupModel() {
		mDetailRequestor = new LectureDetailRequestor(getActivity(),
				getArguments().getInt(KEY_ID), this);
		mInterestRequestor = new LectureInterestRequestor(getActivity(),
				getArguments().getInt(KEY_ID), this);

		mModelManageQueue.addTaskModel(KEY_MODEL_REQUESTOR_DETAIL,
				mDetailRequestor);
		mModelManageQueue.addTaskModel(KEY_MODEL_REQUESTOR_INTEREST,
				mInterestRequestor);
	}

	@Override
	protected void setImageCacheParams() {
		mImageSize = mWidth / 3;
		mImageCacheDir = "lecturedetail";
	}

	/**
	 * 界面view holder
	 * 
	 * @author Calvin
	 * 
	 */
	private class LectureDetailViewHolder {
		/** 缩略图片 */
		public ImageView mImage;
		/** 名称 */
		public TextView mName;
		/** left btn */
		public Button mLeftBtn;
		/** right btn */
		public Button mRightBtn;

		/** 时间 */
		public TextView mTime;
		/** 地点 */
		public TextView mLocation;
		/** 活动介绍 */
		public TextView mActivityIntroduce;

		/** 嘉宾介绍 */
		public TextView mGuestIntroduce;

		/** 活动图片group */
		public ViewGroup mActivityGroup;

		/** 活动图片left */
		public ImageView mActivityLeft;
		/** 活动图片right */
		public ImageView mActivityRight;
		/** 活动图片middle */
		public ImageView mActivityMiddle;
	}

}
