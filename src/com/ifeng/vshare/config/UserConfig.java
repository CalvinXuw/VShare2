package com.ifeng.vshare.config;

import java.util.Date;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;

import com.ifeng.util.ConfigPreference;
import com.ifeng.util.logging.Log;
import com.ifeng.vshare.R;
import com.ifeng.vshare.ui.DialogManager;
import com.ifeng.vshare.ui.DialogManager.DialogStateCallback;

/**
 * 用户设置相关的Preference，包含对移动网络下的图片及视频加载的配置等。
 * 
 * @author Calvin
 * 
 */
public class UserConfig extends ConfigPreference {

	/** 文件名 */
	private final static String SP_NAME = ".preference.user";
	/** 写入类型 */
	private final static int MODE = Context.MODE_PRIVATE;

	/** key 2g 3g can load image */
	private final static String KEY_ALLOW_2G3G_PICUTRE = "allow_2g3g_picture";
	/** key 2g 3g can watch video */
	private final static String KEY_ALLOW_2G3G_VIDEO = "allow_2g3g_video";

	/** key whether continue to load image when using 2g 3g network environment */
	private final static String KEY_NOT_ASK_2G3G_PICTURE = "not_ask_2g3g_picture";
	/** key whether continue to load video when using 2g 3g network environment */
	private final static String KEY_NOT_ASK_2G3G_VIDEO = "not_ask_2g3g_video";

	/** key when the lasttime ask about loading picture network */
	private final static String KEY_LASTTIME_ASK_2G3G_PICTURE = "lasttime_ask_2g3g_picture";
	/** key when the lasttime ask about loading video network */
	private final static String KEY_LASTTIME_ASK_2G3G_VIDEO = "lastime_ask_2g3g_video";
	/** 60mins,the minimum timeinterval of asking about the 2g3g network things */
	private final static int ASK_2G3G_TIME_INTERVAL_HOUR = 60 * 60 * 1000;
	/** no interval , ask every times */
	private final static int ASK_2G3G_TIME_INTERVAL_NONE = 0;

	/** 静态实例 */
	private static UserConfig sUserConfig;
	/** 等待用户交互的暂挂线程锁，由于图片访问可能在不同fetcher中进行，所以采用静态锁 */
	private static Object mWaitForUserResultLock = new Object();
	/** 等待用户交互的暂挂线标识 */
	private boolean mWaitForUserResult;

	/**
	 * 获取静态实例
	 * 
	 * @param context
	 * @return
	 */
	public static UserConfig getInstance(Context context) {
		if (sUserConfig == null) {
			sUserConfig = new UserConfig(context);
		}
		return sUserConfig;
	}

	/**
	 * 构造，及对首次调用的配置参数初始化
	 * 
	 * @param context
	 */
	private UserConfig(Context context) {
		super(context, SP_NAME, MODE);

		/*
		 * 初始化配置参数，可依据需求进行更改
		 */
		if (!contain(KEY_ALLOW_2G3G_PICUTRE)) {
			putBoolean(KEY_ALLOW_2G3G_PICUTRE, false);
		}
		if (!contain(KEY_ALLOW_2G3G_VIDEO)) {
			putBoolean(KEY_ALLOW_2G3G_VIDEO, false);
		}

		if (!contain(KEY_NOT_ASK_2G3G_PICTURE)) {
			putBoolean(KEY_NOT_ASK_2G3G_PICTURE, false);
		}
		if (!contain(KEY_NOT_ASK_2G3G_VIDEO)) {
			putBoolean(KEY_NOT_ASK_2G3G_VIDEO, false);
		}

	}

	/*
	 * Picture Preference Begin
	 */

	/**
	 * is allow to load image with a 2g or 3g network environment
	 * 
	 * @return
	 */
	public boolean isAllowAccessPictureWithoutWifi(boolean fromRequest) {
		/*
		 * 若从设置中获取则仅返回结果值，若从网络请求中访问则继续走用户交互逻辑
		 */
		if (!fromRequest) {
			return getBoolean(KEY_ALLOW_2G3G_PICUTRE);
		}

		/*
		 * 是否已经设置为不在询问用户
		 */
		if (getBoolean(KEY_NOT_ASK_2G3G_PICTURE)) {
			return getBoolean(KEY_ALLOW_2G3G_PICUTRE);
		}

		/*
		 * 是否仍处于刚刚询问过的状态
		 */
		if (new Date(System.currentTimeMillis()).before(new Date(
				getLong(KEY_LASTTIME_ASK_2G3G_PICTURE)
						+ ASK_2G3G_TIME_INTERVAL_HOUR))) {
			return getBoolean(KEY_ALLOW_2G3G_PICUTRE);
		}

		synchronized (mWaitForUserResultLock) {
			/*
			 * 若此前有其它请求加载线程介入，则将此次线程操作挂起
			 */
			if (mWaitForUserResult) {
				try {
					mWaitForUserResultLock.wait();
				} catch (InterruptedException e) {
					if (DEBUG) {
						Log.e(TAG, e);
					}
				}
			} else {
				/*
				 * 等待用户对弹出的Dialog进行操作
				 */
				DialogManager
						.getInstance()
						.createDialog(
								R.string.dialog_title,
								R.string.allow_2g3g_picture_message,
								new DialogStateCallback() {

									@Override
									public void onClick(int which) {
										if (which == DialogInterface.BUTTON_POSITIVE) {
											setAllowAccessPircureWithoutWifiAuto(true);
										} else if (which == DialogInterface.BUTTON_NEGATIVE) {
											setAllowAccessPircureWithoutWifiAuto(false);
										}

										mWaitForUserResult = false;
										synchronized (mWaitForUserResultLock) {
											mWaitForUserResultLock.notifyAll();
										}
									}

									@Override
									public void onCancel() {
										mWaitForUserResult = false;
										synchronized (mWaitForUserResultLock) {
											mWaitForUserResultLock.notifyAll();
										}
									}
								}, true, R.string.dialog_yes,
								R.string.dialog_no).setCancelable(false);

				try {
					/*
					 * 挂起此次请求，等待用户操作。
					 */
					mWaitForUserResult = true;
					mWaitForUserResultLock.wait();
				} catch (InterruptedException e) {
					if (DEBUG) {
						Log.e(TAG, e);
					}
				}

			}
		}
		return getBoolean(KEY_ALLOW_2G3G_PICUTRE);
	}

	/**
	 * set is allow to load image with a 2g or 3g network environment
	 * 
	 * @param enable
	 * @return
	 */
	public boolean setAllowAccessPictureWithoutWifi(boolean enable) {
		/*
		 * 若用户手动从设置中对此进行更新，则在允许加载时改变 KEY_NOT_ASK_2G3G_PICTURE 的值
		 */
		if (enable) {
			putBoolean(KEY_NOT_ASK_2G3G_PICTURE, true);
		}
		return setAllowAccessPircureWithoutWifiAuto(enable);
	}

	/**
	 * 内部自动设置，不直接触发{@link #KEY_NOT_ASK_2G3G_PICTURE}，仅更新
	 * {@link #KEY_LASTTIME_ASK_2G3G_PICTURE}和{@link #KEY_ALLOW_2G3G_PICUTRE}
	 * 
	 * @param enable
	 * @return
	 */
	private boolean setAllowAccessPircureWithoutWifiAuto(boolean enable) {
		long currentTime = System.currentTimeMillis();
		putLong(KEY_LASTTIME_ASK_2G3G_PICTURE, currentTime);
		return putBoolean(KEY_ALLOW_2G3G_PICUTRE, enable);
	}

	/*
	 * Picture Preference End
	 */

	/*
	 * Video Preference Begin
	 */

	/**
	 * is allow to watch video with a 2g or 3g network environment. use it just
	 * from setting
	 * 
	 * @return
	 */
	public boolean isAllowAccessVideoWithoutWifi() {
		return getBoolean(KEY_ALLOW_2G3G_VIDEO);
	}

	/**
	 * is allow to watch video with a 2g or 3g network environment. use it from
	 * ui thread, the result will be return after the user has made a choice
	 * 
	 * @return
	 */
	public void isAllowAccessVideoWithoutWifi(
			final AllowAccessVideoCallback callback) {
		/*
		 * 是否已经设置为不在询问用户
		 */
		if (getBoolean(KEY_NOT_ASK_2G3G_VIDEO)) {
			callback.isAllow(getBoolean(KEY_ALLOW_2G3G_VIDEO));
			return;
		}

		/*
		 * 是否仍处于刚刚询问过的状态，当前设置为每次都询问
		 */
		if (new Date(System.currentTimeMillis()).before(new Date(
				getLong(KEY_LASTTIME_ASK_2G3G_VIDEO)
						+ ASK_2G3G_TIME_INTERVAL_NONE))) {
			callback.isAllow(getBoolean(KEY_ALLOW_2G3G_VIDEO));
			return;
		}

		/*
		 * 等待用户对弹出的Dialog进行操作
		 */
		DialogManager
				.getInstance()
				.createDialog(R.string.dialog_title,
						R.string.allow_2g3g_video_message,
						new DialogStateCallback() {

							@Override
							public void onClick(int which) {
								if (which == DialogInterface.BUTTON_POSITIVE) {
									setAllowAccessVideoWithoutWifiAuto(true);
								} else if (which == DialogInterface.BUTTON_NEGATIVE) {
									setAllowAccessVideoWithoutWifiAuto(false);
								}
								postResult(getBoolean(KEY_ALLOW_2G3G_VIDEO));
							}

							@Override
							public void onCancel() {
								postResult(getBoolean(KEY_ALLOW_2G3G_VIDEO));
							}

							/**
							 * 在ui线程中通知回调
							 * 
							 * @param result
							 */
							private void postResult(final boolean result) {
								new Handler(Looper.getMainLooper())
										.post(new Runnable() {

											@Override
											public void run() {
												callback.isAllow(result);
											}
										});
							}
						}, true, R.string.dialog_yes, R.string.dialog_no)
				.setCancelable(false);

	}

	/**
	 * set is allow to watch video with a 2g or 3g network environment
	 * 
	 * @param enable
	 * @return
	 */
	public boolean setAllowAccessVideoWithoutWifi(boolean enable) {
		/*
		 * 若用户手动从设置中对此进行更新，则在允许加载时改变 KEY_NOT_ASK_2G3G_VIDEO 的值
		 */
		if (enable) {
			putBoolean(KEY_NOT_ASK_2G3G_VIDEO, true);
		}
		return setAllowAccessVideoWithoutWifiAuto(enable);
	}

	/**
	 * 内部自动设置，不直接触发{@link #KEY_NOT_ASK_2G3G_VIDEO}，仅更新
	 * {@link #KEY_LASTTIME_ASK_2G3G_VIDEO}和{@link #KEY_ALLOW_2G3G_VIDEO}
	 * 
	 * @param enable
	 * @return
	 */
	private boolean setAllowAccessVideoWithoutWifiAuto(boolean enable) {
		long currentTime = System.currentTimeMillis();
		putLong(KEY_LASTTIME_ASK_2G3G_VIDEO, currentTime);
		return putBoolean(KEY_ALLOW_2G3G_VIDEO, enable);
	}

	/**
	 * 是否允许用户继续访问加载视频资源的监听类，由于大部分对视频访问的方法都处于UI线程，故为了不阻塞UI线程采取回调的方式将结果予以反馈。
	 * 
	 * @author Calvin
	 * 
	 */
	public interface AllowAccessVideoCallback {
		/**
		 * 是否允许用户继续访问加载视频资源
		 * 
		 * @param isAllow
		 */
		public void isAllow(boolean isAllow);
	}

	/*
	 * Video Preference End
	 */
}
