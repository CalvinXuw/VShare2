package com.ifeng.vshare.model;

import java.util.List;

import com.ifeng.util.net.parser.AbstractIFJSONItem;
import com.ifeng.vshare.requestor.BaseVSharePageRequestor.PageItem;

/**
 * 图片接口数据解析处理item
 * 
 * @author Calvin
 * 
 */
public class PhotosListItem extends PageItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -437977248468066439L;

	@Override
	public void addDataArrayMapping() {
		addMappingRuleArrayField(KEY_ITEMS, "info/news", PhotosItem.class);
	}

	/**
	 * 嵌套解析的图片item
	 * 
	 * @author Calvin
	 * 
	 */
	public static class PhotosItem extends AbstractIFJSONItem {

		/**
	 * 
	 */
		private static final long serialVersionUID = -3704000793289996570L;
		/** 图片id */
		public int mId;
		/** 标题 */
		public String mTitle;
		/** 时间 */
		public long mCreatetime;
		/** 图片数量 */
		public int mCount;
		/** 图片地址 */
		public List<PhotosUrlItem> mImgs;

		/**
		 * 构造
		 */
		public PhotosItem() {
			addMappingRuleField("mId", "id");
			addMappingRuleField("mTitle", "title");
			addMappingRuleField("mCreatetime", "createtime");
			addMappingRuleField("mCount", "imgcount");
			addMappingRuleArrayField("mImgs", "img", PhotosUrlItem.class);
		}
	}

	/**
	 * 嵌套图片item中的url解析item
	 * 
	 * @author Calvin
	 * 
	 */
	public static class PhotosUrlItem extends AbstractIFJSONItem {

		/**
	 * 
	 */
		private static final long serialVersionUID = -723015350842246746L;
		/** 图片url */
		public String mUrl;

		/**
		 * 构造
		 */
		public PhotosUrlItem() {
			addMappingRuleField("mUrl", "img");
		}
	}
}