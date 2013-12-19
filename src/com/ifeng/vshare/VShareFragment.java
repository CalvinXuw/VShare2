package com.ifeng.vshare;

import com.ifeng.BaseFragment;
import com.ifeng.util.ui.StateView.State.OnStateActionListener;
import com.ifeng.vshare.ui.state.EmptyRequestState;
import com.ifeng.vshare.ui.state.ErrorRequestState;
import com.ifeng.vshare.ui.state.ProcessingRequestState;

/**
 * VShareFragment所有实现的Fragment页面应将继承自此类
 * 
 * @author Xuwei
 * 
 */
public class VShareFragment extends BaseFragment implements
		OnStateActionListener {

	/** 正在获取数据状态 */
	protected ProcessingRequestState mProcessingRequestState;
	/** 获取数据异常状态 */
	protected ErrorRequestState mErrorRequestState;
	/** 获取数据为空状态 */
	protected EmptyRequestState mEmptyRequestState;

	/**
	 * 需要一个空的构造方法
	 */
	public VShareFragment() {

	}

	@Override
	protected void onInit() {
		super.onInit();
		mProcessingRequestState = new ProcessingRequestState(getActivity());
		mErrorRequestState = new ErrorRequestState(getActivity(), this);
		mEmptyRequestState = new EmptyRequestState(getActivity(), this);
	}

	@Override
	public void onActionTrigger(int actionId) {
	}

}