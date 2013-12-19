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
import com.ifeng.vshare.adapter.ClubListAdapter;
import com.ifeng.vshare.requestor.BaseVSharePageRequestor;
import com.ifeng.vshare.requestor.BaseVShareRequestor;
import com.ifeng.vshare.requestor.BaseVShareRequestor.VShareRequestResult;
import com.ifeng.vshare.requestor.ClubListRequestor;
import com.ifeng.vshare.requestor.ClubSwitchRequestor;
import com.ifeng.vshare.ui.state.ErrorRequestState;

/**
 * 休息室fragment页面
 * 
 * @author Calvin
 * 
 */
public class ClubCategoryFragment extends VShareFragment implements
		OnPullDownRefreshListener, OnScrollRefreshListener {

	/** key model requestor lobby switch */
	private static final String KEY_MODEL_REQUESTOR_LOBBY_SWITCH = "requestor_lobbyswitch";
	/** key model requestor club */
	private static final String KEY_MODEL_REQUESTOR_CLUB = "requestor_club";

	/** state view 当前状态 */
	private StateView mStateView;
	/** adapter */
	private ClubListAdapter mClubAdapter;
	/** 执行刷新的view */
	private IFRefreshViewLayout<IFRefreshListView> mRefreshView;
	/** RefreshListView */
	private IFRefreshListView mListView;

	/** requestor lobby switch */
	private ClubSwitchRequestor mLobbySwitchRequestor;
	/** requestor club */
	private ClubListRequestor mClubRequestor;

	/**
	 * 构造
	 */
	public ClubCategoryFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		((VShareApplication) getActivity().getApplication())
				.addLocationServiceListener(mLocationListener);
		super.onCreate(new Bundle());
	}

	@Override
	public void onDestroy() {
		((VShareApplication) getActivity().getApplication())
				.removeLocationServiceListener(mLocationListener);
		super.onDestroy();
	}

	@SuppressWarnings("unchecked")
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
		if (((BaseVShareRequestor) requestor).getLastRequestResult() != VShareRequestResult.SUCCESS) {
			onFailed(requestor, IRequestModelErrorCode.ERROR_CODE_SERVICE_ERROR);
			return;
		} else if (requestor instanceof ClubSwitchRequestor) {
			mClubRequestor.requestFirstPage();
			return;
		} else {
			// 请求完成，结束等待页面
			mStateView.dismiss();
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
		if (mClubAdapter == null) {
			mRefreshView.setPullDownRefreshListener(this);
			mRefreshView.setScrollRefreshListener(this);
			mRefreshView.setContentView(mListView);

			mClubAdapter = new ClubListAdapter(getActivity(), mImageFetcher,
					mClubRequestor);
			mListView.setAdapter(mClubAdapter);
		}

		if (!requestor.isHasNext()) {
			mRefreshView.onScrollRefreshNoMore();
		}

		mClubAdapter.notifyDataSetChanged();
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
			mStateView.setState(mErrorRequestState);
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
		mClubRequestor.requestNextPage();
	}

	@Override
	public void onPullDownRefresh() {
		mClubRequestor.requestFirstPage();
	}

	/** 获取定位信息，获得结果之后再进行接口数据请求 */
	private OnCityLocationListener mLocationListener = new OnCityLocationListener() {

		@Override
		public void onLocation(CityInfo info) {
			mLobbySwitchRequestor.updateLocationInfo(info);
			mClubRequestor.updateLocationInfo(info);

			mLobbySwitchRequestor.request();
			((VShareApplication) getActivity().getApplication())
					.removeLocationServiceListener(mLocationListener);
		}
	};

	@Override
	protected void setupModel() {
		mLobbySwitchRequestor = new ClubSwitchRequestor(getActivity(),
				((VShareApplication) getActivity().getApplication())
						.getLocationInfo(), this);
		mClubRequestor = new ClubListRequestor(getActivity(),
				((VShareApplication) getActivity().getApplication())
						.getLocationInfo(), this);

		mModelManageQueue.addTaskModel(KEY_MODEL_REQUESTOR_LOBBY_SWITCH,
				mLobbySwitchRequestor);
		mModelManageQueue
				.addTaskModel(KEY_MODEL_REQUESTOR_CLUB, mClubRequestor);
	}

	@Override
	protected void setImageCacheParams() {
		mImageSize = mWidth;
		mImageCacheDir = "clublist";
	}

}