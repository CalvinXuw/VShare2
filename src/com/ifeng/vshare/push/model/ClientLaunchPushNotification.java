package com.ifeng.vshare.push.model;

import android.content.Intent;

import com.ifeng.vshare.activity.SplashActivity;

/**
 * 客户端启动推送item
 * 
 * @author Calvin
 * 
 */
public class ClientLaunchPushNotification extends AbstractPushNotification {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7297391822784461812L;

	/**
	 * 构造，并完成intent部分数据解析
	 * 
	 * @param pushItemModel
	 */
	public ClientLaunchPushNotification(AbstractPushNotification pushItemModel) {
		super(pushItemModel);
	}

	@Override
	public PushNotificationType getPushNotificationType() {
		return PushNotificationType.CLIENT_LAUNCH;
	}

	@Override
	public Intent getActionIntent() {
		return new Intent(getContext(), SplashActivity.class);
	}

}
