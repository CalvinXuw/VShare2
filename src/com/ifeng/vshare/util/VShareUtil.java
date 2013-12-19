package com.ifeng.vshare.util;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.ifeng.BaseApplicaion;
import com.ifeng.util.logging.Log;
import com.ifeng.vshare.R;
import com.ifeng.vshare.ui.DialogManager;
import com.ifeng.vshare.ui.DialogManager.DialogStateCallback;

/**
 * 移动v享客户端工具类
 * 
 * @author Calvin
 * 
 */
public class VShareUtil {

	/** tag */
	private static final String TAG = VShareUtil.class.getSimpleName();
	/** debug开关 */
	private static boolean DEBUG = BaseApplicaion.DEBUG;

	/** 10086电话 */
	public static final String NUMBER_10086 = "10086";

	/**
	 * 直接拨打电话
	 * 
	 * @param context
	 * @param number
	 */
	public static void makeDial(Context context, String number) {
		try {
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ number));
			context.startActivity(intent);
		} catch (Exception e) {
			if (DEBUG) {
				Log.w(TAG, e);
			}
		}
	}

	/**
	 * 创建对话框的形式拨打电话
	 * 
	 * @param context
	 * @param message
	 * @param number
	 */
	public static void makeDial(final Context context, String message,
			final String number) {
		DialogManager.getInstance().createDialog(
				context.getString(R.string.dialog_title),
				context.getString(R.string.lobby_dial),
				new DialogStateCallback() {

					@Override
					public void onClick(int which) {
						if (which == Dialog.BUTTON_POSITIVE) {
							makeDial(context, number);
						}
					}

					@Override
					public void onCancel() {
					}
				}, true, context.getString(R.string.dialog_confirm),
				context.getString(R.string.dialog_cancel));
	}
}
