package com.ifeng.vshare.model;

import com.ifeng.util.net.parser.AbstractIFJSONItem;
import com.ifeng.vshare.requestor.BaseVSharePageRequestor.PageItem;
import com.ifeng.vshare.requestor.LobbyListRequestor.LobbyType;

/**
 * 休息厅接口数据解析处理item
 * 
 * @author Calvin
 * 
 */
public class LobbyListItem extends PageItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1398670760680151970L;

	public LobbyListItem() {
	}

	@Override
	public void addDataArrayMapping() {
		addMappingRuleArrayField(KEY_ITEMS, "info", LobbyItem.class);
	}

	/**
	 * 嵌套解析的休息厅item
	 * 
	 * @author Calvin
	 * 
	 */
	public static class LobbyItem extends AbstractIFJSONItem {

		/**
	 * 
	 */
		private static final long serialVersionUID = 7804305679569532538L;
		/** 休息厅id */
		public int mId;
		/** 名称 */
		public String mName;
		/** 描述，例如：安检区内、服务时间 */
		public String mInfo;
		/** 联系电话 */
		public String mTel;
		/** 位置 */
		public String mAddress;
		/** 类型，机场or火车 */
		public String mType;
		/** 距离，米为单位 */
		public double mDistance;

		/**
		 * 构造
		 */
		public LobbyItem() {
			addMappingRuleField("mId", "lobbyid");
			addMappingRuleField("mName", "lobbyname");
			addMappingRuleField("mInfo", "lobbyinfo");
			addMappingRuleField("mTel", "lobbytelphone");
			addMappingRuleField("mAddress", "lobbyaddr");
			addMappingRuleField("mType", "lobbytype");
			addMappingRuleField("mDistance", "distance");
		}

		/**
		 * 获取休息厅类型
		 * 
		 * @return
		 */
		public LobbyType getLobbyType() {
			if ("火车".equals(mType)) {
				return LobbyType.RailwayStation;
			} else {
				return LobbyType.Airport;
			}
		}

	}
}