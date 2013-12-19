package com.ifeng.vshare.model;

import com.ifeng.util.net.parser.AbstractIFJSONItem;
import com.ifeng.vshare.requestor.BaseVSharePageRequestor.PageItem;

/**
 * 专属服务接口数据解析处理item
 * 
 * @author Calvin
 * 
 */
public class ServiceListItem extends PageItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 776866687925491929L;

	public ServiceListItem() {
	}

	@Override
	public void addDataArrayMapping() {
		addMappingRuleArrayField(KEY_ITEMS, "info/service", ServiceItem.class);
	}

	/**
	 * 嵌套解析的专属服务item
	 * 
	 * @author Calvin
	 * 
	 */
	public static class ServiceItem extends AbstractIFJSONItem {

		/**
	 * 
	 */
		private static final long serialVersionUID = 5161385147114312135L;
		/** 服务id */
		public int mId;
		/** 名称 */
		public String mName;
		/** 描述 */
		public String mInfo;
		/** 钻石 */
		public int mDiamond;
		/** 金卡 */
		public int mGolden;
		/** 银卡 */
		public int mSilver;

		/**
		 * 构造
		 */
		public ServiceItem() {
			addMappingRuleField("mId", "sid");
			addMappingRuleField("mName", "sname");
			addMappingRuleField("mInfo", "sinfo");
			addMappingRuleField("mDiamond", "sdiamond");
			addMappingRuleField("mGolden", "sgold");
			addMappingRuleField("mSilver", "ssilver");
		}
	}
}