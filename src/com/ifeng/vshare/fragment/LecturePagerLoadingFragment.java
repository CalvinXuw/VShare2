package com.ifeng.vshare.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ifeng.util.model.AbstractModel;
import com.ifeng.util.ui.StateView;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareFragment;
import com.ifeng.vshare.fragment.LectureCategoryFragment.LectureRequestorState;
import com.ifeng.vshare.ui.LecturePagerConnecter;
import com.ifeng.vshare.ui.state.ErrorRequestState;

public class LecturePagerLoadingFragment extends VShareFragment {

	/** key callback of 页面状态变化回调 */
	private static final String KEY_CALLBACK = "callback";

	/** state view 当前状态 */
	private StateView mStateView;
	/** connecter pager的回调 */
	private LecturePagerConnecter mConnecter;

	/**
	 * 获取实例
	 * 
	 * @return
	 */
	public static LecturePagerLoadingFragment getInstance(
			LecturePagerConnecter connecter) {
		LecturePagerLoadingFragment loadingFragment = new LecturePagerLoadingFragment();
		Bundle arg = new Bundle();
		arg.putParcelable(KEY_CALLBACK, connecter);
		loadingFragment.setArguments(arg);
		return loadingFragment;
	}

	/**
	 * 空构造
	 */
	public LecturePagerLoadingFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mConnecter = getArguments().getParcelable(KEY_CALLBACK);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.lecture_page_loading,
				container, false);

		mStateView = (StateView) layout.findViewById(R.id.stateview);

		refreshState();
		return layout;
	}

	@Override
	public void onAttach(Activity activity) {
		refreshState();
		super.onAttach(activity);
	}

	/**
	 * 根据pager状态改变stateview状态
	 */
	public void refreshState() {
		if (mConnecter == null || mStateView == null) {
			return;
		}
		switch (mConnecter.getCurrentState()) {
		case NORMAL:
		case REQUESTING:
			mStateView.setState(mProcessingRequestState);
			break;
		case FAILED:
			mStateView.setState(mErrorRequestState);
			break;
		}
	}

	@Override
	public void onSuccess(AbstractModel requestor) {
	}

	@Override
	public void onFailed(AbstractModel requestor, int errorCode) {
	}

	@Override
	public void onActionTrigger(int actionId) {
		if (actionId == ErrorRequestState.STATE_ACTION_ERROR_RETRY) {
			mConnecter.setCurrentState(LectureRequestorState.REQUESTING);
		}
	}

	@Override
	protected void setImageCacheParams() {

	}

}
