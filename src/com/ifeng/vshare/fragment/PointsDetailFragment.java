package com.ifeng.vshare.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.ifeng.vshare.model.PointsDetailItem;
import com.ifeng.vshare.requestor.BaseVShareRequestor.VShareRequestResult;
import com.ifeng.vshare.requestor.PointsDetailRequestor;
import com.ifeng.vshare.ui.state.ErrorRequestState;

/**
 * 积分详情fragment，负责处理数据请求，页面展示等
 * 
 * @author Calvin
 * 
 */
public class PointsDetailFragment extends VShareFragment implements
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
	public static PointsDetailFragment getInstance(int id) {
		PointsDetailFragment detailFragment = new PointsDetailFragment();
		Bundle arg = new Bundle();
		arg.putInt(KEY_ID, id);
		detailFragment.setArguments(arg);
		return detailFragment;
	}

	/** state view 当前状态 */
	private StateView mStateView;
	/** view holder */
	private PointsDetailViewHolder mHolder;
	/** requestor */
	private PointsDetailRequestor mDetailRequestor;

	/**
	 * 构造
	 */
	public PointsDetailFragment() {

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

		View layout = inflater
				.inflate(R.layout.points_detail, container, false);

		mStateView = (StateView) layout.findViewById(R.id.stateview);

		// 是否可以开启预加载，若可以则开启
		if (mDetailRequestor.hasPreloadCache()) {
			mDetailRequestor.turnOnPreloadFromCache(this);
			mStateView.dismiss();
		} else {
			mStateView.setState(mProcessingRequestState);
		}

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
		if (mDetailRequestor.getLastRequestResult() != VShareRequestResult.SUCCESS) {
			onFailed(requestor, IRequestModelErrorCode.ERROR_CODE_SERVICE_ERROR);
			return;
		} else {
			// 请求完成，结束等待页面
			mStateView.dismiss();
		}

		updateDatas(mDetailRequestor);
	}

	@Override
	public void onCacheLoaded(AbstractRequestor requestor) {
		updateDatas(mDetailRequestor);
	}

	/**
	 * 更新数据
	 * 
	 * @param requestor
	 */
	private synchronized void updateDatas(PointsDetailRequestor requestor) {
		if (mHolder == null) {
			mHolder = new PointsDetailViewHolder();

			mHolder.mImage = (ImageView) getView().findViewById(
					R.id.image_points_detail);
			mHolder.mTitle = (TextView) getView().findViewById(
					R.id.text_points_detail_title);
			mHolder.mGotoneCost = (TextView) getView().findViewById(
					R.id.text_points_detail_gotone);
			mHolder.mMzoneCost = (TextView) getView().findViewById(
					R.id.text_points_detail_mzone);
			mHolder.mEasyownCost = (TextView) getView().findViewById(
					R.id.text_points_detail_eastown);
			mHolder.mExchangeText = (TextView) getView().findViewById(
					R.id.text_points_exchange);
			mHolder.mExchangeBtn = (Button) getView().findViewById(
					R.id.btn_points_exchange);
			mHolder.mExchangeProductHint = (TextView) getView().findViewById(
					R.id.text_points_hint);
			mHolder.mExchangeProductDesc = (TextView) getView().findViewById(
					R.id.text_points_desc);
			mHolder.mPointCostMore = getView().findViewById(
					R.id.btn_point_costmore);
		}

		final PointsDetailItem item = requestor.getDetailItem();

		mImageFetcher.loadImage(item.mImg, mHolder.mImage);
		mHolder.mTitle.setText(item.mTitle);
		mHolder.mGotoneCost.setText(getString(R.string.gotone_cost)
				+ item.mGotonePoints);
		mHolder.mMzoneCost.setText(getString(R.string.mzone_cost)
				+ item.mMzonePoints);
		mHolder.mEasyownCost.setText(getString(R.string.easyown_cost)
				+ item.mEasyownPoints);
		mHolder.mExchangeText.setText(String.format(
				getString(R.string.exchange_message), item.mSmsCode,
				item.mSmsPhoneNum));
		mHolder.mExchangeProductHint.setText(item.mHintText);
		mHolder.mExchangeProductDesc.setText(item.mProductDesc);
		mHolder.mExchangeBtn.setOnClickListener(new OnSingleClickListener() {

			@Override
			public void onSingleClick(View v) {
				try {
					Uri uri = Uri.parse("smsto:" + item.mSmsPhoneNum);
					Intent it = new Intent(Intent.ACTION_SENDTO, uri);
					it.putExtra("sms_body", item.mSmsCode);
					getActivity().startActivity(it);
				} catch (Exception e) {
					// 3.0某些pad机型短信接口调用失败
					try {
						Intent sendIntent = new Intent(Intent.ACTION_VIEW);
						sendIntent.putExtra("address", item.mSmsPhoneNum);
						sendIntent.putExtra("sms_body", item.mSmsCode);
						sendIntent.setType("vnd.android-dir/mms-sms");
						getActivity().startActivity(sendIntent);
					} catch (Exception e2) {
						if (DEBUG) {
							Log.e(TAG, e);
						}
					}
				}
			}
		});

		// 不含有主要提示的商品无需显示该section
		if (TextUtils.isEmpty(item.mHintText)) {
			mHolder.mExchangeProductHint.setVisibility(View.GONE);
			getView().findViewById(R.id.layout_points_hint_section)
					.setVisibility(View.GONE);
		}

		// 标注有额外的金卡钻石卡银卡消费的Dialog显示
		if (item.mGotoneDiamond != -1 || item.mGotoneGold != -1
				|| item.mGotoneSilver != -1) {
			mHolder.mPointCostMore.setVisibility(View.VISIBLE);
			mHolder.mPointCostMore
					.setOnClickListener(new OnSingleClickListener() {

						@Override
						public void onSingleClick(View v) {
							createExtraCostDialog(item);
						}
					});
		} else {
			mHolder.mPointCostMore.setVisibility(View.GONE);
		}
	}

	/**
	 * 生成详情扣费的dialog
	 * 
	 * @param item
	 */
	private void createExtraCostDialog(final PointsDetailItem item) {
		View dialogView = getActivity().getLayoutInflater().inflate(
				R.layout.points_dialog, null);
		TextView diamond = (TextView) dialogView
				.findViewById(R.id.text_dialog_points_diamond);
		TextView gold = (TextView) dialogView
				.findViewById(R.id.text_dialog_points_gold);
		TextView sliver = (TextView) dialogView
				.findViewById(R.id.text_dialog_points_sliver);
		Button close = (Button) dialogView
				.findViewById(R.id.btn_dialog_points_close);

		if (item.mGotoneDiamond != -1) {
			diamond.setVisibility(View.VISIBLE);
			diamond.setText(diamond.getText().toString() + item.mGotoneDiamond);
		}
		if (item.mGotoneGold != -1) {
			gold.setVisibility(View.VISIBLE);
			gold.setText(gold.getText().toString() + item.mGotoneGold);
		}
		if (item.mGotoneSilver != -1) {
			sliver.setVisibility(View.VISIBLE);
			sliver.setText(sliver.getText().toString() + item.mGotoneSilver);
		}

		final Dialog dialog = new Dialog(getActivity(),
				R.style.Dialog_No_Border);
		dialog.setContentView(dialogView);

		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		dialog.show();

		close.setOnClickListener(new OnSingleClickListener() {

			@Override
			public void onSingleClick(View v) {
				dialog.cancel();
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
			mDetailRequestor.request();
		}
	}

	@Override
	protected void setupModel() {
		mDetailRequestor = new PointsDetailRequestor(getActivity(),
				getArguments().getInt(KEY_ID), this);
		mModelManageQueue.addTaskModel(KEY_MODEL_REQUESTOR, mDetailRequestor);
	}

	@Override
	protected void setImageCacheParams() {
		mImageSize = mWidth / 2;
		mImageCacheDir = "pointsdetail";
	}

	/**
	 * 界面view holder
	 * 
	 * @author Calvin
	 * 
	 */
	private class PointsDetailViewHolder {
		/** 商品图片 */
		public ImageView mImage;
		/** 商品名 */
		public TextView mTitle;
		/** 全球通消费 */
		public TextView mGotoneCost;
		/** 动感地带消费 */
		public TextView mMzoneCost;
		/** 神州行消费 */
		public TextView mEasyownCost;
		/** 短信兑换提示文字 */
		public TextView mExchangeText;
		/** 短信兑换按钮 */
		public Button mExchangeBtn;
		/** 商品主要提示 */
		public TextView mExchangeProductHint;
		/** 商品描述 */
		public TextView mExchangeProductDesc;
		/** 更多积分描述 */
		public View mPointCostMore;
	}
}
