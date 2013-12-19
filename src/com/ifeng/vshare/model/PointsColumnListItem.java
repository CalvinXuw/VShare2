package com.ifeng.vshare.model;

import java.util.List;

import com.ifeng.util.net.parser.AbstractIFJSONItem;

/**
 * 积分商城兑换商品栏目列表item
 * 
 * @author Calvin
 * 
 */
public class PointsColumnListItem extends BaseVShareItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1587846250826040218L;

	/** 栏目列表 */
	public List<PointsColumnItem> mColumnList;

	/**
	 * 构造
	 */
	public PointsColumnListItem() {
		addMappingRuleArrayField("mColumnList", "info", PointsColumnItem.class);
	}

	/**
	 * 积分商品详情数据item
	 * 
	 * @author Calvin
	 * 
	 */
	public static class PointsColumnItem extends AbstractIFJSONItem {

		/**
	 * 
	 */
		private static final long serialVersionUID = 456130606921448927L;
		/** cid */
		public int mCid;
		/** 栏目名 */
		public String mColumnName;

		/**
		 * 构造
		 */
		public PointsColumnItem() {
			addMappingRuleField("mCid", "catid");
			addMappingRuleField("mColumnName", "catname");
		}
	}
}