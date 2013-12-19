package com.ifeng.vshare.requestor;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

import com.ifeng.vshare.activity.LocationActivity.CityInfo;
import com.ifeng.vshare.model.LobbyListItem;

/**
 * 休息室页面model
 * 
 * @author Calvin
 * 
 */
public class LobbyListRequestor extends BaseVSharePageRequestor {

	/** key provinceid */
	private static final String KEY_PROVINCE_ID = "provinceid";
	/** key longitude */
	private static final String KEY_LONGITUDE = "longitude";
	/** key latitude */
	private static final String KEY_LATITUDE = "latitude";
	/** key 贵宾厅类型，飞机or火车 */
	private static final String KEY_LOBBY_TYPE = "lobbytype";

	/** 火车 */
	private static final int VALUE_LOBBY_TYPE_RAILWAY = 0;
	/** 飞机 */
	private static final int VALUE_LOBBY_TYPE_AIRPORT = 1;

	/** 省份 */
	private int mProvinceId;
	/** longitude */
	private double mLongitude;
	/** latitude */
	private double mLatitude;
	/** 贵宾厅类型 */
	private LobbyType mLobbyType;

	/**
	 * 构造
	 * 
	 * @param context
	 * @param cityInfo
	 * @param listener
	 */
	public LobbyListRequestor(Context context, CityInfo cityInfo,
			OnModelProcessListener listener) {
		super(context, listener);
		mProvinceId = cityInfo.mId;
		mLatitude = cityInfo.mLat;
		mLongitude = cityInfo.mLon;
		setAutoParseClass(LobbyListItem.class);
	}

	/**
	 * 构造
	 * 
	 * @param context
	 * @param cityInfo
	 * @param lobbyType
	 * @param listener
	 */
	public LobbyListRequestor(Context context, CityInfo cityInfo,
			LobbyType lobbyType, OnModelProcessListener listener) {
		super(context, listener);
		mProvinceId = cityInfo.mId;
		mLatitude = cityInfo.mLat;
		mLongitude = cityInfo.mLon;
		mLobbyType = lobbyType;
		setAutoParseClass(LobbyListItem.class);
	}

	/**
	 * 更新请求位置信息参数
	 * 
	 * @param cityInfo
	 */
	public void updateLocationInfo(CityInfo cityInfo) {
		mProvinceId = cityInfo.mId;
		mLatitude = cityInfo.mLat;
		mLongitude = cityInfo.mLon;
	}

	@Override
	protected List<NameValuePair> getRequestParamsWithoutPagething() {
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair(KEY_PROVINCE_ID, mProvinceId + ""));
		params.add(new BasicNameValuePair(KEY_LONGITUDE, mLongitude + ""));
		params.add(new BasicNameValuePair(KEY_LATITUDE, mLatitude + ""));
		if (mLobbyType == LobbyType.RailwayStation) {
			params.add(new BasicNameValuePair(KEY_LOBBY_TYPE,
					VALUE_LOBBY_TYPE_RAILWAY + ""));
		} else if (mLobbyType == LobbyType.Airport) {
			params.add(new BasicNameValuePair(KEY_LOBBY_TYPE,
					VALUE_LOBBY_TYPE_AIRPORT + ""));
		}
		return params;
	}

	@Override
	protected void handlePageResult(PageItem item) {
	}

	@Override
	public List<?> getDataList() {
		return mItems;
	}

	@Override
	protected List<NameValuePair> getRequestHeaders() {
		return null;
	}

	@Override
	protected String getRequestUrl() {
		return "http://cmv.ifeng.com/Api/lobbylist";
	}

	/**
	 * 休息室类型
	 * 
	 * @author Calvin
	 * 
	 */
	public enum LobbyType implements Serializable {
		RailwayStation, Airport, All;
	}
}
