package com.ifeng.vshare.requestor;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

import com.ifeng.vshare.model.BaseVShareItem;
import com.ifeng.vshare.model.PhotosDetailItem;

/**
 * 图片详情数据获取model
 * 
 * @author Calvin
 * 
 */
public class PhotoDetailRequestor extends BaseVShareRequestor {

	/** key id */
	private static final String KEY_ID = "id";

	/** 图片id */
	private int mId;

	/** 详情item */
	private PhotosDetailItem mDetailItem;

	public PhotoDetailRequestor(Context context, int id,
			OnModelProcessListener listener) {
		super(context, listener);
		mId = id;
		setAutoParseClass(PhotosDetailItem.class);
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
		return "http://i.ifeng.com/gotonenewsimageinfo";
	}

	@Override
	protected void handleResult(BaseVShareItem item) {
		mDetailItem = (PhotosDetailItem) item;
	}

	/**
	 * 获取详情数据
	 * 
	 * @return
	 */
	public PhotosDetailItem getDetailItem() {
		return mDetailItem;
	}

}
