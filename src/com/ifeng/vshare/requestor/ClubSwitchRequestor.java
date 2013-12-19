package com.ifeng.vshare.requestor;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

import com.ifeng.vshare.activity.LocationActivity.CityInfo;
import com.ifeng.vshare.model.BaseVShareItem;
import com.ifeng.vshare.model.ClubCategorySwitchItem;

/**
 * 俱乐部页面开关model
 * 
 * @author Calvin
 * 
 */
public class ClubSwitchRequestor extends BaseVShareRequestor {

	/** key provinceid */
	private static final String KEY_PROVINCE_ID = "provinceid";

	/** 省份 */
	private int mProvinceId;

	/** 请求结果item */
	private boolean mIsLobbyOn;

	public ClubSwitchRequestor(Context context, CityInfo cityInfo,
			OnModelProcessListener listener) {
		super(context, listener);
		mProvinceId = cityInfo.mId;
		setAutoParseClass(ClubCategorySwitchItem.class);
	}

	/**
	 * 更新请求位置信息参数
	 * 
	 * @param cityInfo
	 */
	public void updateLocationInfo(CityInfo cityInfo) {
		mProvinceId = cityInfo.mId;
	}

	@Override
	protected List<NameValuePair> getRequestHeaders() {
		return null;
	}

	@Override
	protected List<NameValuePair> getRequestParams() {
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair(KEY_PROVINCE_ID, mProvinceId + ""));
		return params;
	}

	@Override
	protected String getRequestUrl() {
		return "http://cmv.ifeng.com/Api/catdisplay";
	}

	@Override
	protected void handleResult(BaseVShareItem item) {
		mIsLobbyOn = ((ClubCategorySwitchItem) item).isLobbyOn();
	}

	/**
	 * 是否开启贵宾厅
	 * 
	 * @return
	 */
	public boolean isLobbyOn() {
		return mIsLobbyOn;
	}

}
