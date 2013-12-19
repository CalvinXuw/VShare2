package com.ifeng.vshare.activity;

import android.os.Bundle;

import com.ifeng.util.ui.NavgationbarView;
import com.ifeng.vshare.R;
import com.ifeng.vshare.fragment.ClubCategoryFragment;

/**
 * 俱乐部分类页面，其中包含俱乐部详情、贵宾厅、会员服务三个页面的入口
 * 
 * @author Calvin
 * 
 */
public class ClubCategoryActivity extends VShareCategoryActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.base_activity_single);

		NavgationbarView navgationbar = (NavgationbarView) findViewById(R.id.navgationbar);

		/*
		 * 设置导航条部分
		 */
		navgationbar.setTitle(getString(R.string.title_club));
		navgationbar.setBackItem(this);

		ClubCategoryFragment clubFragment = new ClubCategoryFragment();
		getSupportFragmentManager().beginTransaction()
				.add(R.id.layout_content, clubFragment).commit();
	}

}
