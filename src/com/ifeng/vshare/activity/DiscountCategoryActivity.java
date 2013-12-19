package com.ifeng.vshare.activity;

import android.os.Bundle;

import com.ifeng.util.ui.NavgationbarView;
import com.ifeng.vshare.R;
import com.ifeng.vshare.fragment.DiscountCategoryFragment;

/**
 * 商盟特惠分类页面
 * 
 * @author Calvin
 * 
 */
public class DiscountCategoryActivity extends VShareCategoryActivity {
	@Override
	protected void onCreate(Bundle arg0) {

		super.onCreate(arg0);
		setContentView(R.layout.base_activity_single);

		NavgationbarView navgationbar = (NavgationbarView) findViewById(R.id.navgationbar);

		/*
		 * 设置导航条部分
		 */
		navgationbar.setTitle(getString(R.string.title_discount));
		navgationbar.setBackItem(this);

		DiscountCategoryFragment discountFragment = new DiscountCategoryFragment();
		getSupportFragmentManager().beginTransaction()
				.add(R.id.layout_content, discountFragment).commit();
	}

}
