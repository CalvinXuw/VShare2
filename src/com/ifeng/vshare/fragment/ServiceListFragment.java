package com.ifeng.vshare.fragment;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ifeng.util.model.AbstractModel;
import com.ifeng.util.net.requestor.AbstractRequestor;
import com.ifeng.util.net.requestor.AbstractRequestor.OnPreloadFromCacheListener;
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
import com.ifeng.vshare.adapter.ServiceListAdapter;
import com.ifeng.vshare.requestor.BaseVSharePageRequestor;
import com.ifeng.vshare.requestor.BaseVShareRequestor.VShareRequestResult;
import com.ifeng.vshare.requestor.ServiceListRequestor;
import com.ifeng.vshare.ui.ClubDialFootView;
import com.ifeng.vshare.ui.state.ErrorRequestState;
import com.ifeng.vshare.ui.state.ServiceEmptyRequestState;

/**
 * 会员专享服务fragment页面
 * 
 * @author Calvin
 * 
 */
public class ServiceListFragment extends VShareFragment implements
		OnPullDownRefreshListener, OnScrollRefreshListener,
		OnPreloadFromCacheListener {

	/** key model requestor */
	private static final String KEY_MODEL_REQUESTOR = "requestor";

	/** state view 当前状态 */
	private StateView mStateView;
	/** adapter */
	private ServiceListAdapter mServiceAdapter;
	/** 执行刷新的view */
	private IFRefreshViewLayout<IFRefreshListView> mRefreshView;
	/** RefreshListView */
	private IFRefreshListView mListView;
	/** 头图 */
	private HeadViewHolder mHeader;
	/** requestor */
	private ServiceListRequestor mServiceRequestor;

	/**
	 * 构造
	 */
	public ServiceListFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		((VShareApplication) getActivity().getApplication())
				.addLocationServiceListener(mLocationListener);
		super.onCreate(new Bundle());

		mEmptyRequestState = new ServiceEmptyRequestState(getActivity(), this);
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

		/*
		 * HeadView的初始设置不能为空，所以adapter的设置需要在获取到数据之后
		 */
		mHeader = new HeadViewHolder();
		mHeader.mHeadView = (ViewGroup) inflater.inflate(
				R.layout.service_description, null);
		mHeader.mImg = (ImageView) mHeader.mHeadView
				.findViewById(R.id.image_head_service);
		mHeader.mImg.setLayoutParams(new LinearLayout.LayoutParams(
				(int) (mWidth * mDensity), (int) (mWidth * mDensity / 2)));

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

		// 是否可以开启预加载，若可以则开启
		if (mServiceRequestor.hasPreloadCache()) {
			mServiceRequestor.turnOnPreloadFromCache(this);
		}

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
		if (mServiceRequestor.getLastRequestResult() != VShareRequestResult.SUCCESS) {
			onFailed(requestor, IRequestModelErrorCode.ERROR_CODE_SERVICE_ERROR);
			return;
		} else {
			// 请求完成，结束等待页面

			if (mServiceRequestor.getDataList().size() == 0) {
				mStateView.setState(mEmptyRequestState);
			} else {
				mStateView.dismiss();
			}
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

		updateDatas(mServiceRequestor);
	}

	@Override
	public void onCacheLoaded(AbstractRequestor requestor) {
		// 若当前 adapter不为空，则网络请求的回调已经被调用过了，直接return
		if (mServiceAdapter != null) {
			return;
		}

		updateDatas(mServiceRequestor);
		mRefreshView.forcePullDownRefresh();
	}

	/**
	 * 添加headerview
	 * 
	 * @param requestor
	 */
	private synchronized void addHeader(BaseVSharePageRequestor requestor) {
		if (requestor.getPageIndex() == BaseVSharePageRequestor.FIRSTPAGE) {

			mHeader.mImg.setImageBitmap(BitmapFactory
					.decodeStream(getResources().openRawResource(
							R.drawable.background_vip_head)));
			mHeader.mHeadView.invalidate();
			mHeader.mHeadView.setOnClickListener(null);
			if (mListView.indexOfChild(mHeader.mHeadView) < 0) {
				mListView.addHeaderView(mHeader.mHeadView);
				mListView.addFooterView(new ClubDialFootView(getActivity())
						.getContentView());
			}
		}
	}

	/**
	 * 更新数据
	 * 
	 * @param requestor
	 */
	private synchronized void updateDatas(BaseVSharePageRequestor requestor) {
		if (mServiceAdapter == null) {
			mRefreshView.setPullDownRefreshListener(this);
			mRefreshView.setScrollRefreshListener(this);
			mRefreshView.setContentView(mListView);

			addHeader((BaseVSharePageRequestor) requestor);
			mServiceAdapter = new ServiceListAdapter(getActivity(),
					mServiceRequestor);
			mListView.setAdapter(mServiceAdapter);
		}

		if (!requestor.isHasNext()) {
			mRefreshView.onScrollRefreshNoMore();
		}

		mServiceAdapter.notifyDataSetChanged();
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
			if (mServiceRequestor.isPreloaded()) {
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
		mServiceRequestor.requestNextPage();
	}

	@Override
	public void onPullDownRefresh() {
		mServiceRequestor.requestFirstPage();
	}

	/**
	 * ServiceDescriptionView 容器
	 * 
	 * @author Calvin
	 * 
	 */
	private class HeadViewHolder {
		/** HeadView */
		public ViewGroup mHeadView;
		/** 图片 */
		public ImageView mImg;
	}

	/** 获取定位信息，获得结果之后再进行接口数据请求 */
	private OnCityLocationListener mLocationListener = new OnCityLocationListener() {

		@Override
		public void onLocation(CityInfo info) {
			mServiceRequestor.updateLocationInfo(info);
			mServiceRequestor.requestFirstPage();
		}
	};

	@Override
	protected void setupModel() {
		mServiceRequestor = new ServiceListRequestor(getActivity(),
				((VShareApplication) getActivity().getApplication())
						.getLocationInfo(), this);
		mModelManageQueue.addTaskModel(KEY_MODEL_REQUESTOR, mServiceRequestor);
	}

	@Override
	protected void setImageCacheParams() {
		mImageSize = mWidth / 2;
		mImageCacheDir = "servicelist";
	}

}