package com.ifeng.vshare.model;

import java.util.List;

import com.ifeng.util.net.parser.AbstractIFJSONItem;

/**
 * 进行评论请求，解析接口返还数据的item
 * 
 * @author Calvin
 * 
 */
public class CommentListItem extends AbstractIFJSONItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -48136831859036484L;
	/** 总评论数 */
	public int mCommentCount;
	/** 总参与人数 */
	public int mJoinCount;
	/** 评论 */
	public List<CommentItem> mComments;

	/**
	 * 构造，填入映射关系
	 */
	public CommentListItem() {
		addMappingRuleField("mCommentCount", "count");
		addMappingRuleField("mJoinCount", "join_count");
		addMappingRuleArrayField("mComments", "comments", CommentItem.class);
	}

	/**
	 * 嵌套解析评论的item
	 * 
	 * @author Calvin
	 * 
	 */
	public static class CommentItem extends AbstractIFJSONItem {

		/**
	 * 
	 */
		private static final long serialVersionUID = -6805271190564503457L;
		/** 用户名 */
		public String mUserName;
		/** ip地址 */
		public String mClientIp;
		/** ip所在地 */
		public String mIpFrom;
		/** 评论日期 */
		public String mCommentDate;
		/** 评论内容 */
		public String mCommentContent;

		/**
		 * 构造
		 */
		public CommentItem() {
			addMappingRuleField("mUserName", "uname");
			addMappingRuleField("mClientIp", "client_ip");
			addMappingRuleField("mIpFrom", "ip_from");
			addMappingRuleField("mCommentDate", "comment_date");
			addMappingRuleField("mCommentContent", "comment_contents");
		}
	}
}
