package com.ifeng.vshare.ui.state;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import com.ifeng.util.ui.StateView.State;
import com.ifeng.vshare.R;

/**
 * 正在loading中请求状态页面
 * 
 * @author XuWei
 * 
 */
public class ProcessingRequestState extends State {

	/**
	 * 样式，light普通页面采用，dark图片详情页面采用
	 * 
	 * @author Calvin
	 * 
	 */
	public enum Style {
		LIGHT, DARK
	}

	/** 采用样式 */
	private Style mStyle;

	/**
	 * 构造
	 * 
	 * @param context
	 */
	public ProcessingRequestState(Activity activity) {
		this(activity, Style.LIGHT);
	}

	/**
	 * 构造
	 * 
	 * @param context
	 * @param style
	 *            采用样式
	 */
	public ProcessingRequestState(Activity activity, Style style) {
		super(activity);
		mStyle = style;
	}

	@Override
	protected View createView() {
		View stateView = null;
		if (mStyle == Style.LIGHT) {
			stateView = LayoutInflater.from(mActivity).inflate(
					R.layout.state_process, null);
		} else if (mStyle == Style.DARK) {
			stateView = LayoutInflater.from(mActivity).inflate(
					R.layout.state_process_dark, null);
		}

		return stateView;
	}

	@Override
	protected void addAction(View container) {
		// Do nothing
	}

}
