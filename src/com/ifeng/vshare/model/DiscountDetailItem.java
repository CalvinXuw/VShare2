package com.ifeng.vshare.model;

/**
 * 商盟特惠数据item
 * 
 * @author Calvin
 * 
 */
public class DiscountDetailItem extends BaseVShareItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3442224762973990864L;
	/** 图片url */
	public String mImage;
	/** 简介 */
	public String mDiscountInfo;
	/** 详细描述 */
	public String mDiscountMessage;
	/** 额外信息 */
	public String mDiscountExtra;
	/** 起始时间 */
	public long mStarttime;
	/** 终止时间 */
	public long mEndtime;
	/** 商铺位置 */
	public String mAddr;
	/** 商铺电话 */
	public String mNumber;

	/**
	 * 构造
	 */
	public DiscountDetailItem() {
		addMappingRuleField("mImage", "info/primg");
		addMappingRuleField("mDiscountInfo", "info/prtitle");
		addMappingRuleField("mDiscountMessage", "info/prinfo");
		addMappingRuleField("mDiscountExtra", "info/pinfo");
		addMappingRuleField("mStarttime", "info/prstart");
		addMappingRuleField("mEndtime", "info/prend");
		addMappingRuleField("mAddr", "info/praddr");
		addMappingRuleField("mNumber", "info/prtelephone");
	}
}