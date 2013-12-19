package com.ifeng.vshare.push.model;

import android.content.Intent;

import com.ifeng.vshare.activity.LectureDetailActivity;

/**
 * 讲堂类推送item
 * 
 * @author Calvin
 * 
 */
public class LecturePushNotification extends AbstractPushNotification {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1475496135161175295L;
	/** 讲堂id */
	public int mId;

	/**
	 * 构造，并完成intent部分数据解析
	 * 
	 * @param pushItemModel
	 */
	public LecturePushNotification(AbstractPushNotification pushItemModel) {
		super(pushItemModel);
		addMappingRuleField("mId", "id");
		parseData(getExtraInfo());
	}

	@Override
	public PushNotificationType getPushNotificationType() {
		return PushNotificationType.LECTURE;
	}

	@Override
	public Intent getActionIntent() {
		return LectureDetailActivity.getIntent(getContext(), mId);
	}

}
