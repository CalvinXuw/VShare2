package com.ifeng.vshare.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.widget.Toast;

import com.ifeng.util.AppUtils;
import com.ifeng.util.download.DownloadInfo;
import com.ifeng.util.download.DownloadManager;
import com.ifeng.util.download.Downloads;
import com.ifeng.util.model.AbstractModel;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareActivity;

/**
 * 客户端更新下载处理类
 * 
 * @author Calvin
 * 
 */
public class ClientUpdateProcesser extends AbstractModel {

	/** 默认初始id */
	private static final int INVALID_ID = -1;

	/** 客户端更新下载处理类 */
	private static ClientUpdateProcesser sClientUpdateProcesser;

	/**
	 * 获取实例
	 * 
	 * @param context
	 * @return
	 */
	public static ClientUpdateProcesser getInstance(Context context,
			String downloadurl, String version) {
		if (sClientUpdateProcesser == null) {
			synchronized (ClientUpdateProcesser.class) {
				sClientUpdateProcesser = new ClientUpdateProcesser(
						context.getApplicationContext(), downloadurl, version);
			}
		}
		return sClientUpdateProcesser;
	}

	/** 下载服务管理类 */
	private DownloadManager mDownloadManager;
	/** 下载进度状态变化监听 */
	private DownloadsObserver mDownloadsObserver;
	/** 客户端更新下载任务的id */
	private long mDownloadTaskId = INVALID_ID;
	/** 客户端下载地址 */
	private String mDownloadUrl;
	/** 客户端新版本 */
	private String mDownloadVersion;
	/** 下载进度显示 */
	private ProgressDialog mDownloadDialog;

	/**
	 * 私有化构造方法
	 * 
	 * @param context
	 */
	private ClientUpdateProcesser(Context context, String downloadurl,
			String version) {
		super(context, new OnModelProcessListener() {

			@Override
			public void onSuccess(AbstractModel model) {

			}

			@Override
			public void onFailed(AbstractModel model, int errorCode) {

			}

			@Override
			public void onProgress(AbstractModel model, int progress) {

			}
		});

		mDownloadUrl = downloadurl;
		mDownloadVersion = version;
	}

	@Override
	protected void init() {
		mDownloadManager = new DownloadManager(mContext.getContentResolver(),
				mContext.getPackageName());
		mDownloadsObserver = new DownloadsObserver();

		// 注册observer
		mContext.getContentResolver().registerContentObserver(
				Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI, true,
				mDownloadsObserver);
	}

	@Override
	protected void process() {
		initDownloadDialog();
		if (mDownloadTaskId == INVALID_ID) {
			mDownloadTaskId = mDownloadManager.enqueueAppDownloadTask(
					mDownloadUrl, mContext.getString(R.string.app_name) + "_"
							+ mDownloadVersion, mDownloadVersion, false);
		}
	}

	/**
	 * 初始化下载进度更新Dialog
	 */
	private void initDownloadDialog() {
		mDownloadDialog = null;
		if (VShareActivity.getTopActivity() != null) {
			mDownloadDialog = new ProgressDialog(VShareActivity.getTopActivity());
			mDownloadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mDownloadDialog.setTitle(R.string.update_dialog_title);
			mDownloadDialog.setMessage(String.format(
					mContext.getString(R.string.update_dialog_message), 0)
					+ "%...");
			mDownloadDialog.show();
		}
	}

	@Override
	public void cancel() {
		mContext.getContentResolver().unregisterContentObserver(
				mDownloadsObserver);
		super.cancel();
	}

	/**
	 * 监听downloaddatabase，获取下载进度
	 * 
	 * @author Calvin
	 * 
	 */
	private class DownloadsObserver extends ContentObserver {

		public DownloadsObserver() {
			super(new Handler(mContext.getMainLooper()));
		}

		@Override
		public void onChange(boolean selfChange) {
			if (mDownloadTaskId != INVALID_ID) {
				updateProgress();
			}
		}

		/**
		 * 更新下载进度
		 */
		private void updateProgress() {
			DownloadInfo downloadInfo = mDownloadManager.getDownloadById(
					mContext, mDownloadTaskId);
			if (downloadInfo == null) {
				return;
			}

			int progress = (int) (downloadInfo.mCurrentBytes
					/ (float) downloadInfo.mTotalBytes * 100);
			if (mDownloadDialog != null && mDownloadDialog.isShowing()) {
				mDownloadDialog.setMessage(String.format(
						mContext.getString(R.string.update_dialog_message),
						progress) + "%...");
			}

			if (Downloads.isStatusSuccess(downloadInfo.mStatus)) {
				// 如果下载完成，进行安装
				AppUtils.installApk(mContext, downloadInfo.mFileName);
				mDownloadTaskId = INVALID_ID;
				mDownloadDialog.dismiss();
			} else if (Downloads.isStatusError(downloadInfo.mStatus)) {
				// 如果下载失败，进行提示
				mDownloadTaskId = INVALID_ID;
				mDownloadDialog.dismiss();
				Toast.makeText(mContext, R.string.update_failed,
						Toast.LENGTH_SHORT).show();
			}
		}
	}
}
