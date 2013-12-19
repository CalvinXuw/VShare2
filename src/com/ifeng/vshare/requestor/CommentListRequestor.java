package com.ifeng.vshare.requestor;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

import com.ifeng.util.model.AbstractModel;
import com.ifeng.util.net.parser.AbstractIFItem;
import com.ifeng.vshare.model.CommentListItem;
import com.ifeng.vshare.model.CommentListItem.CommentItem;

/**
 * 通用评论获取requestor
 * 
 * @author Calvin
 * 
 */
public class CommentListRequestor extends BaseIfengCommentRequestor {

	/** key 评论接口类型 */
	private static final String KEY_JOB = "job";
	/** key 数据返回格式 */
	private static final String KEY_FORMAT = "format";
	/** key pageindex */
	private static final String KEY_PAGEINDEX = "p";
	/** key pagesize */
	private static final String KEY_PAGESIZE = "pagesize";
	/** key 评论唯一鉴别id */
	private static final String KEY_UNIKEY = "docurl";
	/** value 评论接口类型，获取评论 */
	private static final String VALUE_JOB = "1";
	/** value 数据格式json */
	private static final String VALUE_FORMAT = "json";

	/** 默认起始页码 */
	public static final int FIRSTPAGE = 1;
	/** 默认页容量 */
	private static final int PAGESIZE = 20;

	/** 当前分页索引 */
	private int mPageIndex = FIRSTPAGE;
	/** 分页请求页容量 */
	private int mPageSize = PAGESIZE;
	/** 列表数据源 */
	protected List<CommentItem> mItems;
	/** 本次请求状态前的页码 */
	private int mLastStep;
	/** 根据最后一次请求，获取是否还有下一页数据 */
	private boolean mHasNext;
	/** 最后一次请求是否成功获取到数据 */
	private boolean mLastRequestSuccess = true;
	/** 参与人数 */
	private int mJoinCount;
	/** 评论条数 */
	private int mCommentCount;

	/**
	 * 构造，需要传入大的类型和当前资源id，如：type=news，id=56153
	 * 
	 * @param context
	 * @param type
	 * @param id
	 * @param listener
	 */
	public CommentListRequestor(Context context, String key,
			OnModelProcessListener listener) {
		super(context, key, listener);
		super.mOnModelProcessListener = new MiddleRequestorListener(
				super.mOnModelProcessListener);
		mItems = new LinkedList<CommentItem>();
		setAutoParseClass(CommentListItem.class);
	}

	/**
	 * 中间处理层，处理在异常返还或请求失败时，对于pageindex参数的回退，保证调用者在仅进行
	 * {@link BaseVSharePageRequestor#requestNextPage()}既可获取正确数据。
	 */
	private class MiddleRequestorListener implements OnModelProcessListener {

		/** 真实回调 */
		private OnModelProcessListener mRealRequestorListener;

		/**
		 * 构造方法
		 * 
		 * @param requestorListener
		 */
		public MiddleRequestorListener(OnModelProcessListener requestorListener) {
			mRealRequestorListener = requestorListener;
		}

		@Override
		public void onSuccess(AbstractModel requestor) {
			// 如果非网络返回错误，而是服务端返回错误，同样需要进行回退参数
			if (!mLastRequestSuccess) {
				backToLastStep();
			}
			if (mRealRequestorListener != null) {
				mRealRequestorListener.onSuccess(requestor);
			}
		}

		@Override
		public void onFailed(AbstractModel requestor, int errorCode) {
			backToLastStep();
			if (mRealRequestorListener != null) {
				mRealRequestorListener.onFailed(requestor, errorCode);
			}
		}

		@Override
		public void onProgress(AbstractModel model, int progress) {
			if (mRealRequestorListener != null) {
				mRealRequestorListener.onProgress(model, progress);
			}
		}

		/**
		 * 返回到上次请求的页面参数
		 */
		private void backToLastStep() {
			mPageIndex = mLastStep;
		}
	};

	/**
	 * 请求下一页数据
	 */
	public void requestNextPage() {
		mLastStep = mPageIndex;
		mPageIndex++;
		super.request();
	}

	/**
	 * 请求第一页数据
	 */
	public void requestFirstPage() {
		mLastStep = mPageIndex;
		mPageIndex = FIRSTPAGE;
		super.request();
	}

	/**
	 * 是否还有下一页
	 * 
	 * @return
	 */
	public boolean isHasNext() {
		return mHasNext;
	}

	/**
	 * 不要在GetCommentRequestor中调用request
	 */
	@Override
	@Deprecated
	public void request() {
		// Do nothing
	}

	/**
	 * 不要在GetCommentRequestor中调用reload
	 */
	@Override
	@Deprecated
	public void reload() {
		// Do nothing
	}

	/**
	 * 获取评论
	 * 
	 * @return
	 */
	public List<CommentItem> getComments() {
		return mItems;
	}

	/**
	 * 获取总评论条数
	 * 
	 * @return
	 */
	public int getCommentCount() {
		return mCommentCount;
	}

	/**
	 * 获取总参与人数
	 * 
	 * @return
	 */
	public int getJoinCount() {
		return mJoinCount;
	}

	/**
	 * 获取当前页码
	 * 
	 * @return
	 */
	public int getPageIndex() {
		return mPageIndex;
	}

	/**
	 * 返回上次请求是否成功
	 * 
	 * @return
	 */
	public boolean getLastResultSuccess() {
		return mLastRequestSuccess;

	}

	@Override
	protected List<NameValuePair> getRequestHeaders() {
		return null;
	}

	@Override
	protected List<NameValuePair> getRequestParams() {
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair(KEY_JOB, VALUE_JOB));
		params.add(new BasicNameValuePair(KEY_FORMAT, VALUE_FORMAT));
		params.add(new BasicNameValuePair(KEY_PAGEINDEX, mPageIndex + ""));
		params.add(new BasicNameValuePair(KEY_PAGESIZE, mPageSize + ""));
		params.add(new BasicNameValuePair(KEY_UNIKEY, mUniKey));
		return params;
	}

	@Override
	protected List<NameValuePair> getExtraParams() {
		return null;
	}

	@Override
	protected String getRequestUrl() {
		return "http://comment.ifeng.com/get.php";
	}

	@Override
	protected void handleResult(AbstractIFItem item) {
		CommentListItem requestorItem = (CommentListItem) item;

		// 下拉刷新或首次请求时清空数据
		if (mPageIndex == FIRSTPAGE) {
			mItems.clear();
		}

		if (requestorItem.mComments != null) {
			mItems.addAll(requestorItem.mComments);
		}

		mCommentCount = requestorItem.mCommentCount;
		mJoinCount = requestorItem.mJoinCount;

		// 根据评论数判断是否还有下一页
		if (mItems.size() >= requestorItem.mCommentCount) {
			mHasNext = false;
		} else {
			mHasNext = true;
		}

	}
}
