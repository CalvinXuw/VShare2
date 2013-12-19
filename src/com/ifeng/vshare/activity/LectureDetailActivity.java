package com.ifeng.vshare.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ifeng.util.ui.NavgationbarView;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareActivity;
import com.ifeng.vshare.fragment.LectureDetailFragment;

/**
 * 大讲堂详情 activity页面
 * 
 * @author Calvin
 * 
 */
public class LectureDetailActivity extends VShareActivity {

	/** key id */
	private static final String KEY_ID = "id";

	/**
	 * 获取跳转intent
	 * 
	 * @param activity
	 * @param id
	 * @return
	 */
	public static Intent getIntent(Context activity, int id) {
		Intent intent = new Intent(activity, LectureDetailActivity.class);
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
		navgationbar.setTitle(getString(R.string.title_detail));
		navgationbar.setBackItem(this);

		LectureDetailFragment detailFragment = LectureDetailFragment
				.getInstance(getIntent().getExtras().getInt(KEY_ID));
		getSupportFragmentManager().beginTransaction()
				.add(R.id.layout_content, detailFragment).commit();
	}

	@Override
	public void finish() {
		// 由推送进入详情页面，需要返回主菜单
		if (sActivityStack.size() == 1) {
			startActivity(new Intent(this, LectureCategoryActivity.class));
		}
		super.finish();
	}
}
