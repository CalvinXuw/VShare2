package com.ifeng.vshare.model;

import java.util.List;

import com.ifeng.util.net.parser.AbstractIFJSONItem;
import com.ifeng.vshare.requestor.BaseVSharePageRequestor.PageItem;

/**
 * 咨询接口数据解析处理item
 * 
 * @author Calvin
 * 
 */
public class NewsListItem extends PageItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6612214077917204824L;

	/** 头图数据 */
	public NewsItem mTopItem;

	/**
	 * 构造
	 */
	public NewsListItem() {
		addMappingRuleField("mTopItem", "info/top");
	}

	@Override
	public void addDataArrayMapping() {
		addMappingRuleArrayField(KEY_ITEMS, "info/news", NewsItem.class);
	}

	/**
	 * 嵌套解析的咨询item
	 * 
	 * @author Calvin
	 * 
	 */
	public static class NewsItem extends AbstractIFJSONItem {

		/**
	 * 
	 */
		private static final long serialVersionUID = 2351452098056697258L;
		/** 咨询id */
		public int mId;
		/** 标题 */
		public String mTitle;
		/** 来源 */
		public String mSource;
		/** 时间 */
		public long mCreatetime;
		/** 图片地址 */
		public String mImg;
		/** */
		public List<NewsPhotoUrlItem> mImgArray;
		/** comment标识 */
		public String mCommentKey;

		/** 专题id */
		public int mTopicId;
		/** 资源类型 */
		public String mNewsType;

		/**
		 * 构造
		 */
		public NewsItem() {
			addMappingRuleField("mId", "id");
			addMappingRuleField("mTitle", "title");
			addMappingRuleField("mSource", "source");
			addMappingRuleField("mCreatetime", "createtime");
			addMappingRuleField("mImg", "img");
			addMappingRuleArrayField("mImgArray", "img", NewsPhotoUrlItem.class);
			addMappingRuleField("mCommentKey", "comment");
			addMappingRuleField("mTopicId", "type");
			addMappingRuleField("mNewsType", "newstype");
		}

		/**
		 * 获取资讯类型
		 * 
		 * @return
		 */
		public NewsType getNewsType() {
			if (mTopicId != 0) {
				return NewsType.TOPIC;
			} else if ("video".equals(mNewsType)) {
				return NewsType.VIDEO;
			} else if ("image".equals(mNewsType)) {
				return NewsType.IMAGE;
			} else {
				return NewsType.NEWS;
			}
		}

		/**
		 * 资讯item类型，新闻、视频、图片、专题
		 * 
		 * @author Calvin
		 * 
		 */
		public enum NewsType {
			NEWS, VIDEO, IMAGE, TOPIC
		}
	}

	/**
	 * 嵌套item中的图片url解析item
	 * 
	 * @author Calvin
	 * 
	 */
	public static class NewsPhotoUrlItem extends AbstractIFJSONItem {

		/**
	 * 
	 */
		private static final long serialVersionUID = -1570085849747188365L;
		/** 图片url */
		public String mUrl;

		/**
		 * 构造
		 */
		public NewsPhotoUrlItem() {
			addMappingRuleField("mUrl", "img");
		}
	}
}