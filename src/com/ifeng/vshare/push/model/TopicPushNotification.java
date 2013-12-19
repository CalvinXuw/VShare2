package com.ifeng.vshare.push.model;

import android.content.Intent;

import com.ifeng.vshare.activity.NewsTopicActivity;

/**
 * 专题类推送item
 * 
 * @author Calvin
 * 
 */
public class TopicPushNotification extends AbstractPushNotification {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6755883695287106923L;
	/** 专题id */
	public int mId;

	/**
	 * 构造，并完成intent部分数据解析
	 * 
	 * @param pushItemModel
	 */
	public TopicPushNotification(AbstractPushNotification pushItemModel) {
		super(pushItemModel);
		addMappingRuleField("mId", "id");
		parseData(getExtraInfo());
	}

	@Override
	public PushNotificationType getPushNotificationType() {
		return PushNotificationType.TOPIC;
	}

	@Override
	public Intent getActionIntent() {
		return NewsTopicActivity.getIntent(getContext(), mId);
	}

}
