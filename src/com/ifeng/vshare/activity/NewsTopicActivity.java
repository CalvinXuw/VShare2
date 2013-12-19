package com.ifeng.vshare.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ifeng.util.ui.NavgationbarView;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareActivity;
import com.ifeng.vshare.fragment.NewsTopicFragment;

/**
 * @author qianzy
 * @time 2013-6-4 下午3:43:23
 * @describe 加载新闻话题fragment的activity
 */
public class NewsTopicActivity extends VShareActivity {

	/** key topic id */
	private static final String KEY_TOPICID = "topic_id";

	/**
	 * 获取intent
	 * 
	 * @param activity
	 * @param id
	 * @return
	 */
	public static Intent getIntent(Context activity, int id) {
		Intent intent = new Intent(activity, NewsTopicActivity.class);
		intent.putExtra(KEY_TOPICID, id);
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
		navgationbar.setTitle(getString(R.string.title_topic));
		navgationbar.setBackItem(this);

		NewsTopicFragment newsTopicFragment = NewsTopicFragment
				.getInstance(getIntent().getExtras().getInt(KEY_TOPICID));
		getSupportFragmentManager().beginTransaction()
				.add(R.id.layout_content, newsTopicFragment).commit();

	}

	@Override
	public void finish() {
		// 由推送进入详情页面，需要返回主菜单
		if (sActivityStack.size() == 1) {
			startActivity(new Intent(this, HeadlineCategoryActivity.class));
		}
		super.finish();
	}
}
