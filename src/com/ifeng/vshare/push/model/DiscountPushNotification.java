package com.ifeng.vshare.push.model;

import android.content.Intent;

import com.ifeng.vshare.activity.DiscountDetailActivity;

/**
 * 商盟特惠类推送item
 * 
 * @author Calvin
 * 
 */
public class DiscountPushNotification extends AbstractPushNotification {

	/**
	 * 
	 */
	private static final long serialVersionUID = -900222763852663744L;
	/** 商盟特惠部id */
	public int mId;
	/** 商盟特惠名称 */
	public String mTitle;

	/**
	 * 构造，并完成intent部分数据解析
	 * 
	 * @param pushItemModel
	 */
	public DiscountPushNotification(AbstractPushNotification pushItemModel) {
		super(pushItemModel);
		addMappingRuleField("mId", "id");
		addMappingRuleField("mTitle", "title");
		parseData(getExtraInfo());
	}

	@Override
	public PushNotificationType getPushNotificationType() {
		return PushNotificationType.DISCOUNT;
	}

	@Override
	public Intent getActionIntent() {
		return DiscountDetailActivity.getIntent(getContext(), mTitle, mId);
	}

}
