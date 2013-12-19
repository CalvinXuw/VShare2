package com.ifeng.vshare.requestor;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.ifeng.ipush.client.Ipush;
import com.ifeng.util.Utility;
import com.ifeng.util.logging.Log;
import com.ifeng.vshare.VShareActivity;
import com.ifeng.vshare.VShareApplication;
import com.ifeng.vshare.model.BaseVShareItem;
import com.ifeng.vshare.model.FeedbackItem;

/**
 * 意见反馈接口requestor
 * 
 * @author Calvin
 * 
 */
public class FeedbackRequestor extends BaseVShareRequestor {

	/** manifest中application层级参数，渠道号 */
	private static final String KEY_META_CHANNEL_ID = "channelId";

	/*
	 * 个人信息部分
	 */
	/** key 系统类型 */
	private static final String KEY_OS = "os";
	/** key 注册信息 */
	private static final String KEY_INFO = "info";
	/** key 应用版本 */
	private static final String KEY_VERSION = "version";
	/** key mac地址 */
	private static final String KEY_MAC = "mac";
	/** key 渠道号 */
	private static final String KEY_CHANNELID = "channnelid";
	/** key 系统版本 */
	private static final String KEY_OSCODE = "oscode";
	/** key 推送标识 */
	private static final String KEY_TOKEN = "token";
	/** key 设备名 */
	private static final String KEY_PLATFORM = "platform";
	/** key ip */
	private static final String KEY_IP = "ip";
	/** key provinceId */
	private static final String KEY_PROVINCE_ID = "provinceid";

	/*
	 * 反馈信息部分
	 */
	/** key contact */
	private static final String KEY_CONTACT = "email";
	/** key feedback */
	private static final String KEY_FEEDBACK = "feedback";

	/** value android系统 */
	private static final String VALUE_OS = "ANDROID";

	/** 联系方式 */
	private String mContact;
	/** 反馈信息 */
	private String mFeedback;

	/**
	 * 构造
	 * 
	 * @param context
	 * @param listener
	 */
	public FeedbackRequestor(Context context, OnModelProcessListener listener) {
		super(context, listener);
		mRequestDataCache.getDataCacheParams().cacheValidityTime = 0;
		setAutoParseClass(FeedbackItem.class);
	}

	@Override
	@Deprecated
	public void request() {
	}

	/**
	 * 提交反馈
	 * 
	 * @param contact
	 * @param feedbackContent
	 */
	public void handUpFeedback(String contact, String feedbackContent) {
		mContact = contact;
		mFeedback = feedbackContent;
		super.request();
	}

	@Override
	protected void handleResult(BaseVShareItem item) {
	}

	@Override
	protected List<NameValuePair> getRequestHeaders() {
		return null;
	}

	@Override
	protected List<NameValuePair> getRequestParams() {
		List<NameValuePair> params = new LinkedList<NameValuePair>();

		try {
			JSONObject infoObject = new JSONObject();
			infoObject.put(KEY_OS, VALUE_OS);
			infoObject.put(KEY_VERSION, Utility.getAppVersionCode(mContext));
			infoObject.put(KEY_MAC, Utility.getMacAddress(mContext));
			infoObject.put(KEY_CHANNELID, Utility.getApplicationMetaData(
					mContext, KEY_META_CHANNEL_ID));
			infoObject.put(KEY_OSCODE, Utility.getSdkVersion());
			infoObject.put(KEY_TOKEN, Ipush.getDeviceId(mContext));
			infoObject.put(KEY_PLATFORM, Utility.getMobileName());
			infoObject.put(KEY_IP, Utility.getIpInfo());

			if (!TextUtils.isEmpty(mContact)) {
				infoObject.put(KEY_CONTACT, mContact);
			}
			infoObject.put(KEY_FEEDBACK, mFeedback);

			VShareApplication vShareApplication = (VShareApplication) (VShareActivity
					.getTopActivity()).getApplication();
			infoObject.put(KEY_PROVINCE_ID,
					vShareApplication.getLocationInfo().mId);

			params.add(new BasicNameValuePair(KEY_INFO, infoObject.toString()));
		} catch (Exception e) {
			if (DEBUG) {
				Log.w(TAG, e);
			}
		}
		return params;
	}

	@Override
	protected String getRequestUrl() {
		return "http://cmv.ifeng.com/Api/feedback";
	}

}
