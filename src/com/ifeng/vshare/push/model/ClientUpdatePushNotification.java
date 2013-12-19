package com.ifeng.vshare.push.model;

import android.content.Intent;
import android.net.Uri;

/**
 * 客户端更新推送item
 * 
 * @author Calvin
 * 
 */
public class ClientUpdatePushNotification extends AbstractPushNotification {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1475496135161175295L;
	/** url */
	public String mUrl;

	/**
	 * 构造，并完成intent部分数据解析
	 * 
	 * @param pushItemModel
	 */
	public ClientUpdatePushNotification(AbstractPushNotification pushItemModel) {
		super(pushItemModel);
		addMappingRuleField("mUrl", "url");
		parseData(getExtraInfo());
	}

	@Override
	public PushNotificationType getPushNotificationType() {
		return PushNotificationType.CLIENT_UPDATE;
	}

	@Override
	public Intent getActionIntent() {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrl));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		return intent;
	}

}
