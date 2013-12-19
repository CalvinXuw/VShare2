package com.ifeng.vshare.model;

import com.ifeng.util.net.parser.AbstractIFJSONItem;
import com.ifeng.vshare.requestor.BaseVSharePageRequestor.PageItem;

/**
 * 大讲堂接口数据解析处理item
 * 
 * @author Calvin
 * 
 */
public class LectureListItem extends PageItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6223575505651514361L;

	/** 讲堂数量 */
	public int mCount;

	/**
	 * 构造
	 */
	public LectureListItem() {
	}

	@Override
	public void addDataArrayMapping() {
		addMappingRuleField("mCount", "info/count");
		addMappingRuleArrayField(KEY_ITEMS, "info/local", LectureItem.class);
	}

	/**
	 * 嵌套解析的大讲堂item
	 * 
	 * @author Calvin
	 * 
	 */
	public static class LectureItem extends AbstractIFJSONItem {

		/**
	 * 
	 */
		private static final long serialVersionUID = 8480895797862841028L;
		/** 讲堂id */
		public int mId;
		/** 名称 */
		public String mName;
		/** image url */
		public String mImg;
		/** 评论tag */
		public String mCommentTag;
		/** 讲堂时间 */
		public String mTime;
		/** 讲堂地址 */
		public String mAddr;
		/** 讲堂概要描述 */
		public String mSummary;
		/** 讲堂状态类型，解析使用。如需获取类型，调用 {@link #getLectureState()} */
		public int mType;

		/**
		 * 构造
		 */
		public LectureItem() {
			addMappingRuleField("mId", "studyid");
			addMappingRuleField("mName", "studyname");
			addMappingRuleField("mImg", "studyicon");
			addMappingRuleField("mCommentTag", "comment");
			addMappingRuleField("mTime", "studytime");
			addMappingRuleField("mAddr", "studyaddr");
			addMappingRuleField("mSummary", "studysummary");
			addMappingRuleField("mType", "studytype");
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
		 * 讲堂状态，预告、现场、回顾
		 * 
		 * @author Calvin
		 * 
		 */
		public enum LectureState {
			NOTICE, GOING, HISTORY
		}
	}
}