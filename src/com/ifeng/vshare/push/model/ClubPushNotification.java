package com.ifeng.vshare.push.model;

import android.content.Intent;

import com.ifeng.vshare.activity.ClubDetailActivity;

/**
 * 俱乐部类推送item
 * 
 * @author Calvin
 * 
 */
public class ClubPushNotification extends AbstractPushNotification {

	/**
	 * 
	 */
	private static final long serialVersionUID = -900222763852663744L;
	/** 俱乐部id */
	public int mId;
	/** 俱乐部名称 */
	public String mTitle;

	/**
	 * 构造，并完成intent部分数据解析
	 * 
	 * @param pushItemModel
	 */
	public ClubPushNotification(AbstractPushNotification pushItemModel) {
		super(pushItemModel);
		addMappingRuleField("mId", "id");
		addMappingRuleField("mTitle", "title");
		parseData(getExtraInfo());
	}

	@Override
	public PushNotificationType getPushNotificationType() {
		return PushNotificationType.CLUB;
	}

	@Override
	public Intent getActionIntent() {
		return ClubDetailActivity.getIntent(getContext(), mTitle, mId);
	}

}
