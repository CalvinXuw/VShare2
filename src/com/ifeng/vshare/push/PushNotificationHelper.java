package com.ifeng.vshare.push;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;

import com.ifeng.util.download.VersionedNotification;
import com.ifeng.vshare.R;
import com.ifeng.vshare.push.model.AbstractPushNotification;

/**
 * 推送通知工具类
 * 
 * @author qianzy
 * @time 2013-6-24 13:32:39
 */
public class PushNotificationHelper {

	/**
	 * 发送通知
	 * 
	 * @param notificationModel
	 */
	public static void sendNotification(Context context,
			AbstractPushNotification notificationModel) {
		int id = (int) System.currentTimeMillis();
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		VersionedNotification notification = VersionedNotification
				.getInstance(context);
		notification.setContentTitle(notificationModel.getTitle());
		notification.setContentText(notificationModel.getDescribe());

		Intent intent = notificationModel.getActionIntent();
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		notification.setContentIntent(PendingIntent.getActivity(context, id,
				intent, PendingIntent.FLAG_UPDATE_CURRENT));
		notification.setSmallIcon(R.drawable.logo_notification);
		notification.setAutoCancel(true);

		notification.setTicker(notificationModel.getTitle());
		notification.setVibrate(new long[] { 350, 250, 350, 250 });
		notification.setSound(RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

		nm.notify(id, notification.getNotification());
	}

}
