package com.ifeng.vshare.ui.state;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;

import com.ifeng.util.Utility;
import com.ifeng.vshare.R;
import com.ifeng.vshare.util.VShareUtil;

/**
 * 会员服务空数据页面
 * 
 * @author Calvin
 * 
 */
public class ServiceEmptyRequestState extends EmptyRequestState {

	public ServiceEmptyRequestState(Activity context,
			OnStateActionListener listener) {
		super(context, listener);
	}

	@Override
	protected View createView() {
		View stateView = LayoutInflater.from(mActivity).inflate(
				R.layout.state_empty_service, null);
		View headView = stateView.findViewById(R.id.image_state_empty_service);
		headView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				(int) (Utility.getScreenWidth(mActivity) / 2)));

		Button dial = (Button) stateView.findViewById(R.id.btn_state_call);
		dial.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				VShareUtil.makeDial(mActivity, VShareUtil.NUMBER_10086);
			}
		});

		return stateView;
	}
}
