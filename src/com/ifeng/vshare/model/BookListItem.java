package com.ifeng.vshare.model;

import com.ifeng.util.net.parser.AbstractIFJSONItem;
import com.ifeng.vshare.requestor.BaseVSharePageRequestor.PageItem;

/**
 * 图书接口数据解析处理item
 * 
 * @author Calvin
 * 
 */
public class BookListItem extends PageItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -437977248468066439L;

	@Override
	public void addDataArrayMapping() {
		addMappingRuleArrayField(KEY_ITEMS, "info/news", BookItem.class);
	}

	/**
	 * 嵌套解析的图书item
	 * 
	 * @author Calvin
	 * 
	 */
	public static class BookItem extends AbstractIFJSONItem {

		/**
	 * 
	 */
		private static final long serialVersionUID = -7203293422643196485L;
		/** 图书id */
		public int mId;
		/** 标题 */
		public String mTitle;
		/** 时间 */
		public long mCreatetime;
		/** 作者 */
		public String mAuthor;
		/** 简介 编辑推荐短语 */
		public String mShortDesc;
		/** 简介 描述 */
		public String mDesc;
		/** 图片地址 */
		public String mImg;

		/**
		 * 构造
		 */
		public BookItem() {
			addMappingRuleField("mId", "id");
			addMappingRuleField("mTitle", "title");
			addMappingRuleField("mCreatetime", "createtime");
			addMappingRuleField("mAuthor", "source");
			addMappingRuleField("mShortDesc", "stitle");
			addMappingRuleField("mDesc", "message");
			addMappingRuleField("mImg", "img");
		}
	}
}