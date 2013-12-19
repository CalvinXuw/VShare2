package com.ifeng.vshare.model;

/**
 * 积分商品详情数据item
 * 
 * @author Calvin
 * 
 */
public class PointsDetailItem extends BaseVShareItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5410915350570895482L;
	/** 商品标题 */
	public String mTitle;
	/** 商品图片 */
	public String mImg;
	/** 全球通所需积分 */
	public String mGotonePoints;
	/** 动感地带所需积分 */
	public String mMzonePoints;
	/** 神州行所需积分 */
	public String mEasyownPoints;
	/** 短信兑换编码 */
	public String mSmsCode;
	/** 短信兑换号码 */
	public String mSmsPhoneNum;
	/** 主要提示 */
	public String mHintText;
	/** 礼品描述 */
	public String mProductDesc;

	/** 钻石卡积分 */
	public int mGotoneDiamond;
	/** 金卡积分 */
	public int mGotoneGold;
	/** 银卡积分 */
	public int mGotoneSilver;

	/**
	 * 构造
	 */
	public PointsDetailItem() {
		addMappingRuleField("mTitle", "info/shopname");
		addMappingRuleField("mImg", "info/shopimg");
		addMappingRuleField("mGotonePoints", "info/shopqqt");
		addMappingRuleField("mMzonePoints", "info/shopdgdd");
		addMappingRuleField("mEasyownPoints", "info/shopszx");
		addMappingRuleField("mSmsCode", "info/shopkey");
		addMappingRuleField("mSmsPhoneNum", "info/shopsend");
		// addMappingRuleField("mHintText", "info/shopmessage");
		addMappingRuleField("mProductDesc", "info/shopinfo");

		addMappingRuleField("mGotoneDiamond", "info/shopqqtdiamond");
		addMappingRuleField("mGotoneGold", "info/shopqqtgold");
		addMappingRuleField("mGotoneSilver", "info/shopqqtsilver");
	}
}