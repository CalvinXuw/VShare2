package com.ifeng.vshare.activity;

import android.os.Bundle;

import com.ifeng.util.ui.NavgationbarView;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareActivity;
import com.ifeng.vshare.fragment.ServiceListFragment;

/**
 * 会员服务页面
 * 
 * @author Calvin
 * 
 */
public class ServiceActivity extends VShareActivity {
	@Override
	protected void onCreate(Bundle arg0) {

		super.onCreate(arg0);
		setContentView(R.layout.base_activity_single);

		NavgationbarView navgationbar = (NavgationbarView) findViewById(R.id.navgationbar);

		/*
		 * 设置导航条部分
		 */
		navgationbar.setTitle(getString(R.string.title_service));
		navgationbar.setBackItem(this);

		ServiceListFragment serviceFragment = new ServiceListFragment();
		getSupportFragmentManager().beginTransaction()
				.add(R.id.layout_content, serviceFragment).commit();
	}
}
