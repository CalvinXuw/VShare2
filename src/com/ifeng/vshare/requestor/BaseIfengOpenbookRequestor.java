package com.ifeng.vshare.requestor;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

import com.ifeng.util.Utility;
import com.ifeng.util.net.requestor.AbstractRequestor;
import com.ifeng.util.net.requestor.WebRequestTask.RequestType;

/**
 * 开卷接口requestor，抽象用于管理加密认证，以及约束get请求方式
 * 
 * @author Calvin
 * 
 */
public abstract class BaseIfengOpenbookRequestor extends AbstractRequestor {

	/** key parterid */
	private static final String KEY_PARTER_ID = "partnerId";
	/** key bookid */
	private static final String KEY_BOOK_ID = "bookId";
	/** value parterid */
	private static final String VALUE_PARTER_ID = "1006";

	/** key dev_key */
	private static final String KEY_DEV_KEY = "dev_key";
	/** key timestamp */
	private static final String KEY_TIMESTAMP = "timestamp";
	/** key sign */
	private static final String KEY_SIGN = "sign";

	/** 认证key */
	private static final String KEY = "jcos92u4jd78s7t2gj1vwaw523kaha72k";
	/** 认证key */
	private static final String DEV_KEY = "ifeng";

	/** book id */
	protected int mBookId;

	public BaseIfengOpenbookRequestor(Context context, int bookId,
			OnModelProcessListener listener) {
		super(context, listener);
		mBookId = bookId;
		setRequestType(RequestType.GET);
	}

	@Override
	protected List<NameValuePair> getExtraParams() {
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair(KEY_PARTER_ID, VALUE_PARTER_ID));
		params.add(new BasicNameValuePair(KEY_BOOK_ID, mBookId + ""));

		// 加密认证
		long timestamp = System.currentTimeMillis();
		StringBuffer paramsSb = new StringBuffer();
		paramsSb.append(mBookId).append("#").append(DEV_KEY).append("#")
				.append(VALUE_PARTER_ID).append("#").append(timestamp)
				.append("#").append(KEY);

		params.add(new BasicNameValuePair(KEY_DEV_KEY, DEV_KEY));
		params.add(new BasicNameValuePair(KEY_TIMESTAMP, timestamp + ""));
		params.add(new BasicNameValuePair(KEY_SIGN, Utility.getMD5(
				paramsSb.toString()).toLowerCase(Locale.US)));

		return params;
	}
}
