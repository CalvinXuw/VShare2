package com.ifeng.vshare.ui;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ifeng.vshare.R;
import com.ifeng.vshare.util.VShareUtil;

/**
 * 
 * 会员服务和贵宾厅底部的拨打10086按钮
 * 
 * @author Calvin
 * 
 */
public class ClubDialFootView {

	/** 内容view */
	private View mContentView;

	/**
	 * 构造
	 * 
	 * @param activity
	 */
	public ClubDialFootView(final Activity activity) {
		mContentView = activity.getLayoutInflater().inflate(
				R.layout.club_foot_dial, null);

		Button dial = (Button) mContentView.findViewById(R.id.btn_foot_call);
		dial.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				VShareUtil.makeDial(activity, VShareUtil.NUMBER_10086);
			}
		});
	}

	/**
	 * 获取footview
	 * 
	 * @return
	 */
	public View getContentView() {
		return mContentView;
	}

}
