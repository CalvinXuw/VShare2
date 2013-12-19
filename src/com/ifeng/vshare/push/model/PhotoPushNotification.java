package com.ifeng.vshare.push.model;

import android.content.Intent;

import com.ifeng.vshare.activity.PhotoDetailActivity;

/**
 * 图片类推送item
 * 
 * @author Calvin
 * 
 */
public class PhotoPushNotification extends AbstractPushNotification {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5709961930959321256L;
	/** 图片id */
	public int mId;
	/** 图片标题 */
	public String mTitle;

	/**
	 * 构造，并完成intent部分数据解析
	 * 
	 * @param pushItemModel
	 */
	public PhotoPushNotification(AbstractPushNotification pushItemModel) {
		super(pushItemModel);
		addMappingRuleField("mId", "id");
		addMappingRuleField("mTitle", "title");
		parseData(getExtraInfo());
	}

	@Override
	public PushNotificationType getPushNotificationType() {
		return PushNotificationType.PHOTO;
	}

	@Override
	public Intent getActionIntent() {
		return PhotoDetailActivity.getIntent(getContext(), mTitle, mId);
	}
}
