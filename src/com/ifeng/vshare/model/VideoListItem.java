package com.ifeng.vshare.model;

import com.ifeng.util.net.parser.AbstractIFJSONItem;
import com.ifeng.vshare.requestor.BaseVSharePageRequestor.PageItem;

/**
 * 视频接口数据解析处理item
 * 
 * @author Calvin
 * 
 */
public class VideoListItem extends PageItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6993453048584993413L;
	/** 头图数据 */
	public VideoItem mTopItem;

	/**
	 * 构造
	 */
	public VideoListItem() {
		addMappingRuleField("mTopItem", "info/top");
	}

	@Override
	public void addDataArrayMapping() {
		addMappingRuleArrayField(KEY_ITEMS, "info/news", VideoItem.class);
	}

	/**
	 * 嵌套解析的视频item
	 * 
	 * @author Calvin
	 * 
	 */
	public static class VideoItem extends AbstractIFJSONItem {

		/**
	 * 
	 */
		private static final long serialVersionUID = 3744495446514949447L;
		/** 视频id */
		public int mId;
		/** 标题 */
		public String mTitle;
		/** 描述 */
		public String mDesc;
		/** 来源 */
		public String mSource;
		/** 时间 */
		public long mCreatetime;
		/** 图片地址 */
		public String mImg;
		/** 视频时长 */
		public String mTimeLength;

		/** 专题id */
		public int mTopicId;

		/**
		 * 构造
		 */
		public VideoItem() {
			addMappingRuleField("mId", "id");
			addMappingRuleField("mTitle", "title");
			addMappingRuleField("mDesc", "stitle");
			addMappingRuleField("mSource", "source");
			addMappingRuleField("mCreatetime", "createtime");
			addMappingRuleField("mImg", "img");
			addMappingRuleField("mTimeLength", "DurationText");

			addMappingRuleField("mTopicId", "type");
		}

		/**
		 * 获取视频类型
		 * 
		 * @return
		 */
		public VideoType getVideoType() {
			if (mTopicId != 0) {
				return VideoType.TOPIC;
			} else {
				return VideoType.VIDEO;
			}
		}

		/**
		 * 视频item类型，视频、专题
		 * 
		 * @author Calvin
		 * 
		 */
		public enum VideoType {
			VIDEO, TOPIC
		}
	}
}