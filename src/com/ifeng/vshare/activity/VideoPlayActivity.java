package com.ifeng.vshare.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ifeng.util.ui.NavgationbarView;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareActivity;
import com.ifeng.vshare.fragment.VideoPlayFragment;
import com.ifeng.vshare.fragment.VideoPlayFragment.OnVideoEventCallback;

/**
 * 视频播放页
 * 
 * @author Calvin
 * 
 */
public class VideoPlayActivity extends VShareActivity {

	/** title key */
	private static final String KEY_TITLE = "media_title";

	// 调用自视频列表页面
	/** url key */
	private static final String KEY_ID = "media_id";

	// 调用自资讯详情页面
	private static final String KEY_URL = "media_url";

	/**
	 * get intent
	 * 
	 * @param activity
	 * @param title
	 * @param id
	 * @return
	 */
	public static Intent getIntent(Context activity, String title, int id) {
		Intent intent = new Intent(activity, VideoPlayActivity.class);
		intent.putExtra(KEY_TITLE, title);
		intent.putExtra(KEY_ID, id);
		return intent;
	}

	/**
	 * get intent
	 * 
	 * @param activity
	 * @param title
	 * @param url
	 * @return
	 */
	public static Intent getIntent(Context activity, String title, String url) {
		Intent intent = new Intent(activity, VideoPlayActivity.class);
		intent.putExtra(KEY_TITLE, title);
		intent.putExtra(KEY_URL, url);
		return intent;
	}

	/** navgationbar */
	private NavgationbarView mNavgationbar;
	/** 视频播放fragment */
	private VideoPlayFragment mVideoPlayFragment;

	@Override
	protected void onCreate(Bundle arg0) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
		super.onCreate(arg0);
		setContentView(R.layout.base_activity_single);

		mNavgationbar = (NavgationbarView) findViewById(R.id.navgationbar);
		mNavgationbar.setVisibility(View.GONE);

		String title = getIntent().getExtras().getString(KEY_TITLE);
		int id = getIntent().getExtras().getInt(KEY_ID);
		String url = getIntent().getExtras().getString(KEY_URL);

		OnVideoEventCallback callback = new OnVideoEventCallback() {

			@Override
			public void writeToParcel(Parcel dest, int flags) {

			}

			@Override
			public int describeContents() {
				return 0;
			}

			@Override
			public int getVideoViewWidth() {
				// 获取全屏宽度
				DisplayMetrics dm = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dm);
				return dm.widthPixels;
			}

			@Override
			public int getVideoViewHeight() {
				// 获取全屏高度
				DisplayMetrics dm = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dm);
				return dm.heightPixels;
			}

			@Override
			public void onVideoFinish() {
				// do nothing
			}
		};

		if (url != null) {
			// 调用自资讯详情
			mVideoPlayFragment = VideoPlayFragment.getInstance(title, url,
					callback);
		} else {
			// 调用自视频列表
			mVideoPlayFragment = VideoPlayFragment.getInstance(title, id,
					callback);
		}

		getSupportFragmentManager().beginTransaction()
				.add(R.id.layout_content, mVideoPlayFragment).commit();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		VideoPlayFragment videoPlayFragment = (VideoPlayFragment) getSupportFragmentManager()
				.findFragmentById(R.id.layout_content);
		if (videoPlayFragment != null) {
			videoPlayFragment.resetOrientation();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
				|| keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			sendBroadcast(new Intent(VideoPlayFragment.BROADCAST_VOLUMECHANGED));
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void finish() {
		// 由推送进入详情页面，需要返回主菜单
		if (sActivityStack.size() == 1) {
			startActivity(new Intent(this, VideoCategoryActivity.class));
		}
		super.finish();
	}
}
