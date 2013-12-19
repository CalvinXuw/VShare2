package com.ifeng.vshare.requestor;

import android.content.Context;

import com.ifeng.util.net.requestor.AbstractRequestor;

/**
 * 抽象评论requestor，用于管理评论唯一key
 * 
 * @author Calvin
 * 
 */
public abstract class BaseIfengCommentRequestor extends AbstractRequestor {

	/** 域名 */
	private static final String DOMAIN_NAME = "http://cmv.ifeng.com";

	/** 评论唯一id */
	protected String mUniKey;

	/**
	 * 构造
	 * 
	 * @param context
	 * @param key
	 * @param listener
	 */
	public BaseIfengCommentRequestor(Context context, String key,
			OnModelProcessListener listener) {
		super(context, listener);

		if (key.contains("http")) {
			mUniKey = key;
		} else {
			mUniKey = DOMAIN_NAME + key;
		}
	}

}
