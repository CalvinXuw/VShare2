package com.ifeng.vshare.activity;

import java.util.LinkedList;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.ifeng.util.Utility;
import com.ifeng.util.ui.NavgationbarView;
import com.ifeng.util.ui.SlideTabbarView;
import com.ifeng.util.ui.SlideTabbarView.OnTabSelectedListener;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareActivity;
import com.ifeng.vshare.adapter.PagerSliderConnectAdapter;
import com.ifeng.vshare.fragment.CommentBarFragment;
import com.ifeng.vshare.fragment.CommentListFragment;
import com.ifeng.vshare.fragment.LectureVideoCommentFragment;
import com.ifeng.vshare.fragment.LectureVideoListFragment;
import com.ifeng.vshare.fragment.LectureVideoListFragment.OnVideoListCallback;
import com.ifeng.vshare.fragment.VideoPlayFragment;
import com.ifeng.vshare.fragment.VideoPlayFragment.OnVideoEventCallback;
import com.ifeng.vshare.model.LectureDetailItem;

/**
 * 讲堂具体的详情页面，包含现场以及回顾的详情页面，内容由 {@link VideoPlayFragment}、
 * {@link CommentBarFragment}、 {@link CommentListFragment}构成
 * 
 * @author Calvin
 * 
 */
public class LectureVideoActivity extends VShareActivity {

	/** key comment name */
	private static final String KEY_LECTURE_ITEM = "item";
	/** key is playnow */
	private static final String KEY_PLAYNOW = "playnow";

	/**
	 * 获取实例
	 * 
	 * @param activity
	 * @param item
	 * @param isPlay
	 *            是否立即播放
	 * @return
	 */
	public static Intent getIntent(Activity activity, LectureDetailItem item,
			boolean isPlay) {
		Intent intent = new Intent(activity, LectureVideoActivity.class);
		intent.putExtra(KEY_LECTURE_ITEM, item);
		intent.putExtra(KEY_PLAYNOW, isPlay);
		return intent;
	}

	/** 导航条 */
	private View mNavgationbar;
	/** 视频位置 */
	private View mVideo;
	/** 视频相关分页标签 */
	private View mVideoPager;
	/** 讲堂详情数据 */
	private LectureDetailItem mDetailItem;
	/** 视频分集列表Fragment */
	private LectureVideoListFragment mLectureVideoListFragment;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		mDetailItem = (LectureDetailItem) getIntent().getExtras()
				.getSerializable(KEY_LECTURE_ITEM);

		setContentView(R.layout.lecture_video);

		NavgationbarView navgationbar = (NavgationbarView) findViewById(R.id.navgationbar);

		/*
		 * 设置导航条部分
		 */
		navgationbar.setTitle(getString(R.string.title_detail));
		navgationbar.setBackItem(this);

		mNavgationbar = navgationbar;
		mVideo = findViewById(R.id.layout_video);
		mVideoPager = findViewById(R.id.layout_video_pager);

		LinearLayout.LayoutParams oldParams = (LayoutParams) mVideo
				.getLayoutParams();
		oldParams.height = (int) (Utility.getScreenHeight(this) / 3);
		mVideo.setLayoutParams(oldParams);

		/*
		 * 分页标签部分
		 */
		SlideTabbarView slideTabbar = (SlideTabbarView) findViewById(R.id.tab_slidebar);
		final ViewPager pager = (ViewPager) findViewById(R.id.pager_content);

		/*
		 * 处理分页数据
		 */
		LinkedList<Fragment> subPageFragments = new LinkedList<Fragment>();
		subPageFragments.add(LectureVideoCommentFragment.getInstance(
				mDetailItem.mTitle, mDetailItem.mCommentTag));
		mLectureVideoListFragment = LectureVideoListFragment.getInstance(
				mDetailItem.mVideos, mOnVideoListCallback);
		subPageFragments.add(mLectureVideoListFragment);

		/*
		 * 设置tab滑块和pager，连接起两个控件
		 */
		slideTabbar
				.setHintBackground(R.drawable.background_slidebar_hint_video);
		slideTabbar.setNormalTextSizeFromDimen(getResources()
				.getDimensionPixelSize(R.dimen.font_slidebar));
		slideTabbar.setActiveTextSizeFromDimen(getResources()
				.getDimensionPixelSize(R.dimen.font_slidebar));
		slideTabbar.setBackgroundResource(R.drawable.background_slidebar_video);
		slideTabbar.setNormalTextColor(R.color.font_slidebar_video_nag);
		slideTabbar.setActiveTextColor(R.color.font_slidebar_video_pos);

		PagerSliderConnectAdapter mAdapter = new PagerSliderConnectAdapter(
				getSupportFragmentManager(), subPageFragments, slideTabbar);

		pager.setAdapter(mAdapter);
		pager.setOnPageChangeListener(mAdapter);
		slideTabbar.addTabsByTabs(new OnTabSelectedListener() {
			@Override
			public void onSelected(int which) {
				pager.setCurrentItem(which);
			}
		}, getString(R.string.title_comment),
				getString(R.string.title_videolist));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
				|| keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			sendBroadcast(new Intent(VideoPlayFragment.BROADCAST_VOLUMECHANGED));
		} else if (keyCode == KeyEvent.KEYCODE_BACK
				&& getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			VideoPlayFragment videoPlayFragment = (VideoPlayFragment) getSupportFragmentManager()
					.findFragmentById(R.id.layout_video);
			videoPlayFragment.setWindowScreen();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		VideoPlayFragment videoPlayFragment = (VideoPlayFragment) getSupportFragmentManager()
				.findFragmentById(R.id.layout_video);
		if (videoPlayFragment != null) {
			videoPlayFragment.resetOrientation();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// 横屏时全屏播放
			mNavgationbar.setVisibility(View.GONE);
			mVideoPager.setVisibility(View.GONE);

			LinearLayout.LayoutParams oldParams = (LayoutParams) mVideo
					.getLayoutParams();
			oldParams.height = RelativeLayout.LayoutParams.FILL_PARENT;
			mVideo.setLayoutParams(oldParams);
		} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			// 竖屏时窗口播放
			mNavgationbar.setVisibility(View.VISIBLE);
			mVideoPager.setVisibility(View.VISIBLE);

			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			LinearLayout.LayoutParams oldParams = (LayoutParams) mVideo
					.getLayoutParams();
			oldParams.height = dm.heightPixels / 3;
			mVideo.setLayoutParams(oldParams);
		}
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * 提供给视频fragment的视图大小尺寸数据支撑
	 */
	private OnVideoEventCallback mOnVideoEventCallback = new OnVideoEventCallback() {

		@Override
		public void writeToParcel(Parcel dest, int flags) {
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public int getVideoViewWidth() {
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			return dm.widthPixels;
		}

		@Override
		public int getVideoViewHeight() {
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				return dm.heightPixels;
			} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				return dm.heightPixels / 3;
			}

			return 0;
		}

		@Override
		public void onVideoFinish() {
			if (mLectureVideoListFragment != null) {
				mLectureVideoListFragment.playNextOne();
			}
		}
	};

	/**
	 * 分集视频列表回调
	 */
	private OnVideoListCallback mOnVideoListCallback = new OnVideoListCallback() {

		@Override
		public void writeToParcel(Parcel dest, int flags) {
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void onVideoSelected(int position) {
			if (getSupportFragmentManager().findFragmentById(R.id.layout_video) == null) {
				VideoPlayFragment videoPlayFragment = VideoPlayFragment
						.getInstance(
								mDetailItem.mVideos.get(position).mVideoTitle,
								mDetailItem.mVideos.get(position).mVideoUrl,
								getIntent().getExtras().getBoolean(KEY_PLAYNOW),
								mOnVideoEventCallback);
				getSupportFragmentManager().beginTransaction()
						.add(R.id.layout_video, videoPlayFragment).commit();
			} else {
				VideoPlayFragment videoPlayFragment = VideoPlayFragment
						.getInstance(
								mDetailItem.mVideos.get(position).mVideoTitle,
								mDetailItem.mVideos.get(position).mVideoUrl,
								true, mOnVideoEventCallback);
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.layout_video, videoPlayFragment).commit();
			}

		}
	};
}
