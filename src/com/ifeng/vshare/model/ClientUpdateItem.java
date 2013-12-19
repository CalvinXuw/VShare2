package com.ifeng.vshare.model;

/**
 * 解析客户端升级接口
 * 
 * @author Calvin
 * 
 */
public class ClientUpdateItem extends BaseVShareItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8899888216299305472L;

	/** 版本名 */
	public String mNewVersion;
	/** 版本号 */
	public int mNewVersionCode;

	/** 当前版本名 */
	public String mCurrentVersion;

	/** 客户端下载地址 */
	public String mUrl;
	/** 更新描述 */
	public String mDesc;

	/**
	 * 构造
	 */
	public ClientUpdateItem() {
		addMappingRuleField("mNewVersion", "info/versionid");
		addMappingRuleField("mNewVersionCode", "info/officialid");
		addMappingRuleField("mUrl", "info/url");
		addMappingRuleField("mDesc", "info/explanation");
	}
}