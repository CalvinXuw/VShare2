package com.ifeng.vshare.push.model;

import android.content.Context;
import android.content.Intent;

/**
 * 推送消息model行为抽象接口
 * 
 * @author Calvin
 * 
 */
public interface IPushNotification {

	/**
	 * 推送消息类型 {@link #CLIENT_UPDATE},{@link #CLIENT_LAUNCH}, {@link #NEWS},
	 * {@link #TOPIC},{@link #PHOTO},{@link #BOOK},{@link #VIDEO},
	 * {@link #LECTURE},{@link #POINTS},{@link #CLUB},{@link #DISCOUNT}
	 * 
	 * @author Calvin
	 * 
	 */
	public enum PushNotificationType {
		/**
		 * 客户端更新提示
		 */
		CLIENT_UPDATE,
		/**
		 * 客户端建议打开
		 */
		CLIENT_LAUNCH,
		/**
		 * 资讯
		 */
		NEWS,
		/**
		 * 专题
		 */
		TOPIC,
		/**
		 * 图片
		 */
		PHOTO,
		/**
		 * 图书
		 */
		BOOK,
		/**
		 * 视频
		 */
		VIDEO,
		/**
		 * 讲堂
		 */
		LECTURE,
		/**
		 * 积分
		 */
		POINTS,
		/**
		 * 俱乐部
		 */
		CLUB,
		/**
		 * 商盟特惠
		 */
		DISCOUNT
	}

	/**
	 * 获取context
	 * 
	 * @return
	 */
	public Context getContext();

	/**
	 * 获取推送消息类型 {@link PushNotificationType}
	 * 
	 * @return
	 */
	public PushNotificationType getPushNotificationType();

	/**
	 * 获取推送消息id
	 * 
	 * @return
	 */
	public int getId();

	/**
	 * 获取推送标题
	 * 
	 * @return
	 */
	public String getTitle();

	/**
	 * 获取推送描述
	 * 
	 * @return
	 */
	public String getDescribe();

	/**
	 * 获取额外信息
	 * 
	 * @return
	 */
	public String getExtraInfo();

	/**
	 * 获取跳转intent
	 * 
	 * @return
	 */
	public abstract Intent getActionIntent();
}
