package com.ifeng.vshare.ui.state;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import com.ifeng.util.ui.StateView.State;
import com.ifeng.vshare.R;

/**
 * 返回结果为空数据状态页面
 * 
 * @author XuWei
 * 
 */
public class EmptyRequestState extends State {

	/**
	 * 构造
	 * 
	 * @param context
	 * @param listener
	 *            在请求controller中加入公共处理的listener，在listener中以actionid予以区分具体事件。
	 */
	public EmptyRequestState(Activity activity, OnStateActionListener listener) {
		super(activity);
	}

	@Override
	protected View createView() {
		View stateView = LayoutInflater.from(mActivity).inflate(
				R.layout.state_empty, null);
		return stateView;
	}

	@Override
	protected void addAction(View container) {
	}

}
