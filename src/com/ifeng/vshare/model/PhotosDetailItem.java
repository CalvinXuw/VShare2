package com.ifeng.vshare.model;

import java.util.List;

import com.ifeng.util.net.parser.AbstractIFJSONItem;

/**
 * 图片详情数据item
 * 
 * @author Calvin
 * 
 */
public class PhotosDetailItem extends BaseVShareItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4863802215643800782L;
	/** 图片标题 */
	public String mTitle;
	/** 时间 */
	public long mCreatetime;
	/** html数据 */
	public List<PhotosDetailInnerItem> mPhotoItems;

	/**
	 * 构造
	 */
	public PhotosDetailItem() {
		addMappingRuleField("mTitle", "info/news/title");
		addMappingRuleField("mSource", "info/news/source");
		addMappingRuleField("mCreatetime", "info/news/createtime");
		addMappingRuleArrayField("mPhotoItems", "info/news/message",
				PhotosDetailInnerItem.class);
	}

	/**
	 * 图片详情嵌套解析item
	 * 
	 * @author Calvin
	 * 
	 */
	public static class PhotosDetailInnerItem extends AbstractIFJSONItem {

		/**
	 * 
	 */
		private static final long serialVersionUID = -7834680589286303021L;
		/** 图片信息 */
		public String mInfo;
		/** 图片Url */
		public String mImg;

		/**
		 * 构造
		 */
		public PhotosDetailInnerItem() {
			addMappingRuleField("mInfo", "txt");
			addMappingRuleField("mImg", "img");
		}
	}
}