package com.ifeng.vshare.model;

import java.util.LinkedList;

import com.ifeng.util.net.parser.AbstractIFJSONItem;

/**
 * 贵宾厅开关接口数据解析处理item
 * 
 * @author Calvin
 * 
 */
public class ClubCategorySwitchItem extends BaseVShareItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8727382239176451843L;
	/** 俱乐部开关 */
	public LinkedList<ClubSwitchItem> mClubItemSwitch;

	/**
	 * 构造
	 */
	public ClubCategorySwitchItem() {
		addMappingRuleArrayField("mClubItemSwitch", "info",
				ClubSwitchItem.class);
	}

	/**
	 * 是否开启贵宾厅
	 * 
	 * @return
	 */
	public boolean isLobbyOn() {
		if (mClubItemSwitch == null) {
			return false;
		}

		for (ClubSwitchItem subSwitchItem : mClubItemSwitch) {
			if ("catname".equals("lobby")) {
				return subSwitchItem.mSwitch == 1;
			}
		}
		return false;
	}

	/**
	 * 是否开启俱乐部
	 * 
	 * @return
	 */
	public boolean isClubOn() {
		if (mClubItemSwitch == null) {
			return false;
		}

		for (ClubSwitchItem subSwitchItem : mClubItemSwitch) {
			if ("catname".equals("club")) {
				return subSwitchItem.mSwitch == 1;
			}
		}
		return false;
	}

	/**
	 * 子项开关控制
	 * 
	 * @author Calvin
	 * 
	 */
	public static class ClubSwitchItem extends AbstractIFJSONItem {

		/**
	 * 
	 */
		private static final long serialVersionUID = 4898239293467501950L;

		/** 控制单元名称 */
		public String mSwitchName;
		/** 控制开关 */
		public int mSwitch;

		/**
		 * 构造
		 */
		public ClubSwitchItem() {
			addMappingRuleField("mSwitchName", "catname");
			addMappingRuleField("mSwitch", "catdisplay");
		}
	}
}