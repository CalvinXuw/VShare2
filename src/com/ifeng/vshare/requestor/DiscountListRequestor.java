package com.ifeng.vshare.requestor;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

import com.ifeng.vshare.activity.LocationActivity.CityInfo;
import com.ifeng.vshare.model.DiscountListItem;

/**
 * 特惠商盟页面model
 * 
 * @author Calvin
 * 
 */
public class DiscountListRequestor extends BaseVSharePageRequestor {

	/** key provinceid */
	private static final String KEY_PROVINCE_ID = "provinceid";
	/** key longitude */
	private static final String KEY_LONGITUDE = "longitude";
	/** KEY latitude */
	private static final String KEY_LATITUDE = "latitude";

	/** 省份 */
	private int mProvinceId;
	/** longitude */
	private double mLongitude;
	/** latitude */
	private double mLatitude;

	/**
	 * 构造
	 * 
	 * @param context
	 * @param cityInfo
	 * @param listener
	 */
	public DiscountListRequestor(Context context, CityInfo cityInfo,
			OnModelProcessListener listener) {
		super(context, listener);
		mProvinceId = cityInfo.mId;
		mLatitude = cityInfo.mLat;
		mLongitude = cityInfo.mLon;
		setAutoParseClass(DiscountListItem.class);
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
		return "http://cmv.ifeng.com/Api/privilegelist";
	}

}
