package com.ifeng.vshare.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
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
import com.ifeng.vshare.VShareFragment;
import com.ifeng.vshare.adapter.PhotoListAdapter;
import com.ifeng.vshare.requestor.BaseVSharePageRequestor;
import com.ifeng.vshare.requestor.BaseVShareRequestor;
import com.ifeng.vshare.requestor.BaseVShareRequestor.VShareRequestResult;
import com.ifeng.vshare.requestor.PhotoListRequestor;
import com.ifeng.vshare.ui.state.ErrorRequestState;

/**
 * 图片列表页面Fragment
 * 
 * @author Calvin
 * 
 */
public class PhotoListFragment extends VShareFragment implements
		OnPullDownRefreshListener, OnScrollRefreshListener,
		OnPreloadFromCacheListener {

	/** key model requestor */
	private static final String KEY_MODEL_REQUESTOR = "requestor";

	/** state view 当前状态 */
	private StateView mStateView;
	/** adapter */
	private PhotoListAdapter mPhotosAdapter;
	/** 执行刷新的view */
	private IFRefreshViewLayout<IFRefreshListView> mRefreshView;
	/** RefreshListView */
	private IFRefreshListView mListView;

	/** photolist requestor */
	private PhotoListRequestor mPhotosRequestor;

	/**
	 * 构造
	 */
	public PhotoListFragment() {
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

		// 是否可以开启预加载，若可以则开启
		if (mPhotosRequestor.hasPreloadCache()) {
			mPhotosRequestor.turnOnPreloadFromCache(this);
			mStateView.dismiss();
		} else {
			mStateView.setState(mProcessingRequestState);
		}

		mPhotosRequestor.requestFirstPage();

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

	@Override
	public void onCacheLoaded(AbstractRequestor requestor) {
		// 若当前 adapter不为空，则网络请求的回调已经被调用过了，直接return
		if (mPhotosAdapter != null) {
			return;
		}

		updateDatas((BaseVSharePageRequestor) requestor);

		mRefreshView.forcePullDownRefresh();
	}

	/**
	 * 更新数据
	 * 
	 * @param requestor
	 */
	private synchronized void updateDatas(BaseVSharePageRequestor requestor) {
		if (mPhotosAdapter == null) {
			mRefreshView.setPullDownRefreshListener(this);
			mRefreshView.setScrollRefreshListener(this);
			mRefreshView.setContentView(mListView);

			mPhotosAdapter = new PhotoListAdapter(getActivity(), mImageFetcher,
					mPhotosRequestor);
			mListView.setAdapter(mPhotosAdapter);
		}

		if (!requestor.isHasNext()) {
			mRefreshView.onScrollRefreshNoMore();
		}

		mPhotosAdapter.notifyDataSetChanged();
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
			if (mPhotosRequestor.isPreloaded()) {
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
			mPhotosRequestor.requestFirstPage();
		}
	}

	/*
	 * 避免某些高分辨率手机的图片过大问题，手动设置ImageCacheParams
	 * 
	 * @see com.ifeng.vshare.BaseFragment#setImageCacheParams()
	 */

	@Override
	protected void setImageCacheParams() {
		mImageSize = mWidth / 3;
		mImageCacheDir = "photolist";
	}

	/*
	 * 需要设置一个图片列表的Requestor
	 * 
	 * @see com.ifeng.vshare.BaseFragment#setRequestor()
	 */
	@Override
	protected void setupModel() {
		mPhotosRequestor = new PhotoListRequestor(getActivity(), this);
		mModelManageQueue.addTaskModel(KEY_MODEL_REQUESTOR, mPhotosRequestor);
	}

	/*
	 * 滚动加载回调
	 * 
	 * @see
	 * com.ifeng.util.ui.RefreshListView.OnScrollRefreshListener#onScrollRefresh
	 * ()
	 */

	@Override
	public void onScrollRefresh() {
		mPhotosRequestor.requestNextPage();
	}

	/*
	 * 下拉刷新回调
	 * 
	 * @see
	 * com.ifeng.util.ui.RefreshListView.OnPullRefreshListener#onPullRefresh()
	 */

	@Override
	public void onPullDownRefresh() {
		mPhotosRequestor.requestFirstPage();
	}

}
