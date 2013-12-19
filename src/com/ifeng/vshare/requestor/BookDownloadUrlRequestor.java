package com.ifeng.vshare.requestor;

import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;

import com.ifeng.util.net.parser.AbstractIFItem;
import com.ifeng.vshare.model.BookDownloadUrlItem;

/**
 * 实体电子书文件下载地址获取的requestor
 * 
 * @author Calvin
 * 
 */
public class BookDownloadUrlRequestor extends BaseIfengOpenbookRequestor {

	/** 下载地址 */
	private String mEpubUrl;

	public BookDownloadUrlRequestor(Context context, int bookId,
			OnModelProcessListener listener) {
		super(context, bookId, listener);
		setAutoParseClass(BookDownloadUrlItem.class);
	}

	@Override
	protected List<NameValuePair> getRequestHeaders() {
		return null;
	}

	@Override
	protected List<NameValuePair> getRequestParams() {
		return null;
	}

	@Override
	protected String getRequestUrl() {
		return "http://inf.read.ifeng.com/readInf/bookInfo.htm";
	}

	@Override
	protected void handleResult(AbstractIFItem item) {
		BookDownloadUrlItem requestorItem = (BookDownloadUrlItem) item;
		mEpubUrl = requestorItem.mEpubUrl;
	}

	/**
	 * 获取下载地址
	 * 
	 * @return
	 */
	public String getEpubUrl() {
		return mEpubUrl;
	}

}
