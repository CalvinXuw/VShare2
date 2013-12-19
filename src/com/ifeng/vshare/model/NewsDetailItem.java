package com.ifeng.vshare.model;

/**
 * 资讯详情数据item
 * 
 * @author Calvin
 * 
 */
public class NewsDetailItem extends BaseVShareItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8054296252191504176L;
	/** 资讯标题 */
	public String mTitle;
	/** 资讯来源 */
	public String mSource;
	/** 时间 */
	public long mCreatetime;
	/** html数据 */
	public String mHtmlData;

	/**
	 * 构造
	 */
	public NewsDetailItem() {
		addMappingRuleField("mTitle", "info/news/title");
		addMappingRuleField("mSource", "info/news/source");
		addMappingRuleField("mCreatetime", "info/news/createtime");
		addMappingRuleField("mHtmlData", "info/news/message");
	}
}