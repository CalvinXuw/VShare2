package com.ifeng.vshare.model;

import com.ifeng.util.net.parser.AbstractIFJSONItem;
import com.ifeng.vshare.requestor.BaseVSharePageRequestor.PageItem;

/**
 * 视频接口数据解析处理item
 * 
 * @author Calvin
 * 
 */
public class PointsListItem extends PageItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -510826152595548540L;

	/**
	 * 构造
	 */
	public PointsListItem() {
	}

	@Override
	public void addDataArrayMapping() {
		addMappingRuleArrayField(KEY_ITEMS, "info/news", PointsItem.class);
	}

	/**
	 * 嵌套解析的商品item
	 * 
	 * @author Calvin
	 * 
	 */
	public static class PointsItem extends AbstractIFJSONItem {

		/**
	 * 
	 */
		private static final long serialVersionUID = 4316884915422476345L;
		/** 商品id */
		public int mId;
		/** 标题 */
		public String mTitle;
		/** 图片地址 */
		public String mImg;
		/** 全球通所需积分 */
		public String mGotonePoints;
		/** 动感地带所需积分 */
		public String mMzonePoints;
		/** 神州行所需积分 */
		public String mEasyownPoints;
		/** 物品编码 */
		public String mProductId;

		/**
		 * 构造
		 */
		public PointsItem() {
			addMappingRuleField("mId", "shopid");
			addMappingRuleField("mTitle", "shopname");
			addMappingRuleField("mImg", "shopimg");
			addMappingRuleField("mGotonePoints", "shopqqt");
			addMappingRuleField("mMzonePoints", "shopdgdd");
			addMappingRuleField("mEasyownPoints", "shopszx");
			addMappingRuleField("mProductId", "shopsort");
		}
	}
}