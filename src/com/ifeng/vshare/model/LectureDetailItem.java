package com.ifeng.vshare.model;

import java.util.List;

import com.ifeng.util.net.parser.AbstractIFJSONItem;
import com.ifeng.vshare.model.LectureListItem.LectureItem.LectureState;

/**
 * 大讲堂详情数据item
 * 
 * @author Calvin
 * 
 */
public class LectureDetailItem extends BaseVShareItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3012191160484464252L;
	/** 讲堂标题 */
	public String mTitle;
	/** 讲堂状态类型，使用 {@link #getLectureState()}获取 */
	public int mType;
	/** 图片url */
	public String mImg;
	/** 时间 */
	public String mTime;
	/** 地址 */
	public String mAddr;
	/** 活动介绍 */
	public String mSummary;
	/** 嘉宾介绍 */
	public String mMessage;
	/** 短信参与号码 */
	public String mSmsNum;
	/** 短信参与内容 */
	public String mSmsText;
	/** 参与人数 */
	public int mJoinNum;
	/** 评论tag */
	public String mCommentTag;

	/** 活动图片数组 */
	public List<LectureDetailPhotoItem> mActivityPhotos;
	/** 视频分集数组 */
	public List<LectureDetailVideoItem> mVideos;

	/**
	 * 构造
	 */
	public LectureDetailItem() {
		addMappingRuleField("mTitle", "info/studyname");
		addMappingRuleField("mType", "info/studytype");
		addMappingRuleField("mImg", "info/studyicon");
		addMappingRuleField("mTime", "info/studytime");
		addMappingRuleField("mAddr", "info/studyaddr");
		addMappingRuleField("mSummary", "info/studysummary");
		addMappingRuleField("mMessage", "info/studymessage");
		addMappingRuleField("mSmsNum", "info/studykey");
		addMappingRuleField("mSmsText", "info/studysend");
		addMappingRuleField("mJoinNum", "info/studynum");
		addMappingRuleArrayField("mVideos", "info/studyvideo",
				LectureDetailVideoItem.class);
		addMappingRuleField("mCommentTag", "info/comment");
		addMappingRuleArrayField("mActivityPhotos", "info/studyimg",
				LectureDetailPhotoItem.class);
	}

	/**
	 * 获取讲堂状态信息，预告、现场、回顾
	 * 
	 * @return
	 */
	public LectureState getLectureState() {
		if (mType == 2) {
			return LectureState.NOTICE;
		} else if (mType == 1) {
			return LectureState.GOING;
		} else if (mType == 3) {
			return LectureState.HISTORY;
		} else {
			return LectureState.NOTICE;
		}
	}

	/**
	 * 嵌套解析讲堂活动图片的item
	 * 
	 * @author Calvin
	 * 
	 */
	public static class LectureDetailPhotoItem extends AbstractIFJSONItem {

		/**
	 * 
	 */
		private static final long serialVersionUID = 8424216878252746791L;
		/** 图片url */
		public String mImage;

		/**
		 * 构造
		 */
		public LectureDetailPhotoItem() {
			addMappingRuleField("mImage", "img");
		}
	}

	/**
	 * 嵌套解析讲堂分集视频的item
	 * 
	 * @author Calvin
	 * 
	 */
	public static class LectureDetailVideoItem extends AbstractIFJSONItem {

		/**
	 * 
	 */
		private static final long serialVersionUID = 3691523838629310815L;
		/** 视频标题 */
		public String mVideoTitle;
		/** 视频url */
		public String mVideoUrl;
		/** 视频缩略图 */
		public String mVideoImage;
		/** 视频短标题 */
		public String mVideoDesc;

		/**
		 * 构造
		 */
		public LectureDetailVideoItem() {
			addMappingRuleField("mVideoTitle", "videotitle");
			addMappingRuleField("mVideoUrl", "videoaddress");
			addMappingRuleField("mVideoImage", "videoimg");
			addMappingRuleField("mVideoDesc", "videoid");
		}
	}
}