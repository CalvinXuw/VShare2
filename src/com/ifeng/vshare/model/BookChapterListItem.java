package com.ifeng.vshare.model;

import java.util.List;

import com.ifeng.util.net.parser.AbstractIFXMLItem;

/**
 * 图书章节列表解析
 * 
 * @author Calvin
 * 
 */
public class BookChapterListItem extends AbstractIFXMLItem {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5399878578468993538L;
	/** 单章item */
	public List<RemoteChapterItem> mChapterItems;

	/**
	 * 构造
	 */
	public BookChapterListItem() {
		addMappingRuleArrayField("mChapterItems", "#", RemoteChapterItem.class);
	}

	/**
	 * 获取章节列表
	 * 
	 * @return
	 */
	public List<RemoteChapterItem> getChapters() {
		return mChapterItems;
	}

	/**
	 * 单章item解析
	 * 
	 * @author Calvin
	 * 
	 */
	public static class RemoteChapterItem extends AbstractIFXMLItem {

		/**
	 * 
	 */
		private static final long serialVersionUID = -985778511045176982L;

		/** 章节名 */
		public String mChapterName;
		/** 章节id */
		public String mChapterNum;

		/**
		 * 构造
		 */
		public RemoteChapterItem() {
			addMappingRuleField("mChapterName", "chapterName/#");
			addMappingRuleField("mChapterNum", "chapterNo/#");
		}
	}

}
