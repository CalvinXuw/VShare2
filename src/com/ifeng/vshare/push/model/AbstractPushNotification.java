package com.ifeng.vshare.push.model;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;

import com.ifeng.util.net.parser.AbstractIFJSONItem;

/**
 * 推送通知model基类,Notification为需要弹出系统通知的推送
 * 
 * @author Calvin
 * 
 */
public class AbstractPushNotification extends AbstractIFJSONItem implements
		IPushNotification {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1289303225905129589L;

	/** 客户端升级 */
	private static final int CLIENT_UPDATE = 0;
	/** 客户端建议启动 */
	private static final int CLIENT_LAUNCH = 1;
	/** 资讯 */
	private static final int NEWS = 2;
	/** 专题 */
	private static final int TOPIC = 3;
	/** 图片 */
	private static final int PHOTO = 4;
	/** 书籍 */
	private static final int BOOK = 5;
	/** 视频 */
	private static final int VIDEO = 6;
	/** 讲堂 */
	private static final int LECTURE = 7;
	/** 积分 */
	private static final int POINTS = 8;
	/** 俱乐部 */
	private static final int CLUB = 9;
	/** 商盟特惠 */
	private static final int DISCOUNT = 10;

	/** 源对象 */
	private AbstractPushNotification mPushItemModel;
	/** context */
	public Context mContext;

	/** 消息id */
	public String mId;
	/** 标题 */
	public String mTitle;
	/** 描述文字 */
	public String mDescribe;
	/** 推送消息类型 */
	public int mPushStyleId;
	/** extra info */
	public String mExtraInfo;
	/** 接收notification的跳转intent */
	public Intent mActionIntent;

	/**
	 * 构造生成model
	 * 
	 * @param pushJsonStr
	 */
	public AbstractPushNotification(Context context, String id, String pushJsonStr) {
		mContext = context;
		mId = id;
		addMappingRuleField("mTitle", "title");
		addMappingRuleField("mDescribe", "content");
		addMappingRuleField("mExtraInfo", "extra");
		addMappingRuleField("mPushStyleId", "styleId");
		parseData(pushJsonStr);
		mPushItemModel = this;
	}

	/**
	 * 构造生成model
	 * 
	 * @param pushJsonStr
	 */
	public AbstractPushNotification(AbstractPushNotification pushItemModel) {
		mPushItemModel = pushItemModel;
	}

	@Override
	public Context getContext() {
		return mPushItemModel.mContext;
	}

	@Override
	public int getId() {
		try {
			return Integer.parseInt(mPushItemModel.mId);
		} catch (Exception e) {
		}
		return (int) System.currentTimeMillis();
	}

	@Override
	public String getTitle() {
		return mPushItemModel.mTitle;
	}

	@Override
	public String getDescribe() {
		return mPushItemModel.mDescribe;
	}

	@Override
	public String getExtraInfo() {
		return mPushItemModel.mExtraInfo;
	}

	@Override
	public PushNotificationType getPushNotificationType() {
		return mPushItemModel.getPushNotificationType();
	}

	@Override
	public Intent getActionIntent() {
		return mPushItemModel.getActionIntent();
	}

	/**
	 * 转换类型
	 * 
	 * @param styleId
	 * @return
	 */
	private PushNotificationType convertPushItemType(int styleId) {
		switch (styleId) {
		case CLIENT_UPDATE:
			return PushNotificationType.CLIENT_UPDATE;
		case CLIENT_LAUNCH:
			return PushNotificationType.CLIENT_LAUNCH;
		case NEWS:
			return PushNotificationType.NEWS;
		case TOPIC:
			return PushNotificationType.TOPIC;
		case PHOTO:
			return PushNotificationType.PHOTO;
		case BOOK:
			return PushNotificationType.BOOK;
		case VIDEO:
			return PushNotificationType.VIDEO;
		case LECTURE:
			return PushNotificationType.LECTURE;
		case POINTS:
			return PushNotificationType.POINTS;
		case CLUB:
			return PushNotificationType.CLUB;
		case DISCOUNT:
			return PushNotificationType.DISCOUNT;
		}
		return null;
	}

	/** 通知类型/class映射表 */
	private static HashMap<PushNotificationType, Class<? extends AbstractPushNotification>> sNotificationMapping;
	static {
		sNotificationMapping = new HashMap<PushNotificationType, Class<? extends AbstractPushNotification>>();

		sNotificationMapping.put(PushNotificationType.CLIENT_UPDATE,
				ClientUpdatePushNotification.class);
		sNotificationMapping.put(PushNotificationType.CLIENT_LAUNCH,
				ClientLaunchPushNotification.class);
		sNotificationMapping.put(PushNotificationType.NEWS,
				NewsPushNotification.class);
		sNotificationMapping.put(PushNotificationType.TOPIC,
				TopicPushNotification.class);
		sNotificationMapping.put(PushNotificationType.PHOTO,
				PhotoPushNotification.class);
		sNotificationMapping.put(PushNotificationType.BOOK,
				BookPushNotification.class);
		sNotificationMapping.put(PushNotificationType.VIDEO,
				VideoPushNotification.class);
		sNotificationMapping.put(PushNotificationType.LECTURE,
				LecturePushNotification.class);
		sNotificationMapping.put(PushNotificationType.POINTS,
				PointsPushNotification.class);
		sNotificationMapping.put(PushNotificationType.CLUB,
				ClubPushNotification.class);
		sNotificationMapping.put(PushNotificationType.DISCOUNT,
				DiscountPushNotification.class);
	}

	/**
	 * 获取styleId对应的Class类型
	 * 
	 * @return
	 */
	public Class<? extends AbstractPushNotification> getNotificationClass() {
		PushNotificationType notificationType = convertPushItemType(mPushStyleId);
		return sNotificationMapping.get(notificationType);
	}
}
