package com.ifeng.vshare.requestor;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

import com.ifeng.ipush.client.Ipush;
import com.ifeng.vshare.model.BaseVShareItem;
import com.ifeng.vshare.model.LectureInterestItem;

/**
 * 大讲堂感兴趣model
 * 
 * @author Calvin
 * 
 */
public class LectureInterestRequestor extends BaseVShareRequestor {

	/** key id */
	private static final String KEY_ID = "id";
	/** key pushid */
	private static final String KEY_PUSH_DEVICE_ID = "key";

	/** 讲堂id */
	private int mId;

	/**
	 * 构造
	 * 
	 * @param context
	 * @param id
	 * @param listener
	 */
	public LectureInterestRequestor(Context context, int id,
			OnModelProcessListener listener) {
		super(context, listener);
		mId = id;
		setAutoParseClass(LectureInterestItem.class);
	}

	@Override
	protected List<NameValuePair> getRequestHeaders() {
		return null;
	}

	@Override
	protected List<NameValuePair> getRequestParams() {
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair(KEY_ID, mId + ""));
		params.add(new BasicNameValuePair(KEY_PUSH_DEVICE_ID, Ipush
				.getDeviceId(mContext)));
		return params;
	}

	@Override
	protected String getRequestUrl() {
		return "http://cmv.ifeng.com/Api/setstudy";
	}

	@Override
	protected void handleResult(BaseVShareItem item) {
	}

}
