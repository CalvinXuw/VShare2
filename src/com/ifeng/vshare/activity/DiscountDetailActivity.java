package com.ifeng.vshare.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ifeng.util.ui.NavgationbarView;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareActivity;
import com.ifeng.vshare.fragment.DiscountDetailFragment;

/**
 * 商盟详情页面activity
 * 
 * @author Calvin
 * 
 */
public class DiscountDetailActivity extends VShareActivity {

	/** key id */
	private static final String KEY_ID = "id";
	/** key title */
	private static final String KEY_TITLE = "title";

	/**
	 * get intent
	 * 
	 * @param activity
	 * @param title
	 * @param id
	 * @return
	 */
	public static Intent getIntent(Context activity, String title, int id) {
		Intent intent = new Intent(activity, DiscountDetailActivity.class);
		intent.putExtra(KEY_TITLE, title);
		intent.putExtra(KEY_ID, id);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.base_activity_single);

		NavgationbarView navgationbar = (NavgationbarView) findViewById(R.id.navgationbar);

		/*
		 * 设置导航条部分
		 */
		navgationbar.setTitle(getIntent().getExtras().getString(KEY_TITLE));
		navgationbar.setBackItem(this);

		DiscountDetailFragment detailFragment = DiscountDetailFragment
				.getInstance(getIntent().getExtras().getInt(KEY_ID));
		getSupportFragmentManager().beginTransaction()
				.add(R.id.layout_content, detailFragment).commit();
	}

	@Override
	public void finish() {
		// 由推送进入详情页面，需要返回主菜单
		if (sActivityStack.size() == 1) {
			startActivity(new Intent(this, DiscountCategoryActivity.class));
		}
		super.finish();
	}
}
