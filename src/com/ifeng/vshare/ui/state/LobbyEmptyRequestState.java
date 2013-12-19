package com.ifeng.vshare.ui.state;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareApplication;
import com.ifeng.vshare.requestor.LobbyListRequestor.LobbyType;
import com.ifeng.vshare.ui.LobbyHeadView;
import com.ifeng.vshare.util.VShareUtil;

/**
 * 贵宾厅空数据页面
 * 
 * @author Calvin
 * 
 */
public class LobbyEmptyRequestState extends EmptyRequestState {

	/** 贵宾厅类型 */
	private LobbyType mLobbyType;

	public LobbyEmptyRequestState(Activity context,
			OnStateActionListener listener, LobbyType lobbyType) {
		super(context, listener);
		mLobbyType = lobbyType;
	}

	@Override
	protected View createView() {
		View stateView = LayoutInflater.from(mActivity).inflate(
				R.layout.state_empty_lobby, null);

		LobbyHeadView mLobbyHeadView = new LobbyHeadView(mActivity);
		mLobbyHeadView.initWithLobbyType(mLobbyType);
		mLobbyHeadView.setCityName(((VShareApplication) mActivity
				.getApplication()).getLocationInfo().mChineseName);

		ViewGroup headContainer = (ViewGroup) stateView
				.findViewById(R.id.layout_headcontainer);
		headContainer.addView(mLobbyHeadView.getContentView());

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
