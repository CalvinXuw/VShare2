package com.ifeng.vshare;

import java.util.LinkedList;

import android.content.DialogInterface;

import com.baidu.location.BDLocation;
import com.ifeng.BaseApplicaion;
import com.ifeng.ipush.client.Ipush;
import com.ifeng.util.logging.Log;
import com.ifeng.vshare.activity.LocationActivity;
import com.ifeng.vshare.activity.LocationActivity.CityInfo;
import com.ifeng.vshare.book.BookDownloadManager;
import com.ifeng.vshare.config.LocationInfoConfig;
import com.ifeng.vshare.ui.DialogManager;
import com.ifeng.vshare.ui.DialogManager.DialogStateCallback;

/**
 * 本客户端使用的application，继承自ifengapplication基类，承载客户端所需的全局信息，如省份定位等
 * 
 * @author Calvin
 * 
 */
public class VShareApplication extends BaseApplicaion {

	// 相关框架管理
	@Override
	public void onCreate() {
		super.onCreate();
		BookDownloadManager.init(getApplicationContext());
		Ipush.init(this, 5, false);
		super.addLocationServiceListener(mAutoLocationListener);
	}

	@Override
	public void onTerminate() {
		BookDownloadManager.release();
		super.removeLocationServiceListener(mAutoLocationListener);
		super.onTerminate();
	}

	// 定位部分

	/** 定位信息 */
	private CityInfo mLocationInfo;
	/** 定位回调 */
	private CityLocationObservable mCityLocationObservable = new CityLocationObservable();

	/*
	 * bug fix 由于定位控制都在application层级中处理，所以导致在城市选择界面定位之前返回到主页，可能仍然会弹出矫正城市对话框的bug
	 */
	private boolean mHasShowed;

	/** getlocation */
	public CityInfo getLocationInfo() {
		if (mLocationInfo == null) {
			mLocationInfo = LocationInfoConfig.getInstance(
					getApplicationContext()).getLocationCity();
		}
		if (mLocationInfo == null) {
			mLocationInfo = LocationActivity.getCity(null);
		}

		return mLocationInfo;
	}

	/**
	 * 定位城市检查提醒，如果当前定位城市和选中城市不符时，需要提示用户是否做出修正
	 * 
	 * @param newLocationInfo
	 */
	private synchronized void checkLocation(final CityInfo newLocationInfo) {
		// 首次获取位置成功，直接通知
		if (mLocationInfo == null) {
			mCityLocationObservable.notifyLocation(newLocationInfo);
			return;
		}

		// 选中城市和定位城市不符
		if (mLocationInfo.mId != newLocationInfo.mId) {
			// 每次运行客户端仅进行一次检测
			if (mHasShowed) {
				return;
			}
			mHasShowed = true;
			try {
				DialogManager
						.getInstance()
						.createDialog(
								getString(R.string.dialog_title),
								String.format(
										getString(R.string.location_adjust),
										newLocationInfo.mChineseName,
										newLocationInfo.mChineseName),
								new DialogStateCallback() {

									@Override
									public void onClick(int which) {
										if (which == DialogInterface.BUTTON_POSITIVE) {
											mCityLocationObservable
													.notifyLocation(newLocationInfo);
										} else if (which == DialogInterface.BUTTON_NEGATIVE) {
											CityInfo adjustInfo = mLocationInfo;
											adjustInfo.mLat = newLocationInfo.mLat;
											adjustInfo.mLon = newLocationInfo.mLon;
											mCityLocationObservable
													.notifyLocation(adjustInfo);
										}
									}

									@Override
									public void onCancel() {
										mCityLocationObservable
												.notifyLocation(newLocationInfo);
									}
								}, true, getString(R.string.dialog_yes),
								getString(R.string.dialog_no))
						.setCancelable(false);
			} catch (Exception e) {
				if (DEBUG) {
					Log.w(TAG, e);
				}
				mCityLocationObservable.notifyLocation(newLocationInfo);
			}
		} else {
			mCityLocationObservable.notifyLocation(newLocationInfo);
		}
	}

	/** 设置定位城市 */
	public void setLocationInfo(CityInfo newLocationInfo) {
		// 如果该定位为用户手选定位，则保留原有经纬度定位坐标
		if (mLocationInfo != null && newLocationInfo.mLat == 0
				&& newLocationInfo.mLon == 0) {
			newLocationInfo.mLat = mLocationInfo.mLat;
			newLocationInfo.mLon = mLocationInfo.mLon;
		}
		mLocationInfo = newLocationInfo;
		LocationInfoConfig.getInstance(getApplicationContext())
				.setLocationCity(mLocationInfo);
	}

	@Override
	@Deprecated
	public void addLocationServiceListener(LocationServiceListener listener) {
	}

	/**
	 * 添加城市定位回调
	 * 
	 * @param listener
	 */
	public void addLocationServiceListener(OnCityLocationListener listener) {
		mCityLocationObservable.addListener(listener);
	}

	@Override
	@Deprecated
	public void removeLocationServiceListener(LocationServiceListener listener) {
	}

	/**
	 * 移除城市定位回调
	 * 
	 * @param listener
	 */
	public void removeLocationServiceListener(OnCityLocationListener listener) {
		mCityLocationObservable.removeListener(listener);
	}

	@Override
	public void requestLocation() {
		if (mLocationInfo == null) {
			// 首次定位优先从历史记录中读取
			mLocationInfo = LocationInfoConfig.getInstance(
					getApplicationContext()).getLocationCity();
			if (mLocationInfo != null) {
				mCityLocationObservable.notifyLocation(mLocationInfo);
				return;
			}
		}

		if (mLocationInfo != null && mLocationInfo.mLat != 0
				&& mLocationInfo.mLon != 0) {
			mCityLocationObservable.notifyLocation(mLocationInfo);
			return;
		}
		super.requestLocation();
	}

	/**
	 * 强制重新定位
	 */
	public void forceRequestLocation() {
		super.requestLocation();
	}

	/** 获取定位信息 */
	private LocationServiceListener mAutoLocationListener = new LocationServiceListener() {

		@Override
		public void onReceivePoi(BDLocation location) {

		}

		@Override
		public void onReceiveLocation(BDLocation location) {
			stopLocationService();
			CityInfo locationCity = null;
			if (location != null) {
				CityInfo info = LocationActivity
						.getCity(location.getProvince());
				locationCity = info;
				info.mLat = location.getLatitude();
				info.mLon = location.getLongitude();
			}

			mCityLocationObservable.notifyForceLocation(locationCity);

			// 定位失败且当前无选择城市的情况，指定默认城市
			if (locationCity == null && mLocationInfo == null) {
				locationCity = LocationActivity.getCity(null);
			}

			// 如果有新的定位信息，则更新定位城市
			if (locationCity != null) {
				checkLocation(locationCity);
			} else {
				mCityLocationObservable.notifyLocation(mLocationInfo);
			}
		}
	};

	/**
	 * 定位请求观察者
	 * 
	 * @author Calvin
	 * 
	 */
	private class CityLocationObservable {

		/** 观察者队列 */
		private LinkedList<OnCityLocationListener> mLocationObservers;

		/**
		 * 构造
		 */
		public CityLocationObservable() {
			mLocationObservers = new LinkedList<OnCityLocationListener>();
		}

		/**
		 * 添加观察者
		 * 
		 * @param listener
		 */
		public synchronized void addListener(OnCityLocationListener listener) {
			if (listener == null || mLocationObservers.contains(listener)) {
				return;
			}
			mLocationObservers.add(listener);
		}

		/**
		 * 移除观察者
		 * 
		 * @param listener
		 */
		public synchronized void removeListener(OnCityLocationListener listener) {
			if (listener == null || !mLocationObservers.contains(listener)) {
				return;
			}
			mLocationObservers.remove(listener);
		}

		/**
		 * 通知监听回调
		 * 
		 * @param info
		 */
		public void notifyLocation(CityInfo info) {
			setLocationInfo(info);
			for (OnCityLocationListener listener : mLocationObservers) {
				if (!(listener instanceof OnCityForceLocationListener)) {
					listener.onLocation(info);
				}
			}
		}

		/**
		 * 通知强制更新监听
		 * 
		 * @param info
		 */
		public void notifyForceLocation(CityInfo info) {
			for (OnCityLocationListener listener : mLocationObservers) {
				if (listener instanceof OnCityForceLocationListener) {
					listener.onLocation(info);
				}
			}
		}
	}

	/**
	 * 城市定位监听
	 * 
	 * @author Calvin
	 * 
	 */
	public interface OnCityLocationListener {
		/**
		 * 定位成功
		 * 
		 * @param info
		 */
		public void onLocation(CityInfo info);
	}

	/**
	 * 强制刷新定位监听
	 * 
	 * @author Calvin
	 * 
	 */
	public interface OnCityForceLocationListener extends OnCityLocationListener {

	}
}
