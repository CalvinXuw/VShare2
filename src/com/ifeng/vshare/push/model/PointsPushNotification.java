package com.ifeng.vshare.push.model;

import android.content.Intent;

import com.ifeng.vshare.activity.PointsDetailActivity;

/**
 * 积分类推送item
 * 
 * @author Calvin
 * 
 */
public class PointsPushNotification extends AbstractPushNotification {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9112563693133170295L;
	/** 积分商品id */
	public int mId;

	/**
	 * 构造，并完成intent部分数据解析
	 * 
	 * @param pushItemModel
	 */
	public PointsPushNotification(AbstractPushNotification pushItemModel) {
		super(pushItemModel);
		addMappingRuleField("mId", "id");
		parseData(getExtraInfo());
	}

	@Override
	public PushNotificationType getPushNotificationType() {
		return PushNotificationType.POINTS;
	}

	@Override
	public Intent getActionIntent() {
		return PointsDetailActivity.getIntent(getContext(), mId);
	}

}
