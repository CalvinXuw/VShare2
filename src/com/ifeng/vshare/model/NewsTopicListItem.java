package com.ifeng.vshare.model;

import com.ifeng.vshare.model.NewsListItem.NewsItem;
import com.ifeng.vshare.requestor.BaseVSharePageRequestor.PageItem;

/**
 * 专题接口数据解析处理item
 * 
 * @author Calvin
 * 
 */
public class NewsTopicListItem extends PageItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1001394231969897241L;
	/** 专题名 */
	public String mTitle;
	/** 专题缩略图 */
	public String mImg;
	/** 专题简介 */
	public String mInfo;
	/** 相关咨询数(编辑录入，不保证为真实数据) */
	public int mTotal;

	public NewsTopicListItem() {
		addMappingRuleField("mTitle", "info/title");
		addMappingRuleField("mImg", "info/img");
		addMappingRuleField("mInfo", "info/info");
		addMappingRuleField("mTotal", "info/total");
	}

	@Override
	public void addDataArrayMapping() {
		addMappingRuleArrayField(KEY_ITEMS, "info/list", NewsItem.class);
	}
}