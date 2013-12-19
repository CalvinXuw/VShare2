package com.ifeng.vshare.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ifeng.util.model.AbstractModel;
import com.ifeng.util.net.requestor.IRequestModelErrorCode;
import com.ifeng.util.ui.IFRefreshListView;
import com.ifeng.util.ui.IFRefreshViewLayout;
import com.ifeng.util.ui.IFRefreshViewLayout.OnPullDownRefreshListener;
import com.ifeng.util.ui.IFRefreshViewLayout.OnScrollRefreshListener;
import com.ifeng.util.ui.StateView;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareFragment;
import com.ifeng.vshare.adapter.CommentListAdapter;
import com.ifeng.vshare.requestor.CommentListRequestor;
import com.ifeng.vshare.ui.state.ErrorRequestState;

/**
 * 评论页fragment
 * 
 * @author Calvin
 * 
 */
public class CommentListFragment extends VShareFragment implements
		OnPullDownRefreshListener, OnScrollRefreshListener {

	/** key comment tag */
	private static final String KEY_COMMENT_TAG = "comment_tag";
	/** key comment title */
	private static final String KEY_COMMENT_TITLE = "comment_title";
	/** key comment need title */
	private static final String KEY_COMMENT_NEED_TITLE = "comment_needtitle";

	/**
	 * 获取一个评论条实例，默认需要评论标题
	 * 
	 * @param title
	 * @param commentTag
	 * @return
	 */
	public static CommentListFragment getInstance(String title,
			String commentTag) {
		return getInstance(title, commentTag, true);
	}

	/**
	 * 获取一个评论条实例
	 * 
	 * @param title
	 * @param commentTag
	 * @param needTitle
	 * @return
	 */
	public static CommentListFragment getInstance(String title,
			String commentTag, boolean needTitle) {
		CommentListFragment commentListFragment = new CommentListFragment();
		Bundle arg = new Bundle();
		arg.putString(KEY_COMMENT_TITLE, title);
		arg.putString(KEY_COMMENT_TAG, commentTag);
		arg.putBoolean(KEY_COMMENT_NEED_TITLE, needTitle);
		commentListFragment.setArguments(arg);
		return commentListFragment;
	}

	/** key model requestor */
	private static final String KEY_MODEL_REQUESTOR = "requestor";

	/** state view 当前状态 */
	private StateView mStateView;
	/** adapter */
	private CommentListAdapter mCommentAdapter;
	/** 执行刷新的view */
	private IFRefreshViewLayout<IFRefreshListView> mRefreshView;
	/** RefreshListView */
	private IFRefreshListView mListView;
	/** requestor */
	private CommentListRequestor mCommentRequestor;

	/**
	 * 构造
	 */
	public CommentListFragment() {
		setNeedImageFetcher(false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View layout = inflater.inflate(R.layout.base_fragment_list, container,
				false);

		mStateView = (StateView) layout.findViewById(R.id.stateview);

		mRefreshView = (IFRefreshViewLayout<IFRefreshListView>) layout
				.findViewById(R.id.layout_refresh);

		mListView = new IFRefreshListView(getActivity());

		mStateView.setState(mProcessingRequestState);

		mCommentRequestor.requestFirstPage();

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
		if (!mCommentRequestor.getLastResultSuccess()) {
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

		updateDatas(mCommentRequestor);
	}

	/**
	 * 更新数据
	 * 
	 * @param requestor
	 */
	private synchronized void updateDatas(CommentListRequestor requestor) {
		if (mCommentAdapter == null) {
			mRefreshView.setPullDownRefreshListener(this);
			mRefreshView.setScrollRefreshListener(this);
			mRefreshView.setContentView(mListView);

			String title = getArguments().getString(KEY_COMMENT_TITLE);
			boolean needTitle = getArguments().getBoolean(
					KEY_COMMENT_NEED_TITLE);
			mCommentAdapter = new CommentListAdapter(getActivity(), requestor,
					needTitle ? title : null);
			mListView.setAdapter(mCommentAdapter);
		}

		if (!requestor.isHasNext()) {
			mRefreshView.onScrollRefreshNoMore();
		}

		mCommentAdapter.notifyDataSetChanged();
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
			mCommentRequestor.requestFirstPage();
		}
	}

	@Override
	public void onScrollRefresh() {
		mCommentRequestor.requestNextPage();

	}

	@Override
	public void onPullDownRefresh() {
		mCommentRequestor.requestFirstPage();
	}

	@Override
	protected void setupModel() {
		mCommentRequestor = new CommentListRequestor(getActivity(),
				getArguments().getString(KEY_COMMENT_TAG), this);
		mModelManageQueue.addTaskModel(KEY_MODEL_REQUESTOR, mCommentRequestor);
	}

	@Override
	protected void setImageCacheParams() {
	}

}
