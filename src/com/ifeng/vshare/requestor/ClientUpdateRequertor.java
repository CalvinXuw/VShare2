package com.ifeng.vshare.requestor;

import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.content.DialogInterface;

import com.ifeng.util.Utility;
import com.ifeng.vshare.R;
import com.ifeng.vshare.config.ClientInfoConfig;
import com.ifeng.vshare.helper.ClientUpdateProcesser;
import com.ifeng.vshare.model.BaseVShareItem;
import com.ifeng.vshare.model.ClientUpdateItem;
import com.ifeng.vshare.ui.DialogManager;

/**
 * 客户端升级检测model
 * 
 * @author Calvin
 * 
 */
public class ClientUpdateRequertor extends BaseVShareRequestor {

	/**
	 * 构造
	 * 
	 * @param context
	 * @param listener
	 */
	public ClientUpdateRequertor(Context context,
			OnModelProcessListener listener) {
		super(context, listener);
		setAutoParseClass(ClientUpdateItem.class);
	}

	/** 客户端更新item */
	private ClientUpdateItem mClientUpdateItem;

	@Override
	@Deprecated
	public void request() {

	}

	/**
	 * 自动检查更新，加以间隔限制
	 */
	public void checkUpdateAuto() {
		if (ClientInfoConfig.getInstance(mContext).shouldCheckUpdate()) {
			super.request();
		}
	}

	/**
	 * 请求检查更新，跨过请求间隔限制
	 */
	public void checkUpdate() {
		super.request();
	}

	/**
	 * 弹出更新对话框
	 */
	public void showUpdateDialog() {
		if (mClientUpdateItem == null) {
			// 无可用更新
			return;
		}

		StringBuffer messageBuffer = new StringBuffer();
		messageBuffer.append(String.format(
				mContext.getString(R.string.update_version_compare),
				mClientUpdateItem.mNewVersion,
				mClientUpdateItem.mCurrentVersion));
		messageBuffer.append("\n");
		messageBuffer.append(mClientUpdateItem.mDesc);

		DialogManager.getInstance().createDialog(
				mContext.getString(R.string.update_title),
				messageBuffer.toString(),
				new DialogManager.DialogStateCallback() {

					@Override
					public void onClick(int which) {
						if (which == DialogInterface.BUTTON_POSITIVE) {
							ClientUpdateProcesser.getInstance(mContext,
									mClientUpdateItem.mUrl,
									mClientUpdateItem.mNewVersion)
									.executeSyncTask();
						}
					}

					@Override
					public void onCancel() {

					}
				}, true, mContext.getString(R.string.update_update),
				mContext.getString(R.string.dialog_cancel));
	}

	@Override
	protected void handleResult(BaseVShareItem item) {
		ClientUpdateItem clientUpdateItem = (ClientUpdateItem) item;

		int currentVersionCode = Utility.getAppVersionCode(mContext);

		// 版本检测
		if (clientUpdateItem.mNewVersionCode > currentVersionCode) {
			mClientUpdateItem = clientUpdateItem;
			mClientUpdateItem.mCurrentVersion = Utility
					.getAppVersionName(mContext);
		}
	}

	/**
	 * 获取更新item，如果没有可用更新，则返回null
	 * 
	 * @return
	 */
	public ClientUpdateItem getClientUpdateItem() {
		return mClientUpdateItem;
	}

	@Override
	protected List<NameValuePair> getRequestHeaders() {
		return null;
	}

	@Override
	protected List<NameValuePair> getRequestParams() {
		return null;
	}

	@Override
	protected String getRequestUrl() {
		return "http://cmv.ifeng.com/Api/getAndroidVersion";
	}

}
