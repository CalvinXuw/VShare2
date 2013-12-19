package com.ifeng.vshare.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ifeng.util.ui.NavgationbarView;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareActivity;
import com.ifeng.vshare.fragment.CommentBarFragment;
import com.ifeng.vshare.fragment.CommentListFragment;
import com.ifeng.vshare.fragment.VideoPlayFragment;

/**
 * 讲堂具体的详情页面，包含现场以及回顾的详情页面，内容由 {@link VideoPlayFragment}、
 * {@link CommentBarFragment}、 {@link CommentListFragment}构成
 * 
 * @author Calvin
 * 
 */
public class LectureCommentActivity extends VShareActivity {

	/** key comment name */
	private static final String KEY_LECTURE_NAME = "name";
	/** key comment tag */
	private static final String KEY_COMMENT_TAG = "comment";

	/**
	 * 获取实例
	 * 
	 * @param activity
	 * @param name
	 * @param string
	 * @return
	 */
	public static Intent getIntent(Activity activity, String name,
			String commentTag) {
		Intent intent = new Intent(activity, LectureCommentActivity.class);
		intent.putExtra(KEY_LECTURE_NAME, name);
		intent.putExtra(KEY_COMMENT_TAG, commentTag);
		return intent;
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		setContentView(R.layout.lecture_comment);

		NavgationbarView navgationbar = (NavgationbarView) findViewById(R.id.navgationbar);

		/*
		 * 设置导航条部分
		 */
		navgationbar.setTitle(getString(R.string.title_detail));
		navgationbar.setBackItem(this);

		// 初始化三大组件
		CommentListFragment commentListFragment = CommentListFragment
				.getInstance(getString(R.string.lecture_comment_title_user),
						getIntent().getExtras().getString(KEY_COMMENT_TAG));
		CommentBarFragment commentBarFragment = CommentBarFragment.getInstance(
				getIntent().getExtras().getString(KEY_LECTURE_NAME),
				getIntent().getExtras().getString(KEY_COMMENT_TAG));

		getSupportFragmentManager().beginTransaction()
				.add(R.id.layout_commentlist, commentListFragment).commit();
		getSupportFragmentManager().beginTransaction()
				.add(R.id.layout_commentbar, commentBarFragment).commit();
	}

}
