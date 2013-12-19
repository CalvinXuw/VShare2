package com.ifeng.vshare.push.model;

import android.content.Intent;

import com.ifeng.vshare.activity.NewsDetailActivity;

/**
 * 资讯类推送item
 * 
 * @author Calvin
 * 
 */
public class NewsPushNotification extends AbstractPushNotification {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2570896797666596524L;
	/** 资讯id */
	public int mId;
	/** 评论标题 */
	public String mCommentTitle;
	/** 评论标识 */
	public String mCommentTag;

	/**
	 * 构造，并完成intent部分数据解析
	 * 
	 * @param pushItemModel
	 */
	public NewsPushNotification(AbstractPushNotification pushItemModel) {
		super(pushItemModel);
		addMappingRuleField("mId", "id");
		addMappingRuleField("mCommentTitle", "title");
		addMappingRuleField("mCommentTag", "comment");
		parseData(getExtraInfo());
	}

	@Override
	public PushNotificationType getPushNotificationType() {
		return PushNotificationType.NEWS;
	}

	@Override
	public Intent getActionIntent() {
		return NewsDetailActivity.getIntent(getContext(), mId, mCommentTitle,
				mCommentTag);
	}

}
