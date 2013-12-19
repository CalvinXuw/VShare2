package com.ifeng.vshare.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ifeng.util.Utility;
import com.ifeng.util.ui.NavgationbarView;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareActivity;
import com.ifeng.vshare.VShareFragment;

/**
 * 关于
 * 
 * @author Calvin
 * 
 */
public class AboutActivity extends VShareActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.base_activity_single);

		NavgationbarView navgationbar = (NavgationbarView) findViewById(R.id.navgationbar);

		/*
		 * 设置导航条部分
		 */
		navgationbar.setTitle(getString(R.string.title_about));
		navgationbar.setBackItem(this);

		getSupportFragmentManager().beginTransaction()
				.add(R.id.layout_content, new AboutFragment()).commit();
	}

	/**
	 * 关于
	 * 
	 * @author Calvin
	 * 
	 */
	public static class AboutFragment extends VShareFragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View layout = inflater.inflate(R.layout.about, container, false);
			TextView appname = (TextView) layout
					.findViewById(R.id.text_about_appname);
			appname.setText(getString(R.string.app_name) + " "
					+ Utility.getAppVersionName(getActivity()));

			return layout;
		}

		@Override
		public void onActionTrigger(int actionId) {

		}
	}
}
