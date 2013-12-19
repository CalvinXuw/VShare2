package com.ifeng.vshare.requestor;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

import com.ifeng.vshare.activity.LocationActivity.CityInfo;
import com.ifeng.vshare.model.LectureListItem;

/**
 * 大讲堂页面model
 * 
 * @author Calvin
 * 
 */
public class LectureListRequestor extends BaseVSharePageRequestor {

	/** key provinceid */
	private static final String KEY_PROVINCE_ID = "provinceid";

	/** 省份 */
	private int mProvinceId;
	/** count */
	private int mCount;

	public LectureListRequestor(Context context, CityInfo cityInfo,
			OnModelProcessListener listener) {
		super(context, listener);
		mProvinceId = cityInfo.mId;
		setAutoParseClass(LectureListItem.class);
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
	protected List<NameValuePair> getRequestParamsWithoutPagething() {
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair(KEY_PROVINCE_ID, mProvinceId + ""));
		return params;
	}

	@Override
	protected void handlePageResult(PageItem item) {
		LectureListItem lectureRequestorItem = (LectureListItem) item;
		mCount = lectureRequestorItem.mCount;
	}

	/**
	 * 获取讲堂总数
	 * 
	 * @return
	 */
	public int getCount() {
		return mCount;
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
		return "http://cmv.ifeng.com/Api/studylist";
	}

}
