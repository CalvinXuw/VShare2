package com.ifeng.vshare.model;

import java.util.List;

import com.ifeng.util.net.parser.AbstractIFJSONItem;

/**
 * 俱乐部详情数据item
 * 
 * @author Calvin
 * 
 */
public class ClubDetailItem extends BaseVShareItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8629557479872140402L;
	/** 俱乐部名称 */
	public String mClubName;
	/** 宣传大图图片 */
	public String mImg;
	/** 活动时间 */
	public String mClubTime;
	/** 俱乐部地址 */
	public String mClubAddr;
	/** 俱乐部介绍 */
	public String mClubIntroduce;
	/** 服务对象 */
	public String mServiceTarget;
	/** 参与方式 */
	public String mParticipateWay;
	/** 活动图片 */
	public List<ClubPhotoItem> mPhotos;

	/**
	 * 构造
	 */
	public ClubDetailItem() {
		addMappingRuleField("mClubName", "info/clubname");
		addMappingRuleField("mImg", "info/clubimg");
		addMappingRuleField("mClubTime", "info/clubtime");
		addMappingRuleField("mClubAddr", "info/clubaddr");
		addMappingRuleField("mClubIntroduce", "info/clubmessage");
		addMappingRuleField("mServiceTarget", "info/clubinfo");
		addMappingRuleField("mParticipateWay", "info/clubparticipate");
		addMappingRuleArrayField("mPhotos", "info/clubmoreimg",
				ClubPhotoItem.class);
	}

	/**
	 * 嵌套解析俱乐部活动图片item
	 * 
	 * @author Calvin
	 * 
	 */
	public static class ClubPhotoItem extends AbstractIFJSONItem {

		/**
	 * 
	 */
		private static final long serialVersionUID = -3015370010103433031L;
		/** 图片url */
		public String mUrl;

		/**
		 * 构造
		 */
		public ClubPhotoItem() {
			addMappingRuleField("mUrl", "img");
		}
	}
}