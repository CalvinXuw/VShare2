package com.ifeng.vshare.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ifeng.util.ui.NavgationbarView;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareActivity;
import com.ifeng.vshare.fragment.ClubDetailFragment;

/**
 * 俱乐部详情页面
 * 
 * @author Calvin
 * 
 */
public class ClubDetailActivity extends VShareActivity {

	/** key title */
	private static final String KEY_TITLE = "title";
	/** key id */
	private static final String KEY_ID = "id";

	/**
	 * get intent
	 * 
	 * @param activity
	 * @param title
	 * @param id
	 * @return
	 */
	public static Intent getIntent(Context activity, String title, int id) {
		Intent intent = new Intent(activity, ClubDetailActivity.class);
		intent.putExtra(KEY_TITLE, title);
		intent.putExtra(KEY_ID, id);
		return intent;
	}

	@Override
	protected void onCreate(Bundle arg0) {

		super.onCreate(arg0);
		setContentView(R.layout.base_activity_single);

		NavgationbarView navgationbar = (NavgationbarView) findViewById(R.id.navgationbar);

		/*
		 * 设置导航条部分
		 */
		navgationbar.setTitle(getIntent().getExtras().getString(KEY_TITLE));
		navgationbar.setBackItem(this);

		ClubDetailFragment clubDetailFragment = ClubDetailFragment
				.getInstance(getIntent().getExtras().getInt(KEY_ID));
		getSupportFragmentManager().beginTransaction()
				.add(R.id.layout_content, clubDetailFragment).commit();
	}

	@Override
	public void finish() {
		// 由推送进入详情页面，需要返回主菜单
		if (sActivityStack.size() == 1) {
			startActivity(new Intent(this, ClubCategoryActivity.class));
		}
		super.finish();
	}
}
