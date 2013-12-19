package com.ifeng.vshare.fragment;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ifeng.util.model.AbstractModel;
import com.ifeng.util.net.requestor.AbstractRequestor;
import com.ifeng.util.net.requestor.AbstractRequestor.OnPreloadFromCacheListener;
import com.ifeng.util.net.requestor.IRequestModelErrorCode;
import com.ifeng.util.ui.OnSingleClickListener;
import com.ifeng.util.ui.StateView;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareFragment;
import com.ifeng.vshare.activity.ClubDetailPhotoActivity;
import com.ifeng.vshare.model.ClubDetailItem;
import com.ifeng.vshare.model.ClubDetailItem.ClubPhotoItem;
import com.ifeng.vshare.requestor.BaseVShareRequestor.VShareRequestResult;
import com.ifeng.vshare.requestor.ClubDetailRequestor;
import com.ifeng.vshare.ui.state.ErrorRequestState;

/**
 * 俱乐部详情fragment
 * 
 * @author Calvin
 * 
 */
public class ClubDetailFragment extends VShareFragment implements
		OnPreloadFromCacheListener {

	/** key id */
	private static final String KEY_ID = "id";
	/** key model requestor */
	private static final String KEY_REQUESTOR = "requestor";

	/**
	 * 获取实例
	 * 
	 * @param id
	 * @return
	 */
	public static ClubDetailFragment getInstance(int id) {
		ClubDetailFragment detailFragment = new ClubDetailFragment();
		Bundle arg = new Bundle();
		arg.putInt(KEY_ID, id);
		detailFragment.setArguments(arg);
		return detailFragment;
	}

	/** state view 当前状态 */
	private StateView mStateView;
	/** view holder */
	private ClubDetailViewHolder mHolder;
	/** requestor */
	private ClubDetailRequestor mClubDetailRequestor;

	/**
	 * 构造
	 */
	public ClubDetailFragment() {

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
		mImageFetcher.setLoadingImage(R.drawable.default_image_large_light);

		View layout = inflater.inflate(R.layout.club_detail, container, false);

		mStateView = (StateView) layout.findViewById(R.id.stateview);

		mStateView.setState(mProcessingRequestState);

		mClubDetailRequestor.request();

		return layout;
	}

	/*
	 * 处理网络请求成功的数据，其中需要根据RefreshListView的状态进行变更 (non-Javadoc)
	 * 
	 * @see
	 * com.ifeng.util.model.AbstractModel.OnModelProcessListener#onSuccess(com
	 * .ifeng.util.model.AbstractModel)
	 */
	@Override
	public void onSuccess(AbstractModel requestor) {
		// 筛除服务器返回错误
		if (mClubDetailRequestor.getLastRequestResult() != VShareRequestResult.SUCCESS) {
			onFailed(requestor, IRequestModelErrorCode.ERROR_CODE_SERVICE_ERROR);
			return;
		} else {
			// 请求完成，结束等待页面
			mStateView.dismiss();
		}

		updateDatas(mClubDetailRequestor);
	}

	@Override
	public void onCacheLoaded(AbstractRequestor requestor) {
		updateDatas(mClubDetailRequestor);
	}

	/**
	 * 更新数据
	 * 
	 * @param requestor
	 */
	private synchronized void updateDatas(ClubDetailRequestor requestor) {
		if (mHolder == null) {
			mHolder = new ClubDetailViewHolder();

			mHolder.mImage = (ImageView) getView()
					.findViewById(R.id.image_club);
			mHolder.mImage.setLayoutParams(new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.FILL_PARENT, (int) (mWidth
							* mDensity / 720 * 320)));

			mHolder.mName = (TextView) getView().findViewById(
					R.id.text_club_name);
			mHolder.mTime = (TextView) getView().findViewById(
					R.id.text_club_time);
			mHolder.mLocation = (TextView) getView().findViewById(
					R.id.text_club_location);
			mHolder.mIntroduce = (TextView) getView().findViewById(
					R.id.text_club_introduce);
			mHolder.mServiceTarget = (TextView) getView().findViewById(
					R.id.text_club_servicetarget);
			mHolder.mParticipate = (TextView) getView().findViewById(
					R.id.text_club_participate);
			mHolder.mActivityGroup = (ViewGroup) getView().findViewById(
					R.id.layout_club_activity);
			LinearLayout.LayoutParams oldGroupParams = (LinearLayout.LayoutParams) mHolder.mActivityGroup
					.getLayoutParams();
			// 屏幕适配调整，原设计图比例为720*150 + 50 section高度 10 margin
			oldGroupParams.height = (int) ((int) (mWidth * mDensity / 720 * 140) + (50 + 10 + 5)
					* mDensity);
			mHolder.mActivityGroup.setLayoutParams(oldGroupParams);
			mHolder.mActivityLeft = (ImageView) getView().findViewById(
					R.id.image_club_activity_left);
			mHolder.mActivityRight = (ImageView) getView().findViewById(
					R.id.image_club_activity_right);
			mHolder.mActivityMiddle = (ImageView) getView().findViewById(
					R.id.image_club_activity_middle);
		}

		final ClubDetailItem item = requestor.getDetailItem();

		mImageFetcher.loadImage(item.mImg, mHolder.mImage);
		mHolder.mName.setText(item.mClubName);
		mHolder.mTime.setText(item.mClubTime);
		mHolder.mLocation.setText(item.mClubAddr);
		mHolder.mIntroduce.setText(item.mClubIntroduce);
		mHolder.mServiceTarget.setText(item.mServiceTarget);
		mHolder.mParticipate.setText(item.mParticipateWay);

		List<ClubPhotoItem> photos = item.mPhotos;
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

		mImageFetcher.loadImage(photos.get(0).mUrl, mHolder.mActivityLeft,
				mWidth / 3);

		if (photos.size() > 1) {
			mImageFetcher.loadImage(photos.get(1).mUrl,
					mHolder.mActivityMiddle, mWidth / 3);
		}

		if (photos.size() > 2) {
			mImageFetcher.loadImage(photos.get(2).mUrl, mHolder.mActivityRight,
					mWidth / 3);
		}

		// 查看图片详情
		mHolder.mActivityGroup.setOnClickListener(new OnSingleClickListener() {

			@Override
			public void onSingleClick(View v) {
				getActivity().startActivity(
						ClubDetailPhotoActivity.getIntent(getActivity(),
								item.mClubName, item));
			}
		});
	}

	/*
	 * 处理网络请求失败的数据，其中需要根据RefreshListView的状态进行变更 (non-Javadoc)
	 * 
	 * @see
	 * com.ifeng.util.model.AbstractModel.OnModelProcessListener#onFailed(com
	 * .ifeng.util.model.AbstractModel, int)
	 */
	@Override
	public void onFailed(AbstractModel requestor, int errorCode) {
		mStateView.setState(mErrorRequestState);
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
			mClubDetailRequestor.request();
		}
	}

	@Override
	protected void setupModel() {
		mClubDetailRequestor = new ClubDetailRequestor(getActivity(),
				getArguments().getInt(KEY_ID), this);
		mModelManageQueue.addTaskModel(KEY_REQUESTOR, mClubDetailRequestor);
	}

	@Override
	protected void setImageCacheParams() {
		mImageSize = mWidth;
		mImageCacheDir = "clubdetail";
	}

	/**
	 * 界面view holder
	 * 
	 * @author Calvin
	 * 
	 */
	private class ClubDetailViewHolder {
		/** 俱乐部图片 */
		public ImageView mImage;
		/** 名称 */
		public TextView mName;
		/** 活动时间 */
		public TextView mTime;
		/** 活动地点 */
		public TextView mLocation;
		/** 俱乐部介绍 */
		public TextView mIntroduce;
		/** 服务对象 */
		public TextView mServiceTarget;
		/** 参与方式 */
		public TextView mParticipate;
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
