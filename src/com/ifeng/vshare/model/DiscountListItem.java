package com.ifeng.vshare.model;

import com.ifeng.util.net.parser.AbstractIFJSONItem;
import com.ifeng.vshare.requestor.BaseVSharePageRequestor.PageItem;

/**
 * 商盟特惠接口数据解析处理item
 * 
 * @author Calvin
 * 
 */
public class DiscountListItem extends PageItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7079322215364437691L;

	public DiscountListItem() {
	}

	@Override
	public void addDataArrayMapping() {
		addMappingRuleArrayField(KEY_ITEMS, "info", DiscountItem.class);
	}

	/**
	 * 嵌套解析的商盟特惠item
	 * 
	 * @author Calvin
	 * 
	 */
	public static class DiscountItem extends AbstractIFJSONItem {

		/**
	 * 
	 */
		private static final long serialVersionUID = -1453126312637333990L;
		/** 特惠id */
		public int mId;
		/** 名称 */
		public String mName;
		/** 描述 */
		public String mInfo;
		/** 电话 */
		public String mTel;
		/** 距离，米为单位 */
		public double mDistance;

		/**
		 * 构造
		 */
		public DiscountItem() {
			addMappingRuleField("mId", "prid");
			addMappingRuleField("mName", "prname");
			addMappingRuleField("mInfo", "praddr");
			addMappingRuleField("mTel", "prtelephone");
			addMappingRuleField("mDistance", "distance");
		}
	}
}
