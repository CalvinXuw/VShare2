package com.ifeng.vshare.activity;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ifeng.util.logging.Log;
import com.ifeng.util.ui.IFRefreshListView;
import com.ifeng.util.ui.IFRefreshViewLayout;
import com.ifeng.util.ui.NavgationbarView;
import com.ifeng.util.ui.OnSingleClickListener;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareApplication;
import com.ifeng.vshare.VShareApplication.OnCityForceLocationListener;
import com.ifeng.vshare.adapter.SectionAdapter;

/**
 * 定位选取activity
 * 
 * @author Calvin
 * 
 */
public class LocationActivity extends VShareCategoryActivity {

	/** 城市源数据 */
	private static final String CITY_ARRAY = "[{\"id\": 11,\"countryid\": 86,\"province_chinese\": \"北京市\",\"province_english\": \"BeiJingShi\",\"districtcode\": 110000,\"shortcut\":\"北京\"}, {\"id\": 12,\"countryid\": 86,\"province_chinese\": \"天津市\",\"province_english\": \"TianJinShi\",\"districtcode\": 120000,\"shortcut\":\"天津\"}, {\"id\": 13,\"countryid\": 86,\"province_chinese\": \"河北省\",\"province_english\": \"HeBeiSheng\",\"districtcode\": 130000,\"shortcut\":\"河北\"}, {\"id\": 14,\"countryid\": 86,\"province_chinese\": \"山西省\",\"province_english\": \"ShanXiSheng\",\"districtcode\": 140000,\"shortcut\":\"山西\"}, {\"id\": 15,\"countryid\": 86,\"province_chinese\": \"内蒙古自治区\",\"province_english\": \"NaMengGuZiZhiQu\",\"districtcode\": 150000,\"shortcut\":\"内蒙古\"}, {\"id\": 21,\"countryid\": 86,\"province_chinese\": \"辽宁省\",\"province_english\": \"LiaoNingSheng\",\"districtcode\": 210000,\"shortcut\":\"辽宁\"}, {\"id\": 22,\"countryid\": 86,\"province_chinese\": \"吉林省\",\"province_english\": \"JiLinSheng\",\"districtcode\": 220000,\"shortcut\":\"吉林\"}, {\"id\": 23,\"countryid\": 86,\"province_chinese\": \"黑龙江省\",\"province_english\": \"HeiLongJiangSheng\",\"districtcode\": 230000,\"shortcut\":\"黑龙江\"}, {\"id\": 31,\"countryid\": 86,\"province_chinese\": \"上海市\",\"province_english\": \"ShangHaiShi\",\"districtcode\": 310000,\"shortcut\":\"上海\"}, {\"id\": 32,\"countryid\": 86,\"province_chinese\": \"江苏省\",\"province_english\": \"JiangSuSheng\",\"districtcode\": 320000,\"shortcut\":\"江苏\"}, {\"id\": 33,\"countryid\": 86,\"province_chinese\": \"浙江省\",\"province_english\": \"ZheJiangSheng\",\"districtcode\": 330000,\"shortcut\":\"浙江\"}, {\"id\": 34,\"countryid\": 86,\"province_chinese\": \"安徽省\",\"province_english\": \"AnHuiSheng\",\"districtcode\": 340000,\"shortcut\":\"安徽\"}, {\"id\": 35,\"countryid\": 86,\"province_chinese\": \"福建省\",\"province_english\": \"FuJianSheng\",\"districtcode\": 350000,\"shortcut\":\"福建\"}, {\"id\": 36,\"countryid\": 86,\"province_chinese\": \"江西省\",\"province_english\": \"JiangXiSheng\",\"districtcode\": 360000,\"shortcut\":\"江西\"}, {\"id\": 37,\"countryid\": 86,\"province_chinese\": \"山东省\",\"province_english\": \"ShanDongSheng\",\"districtcode\": 370000,\"shortcut\":\"山东\"}, {\"id\": 41,\"countryid\": 86,\"province_chinese\": \"河南省\",\"province_english\": \"HeNanSheng\",\"districtcode\": 410000,\"shortcut\":\"河南\"}, {\"id\": 42,\"countryid\": 86,\"province_chinese\": \"湖北省\",\"province_english\": \"HuBeiSheng\",\"districtcode\": 420000,\"shortcut\":\"湖北\"}, {\"id\": 43,\"countryid\": 86,\"province_chinese\": \"湖南省\",\"province_english\": \"HuNanSheng\",\"districtcode\": 430000,\"shortcut\":\"湖南\"}, {\"id\": 44,\"countryid\": 86,\"province_chinese\": \"广东省\",\"province_english\": \"GuangDongSheng\",\"districtcode\": 440000,\"shortcut\":\"广东\"}, {\"id\": 45,\"countryid\": 86,\"province_chinese\": \"广西壮族自治区\",\"province_english\": \"GuangXiZhuangZuZiZhiQu\",\"districtcode\": 450000,\"shortcut\":\"广西\"}, {\"id\": 46,\"countryid\": 86,\"province_chinese\": \"海南省\",\"province_english\": \"HaiNanSheng\",\"districtcode\": 460000,\"shortcut\":\"海南\"}, {\"id\": 50,\"countryid\": 86,\"province_chinese\": \"重庆市\",\"province_english\": \"ChongQingShi\",\"districtcode\": 500000,\"shortcut\":\"重庆\"}, {\"id\": 51,\"countryid\": 86,\"province_chinese\": \"四川省\",\"province_english\": \"SiChuanSheng\",\"districtcode\": 510000,\"shortcut\":\"四川\"}, {\"id\": 52,\"countryid\": 86,\"province_chinese\": \"贵州省\",\"province_english\": \"GuiZhouSheng\",\"districtcode\": 520000,\"shortcut\":\"贵州\"}, {\"id\": 53,\"countryid\": 86,\"province_chinese\": \"云南省\",\"province_english\": \"YunNanSheng\",\"districtcode\": 530000,\"shortcut\":\"云南\"}, {\"id\": 54,\"countryid\": 86,\"province_chinese\": \"西藏自治区\",\"province_english\": \"XiCangZiZhiQu\",\"districtcode\": 540000,\"shortcut\":\"西藏\"}, {\"id\": 61,\"countryid\": 86,\"province_chinese\": \"陕西省\",\"province_english\": \"ShanXiSheng\",\"districtcode\": 610000,\"shortcut\":\"陕西\"}, {\"id\": 62,\"countryid\": 86,\"province_chinese\": \"甘肃省\",\"province_english\": \"GanSuSheng\",\"districtcode\": 620000,\"shortcut\":\"甘肃\"}, {\"id\": 63,\"countryid\": 86,\"province_chinese\": \"青海省\",\"province_english\": \"QingHaiSheng\",\"districtcode\": 630000,\"shortcut\":\"青海\"}, {\"id\": 64,\"countryid\": 86,\"province_chinese\": \"宁夏回族自治区\",\"province_english\": \"NingXiaHuiZuZiZhiQu\",\"districtcode\": 640000,\"shortcut\":\"宁夏\"}, {\"id\": 65,\"countryid\": 86,\"province_chinese\": \"新疆维吾尔自治区\",\"province_english\": \"XinJiangWeiWuErZiZhiQu\",\"districtcode\": 650000,\"shortcut\":\"新疆\"}]";// ,
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																	// {\"id\": 71,\"countryid\": 86,\"province_chinese\": \"台湾省\",\"province_english\": \"TaiWanSheng\",\"districtcode\": 710000,\"shortcut\":\"台湾\"}, {\"id\": 81,\"countryid\": 86,\"province_chinese\": \"香港特别行政区\",\"province_english\": \"XiangGangTeBieXingZhengQu\",\"districtcode\": 810000,\"shortcut\":\"香港\"}, {\"id\": 82,\"countryid\": 86,\"province_chinese\": \"澳门特别行政区\",\"province_english\": \"AoMenTeBieXingZhengQu\",\"districtcode\": 820000,\"shortcut\":\"澳门\"}]";
	/** 城市列表 */
	private static LinkedList<CityInfo> sCityList = new LinkedList<CityInfo>();
	/** 默认的city返回 */
	private static CityInfo sDefaultCity;
	/** 常用省市 */
	private static LinkedList<CityInfo> sHotCityList = new LinkedList<LocationActivity.CityInfo>();

	/**
	 * 省市信息
	 * 
	 * @author Calvin
	 * 
	 */
	public static class CityInfo {
		/** id */
		public int mId;
		/** 中文名称 */
		public String mChineseName;
		/** 拼音 */
		public String mEnglishName;
		/** 简称 */
		public String mShortCut;
		/** 纬度 */
		public double mLat;
		/** 经度 */
		public double mLon;
	}

	/**
	 * 根据名称获取city
	 */
	public static CityInfo getCity(String name) {
		initCity();
		if (TextUtils.isEmpty(name)) {
			return sDefaultCity;
		}

		try {
			for (CityInfo info : sCityList) {
				if (info.mChineseName.contains(name)) {
					return info;
				}
			}
		} catch (Exception e) {
			Log.e("Location", e);
		}
		// 若查询不到，则返回北京
		return sDefaultCity;
	}

	/**
	 * 根据id获取city
	 */
	public static CityInfo getCity(int id) {
		initCity();

		try {
			for (CityInfo info : sCityList) {
				if (info.mId == id) {
					return info;
				}
			}
		} catch (Exception e) {
			Log.e("Location", e);
		}
		// 若查询不到，则返回北京
		return sDefaultCity;
	}

	/**
	 * 初始化城市列表信息
	 */
	private static void initCity() {
		if (sCityList.size() == 0) {
			// 初始化工作，填入全部省份以及常用省份
			try {
				JSONArray cityJson = new JSONArray(CITY_ARRAY);

				for (int i = 0; i < cityJson.length(); i++) {
					JSONObject cityJsonObject = cityJson.optJSONObject(i);
					if (cityJsonObject != null) {
						CityInfo info = new CityInfo();
						info.mId = cityJsonObject.optInt("id");
						info.mChineseName = cityJsonObject
								.optString("province_chinese");
						info.mEnglishName = cityJsonObject
								.optString("province_english");
						info.mShortCut = cityJsonObject.optString("shortcut");
						sCityList.add(info);
						if (info.mChineseName.contains("北京")) {
							sDefaultCity = info;
						}

						if (info.mChineseName.contains("北京")
								|| info.mChineseName.contains("上海")
								|| info.mChineseName.contains("重庆")
								|| info.mChineseName.contains("天津")) {
							sHotCityList.add(info);
						}
					}
				}
				CitySort citySort = new CitySort();
				Collections.sort(sCityList, citySort);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/** 执行刷新的view */
	private IFRefreshViewLayout<IFRefreshListView> mRefreshView;
	/** 城市列表 */
	private IFRefreshListView mCityListView;
	/** section adapter */
	private LocationSectionAdapter mAdapter;
	/** location city */
	private CityInfo mLocationCity;
	/** 定位状态 */
	private LocationState mLocationState;

	/**
	 * 当前定位的状态
	 * 
	 * @author Calvin
	 * 
	 */
	private enum LocationState {
		Locationing, LocationSuccess, LocationFailed
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.location);

		NavgationbarView navgationbar = (NavgationbarView) findViewById(R.id.navgationbar);

		/*
		 * 设置导航条部分
		 */
		navgationbar.setTitle(getString(R.string.title_location));
		navgationbar.setBackItem(this);

		/*
		 * 列表部分
		 */
		mRefreshView = (IFRefreshViewLayout<IFRefreshListView>) findViewById(R.id.layout_refresh);
		mCityListView = new IFRefreshListView(this);
		mAdapter = new LocationSectionAdapter();
		mCityListView.setAdapter(mAdapter);
		mRefreshView.setContentView(mCityListView);

		/*
		 * 定位部分
		 */
		((VShareApplication) getApplication())
				.addLocationServiceListener(mLocationListener);
		requestLocation();
	}

	@Override
	protected void onDestroy() {
		((VShareApplication) getApplication())
				.removeLocationServiceListener(mLocationListener);
		super.onDestroy();
	}

	/**
	 * 请求位置服务
	 */
	private void requestLocation() {
		mLocationState = LocationState.Locationing;
		mAdapter.notifyDataSetChanged();
		((VShareApplication) getApplication()).forceRequestLocation();
	}

	/**
	 * 可以处理section参数的adapter
	 * 
	 * @author Calvin
	 * 
	 */
	private class LocationSectionAdapter extends SectionAdapter {
		/** section location id */
		private static final int SECTION_LOCATION = 0;
		/** section hot id */
		private static final int SECTION_HOT = 1;
		/** section all id */
		private static final int SECTION_ALL = 2;

		@Override
		public int sectionCount() {
			// 定位、常用、全部
			return 3;
		}

		@Override
		public int getCountWithSection(int sectionId) {
			if (sectionId == SECTION_LOCATION) {
				return 1;
			} else if (sectionId == SECTION_HOT) {
				return sHotCityList.size();
			} else if (sectionId == SECTION_ALL) {
				return sCityList.size();
			}
			return 0;
		}

		@Override
		public Object getItemWithSection(int sectionId, int position) {
			return null;
		}

		@Override
		public View getViewWithSection(int sectionId, int position,
				View convertView) {
			LocationViewHolder holder = null;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.location_list_item, null);
				holder = new LocationViewHolder();
				holder.mLocationTag = convertView
						.findViewById(R.id.image_item_location_tag);
				holder.mSectionGroup = (ViewGroup) convertView
						.findViewById(R.id.layout_item_location_section);
				holder.mItemGroup = (ViewGroup) convertView
						.findViewById(R.id.layout_item_location_item);
				holder.mSectionName = (TextView) convertView
						.findViewById(R.id.text_item_location_section);
				holder.mProvinceName = (TextView) convertView
						.findViewById(R.id.text_item_location_province);
				convertView.setTag(holder);
			} else {
				holder = (LocationViewHolder) convertView.getTag();
			}

			// 为每个section开头第一个item设置标题
			if (position == 0) {
				holder.mSectionGroup.setVisibility(View.VISIBLE);
				holder.mSectionName.setText(getSectionName(sectionId));
			} else {
				holder.mSectionGroup.setVisibility(View.GONE);
			}

			switch (sectionId) {
			case SECTION_LOCATION:
				holder.mLocationTag.setVisibility(View.VISIBLE);
				if (mLocationState == LocationState.Locationing) {
					holder.mProvinceName
							.setText(R.string.section_location_locationing);
					holder.mItemGroup.setOnClickListener(null);
				} else if (mLocationState == LocationState.LocationSuccess) {
					holder.mProvinceName.setText(mLocationCity.mChineseName);
					holder.mItemGroup
							.setOnClickListener(new CitySelectClickListener(
									mLocationCity));
				} else if (mLocationState == LocationState.LocationFailed) {
					holder.mProvinceName
							.setText(R.string.section_location_locationfailed);
					holder.mItemGroup
							.setOnClickListener(new ReLocateClickListener());
				}
				break;
			case SECTION_HOT:
				holder.mLocationTag.setVisibility(View.GONE);
				holder.mProvinceName
						.setText(sHotCityList.get(position).mChineseName);
				holder.mItemGroup
						.setOnClickListener(new CitySelectClickListener(
								sHotCityList.get(position)));
				break;
			case SECTION_ALL:
				holder.mLocationTag.setVisibility(View.GONE);
				holder.mProvinceName
						.setText(sCityList.get(position).mChineseName);
				holder.mItemGroup
						.setOnClickListener(new CitySelectClickListener(
								sCityList.get(position)));
				break;
			}

			return convertView;
		}

		@Override
		public String getSectionName(int sectionId) {
			if (sectionId == SECTION_LOCATION) {
				return getString(R.string.section_location);
			} else if (sectionId == SECTION_HOT) {
				return getString(R.string.section_hot);
			} else if (sectionId == SECTION_ALL) {
				return getString(R.string.section_all);
			}
			return null;
		}

		/**
		 * listitem holder
		 * 
		 * @author Calvin
		 * 
		 */
		private class LocationViewHolder {
			/** 定位小标识 */
			public View mLocationTag;
			/** section group */
			public ViewGroup mSectionGroup;
			/** item group */
			public ViewGroup mItemGroup;
			/** section名 */
			public TextView mSectionName;
			/** 省份 */
			public TextView mProvinceName;
		}

		/**
		 * 选取城市的事件监听
		 */
		private class CitySelectClickListener extends OnSingleClickListener {

			/** 选择的城市信息 */
			private CityInfo mCityInfo;

			/**
			 * 构造
			 * 
			 * @param cityInfo
			 */
			public CitySelectClickListener(CityInfo cityInfo) {
				mCityInfo = cityInfo;
			}

			@Override
			public void onSingleClick(View v) {
				((VShareApplication) getApplication())
						.setLocationInfo(mCityInfo);
				LocationActivity.this.finish();
			}
		};

		/**
		 * 请求定位事件监听
		 * 
		 * @author Calvin
		 * 
		 */
		private class ReLocateClickListener extends OnSingleClickListener {
			@Override
			public void onSingleClick(View v) {
				requestLocation();
			}
		}
	}

	/** 获取定位信息 */
	private OnCityForceLocationListener mLocationListener = new OnCityForceLocationListener() {

		@Override
		public void onLocation(CityInfo info) {
			if (info != null) {
				mLocationCity = info;
				mLocationState = LocationState.LocationSuccess;
				mAdapter.notifyDataSetChanged();
			}

			// 定位失败的情况，更新状态
			if (mLocationCity == null) {
				mLocationState = LocationState.LocationFailed;
				mAdapter.notifyDataSetChanged();
			}
		}
	};

	public static class CitySort implements Comparator<CityInfo> {
		public int compare(CityInfo c1, CityInfo c2) {
			return (c1.mEnglishName).compareTo(c2.mEnglishName);
		}
	}
}
