package com.ifeng.vshare.model;

import com.ifeng.util.net.parser.AbstractIFJSONItem;
import com.ifeng.vshare.requestor.BaseVSharePageRequestor.PageItem;

/**
 * 俱乐部接口数据解析处理item
 * 
 * @author Calvin
 * 
 */
public class ClubListItem extends PageItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1398670760680151970L;

	public ClubListItem() {
	}

	@Override
	public void addDataArrayMapping() {
		addMappingRuleArrayField(KEY_ITEMS, "info/local", ClubItem.class);
	}

	/**
	 * 嵌套解析的俱乐部item
	 * 
	 * @author Calvin
	 * 
	 */
	public static class ClubItem extends AbstractIFJSONItem {

		/**
	 * 
	 */
		private static final long serialVersionUID = 8480895797862841028L;
		/** 俱乐部id */
		public int mId;
		/** 名称 */
		public String mName;
		/** image url */
		public String mImg;

		/**
		 * 构造
		 */
		public ClubItem() {
			addMappingRuleField("mId", "clubid");
			addMappingRuleField("mName", "clubname");
			addMappingRuleField("mImg", "clubimg");
		}
	}
}
