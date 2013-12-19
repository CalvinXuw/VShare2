package com.ifeng.vshare.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.ifeng.util.logging.Log;
import com.ifeng.util.model.AbstractModel;
import com.ifeng.util.net.requestor.AbstractRequestor;
import com.ifeng.util.net.requestor.AbstractRequestor.OnPreloadFromCacheListener;
import com.ifeng.util.net.requestor.IRequestModelErrorCode;
import com.ifeng.util.ui.OnSingleClickListener;
import com.ifeng.util.ui.StateView;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareFragment;
import com.ifeng.vshare.model.DiscountDetailItem;
import com.ifeng.vshare.requestor.BaseVShareRequestor.VShareRequestResult;
import com.ifeng.vshare.requestor.DiscountDetailRequestor;
import com.ifeng.vshare.ui.DialogManager;
import com.ifeng.vshare.ui.DialogManager.DialogStateCallback;
import com.ifeng.vshare.ui.state.ErrorRequestState;

/**
 * 商盟详情fragment
 * 
 * @author Calvin
 * 
 */
public class DiscountDetailFragment extends VShareFragment implements
		OnPreloadFromCacheListener {

	/** key id */
	private static final String KEY_ID = "id";
	/** key model requestor */
	private static final String KEY_MODEL_REQUESTOR = "requestor";

	/**
	 * 获取实例
	 * 
	 * @param id
	 * @return
	 */
	public static DiscountDetailFragment getInstance(int id) {
		DiscountDetailFragment detailFragment = new DiscountDetailFragment();
		Bundle arg = new Bundle();
		arg.putInt(KEY_ID, id);
		detailFragment.setArguments(arg);
		return detailFragment;
	}

	/** state view 当前状态 */
	private StateView mStateView;
	/** view holder */
	private DiscountDetailViewHolder mHolder;
	/** requestor */
	private DiscountDetailRequestor mDiscountDetailRequestor;

	/**
	 * 构造
	 */
	public DiscountDetailFragment() {

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

		View layout = inflater.inflate(R.layout.discount_detail, container,
				false);

		mStateView = (StateView) layout.findViewById(R.id.stateview);

		// 是否可以开启预加载，若可以则开启
		if (mDiscountDetailRequestor.hasPreloadCache()) {
			mDiscountDetailRequestor.turnOnPreloadFromCache(this);
			mStateView.dismiss();
		} else {
			mStateView.setState(mProcessingRequestState);
		}

		mDiscountDetailRequestor.request();

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
		if (mDiscountDetailRequestor.getLastRequestResult() != VShareRequestResult.SUCCESS) {
			onFailed(requestor, IRequestModelErrorCode.ERROR_CODE_SERVICE_ERROR);
			return;
		} else {
			// 请求完成，结束等待页面
			mStateView.dismiss();
		}

		updateDatas(mDiscountDetailRequestor);
	}

	@Override
	public void onCacheLoaded(AbstractRequestor requestor) {
		updateDatas(mDiscountDetailRequestor);
	}

	/**
	 * 更新数据
	 * 
	 * @param requestor
	 */
	private synchronized void updateDatas(DiscountDetailRequestor requestor) {
		if (mHolder == null) {
			mHolder = new DiscountDetailViewHolder();

			mHolder.mImage = (ImageView) getView().findViewById(
					R.id.image_discount_detail);
			LinearLayout.LayoutParams oldParams = (LayoutParams) mHolder.mImage
					.getLayoutParams();
			// 屏幕适配调整，原设计图比例为640*340，屏幕为720 Density=2
			oldParams.height = (int) (mWidth * mDensity / 720 * 340);
			mHolder.mImage.setLayoutParams(oldParams);

			mHolder.mInfo = (TextView) getView().findViewById(
					R.id.text_discount_info);
			mHolder.mDate = (TextView) getView().findViewById(
					R.id.text_discount_time);
			mHolder.mMessage = (TextView) getView().findViewById(
					R.id.text_discount_message);
			mHolder.mAddr = (TextView) getView().findViewById(
					R.id.text_discount_addr);
			mHolder.mPhone = (TextView) getView().findViewById(
					R.id.text_discount_phone);
			mHolder.mExtra = (TextView) getView().findViewById(
					R.id.text_discount_extra);
		}

		final DiscountDetailItem item = requestor.getDetailItem();

		mImageFetcher.loadImage(item.mImage, mHolder.mImage);
		mHolder.mInfo.setText(item.mDiscountInfo);
		mHolder.mDate.setText(DateFormat.format("yyyy-MM-dd",
				item.mStarttime * 1000).toString()
				+ " - "
				+ DateFormat.format("yyyy-MM-dd", item.mEndtime * 1000)
						.toString());
		mHolder.mMessage.setText(item.mDiscountMessage);

		if (TextUtils.isEmpty(item.mDiscountExtra)) {
			mHolder.mExtra.setVisibility(View.GONE);
		} else {
			mHolder.mExtra.setText(item.mDiscountExtra);
		}

		mHolder.mAddr.setText(item.mAddr);
		mHolder.mPhone.setText(item.mNumber);
		mHolder.mPhone.setOnClickListener(new OnSingleClickListener() {
			@Override
			public void onSingleClick(View v) {
				makeCall(item.mNumber);
			}
		});

	}

	/**
	 * 拨打商家电话
	 * 
	 * @param tel
	 */
	private void makeCall(final String tel) {
		DialogManager.getInstance().createDialog(
				getString(R.string.dialog_title),
				getString(R.string.discount_call_or_not),
				new DialogStateCallback() {

					@Override
					public void onClick(int which) {
						if (which == Dialog.BUTTON_POSITIVE) {
							try {
								Intent intent = new Intent(Intent.ACTION_CALL,
										Uri.parse("tel:" + tel));
								startActivity(intent);
							} catch (Exception e) {
								if (DEBUG) {
									Log.w(TAG, e);
								}
							}
						}
					}

					@Override
					public void onCancel() {
					}
				}, true, getString(R.string.dialog_confirm),
				getString(R.string.dialog_cancel));
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
			mDiscountDetailRequestor.request();
		}
	}

	@Override
	protected void setupModel() {
		mDiscountDetailRequestor = new DiscountDetailRequestor(getActivity(),
				getArguments().getInt(KEY_ID), this);
		mModelManageQueue.addTaskModel(KEY_MODEL_REQUESTOR,
				mDiscountDetailRequestor);
	}

	@Override
	protected void setImageCacheParams() {
		mImageSize = mWidth;
		mImageCacheDir = "discountdetail";
	}

	/**
	 * 界面view holder
	 * 
	 * @author Calvin
	 * 
	 */
	private class DiscountDetailViewHolder {
		/** 商铺图片 */
		public ImageView mImage;
		/** 简介 */
		public TextView mInfo;
		/** 持续时间 */
		public TextView mDate;
		/** 详细描述 */
		public TextView mMessage;
		/** 地址 */
		public TextView mAddr;
		/** 电话 */
		public TextView mPhone;
		/** 额外信息 */
		public TextView mExtra;
	}

}
