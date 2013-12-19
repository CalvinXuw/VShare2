package com.ifeng.vshare.requestor;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

import com.ifeng.vshare.model.BaseVShareItem;
import com.ifeng.vshare.model.LectureDetailItem;

/**
 * 大讲堂详情数据获取model
 * 
 * @author Calvin
 * 
 */
public class LectureDetailRequestor extends BaseVShareRequestor {

	/** key id */
	private static final String KEY_ID = "id";

	/** 讲堂id */
	private int mId;

	/** 详情item */
	private LectureDetailItem mDetailItem;

	public LectureDetailRequestor(Context context, int id,
			OnModelProcessListener listener) {
		super(context, listener);
		mId = id;
		setAutoParseClass(LectureDetailItem.class);
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
		return "http://cmv.ifeng.com/Api/studyinfo";
	}

	@Override
	protected void handleResult(BaseVShareItem item) {
		mDetailItem = (LectureDetailItem) item;
	}

	@Override
	protected void handlePreloadResult(BaseVShareItem item) {
		handleResult(item);
	}

	/**
	 * 获取详情数据
	 * 
	 * @return
	 */
	public LectureDetailItem getDetailItem() {
		return mDetailItem;
	}

}
