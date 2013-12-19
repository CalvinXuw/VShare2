package com.ifeng.vshare.activity;

import android.os.Bundle;

import com.ifeng.util.ui.NavgationbarView;
import com.ifeng.vshare.R;
import com.ifeng.vshare.fragment.SettingFragment;

/**
 * 设置页面
 * 
 * @author Calvin
 * 
 */
public class SettingActivity extends VShareCategoryActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_activity_single);
		NavgationbarView navgationbar = (NavgationbarView) findViewById(R.id.navgationbar);
		/*
		 * 设置导航条部分
		 */
		navgationbar.setBackItem(this);
		navgationbar.setTitle(getString(R.string.setting));
		SettingFragment mVshareSettingFragment = new SettingFragment();
		getSupportFragmentManager().beginTransaction()
				.add(R.id.layout_content, mVshareSettingFragment).commit();

	}
}
