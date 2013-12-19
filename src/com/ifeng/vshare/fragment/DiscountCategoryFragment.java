package com.ifeng.vshare.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Toast;

import com.ifeng.util.model.AbstractModel;
import com.ifeng.util.net.requestor.IRequestModelErrorCode;
import com.ifeng.util.ui.IFRefreshListView;
import com.ifeng.util.ui.IFRefreshViewLayout;
import com.ifeng.util.ui.IFRefreshViewLayout.OnPullDownRefreshListener;
import com.ifeng.util.ui.IFRefreshViewLayout.OnScrollRefreshListener;
import com.ifeng.util.ui.StateView;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareApplication;
import com.ifeng.vshare.VShareApplication.OnCityLocationListener;
import com.ifeng.vshare.VShareFragment;
import com.ifeng.vshare.activity.LocationActivity.CityInfo;
import com.ifeng.vshare.adapter.DiscountListAdapter;
import com.ifeng.vshare.requestor.BaseVSharePageRequestor;
import com.ifeng.vshare.requestor.BaseVShareRequestor.VShareRequestResult;
import com.ifeng.vshare.requestor.DiscountListRequestor;
import com.ifeng.vshare.ui.state.DiscountEmptyRequestState;
import com.ifeng.vshare.ui.state.ErrorRequestState;

/**
 * 特惠商盟分类fragment页面
 * 
 * @author Calvin
 * 
 */
public class DiscountCategoryFragment extends VShareFragment implements
		OnPullDownRefreshListener, OnScrollRefreshListener {

	/** key model requestor */
	private static final String KEY_MODEL_REQUESTOR = "requestor";

	/** state view 当前状态 */
	private StateView mStateView;
	/** adapter */
	private DiscountListAdapter mDiscountAdapter;
	/** 执行刷新的view */
	private IFRefreshViewLayout<IFRefreshListView> mRefreshView;
	/** RefreshListView */
	private IFRefreshListView mListView;
	/** requestor */
	private DiscountListRequestor mDiscountRequestor;

	/**
	 * 构造
	 */
	public DiscountCategoryFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		((VShareApplication) getActivity().getApplication())
				.addLocationServiceListener(mLocationListener);
		super.onCreate(new Bundle());
		mEmptyRequestState = new DiscountEmptyRequestState(getActivity(), this);
	}

	@Override
	public void onDestroy() {
		((VShareApplication) getActivity().getApplication())
				.removeLocationServiceListener(mLocationListener);
		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		/*
		 * 设置默认加载图片
		 */
		mImageFetcher.setLoadingImage(R.drawable.default_image_large_light);

		View layout = inflater.inflate(R.layout.base_fragment_list, container,
				false);

		mStateView = (StateView) layout.findViewById(R.id.stateview);

		mRefreshView = (IFRefreshViewLayout<IFRefreshListView>) layout
				.findViewById(R.id.layout_refresh);

		mListView = new IFRefreshListView(getActivity());
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				/*
				 * 在滑动状态在不加载图片，优化滚动卡顿
				 */
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
					mImageFetcher.setPauseWork(true);
				} else {
					mImageFetcher.setPauseWork(false);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});

		mStateView.setState(mProcessingRequestState);

		((VShareApplication) getActivity().getApplication()).requestLocation();

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
		if (mDiscountRequestor.getLastRequestResult() != VShareRequestResult.SUCCESS) {
			onFailed(requestor, IRequestModelErrorCode.ERROR_CODE_SERVICE_ERROR);
			return;
		} else {
			// 请求完成，结束等待页面
			mStateView.dismiss();
		}

		if (mDiscountRequestor.getDataList().size() == 0) {
			mStateView.setState(mEmptyRequestState);
			return;
		}

		switch (mRefreshView.getCurrentState()) {
		/*
		 * 下拉刷新结束后
		 */
		case WAITING_PULL_DOWN_REFRESH_RESULT:
			mRefreshView.onPullDownRefreshComplete();
			break;
		/*
		 * 滑动加载结束后
		 */
		case WAITING_SCROLLREFRESH_RESULT:
			mRefreshView.onScrollRefreshComplete();
			break;
		/*
		 * 页面初始状态加载
		 */
		case NORMAL:

			break;
		default:
			break;
		}

		updateDatas((BaseVSharePageRequestor) requestor);
	}

	/**
	 * 更新数据
	 * 
	 * @param requestor
	 */
	private synchronized void updateDatas(BaseVSharePageRequestor requestor) {
		if (mDiscountAdapter == null) {
			mRefreshView.setPullDownRefreshListener(this);
			// discount中暂时没有下一页的情况，数据为全部返还
			// mListView.setOnScrollRefreshListener(this);
			mRefreshView.setContentView(mListView);

			mDiscountAdapter = new DiscountListAdapter(getActivity(),
					mDiscountRequestor);
			mListView.setAdapter(mDiscountAdapter);
		}

		if (!requestor.isHasNext()) {
			mRefreshView.onScrollRefreshNoMore();
		}

		mDiscountAdapter.notifyDataSetChanged();
		mListView.invalidateViews();
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
		switch (mRefreshView.getCurrentState()) {
		case WAITING_PULL_DOWN_REFRESH_RESULT:
			mRefreshView.onPullDownRefreshFailed();
			Toast.makeText(getActivity(), R.string.network_bad,
					Toast.LENGTH_LONG).show();
			break;
		case WAITING_SCROLLREFRESH_RESULT:
			mRefreshView.onScrollRefreshFail();
			break;
		case NORMAL:
			if (mDiscountRequestor.isPreloaded()) {
				Toast.makeText(getActivity(), R.string.network_bad,
						Toast.LENGTH_LONG).show();
			} else {
				mStateView.setState(mErrorRequestState);
			}
			break;
		default:
			break;
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
			((VShareApplication) getActivity().getApplication())
					.requestLocation();
		}
	}

	@Override
	public void onScrollRefresh() {
		mDiscountRequestor.requestNextPage();
	}

	@Override
	public void onPullDownRefresh() {
		((VShareApplication) getActivity().getApplication()).requestLocation();
	}

	/** 获取定位信息，获得结果之后再进行接口数据请求 */
	private OnCityLocationListener mLocationListener = new OnCityLocationListener() {

		@Override
		public void onLocation(CityInfo info) {
			mDiscountRequestor.updateLocationInfo(info);
			mDiscountRequestor.requestFirstPage();
		}
	};

	@Override
	protected void setupModel() {
		mDiscountRequestor = new DiscountListRequestor(getActivity(),
				((VShareApplication) getActivity().getApplication())
						.getLocationInfo(), this);
		mModelManageQueue.addTaskModel(KEY_MODEL_REQUESTOR, mDiscountRequestor);
	};

	@Override
	protected void setImageCacheParams() {
		mImageSize = mWidth / 2;
		mImageCacheDir = "discountlist";
	}

}