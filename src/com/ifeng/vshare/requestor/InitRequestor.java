package com.ifeng.vshare.requestor;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;

import com.ifeng.ipush.client.Ipush;
import com.ifeng.util.Utility;
import com.ifeng.util.logging.Log;
import com.ifeng.util.net.parser.AbstractIFJSONItem;
import com.ifeng.vshare.VShareActivity;
import com.ifeng.vshare.VShareApplication;
import com.ifeng.vshare.model.BaseVShareItem;
import com.ifeng.vshare.model.InitItem;

/**
 * 初始化接口requestor
 * 
 * @author Calvin
 * 
 */
public class InitRequestor extends BaseVShareRequestor {

	/** manifest中application层级参数，渠道号 */
	private static final String KEY_META_CHANNEL_ID = "channelId";

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

	/** value android系统 */
	private static final String VALUE_OS = "ANDROID";

	/**
	 * 构造
	 * 
	 * @param context
	 * @param listener
	 */
	public InitRequestor(Context context, OnModelProcessListener listener) {
		super(context, listener);
		mRequestDataCache.getDataCacheParams().cacheValidityTime = 0;
		setAutoParseClass(InitItem.class);
	}

	@Override
	protected void handleResult(BaseVShareItem item) {

	}

	@Override
	protected AbstractIFJSONItem handleUnparseResult(String result) {
		return null;

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
		return "http://cmv.ifeng.com/Api/register";
	}

}
