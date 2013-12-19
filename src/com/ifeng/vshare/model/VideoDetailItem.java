package com.ifeng.vshare.model;

/**
 * 视频详情数据item
 * 
 * @author Calvin
 * 
 */
public class VideoDetailItem extends BaseVShareItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3887801577823851859L;
	/** 视频标题 */
	public String mTitle;
	/** 视频来源 */
	public String mSource;
	/** 时间 */
	public long mCreatetime;
	/** media url */
	public String mMediaUrl;

	/**
	 * 构造
	 */
	public VideoDetailItem() {
		addMappingRuleField("mTitle", "info/news/title");
		addMappingRuleField("mSource", "info/news/source");
		addMappingRuleField("mCreatetime", "info/news/createtime");
		addMappingRuleField("mMediaUrl", "info/news/mediaurl");
	}
}