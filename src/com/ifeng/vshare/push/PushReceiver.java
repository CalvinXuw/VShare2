package com.ifeng.vshare.push;

import java.lang.reflect.Constructor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ifeng.BaseApplicaion;
import com.ifeng.ipush.client.Ipush;
import com.ifeng.util.logging.Log;
import com.ifeng.vshare.push.model.AbstractPushNotification;

/**
 * 推送消息receiver
 * 
 * @author Calvin
 * 
 */
public class PushReceiver extends BroadcastReceiver {

	/** tag */
	private final String TAG = getClass().getSimpleName();
	/** debug */
	private static final boolean DEBUG = BaseApplicaion.DEBUG;

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();

		if (Ipush.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
			// 接收到通知,弹出notification
			String id = bundle.getString(Ipush.NOTIFICATION_INFO_MSGID);
			String pushJSONStr = bundle
					.getString(Ipush.NOTIFICATION_INFO_CONTENT);
			AbstractPushNotification notificationModel = new AbstractPushNotification(
					context, id, pushJSONStr);

			try {
				Class<? extends AbstractPushNotification> notificationClass = notificationModel
						.getNotificationClass();
				Constructor<? extends AbstractPushNotification> constructor = notificationClass
						.getConstructor(AbstractPushNotification.class);
				notificationModel = constructor.newInstance(notificationModel);

				PushNotificationHelper.sendNotification(context,
						notificationModel);
			} catch (Exception e) {
				if (DEBUG) {
					Log.e(TAG, e);
				}
			}

		} else if (Ipush.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
			// 接收到消息,客户端内部进行处理
		}
	}
}
