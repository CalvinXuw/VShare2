package com.ifeng.vshare.requestor;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.text.TextUtils;

import com.ifeng.util.Utility;
import com.ifeng.util.logging.Log;
import com.ifeng.util.net.parser.AbstractIFItem;

/**
 * 发表评论的requestor
 * 
 * @author Calvin
 * 
 */
public class CommentPublishRequestor extends BaseIfengCommentRequestor {

	/** key 评论接口类型 */
	private static final String KEY_COMMENT_NAME = "docName";
	/** key 数据返回格式 */
	private static final String KEY_COMMENT_CONTENT = "content";
	/** key pageindex */
	private static final String KEY_COMMENT_ID = "docUrl";
	/** key ip */
	private static final String KEY_IP = "ip";

	/** 评论标题 */
	private String mTitle;
	/** 评论内容 */
	private String mComment;

	/** 评论结果 */
	private boolean mCommentResult;

	public CommentPublishRequestor(Context context, String key,
			OnModelProcessListener listener) {
		super(context, key, listener);
	}

	/**
	 * 发送评论
	 * 
	 * @param title
	 * @param comment
	 */
	public void sendAComment(String title, String comment) {
		mTitle = title;
		mComment = comment;
		mCommentResult = false;
		super.request();
	}

	@Override
	@Deprecated
	public void request() {
		// do nothing
	}

	@Override
	@Deprecated
	public void reload() {
		// do nothing
	}

	@Override
	protected List<NameValuePair> getRequestHeaders() {
		return null;
	}

	@Override
	protected List<NameValuePair> getRequestParams() {
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair(KEY_COMMENT_NAME, mTitle));
		params.add(new BasicNameValuePair(KEY_COMMENT_CONTENT, mComment));
		params.add(new BasicNameValuePair(KEY_COMMENT_ID, mUniKey));
		params.add(new BasicNameValuePair(KEY_IP, Utility.getIpInfo()));
		return params;
	}

	@Override
	protected List<NameValuePair> getExtraParams() {
		return null;
	}

	@Override
	protected String getRequestUrl() {
		return "http://comment.ifeng.com/post.php";
	}

	@Override
	protected void handleResult(AbstractIFItem item) {
		// do nothing
	}

	@Override
	protected AbstractIFItem handleUnparseResult(String result) {
		if (DEBUG) {
			Log.d(TAG, "send a comment finished");
			Log.d(TAG, "server feedback: " + result);
		}

		if (TextUtils.isEmpty(result) || result.contains("html")
				|| !result.contains("502 Bad Gateway")) {
			mCommentResult = true;
		}

		return super.handleUnparseResult(result);
	}

	/**
	 * 获取评论结果
	 * 
	 * @return
	 */
	public boolean getCommentResult() {
		return mCommentResult;
	}
}
