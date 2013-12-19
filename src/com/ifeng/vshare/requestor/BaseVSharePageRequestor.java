package com.ifeng.vshare.requestor;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.Looper;

import com.ifeng.util.logging.Log;
import com.ifeng.util.model.AbstractModel;
import com.ifeng.util.net.parser.AbstractIFItem;
import com.ifeng.util.net.parser.AbstractIFJSONItem;
import com.ifeng.vshare.model.BaseVShareItem;

/**
 * 负责分页请求的requestor
 * 
 * @author Calvin
 * 
 */
public abstract class BaseVSharePageRequestor extends BaseVShareRequestor {
	/** key 页码 */
	private static final String KEY_PAGE = "page";
	/** key 页容量 */
	private static final String KEY_PAGESIZE = "limit";
	/** 默认起始页码 */
	public static final int FIRSTPAGE = 1;
	/** 默认页容量 */
	private static final int PAGESIZE = 20;

	/** 当前分页索引 */
	private int mPageIndex = FIRSTPAGE;
	/** 分页请求页容量 */
	private int mPageSize = PAGESIZE;
	/** 列表数据源 */
	protected List<AbstractIFItem> mItems;
	/** 本次请求状态前的页码 */
	private int mLastStep;
	/** 根据最后一次请求，获取是否还有下一页数据 */
	private boolean mHasNext;

	/*
	 * modify by xuwei 2013-7-20 对mItems数据加入同步锁，防止在数据修改过程中导致ListAdapter的越界现象
	 */
	/** 数据修改保护锁 */
	protected Object mDataLock = new Object();

	/**
	 * 构造
	 * 
	 * @param context
	 * @param listener
	 */
	public BaseVSharePageRequestor(Context context, OnModelProcessListener listener) {
		super(context, listener);
		super.mOnModelProcessListener = new MiddleRequestorListener(
				super.mOnModelProcessListener);
		mItems = new LinkedList<AbstractIFItem>();
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
			if (getLastRequestResult() != VShareRequestResult.SUCCESS) {
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

	@Override
	public boolean hasPreloadCache() {
		mPageIndex = FIRSTPAGE;
		return super.hasPreloadCache();
	}

	/**
	 * 不要在PageRequestor中调用request
	 */
	@Override
	@Deprecated
	public void request() {
		// Do nothing
	}

	/**
	 * 不要在PageRequestor中调用reload
	 */
	@Override
	@Deprecated
	public void reload() {
		// Do nothing
	}

	/**
	 * 获取当前页码
	 * 
	 * @return
	 */
	public int getPageIndex() {
		return mPageIndex;
	}

	@Override
	protected final List<NameValuePair> getRequestParams() {
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair(KEY_PAGE, mPageIndex + ""));
		params.add(new BasicNameValuePair(KEY_PAGESIZE, mPageSize + ""));

		List<NameValuePair> paramsFromSubClass = getRequestParamsWithoutPagething();
		if (paramsFromSubClass != null) {
			params.addAll(paramsFromSubClass);
		}
		return params;
	}

	/**
	 * 需要子类requestor填写除去page相关参数之外的参数
	 * 
	 * @return
	 */
	protected abstract List<NameValuePair> getRequestParamsWithoutPagething();

	@Override
	protected final void handleResult(final BaseVShareItem item) {
		if (!(item instanceof PageItem)) {
			throw new IllegalArgumentException(
					"page requestor need process by PageItem");
		}

		// 在指定调用线程中进行耗时处理操作
		processPageResult((PageItem) item);

		// 在ui线程中进行数据同步
		Callable<Boolean> handlePageResultCallable = new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				PageItem pageItem = (PageItem) item;
				pageItem.mHasNextpage = (pageItem.mNextPageNum > 0);
				mHasNext = (pageItem.mNextPageNum > 0);

				if (mPageIndex == FIRSTPAGE) {
					mItems.clear();
				}

				if (pageItem.mItems != null) {
					mItems.addAll(pageItem.mItems);
				}

				handlePageResult(pageItem);
				return true;
			}
		};
		FutureTask<Boolean> futureTask = new FutureTask<Boolean>(
				handlePageResultCallable);

		// 非ui线程时，adapter数据的同步要放到ui线程中进行
		if (Thread.currentThread().getId() != Looper.getMainLooper()
				.getThread().getId()) {
			mHandler.post(futureTask);
		} else {
			futureTask.run();
		}

		try {
			futureTask.get();
		} catch (InterruptedException e) {
			if (DEBUG) {
				Log.e(TAG, e);
			}
		} catch (ExecutionException e) {
			if (DEBUG) {
				Log.e(TAG, e);
			}
		}

	}

	@Override
	protected final void handlePreloadResult(BaseVShareItem item) {
		handleResult(item);
	}

	/**
	 * 在{@link #handlePageResult(PageItem)}之前对于返回结果进行处理
	 * 
	 * @param item
	 */
	protected void processPageResult(PageItem item) {
		// do nothing
	}

	/**
	 * 需要子类处理pageitem数据
	 * 
	 * @param item
	 */
	protected abstract void handlePageResult(PageItem item);

	/**
	 * 子类提供对于数据的处理工作
	 * 
	 * @return
	 */
	public abstract List<?> getDataList();

	/**
	 * 是否还有下一页
	 * 
	 * @return
	 */
	public boolean isHasNext() {
		return mHasNext;
	}

	@Override
	protected final AbstractIFJSONItem handleUnparseResult(String result) {
		throw new IllegalArgumentException(
				"page requestor need process by auto parse");
	}

	/**
	 * PageItem为PageRequestor提供数据容器
	 * 
	 * @author Calvin
	 * 
	 */
	public abstract static class PageItem extends BaseVShareItem {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7919652040742163817L;

		/** key for mapping jsonarray */
		protected static final String KEY_ITEMS = "mItems";

		/** 剩余页数 */
		public int mNextPageNum;
		/** 是否还有下一页 */
		private boolean mHasNextpage;
		/** 列表数据 */
		public List<AbstractIFItem> mItems;

		/**
		 * 构造
		 */
		public PageItem() {
			addMappingRuleField("mNextPageNum", "info/nextpage");

			addDataArrayMapping();
		}

		/**
		 * 添加对数组数据的mapping
		 */
		public abstract void addDataArrayMapping();

		/**
		 * 是否还有下一页数据
		 * 
		 * @return
		 */
		public boolean isHasNextpage() {
			return mHasNextpage;
		}
	}
}
