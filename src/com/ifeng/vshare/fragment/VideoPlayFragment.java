package com.ifeng.vshare.fragment;

import java.io.Serializable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.ifeng.util.Utility;
import com.ifeng.util.logging.Log;
import com.ifeng.util.model.AbstractModel;
import com.ifeng.util.ui.CenterLayout;
import com.ifeng.util.ui.OnSingleClickListener;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareFragment;
import com.ifeng.vshare.config.UserConfig;
import com.ifeng.vshare.config.UserConfig.AllowAccessVideoCallback;
import com.ifeng.vshare.model.VideoDetailItem;
import com.ifeng.vshare.requestor.VideoDetailRequestor;

/**
 * 视频播放的fragment，如需调用一定要在activity中捕获声音按键，并且广播出来
 * 
 * @author Calvin
 * 
 */
public class VideoPlayFragment extends VShareFragment implements
		OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener,
		OnVideoSizeChangedListener, SurfaceHolder.Callback {

	/** 声音按键点击的广播 */
	public static final String BROADCAST_VOLUMECHANGED = "com.ifeng.vshare.volumechange";
	/** controller消失 */
	private static final String BROADCAST_CONTROLLER_DISMISS = "com.ifeng.vshare.controller.dismiss";
	/** 状态更新 */
	private static final String BROADCAST_CONTROLLER_UPDATE = "com.ifeng.vshare.controller.update";

	/** key title */
	private static final String KEY_TITLE = "title";
	/** key come from */
	private static final String KEY_COMEFROM = "comefrom";
	/** key videosize callback */
	private static final String KEY_VIDEOSIZE_CALLBACK = "callback";

	/*
	 * type of VIDEOPLAY_ACTIVITY
	 */
	/** key media id */
	private static final String KEY_MEDIA_ID = "media_id";

	/*
	 * type of LECTURE_ACTIVITY
	 */
	/** key media url */
	private static final String KEY_MEDIA_URL = "media_url";
	/** key is playnow */
	private static final String KEY_PLAYNOW = "playnow";

	/**
	 * 调用来源
	 * 
	 * @author Calvin
	 * 
	 */
	private enum ComeFromType implements Serializable {
		VIDEOPLAY_ACTIVITY, LECTURE_ACTIVITY, NEWS_ACTIVITY
	}

	/**
	 * 向调用者回调获取视频view大小的回调接口，该接口的目的主要是为了解决
	 * {@link Fragment#onConfigurationChanged}
	 * 方法在触发之后才会更改view的width和height，对于处理视频的size大小有延迟，所以将size的获取抛出给activity来手动控制
	 * 
	 * @author xuwei
	 * 
	 */
	public static interface OnVideoEventCallback extends Parcelable {
		/**
		 * 获取宽度
		 * 
		 * @return
		 */
		public int getVideoViewWidth();

		/**
		 * 获取高度
		 * 
		 * @return
		 */
		public int getVideoViewHeight();

		/**
		 * 视频播放完成
		 */
		public void onVideoFinish();
	}

	/**
	 * 获取实例，来自于视频详情播放页面
	 * 
	 * @param title
	 * @param id
	 * @param callback
	 * @return
	 */
	public static VideoPlayFragment getInstance(String title, int id,
			OnVideoEventCallback callback) {
		VideoPlayFragment videoPlayFragment = new VideoPlayFragment();
		Bundle arg = new Bundle();
		arg.putString(KEY_TITLE, title);
		arg.putInt(KEY_MEDIA_ID, id);
		arg.putSerializable(KEY_COMEFROM, ComeFromType.VIDEOPLAY_ACTIVITY);
		arg.putParcelable(KEY_VIDEOSIZE_CALLBACK, callback);
		videoPlayFragment.setArguments(arg);
		return videoPlayFragment;
	}

	/**
	 * 获取实例，来自于大讲堂详情播放页面
	 * 
	 * @param title
	 * @param url
	 * @param isPlayNow
	 * @param callback
	 * @return
	 */
	public static VideoPlayFragment getInstance(String title, String url,
			boolean isPlayNow, OnVideoEventCallback callback) {
		VideoPlayFragment videoPlayFragment = new VideoPlayFragment();
		Bundle arg = new Bundle();
		arg.putString(KEY_TITLE, title);
		arg.putString(KEY_MEDIA_URL, url);
		arg.putBoolean(KEY_PLAYNOW, isPlayNow);
		arg.putSerializable(KEY_COMEFROM, ComeFromType.LECTURE_ACTIVITY);
		arg.putParcelable(KEY_VIDEOSIZE_CALLBACK, callback);
		videoPlayFragment.setArguments(arg);
		return videoPlayFragment;
	}

	/**
	 * 获取实例，来自于资讯详情页面
	 * 
	 * @param title
	 * @param url
	 * @param callback
	 * @return
	 */
	public static VideoPlayFragment getInstance(String title, String url,
			OnVideoEventCallback callback) {
		VideoPlayFragment videoPlayFragment = new VideoPlayFragment();
		Bundle arg = new Bundle();
		arg.putString(KEY_TITLE, title);
		arg.putString(KEY_MEDIA_URL, url);
		arg.putSerializable(KEY_COMEFROM, ComeFromType.NEWS_ACTIVITY);
		arg.putParcelable(KEY_VIDEOSIZE_CALLBACK, callback);
		videoPlayFragment.setArguments(arg);
		return videoPlayFragment;
	}

	/** key model requestor */
	private static final String KEY_MODEL_REQUESTOR = "requestor";

	/** 调用来源 */
	private ComeFromType mFromType;
	/** 视图大小获取回调 */
	private OnVideoEventCallback mVideoEventCallback;

	/** 视频title */
	private String mTitle;

	/** 视频id */
	private int mId;
	/** 视频url */
	private String mUrl;
	/** 是否立即播放 */
	private boolean mIsPlayNow;

	/** 视频播放控件 */
	private SurfaceView mVideoPlayView;
	/** surface holder */
	private SurfaceHolder mVideoPlayHolder;
	/** 播放器 */
	private MediaPlayer mPlayer;

	/** 视频加载状态 */
	private ViewGroup mVideoLoadingView;
	/** 重播按钮 */
	private View mReplayBtn;
	/** 控制器View holder */
	private MediaControllerViewHolder mControllerViewHolder;

	/** 视频横向分辨率 */
	private int mVideoWidth;
	/** 视频纵向分辨率 */
	private int mVideoHeight;
	/** 是否已经获取视频大小 */
	private boolean mIsVideoSizeKnown = false;
	/** 是否已经经过onPrepared可以播放 */
	private boolean mIsVideoReadyToBePlayed = false;

	/** 声音变化广播接收 */
	private ControllerStateBroadCastReceiver mControllerStateBroadCastReceiver;

	/** requestor */
	private VideoDetailRequestor mDetailRequestor;

	/**
	 * 构造
	 */
	public VideoPlayFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mFromType = (ComeFromType) getArguments().getSerializable(KEY_COMEFROM);
		mTitle = getArguments().getString(KEY_TITLE);
		mVideoEventCallback = getArguments().getParcelable(
				KEY_VIDEOSIZE_CALLBACK);

		if (mFromType == null) {
			throw new IllegalArgumentException("must pass the comefrom arg");
		}

		switch (mFromType) {
		case VIDEOPLAY_ACTIVITY:
			mId = getArguments().getInt(KEY_MEDIA_ID);
			mIsPlayNow = true;
			mLeaveScreenOrientation = Configuration.ORIENTATION_LANDSCAPE;
			break;
		case LECTURE_ACTIVITY:
			mUrl = getArguments().getString(KEY_MEDIA_URL);
			mIsPlayNow = getArguments().getBoolean(KEY_PLAYNOW);
			mLeaveScreenOrientation = Configuration.ORIENTATION_PORTRAIT;
			break;
		case NEWS_ACTIVITY:
			mUrl = getArguments().getString(KEY_MEDIA_URL);
			mIsPlayNow = true;
			mLeaveScreenOrientation = Configuration.ORIENTATION_LANDSCAPE;
			break;
		}

		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.video_play, container, false);

		// 视频加载部分
		mVideoLoadingView = (ViewGroup) layout
				.findViewById(R.id.layout_media_loading);
		TextView loadingtitle = (TextView) layout
				.findViewById(R.id.text_media_loadingtitle);

		// 重新播放部分
		mReplayBtn = layout.findViewById(R.id.btn_media_replay);

		// 视频播放部分的surfaceview
		mVideoPlayView = (SurfaceView) layout.findViewById(R.id.videoplay);

		// 视频控制器部分
		mControllerViewHolder = new MediaControllerViewHolder();
		mControllerViewHolder.mControllerGroup = (ViewGroup) layout
				.findViewById(R.id.layout_media_control);
		mControllerViewHolder.mControllerTopView = layout
				.findViewById(R.id.layout_media_control_top);
		mControllerViewHolder.mControllerBottomView = layout
				.findViewById(R.id.layout_media_control_bottom);

		mControllerViewHolder.mTitle = (TextView) layout
				.findViewById(R.id.text_media_control_title);
		mControllerViewHolder.mBack = (Button) layout
				.findViewById(R.id.btn_media_control_back);
		mControllerViewHolder.mControl = (Button) layout
				.findViewById(R.id.btn_media_control_control);
		mControllerViewHolder.mProgressBar = (SeekBar) layout
				.findViewById(R.id.seek_media_control_progress);
		mControllerViewHolder.mVolumeGroup = (ViewGroup) layout
				.findViewById(R.id.layout_media_volume);
		mControllerViewHolder.mVolumeBar = (SeekBar) layout
				.findViewById(R.id.seek_media_control_volume);
		mControllerViewHolder.mFullScreen = (Button) layout
				.findViewById(R.id.btn_media_control_fullscreen);
		mControllerViewHolder.mTime = (TextView) layout
				.findViewById(R.id.text_media_control_time);

		// SurfaceHolder 注入
		mVideoPlayHolder = mVideoPlayView.getHolder();
		mVideoPlayHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mVideoPlayHolder.addCallback(this);

		// 页面信息填充
		mControllerViewHolder.mTitle.setText(mTitle);
		loadingtitle.setText(mTitle);
		mControllerViewHolder.mVolumeBar.setMax(((AudioManager) getActivity()
				.getSystemService(Context.AUDIO_SERVICE))
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC));// 设置最大音量
		mControllerViewHolder.mVolumeBar
				.setProgress(((AudioManager) getActivity().getSystemService(
						Context.AUDIO_SERVICE))
						.getStreamVolume(AudioManager.STREAM_MUSIC));// 设置当前音量

		// 交互事件注入
		mReplayBtn.setOnClickListener(mReplay);

		mControllerViewHolder.mControllerGroup
				.setOnClickListener(mControllerDisplayClickListener);
		mControllerViewHolder.mBack.setOnClickListener(mBackClickListener);
		mControllerViewHolder.mControl
				.setOnClickListener(mControlClickListener);
		mControllerViewHolder.mProgressBar
				.setOnSeekBarChangeListener(mProgressChangeListener);
		mControllerViewHolder.mProgressBar
				.setOnTouchListener(mProgressOnTouchListener);
		mControllerViewHolder.mVolumeBar
				.setOnSeekBarChangeListener(mVolumeChangeListener);
		mControllerViewHolder.mFullScreen
				.setOnClickListener(mFullScreenClickListener);

		mControllerStateBroadCastReceiver = new ControllerStateBroadCastReceiver();
		IntentFilter broadcastFilter = new IntentFilter();
		broadcastFilter.addAction(BROADCAST_VOLUMECHANGED);
		broadcastFilter.addAction(BROADCAST_CONTROLLER_DISMISS);
		broadcastFilter.addAction(BROADCAST_CONTROLLER_UPDATE);
		getActivity().registerReceiver(mControllerStateBroadCastReceiver,
				broadcastFilter);

		return layout;
	}

	@Override
	public void onSuccess(AbstractModel requestor) {
		if (requestor instanceof VideoDetailRequestor) {
			VideoDetailItem detailItem = ((VideoDetailRequestor) requestor)
					.getDetailItem();
			mUrl = detailItem.mMediaUrl;
			checkCouldPlay();
		}
	}

	@Override
	public void onFailed(AbstractModel requestor, int errorCode) {
		if (requestor instanceof VideoDetailRequestor) {
			makeMediaReplay();
		}
	}

	@Override
	public void onActionTrigger(int actionId) {

	}

	@Override
	protected void setImageCacheParams() {

	}

	@Override
	protected void setupModel() {
		if (mFromType == ComeFromType.VIDEOPLAY_ACTIVITY) {
			mDetailRequestor = new VideoDetailRequestor(getActivity(), mId,
					this);
			mModelManageQueue.addTaskModel(KEY_MODEL_REQUESTOR,
					mDetailRequestor);
		}
	}

	/**
	 * 初始化视频播放
	 */
	private void initVideoPlay() {
		mVideoLoadingView.setVisibility(View.VISIBLE);
		mReplayBtn.setVisibility(View.GONE);
		doCleanUp();

		if (mFromType == ComeFromType.VIDEOPLAY_ACTIVITY) {
			mDetailRequestor.request();
		} else if (mFromType == ComeFromType.LECTURE_ACTIVITY) {
			checkCouldPlay();
		} else if (mFromType == ComeFromType.NEWS_ACTIVITY) {
			checkCouldPlay();
		}
	}

	/**
	 * 切换为重播状态
	 */
	private void makeMediaReplay() {
		mVideoLoadingView.setVisibility(View.VISIBLE);
		mReplayBtn.setVisibility(View.VISIBLE);
		releaseMediaPlayer();
	}

	/**
	 * 根据网络环境及用户配置检测是否可以进行播放
	 */
	private void checkCouldPlay() {
		/*
		 * 加入对于wifi加载控制的判断
		 */
		if (!Utility.isWifiNetWork(getActivity())) {
			UserConfig.getInstance(getActivity())
					.isAllowAccessVideoWithoutWifi(mNetworkTypeCheckCallback);
		} else {
			makePlay();
		}
	}

	/**
	 * 播放视频
	 */
	private void makePlay() {
		mIsPlayNow = true;
		doCleanUp();
		releaseMediaPlayer();

		mPlayer = new MediaPlayer();

		try {
			mPlayer.setDataSource(mUrl);
			mPlayer.setDisplay(mVideoPlayHolder);

			mPlayer.prepareAsync();
			mPlayer.setOnBufferingUpdateListener(VideoPlayFragment.this);
			mPlayer.setOnCompletionListener(VideoPlayFragment.this);
			mPlayer.setOnPreparedListener(VideoPlayFragment.this);
			mPlayer.setOnVideoSizeChangedListener(VideoPlayFragment.this);
			getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
			new UpdateProgressThread().start();
		} catch (Exception e) {
			if (DEBUG) {
				Log.e(TAG, e);
			}
		}
	}

	/** 非wifi环境检测的回调处理接口 */
	private AllowAccessVideoCallback mNetworkTypeCheckCallback = new AllowAccessVideoCallback() {

		@Override
		public void isAllow(boolean isAllow) {
			if (isAllow) {
				makePlay();
			} else {
				makeMediaReplay();
			}
		}
	};

	/** 重播 */
	private OnSingleClickListener mReplay = new OnSingleClickListener() {

		@Override
		public void onSingleClick(View v) {
			initVideoPlay();
		}
	};

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (mIsPlayNow) {
			initVideoPlay();
		} else {
			makeMediaReplay();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	@Override
	public void onVideoSizeChanged(MediaPlayer arg0, int width, int height) {
		if (width == 0 || height == 0) {
			if (DEBUG) {
				Log.w(TAG, "invalid video width(" + width + ") or height("
						+ height + ")");
			}
			return;
		}
		/*
		 * 更新Video分辨率
		 */
		mIsVideoSizeKnown = true;
		mVideoWidth = width;
		mVideoHeight = height;
		startVideoPlayback();
	}

	/** 由home返回至桌面再返回页面时会重新createsurface，在重新进行缓冲完毕后，需要跳转至离开屏幕之前的进度 */
	private int mContinuedTime = 0;

	@Override
	public void onPrepared(MediaPlayer arg0) {
		/*
		 * 视频准备结束
		 */
		mIsVideoReadyToBePlayed = true;
		startVideoPlayback();
		if (mContinuedTime != 0) {
			mPlayer.seekTo(mContinuedTime);
		}
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		// 规避异常情况的completion情况，只接收正常播放结束的回调

		if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown
				// bug fix 有些时候mediaplayer会在网络情况不良好的状态下也会调用onCompletion，在此做出筛除
				// bug
				// fix有些机型上getCurrentPosition方法无法返回正常的值,加入对progressbar的进度作为辅助判断
				&& ((Math.abs(arg0.getDuration() - arg0.getCurrentPosition()) < 5000) || Math
						.abs(arg0.getDuration()
								- mControllerViewHolder.mProgressBar
										.getProgress()) < 5000)) {
			mContinuedTime = 0;
			makeMediaReplay();
			if (mVideoEventCallback != null) {
				mVideoEventCallback.onVideoFinish();
			}
		}
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		if (mControllerViewHolder != null) {
			// 其中percent为继当前进度之后的缓冲百分比，因为有可能会持续波动，故只显示更大的进度
			int buffer = (int) (percent / 100f * mp.getDuration() + mp
					.getCurrentPosition());
			if (mControllerViewHolder.mProgressBar.getSecondaryProgress() < buffer) {
				mControllerViewHolder.mProgressBar.setSecondaryProgress(buffer);
			}
		}
	}

	/*
	 * bug fix by calvin
	 * 锁屏之后屏幕的方向异常，原因是当前用户在离开屏幕后系统会自动的调转屏幕为manifest中预设的配置，所以需要先记录下离开屏幕前的方向
	 * ，在回到当前页面时进行调转重绘
	 */
	/** 暂存锁屏或离开屏幕之前的方向状态 */
	private int mLeaveScreenOrientation;

	@Override
	public void onResume() {
		if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
			mVideoPlayHolder = mVideoPlayView.getHolder();
			mVideoPlayHolder.addCallback(this);
			mVideoPlayHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			if (mPlayer != null && !mPlayer.isPlaying()) {
				mControllerViewHolder.mControl.performClick();
			}
			adjustControllerScreenOrientaion();
		}

		resetOrientation();
		super.onResume();
	}

	@Override
	public void onPause() {
		if (mPlayer != null) {
			if (mPlayer.isPlaying()) {
				mControllerViewHolder.mControl.performClick();
			}

			mContinuedTime = mControllerViewHolder.mProgressBar.getProgress();
		}

		super.onPause();
	}

	@Override
	public void onDestroy() {
		doCleanUp();
		releaseMediaPlayer();
		getActivity().unregisterReceiver(mControllerStateBroadCastReceiver);
		super.onDestroy();
	}

	/**
	 * 重置当前转向方向
	 */
	public void resetOrientation() {
		if (mLeaveScreenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
			getActivity().setRequestedOrientation(
					ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else if (mLeaveScreenOrientation == Configuration.ORIENTATION_PORTRAIT) {
			getActivity().setRequestedOrientation(
					ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	/**
	 * 设置为全屏
	 */
	public void setFullScreen() {
		mLeaveScreenOrientation = Configuration.ORIENTATION_LANDSCAPE;
		getActivity().setRequestedOrientation(
				ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	/**
	 * 设置为窗口
	 */
	public void setWindowScreen() {
		mLeaveScreenOrientation = Configuration.ORIENTATION_PORTRAIT;
		getActivity().setRequestedOrientation(
				ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		/*
		 * 在用户切换视频模式时锁定屏幕转向
		 */
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			getActivity().setRequestedOrientation(
					ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			getActivity().setRequestedOrientation(
					ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		super.onConfigurationChanged(newConfig);
		adjustControllerScreenOrientaion();
	}

	/**
	 * 根据屏幕方向调整控制器的显示方式
	 */
	private void adjustControllerScreenOrientaion() {
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			if (mControllerViewHolder != null) {
				mControllerViewHolder.mFullScreen.setVisibility(View.GONE);
				mControllerViewHolder.mVolumeGroup.setVisibility(View.VISIBLE);
				mControllerViewHolder.mBack.setVisibility(View.VISIBLE);
			}
		} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			if (mControllerViewHolder != null) {
				mControllerViewHolder.mFullScreen.setVisibility(View.VISIBLE);
				mControllerViewHolder.mVolumeGroup.setVisibility(View.GONE);
				mControllerViewHolder.mBack.setVisibility(View.GONE);
			}
		}

		/*
		 * 根据屏幕方向和容器大小重新设定surfaceview的大小
		 */
		if (mIsVideoSizeKnown) {
			int frameWidth = mVideoEventCallback.getVideoViewWidth();
			int frameHeight = mVideoEventCallback.getVideoViewHeight();

			int resetWidth = 0;
			int resetHeight = 0;

			// 普通视频，4：3、16：9等等
			if (mVideoWidth > mVideoHeight) {
				resetHeight = frameHeight;
				resetWidth = (int) (mVideoWidth / (float) mVideoHeight * resetHeight);
			} else {
				// 文艺视频，3：4、9：16等等
				resetWidth = frameWidth;
				resetHeight = (int) (mVideoHeight / (float) mVideoWidth * resetWidth);
			}

			mVideoPlayView.setLayoutParams(new CenterLayout.LayoutParams(
					resetWidth, resetHeight, 0, 0));
		}
	}

	/**
	 * 初始化Video相关配置
	 */
	private void doCleanUp() {
		mVideoWidth = 0;
		mVideoHeight = 0;
		mIsVideoReadyToBePlayed = false;
		mIsVideoSizeKnown = false;

		if (sSingleInstance != null) {
			sSingleInstance.stopReport();
		}
		if (mControllerViewHolder != null) {
			mControllerViewHolder.mProgressBar.setProgress(0);
			mControllerViewHolder.mProgressBar.setSecondaryProgress(0);
		}
	}

	/**
	 * 释放MediaPlayer
	 */
	private void releaseMediaPlayer() {
		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
		}
	}

	/**
	 * 在屏幕方向变化或media准备结束后调整视频
	 */
	private void startVideoPlayback() {
		if (!mIsVideoReadyToBePlayed || !mIsVideoSizeKnown) {
			if (DEBUG) {
				Log.w(TAG, "waiting for getvideo size and mediaplay prepared");
			}
			return;
		}

		mVideoLoadingView.setVisibility(View.GONE);
		adjustControllerScreenOrientaion();
		mVideoPlayHolder.setFixedSize(mVideoWidth, mVideoHeight);
		mPlayer.start();

		mControllerViewHolder.mProgressBar.setMax((int) mPlayer.getDuration());
	}

	/**
	 * 媒体控制器的view holder
	 * 
	 * @author Calvin
	 * 
	 */
	private class MediaControllerViewHolder {

		/** controller view */
		public ViewGroup mControllerGroup;
		/** controller top subview , title / back */
		public View mControllerTopView;
		/** controller bottom subview , control / progress / volume ... */
		public View mControllerBottomView;

		/** 返回 */
		public Button mBack;
		/** 标题 */
		public TextView mTitle;
		/** 控制键，播放or暂停 */
		public Button mControl;
		/** 进度条 */
		public SeekBar mProgressBar;
		/** 音量条 */
		public SeekBar mVolumeBar;
		/** 全屏 */
		public Button mFullScreen;
		/** 音量group */
		public ViewGroup mVolumeGroup;
		/** 时间 */
		public TextView mTime;
	}

	/** 消失倒计时时常 */
	private static final int DISMISS_COUNTDOWN = 10;
	/** 控制器消失倒计时 */
	private int mDismissCountdown = DISMISS_COUNTDOWN;
	/** 是否显示控制器 */
	private OnSingleClickListener mControllerDisplayClickListener = new OnSingleClickListener() {

		@Override
		public void onSingleClick(final View v) {
			if (v.getVisibility() == View.INVISIBLE) {
				mDismissCountdown = DISMISS_COUNTDOWN;
				Animation displayAnimation = AnimationUtils.loadAnimation(
						getActivity(), android.R.anim.fade_in);
				displayAnimation.setFillAfter(true);
				displayAnimation.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
						v.setVisibility(View.VISIBLE);
						mControllerViewHolder.mControllerTopView
								.setVisibility(View.VISIBLE);
						mControllerViewHolder.mControllerBottomView
								.setVisibility(View.VISIBLE);
					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {

					}
				});
				v.startAnimation(displayAnimation);
			} else if (v.getVisibility() == View.VISIBLE) {
				mDismissCountdown = -1;
				Animation dismissAnimation = AnimationUtils.loadAnimation(
						getActivity(), android.R.anim.fade_out);
				dismissAnimation.setFillAfter(true);
				dismissAnimation.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						v.setVisibility(View.INVISIBLE);
						mControllerViewHolder.mControllerTopView
								.setVisibility(View.GONE);
						mControllerViewHolder.mControllerBottomView
								.setVisibility(View.GONE);
					}
				});
				v.startAnimation(dismissAnimation);
			}
		}
	};

	/** 回退事件 */
	private OnSingleClickListener mBackClickListener = new OnSingleClickListener() {

		@Override
		public void onSingleClick(View v) {
			// 如果来自于视频播放activity，则直接退出
			if (mFromType == ComeFromType.VIDEOPLAY_ACTIVITY) {
				getActivity().finish();
			} else if (mFromType == ComeFromType.NEWS_ACTIVITY) {
				getActivity().finish();
			} else if (mFromType == ComeFromType.LECTURE_ACTIVITY) {
				// 如果来自于大讲堂activity，则退出全屏
				setWindowScreen();
			}
		}
	};

	/** 播放或者暂停事件 */
	private OnSingleClickListener mControlClickListener = new OnSingleClickListener() {

		@Override
		public void onSingleClick(View v) {
			if (mPlayer != null) {
				// 根据MediaPlayer的状态进行切换，如果当前为暂停则继续播放，反之亦然
				if (mPlayer.isPlaying()) {
					v.setBackgroundResource(R.drawable.btn_mediaplay_play);
					mPlayer.pause();
				} else {
					v.setBackgroundResource(R.drawable.btn_mediaplay_pause);
					mPlayer.start();
				}
			}
			mDismissCountdown = DISMISS_COUNTDOWN;
		}
	};

	/** 全屏 */
	private OnSingleClickListener mFullScreenClickListener = new OnSingleClickListener() {

		@Override
		public void onSingleClick(View v) {
			mDismissCountdown = DISMISS_COUNTDOWN;
			setFullScreen();
		}
	};

	/** 进度条滑块 */
	private OnSeekBarChangeListener mProgressChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			if (mPlayer != null) {
				// 拖拽到头
				if (Math.abs(mPlayer.getDuration() - seekBar.getProgress()) < 3000) {
					mPlayer.stop();
					makeMediaReplay();
					if (mVideoEventCallback != null) {
						mVideoEventCallback.onVideoFinish();
					}
					return;
				}

				// 快进，并且将buffer的进度进行复位
				mPlayer.seekTo(seekBar.getProgress());
				mControllerViewHolder.mProgressBar.setSecondaryProgress(seekBar
						.getProgress());
			}
			mDismissCountdown = DISMISS_COUNTDOWN;
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if (fromUser) {
				mDismissCountdown = DISMISS_COUNTDOWN;
			}
		}
	};

	/** 拖动滑块时保持控制器显示 */
	private OnTouchListener mProgressOnTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			mDismissCountdown = DISMISS_COUNTDOWN;
			return false;
		}
	};

	/** 声音条滑块 */
	private OnSeekBarChangeListener mVolumeChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// 设定用户滑动的音量
			((AudioManager) getActivity().getSystemService(
					Context.AUDIO_SERVICE)).setStreamVolume(
					AudioManager.STREAM_MUSIC, progress, 0);
			mDismissCountdown = DISMISS_COUNTDOWN;
		}
	};

	/**
	 * 声音变化广播监听
	 * 
	 * @author Calvin
	 * 
	 */
	private class ControllerStateBroadCastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context mContext, Intent mIntent) {
			if (mIntent.getAction().equals(BROADCAST_VOLUMECHANGED)) {
				mControllerViewHolder.mVolumeBar
						.setProgress(((AudioManager) getActivity()
								.getSystemService(Context.AUDIO_SERVICE))
								.getStreamVolume(AudioManager.STREAM_MUSIC));
			} else if (mIntent.getAction().equals(BROADCAST_CONTROLLER_DISMISS)) {
				mControllerViewHolder.mControllerGroup.performClick();
			} else if (mIntent.getAction().equals(BROADCAST_CONTROLLER_UPDATE)) {
				if (mControllerViewHolder != null && mPlayer != null
						&& mPlayer.isPlaying()
						&& !mControllerViewHolder.mProgressBar.isPressed()) {
					mControllerViewHolder.mProgressBar
							.setProgress((int) mPlayer.getCurrentPosition());
					mControllerViewHolder.mTime
							.setText(DateFormat.format("mm:ss",
									mPlayer.getCurrentPosition())
									+ "/"
									+ DateFormat.format("mm:ss",
											mPlayer.getDuration()));
				}
			}
		}
	}

	/** 单一刷新线程 */
	private static UpdateProgressThread sSingleInstance;

	/**
	 * 负责刷新seekbar progress的thread
	 * 
	 * @author Calvin
	 * 
	 */
	private class UpdateProgressThread extends Thread {

		/** 开关 */
		private boolean mIsContinue;

		@Override
		public void run() {
			// 保持更新线程唯一
			if (sSingleInstance == null) {
				sSingleInstance = this;
			} else {
				return;
			}

			mIsContinue = true;
			while (mIsContinue) {
				try {
					// 用户拖动进度条时不更新
					if (mControllerViewHolder != null && mPlayer != null
							&& mPlayer.isPlaying()
							&& !mControllerViewHolder.mProgressBar.isPressed()) {
						getActivity().sendBroadcast(
								new Intent(BROADCAST_CONTROLLER_UPDATE));
					}

					// 倒计时消失
					if (mControllerViewHolder != null) {
						mDismissCountdown--;
						if (mDismissCountdown == 0) {
							getActivity().sendBroadcast(
									new Intent(BROADCAST_CONTROLLER_DISMISS));
						}
					}

					sleep(1000);
				} catch (Exception e) {
					if (DEBUG) {
						Log.e(TAG, e);
					}
				}
			}
		}

		/**
		 * 停止更新
		 */
		public void stopReport() {
			mIsContinue = false;
			sSingleInstance = null;
		}
	}
}
