package com.ifeng.vshare.config;

import android.content.Context;

import com.ifeng.util.ConfigPreference;
import com.ifeng.vshare.activity.LocationActivity;
import com.ifeng.vshare.activity.LocationActivity.CityInfo;

/**
 * 城市定位信息参数
 * 
 * @author Calvin
 * 
 */
public class LocationInfoConfig extends ConfigPreference {

	/** 缺省值 */
	public static final int NONE = 0;

	/** 文件名 */
	private final static String SP_NAME = ".preference.location";
	/** 写入类型 */
	private final static int MODE = Context.MODE_PRIVATE;

	/** key when the lasttime location city */
	private final static String KEY_LASTTIME_CITY = "lasttime_city";

	/** 静态实例 */
	private static LocationInfoConfig sLocationInfoConfig;

	/**
	 * 获取静态实例
	 * 
	 * @param context
	 * @return
	 */
	public static LocationInfoConfig getInstance(Context context) {
		if (sLocationInfoConfig == null) {
			sLocationInfoConfig = new LocationInfoConfig(context);
		}
		return sLocationInfoConfig;
	}

	/**
	 * 构造，及对首次调用的配置参数初始化
	 * 
	 * @param context
	 */
	private LocationInfoConfig(Context context) {
		super(context, SP_NAME, MODE);
	}

	/**
	 * 获取上次定位城市信息
	 * 
	 * @return
	 */
	public CityInfo getLocationCity() {
		int lastLocationId = getInt(KEY_LASTTIME_CITY);
		if (lastLocationId == NONE) {
			return null;
		}
		return LocationActivity.getCity(lastLocationId);
	}

	/**
	 * 记录定位城市信息
	 * 
	 * @param info
	 */
	public void setLocationCity(CityInfo info) {
		putInt(KEY_LASTTIME_CITY, info.mId);
	}
}
