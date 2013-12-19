package com.ifeng.vshare.requestor;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

import com.ifeng.vshare.model.BaseVShareItem;
import com.ifeng.vshare.model.DiscountDetailItem;

/**
 * 商盟特惠数据获取model
 * 
 * @author Calvin
 * 
 */
public class DiscountDetailRequestor extends BaseVShareRequestor {

	/** key id */
	private static final String KEY_ID = "id";

	/** 店铺id */
	private int mId;

	/** 详情item */
	private DiscountDetailItem mDetailItem;

	public DiscountDetailRequestor(Context context, int id,
			OnModelProcessListener listener) {
		super(context, listener);
		mId = id;
		setAutoParseClass(DiscountDetailItem.class);
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
		return "http://cmv.ifeng.com/Api/privilegeinfo";
	}

	@Override
	protected void handleResult(BaseVShareItem item) {
		mDetailItem = (DiscountDetailItem) item;
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
	public DiscountDetailItem getDetailItem() {
		return mDetailItem;
	}

}
