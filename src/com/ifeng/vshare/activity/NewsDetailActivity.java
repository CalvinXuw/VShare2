package com.ifeng.vshare.activity;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.ViewGroup;

import com.ifeng.util.ui.NavgationbarView;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareActivity;
import com.ifeng.vshare.fragment.CommentBarFragment;
import com.ifeng.vshare.fragment.CommentListFragment;
import com.ifeng.vshare.fragment.NewsDetailFragment;
import com.ifeng.vshare.ui.CommentBarPagerConnecter;

/**
 * @author qianzy
 * @time 2013-6-4 下午3:43:23
 * @describe 加载新闻详情fragment的activity
 */
public class NewsDetailActivity extends VShareActivity {

	/** key id */
	private static final String KEY_ID = "id";
	/** key title */
	private static final String KEY_TITLE = "title";
	/** key comment tag */
	private static final String KEY_COMMENT_TAG = "comment";

	/** 内容页页码 */
	public static final int PAGE_INDEX_CONTENT = 0;
	/** 评论页页码 */
	public static final int PAGE_INDEX_COMMENT = 1;

	/**
	 * 获取跳转intent
	 * 
	 * @param activity
	 * @param id
	 * @param title
	 * @param commentTag
	 * @return
	 */
	public static Intent getIntent(Context activity, int id, String title,
			String commentTag) {
		Intent intent = new Intent(activity, NewsDetailActivity.class);
		intent.putExtra(KEY_ID, id);
		intent.putExtra(KEY_TITLE, title);
		intent.putExtra(KEY_COMMENT_TAG, commentTag);

		return intent;
	}

	/** 评论条fragment */
	private CommentBarFragment mCommentBarFragment;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.news_detail);

		NavgationbarView navgationbar = (NavgationbarView) findViewById(R.id.navgationbar);
		final ViewPager pager = (ViewPager) findViewById(R.id.pager_content);

		/*
		 * 设置导航条部分
		 */
		navgationbar.setTitle(getString(R.string.title_newsdetail));
		navgationbar.setBackItem(this);

		// 资讯内容页面
		NewsDetailFragment newsDetailFragment = NewsDetailFragment.getInstance(
				getIntent().getExtras().getInt(KEY_ID), getIntent().getExtras()
						.getString(KEY_TITLE));

		// 资讯评论页面
		CommentListFragment commentListFragment = CommentListFragment
				.getInstance(getString(R.string.comment_title_usercomment),
						getIntent().getExtras().getString(KEY_COMMENT_TAG));

		// 评论条
		mCommentBarFragment = CommentBarFragment.getInstance(getIntent()
				.getExtras().getString(KEY_TITLE), getIntent().getExtras()
				.getString(KEY_COMMENT_TAG), new CommentBarPagerConnecter() {

			// 传入页面回调，响应评论条中资讯内容、全部评论按钮点击事件
			@Override
			public void onPageSelected(int which) {
				pager.setCurrentItem(which);
			}

			@Override
			public int getCurrentPage() {
				return pager.getCurrentItem();
			}

			@Override
			public int describeContents() {
				return 0;
			}

			@Override
			public void writeToParcel(Parcel dest, int flags) {

			}
		});

		// pager页
		List<Fragment> newsDetailFragments = new LinkedList<Fragment>();
		newsDetailFragments.add(newsDetailFragment);
		newsDetailFragments.add(commentListFragment);

		NewsDetailPagerAdapter newsDetailPagerAdapter = new NewsDetailPagerAdapter(
				getSupportFragmentManager(), newsDetailFragments);
		pager.setAdapter(newsDetailPagerAdapter);
		pager.setOnPageChangeListener(newsDetailPagerAdapter);

		getSupportFragmentManager().beginTransaction()
				.add(R.id.layout_commentbar, mCommentBarFragment).commit();

	}

	/**
	 * 详情页面的pageradapter，用于控制对commentbar的展示
	 * 
	 * @author Calvin
	 * 
	 */
	private class NewsDetailPagerAdapter extends FragmentPagerAdapter implements
			OnPageChangeListener {

		/** 资源fragments */
		private List<Fragment> mFragments;

		/**
		 * 构造
		 * 
		 * @param fm
		 * @param fragments
		 */
		public NewsDetailPagerAdapter(FragmentManager fm,
				List<Fragment> fragments) {
			super(fm);
			mFragments = fragments;
		}

		@Override
		public int getCount() {
			return mFragments.size();
		}

		@Override
		public Fragment getItem(int position) {
			return mFragments.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
		}

		@Override
		public void onPageSelected(int arg0) {
			if (mCommentBarFragment != null) {
				mCommentBarFragment.updateBtnState();
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
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
