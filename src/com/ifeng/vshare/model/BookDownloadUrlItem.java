package com.ifeng.vshare.model;

import com.ifeng.util.net.parser.AbstractIFXMLItem;

/**
 * 图书实体文件下载地址获取
 * 
 * @author Calvin
 * 
 */
public class BookDownloadUrlItem extends AbstractIFXMLItem {

	/**
	 * 
	*/
	private static final long serialVersionUID = -7739060587504142269L;
	/** epub url */
	public String mEpubUrl;

	/**
	 * 构造
	 */
	public BookDownloadUrlItem() {
		addMappingRuleField("mEpubUrl", "epubURL/#");
	}
}