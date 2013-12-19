package com.ifeng.vshare.requestor;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

import com.ifeng.vshare.model.BaseVShareItem;
import com.ifeng.vshare.model.VideoDetailItem;

/**
 * 视频详情数据获取model
 * 
 * @author Calvin
 * 
 */
public class VideoDetailRequestor extends BaseVShareRequestor {

	/** key id */
	private static final String KEY_ID = "id";

	/** 视频id */
	private int mId;

	/** 详情item */
	private VideoDetailItem mDetailItem;

	public VideoDetailRequestor(Context context, int id,
			OnModelProcessListener listener) {
		super(context, listener);
		mId = id;
		setAutoParseClass(VideoDetailItem.class);
	}

	@Override
	protected List<NameValuePair> getRequestHeaders() {
		return null;
	}

	@Override
	protected List<NameValuePair> getRequestParams() {
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair(KEY_ID, mId + ""));
		return params;
	}

	@Override
	protected String getRequestUrl() {
		return "http://i.ifeng.com/gotonevideo";
	}

	@Override
	protected void handleResult(BaseVShareItem item) {
		mDetailItem = (VideoDetailItem) item;
	}

	/**
	 * 获取详情数据
	 * 
	 * @return
	 */
	public VideoDetailItem getDetailItem() {
		return mDetailItem;
	}

}
