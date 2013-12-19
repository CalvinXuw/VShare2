package com.ifeng.vshare.push.model;

import android.content.Intent;

import com.ifeng.vshare.activity.VideoPlayActivity;

/**
 * 视频类推送item
 * 
 * @author Calvin
 * 
 */
public class VideoPushNotification extends AbstractPushNotification {

	/**
	 * 
	 */
	private static final long serialVersionUID = -729733246367217200L;
	/** 视频id */
	public int mId;
	/** 标题 */
	public String mTitle;

	/**
	 * 构造，并完成intent部分数据解析
	 * 
	 * @param pushItemModel
	 */
	public VideoPushNotification(AbstractPushNotification pushItemModel) {
		super(pushItemModel);
		addMappingRuleField("mId", "id");
		addMappingRuleField("mTitle", "title");
		parseData(getExtraInfo());
	}

	@Override
	public PushNotificationType getPushNotificationType() {
		return PushNotificationType.VIDEO;
	}

	@Override
	public Intent getActionIntent() {
		return VideoPlayActivity.getIntent(getContext(), mTitle, mId);
	}

}
