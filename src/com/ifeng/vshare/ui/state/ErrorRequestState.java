package com.ifeng.vshare.ui.state;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import com.ifeng.util.ui.OnSingleClickListener;
import com.ifeng.util.ui.StateView.State;
import com.ifeng.vshare.R;

/**
 * 返回结果错误，或当前网络状态异常的页面
 * 
 * @author XuWei
 * 
 */
public class ErrorRequestState extends State {

	/**
	 * 样式，light普通页面采用，dark图片详情页面采用
	 * 
	 * @author Calvin
	 * 
	 */
	public enum Style {
		LIGHT, DARK
	}

	/** 页面返回数据错误再次发起请求重试的事件 */
	public static final int STATE_ACTION_ERROR_RETRY = 201;
	/** 页面返回网络不可用检查网络配置的事件 */
	public static final int STATE_ACTION_ERROR_CHECKNETWORK = 202;
	/** 页面事件触发监听 */
	private OnStateActionListener mActionListener;
	/** 采用样式 */
	private Style mStyle;

	/**
	 * 构造
	 * 
	 * @param context
	 * @param listener
	 *            在请求controller中加入公共处理的listener，在listener中以actionid予以区分具体事件。
	 */
	public ErrorRequestState(Activity activity, OnStateActionListener listener) {
		this(activity, Style.LIGHT, listener);
	}

	/**
	 * 构造
	 * 
	 * @param context
	 * @param style
	 *            展现样式
	 * @param listener
	 *            在请求controller中加入公共处理的listener，在listener中以actionid予以区分具体事件。
	 */
	public ErrorRequestState(Activity activity, Style style,
			OnStateActionListener listener) {
		super(activity);
		mStyle = style;
		mActionListener = listener;
	}

	@Override
	protected View createView() {
		View stateView = null;
		if (mStyle == Style.LIGHT) {
			stateView = LayoutInflater.from(mActivity).inflate(
					R.layout.state_error, null);
		} else if (mStyle == Style.DARK) {
			stateView = LayoutInflater.from(mActivity).inflate(
					R.layout.state_error_dark, null);
		}

		return stateView;
	}

	@Override
	protected void addAction(View container) {
		container.setOnClickListener(new OnSingleClickListener() {

			@Override
			public void onSingleClick(View v) {
				mActionListener.onActionTrigger(STATE_ACTION_ERROR_RETRY);
			}
		});
	}

}
