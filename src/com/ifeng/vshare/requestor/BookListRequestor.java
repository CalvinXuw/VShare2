package com.ifeng.vshare.requestor;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.ifeng.util.net.parser.AbstractIFItem;
import com.ifeng.vshare.book.BookDownloadManager;
import com.ifeng.vshare.database.dao.BooksManageDao;
import com.ifeng.vshare.database.dao.BooksManageDao.BooksTaskItem;
import com.ifeng.vshare.model.BookListItem;
import com.ifeng.vshare.model.BookListItem.BookItem;

/**
 * 书籍页面model，其中包含了对于网络请求的书籍列表，以及本地数据库管理的本地书库的处理
 * 
 * @author Calvin
 * 
 */
public class BookListRequestor extends BaseVSharePageRequestor {

	/** key cid */
	private static final String KEY_CID = "cid";
	/** value cid */
	private static final String VALUE_CID = "17932";

	/** 本地书籍列表 */
	private List<BooksTaskItem> mBooksTaskItems;
	/** bookdb数据变化后的ui回调 */
	private OnDataHasChangedListener mOnDataHasChangedListener;

	/** 去重后的数据 */
	private List<BookItem> mReturnBooksItem;

	/** 图书列表状态监听 */
	private DownloadBooksChangedReceiver mDownloadBooksChangedReceiver;

	/**
	 * 构造
	 * 
	 * @param context
	 * @param listener
	 * @param dataHasChangedListener
	 */
	public BookListRequestor(Context context, OnModelProcessListener listener,
			OnDataHasChangedListener dataHasChangedListener) {
		super(context, listener);
		mOnDataHasChangedListener = dataHasChangedListener;
		setAutoParseClass(BookListItem.class);
		mReturnBooksItem = new LinkedList<BookItem>();

		// 初始化数据
		mBooksTaskItems = new LinkedList<BooksTaskItem>();

		mDownloadBooksChangedReceiver = new DownloadBooksChangedReceiver();
		IntentFilter broadcastFilter = new IntentFilter(
				BookDownloadManager.NOTIFICATION_BOOKSTASK_LIST);
		mContext.registerReceiver(mDownloadBooksChangedReceiver,
				broadcastFilter);

		BookDownloadManager.refreshCurrentTask();
		mBooksTaskItems = BookDownloadManager.getCurrentTasks();
	}

	@Override
	public void cancel() {
		mContext.unregisterReceiver(mDownloadBooksChangedReceiver);
		super.cancel();
	}

	/**
	 * 图书列表变化监听
	 * 
	 * @author Calvin
	 * 
	 */
	private class DownloadBooksChangedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context mContext, Intent mIntent) {
			if (mIntent.getAction().equals(
					BookDownloadManager.NOTIFICATION_BOOKSTASK_LIST)) {
				mBooksTaskItems = BookDownloadManager.getCurrentTasks();
				if (mOnDataHasChangedListener != null) {
					adjustRemoteData();
					mOnDataHasChangedListener.onChanged();
				}
			}
		}
	}

	@Override
	protected List<NameValuePair> getRequestParamsWithoutPagething() {
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair(KEY_CID, VALUE_CID));
		return params;
	}

	@Override
	protected void handlePageResult(PageItem item) {
		adjustRemoteData();
	}

	@Override
	@Deprecated
	public List<?> getDataList() {
		// 建议直接使用getRemoteBookItems();
		return mItems;
	}

	/**
	 * 矫正远端数据
	 */
	public void adjustRemoteData() {
		synchronized (mDataLock) {
			mReturnBooksItem.clear();
			for (AbstractIFItem ifengItem : mItems) {
				BookItem booksItem = (BookItem) ifengItem;
				if (!BooksManageDao.getInstance(mContext)
						.queryBookExistByBookId(booksItem.mId)) {
					mReturnBooksItem.add(booksItem);
				}
			}
		}
	}

	/**
	 * 获取本地图书数据
	 * 
	 * @return
	 */
	public List<BooksTaskItem> getLocalBookItems() {
		return mBooksTaskItems;
	}

	/**
	 * 获取在线图书数据
	 * 
	 * @return
	 */
	public List<BookItem> getRemoteBookItems() {
		synchronized (mDataLock) {
			return mReturnBooksItem;
		}
	}

	/**
	 * 添加一本书籍
	 * 
	 * @param item
	 */
	public void addTaskBook(BookItem item) {
		BookDownloadManager.addTaskBook(item);
	}

	/**
	 * 下载任务重试
	 * 
	 * @param item
	 */
	public void retryTaskBook(BooksTaskItem item) {
		BookDownloadManager.retryTaskBook(item);
	}

	/**
	 * 删除下载任务
	 * 
	 * @param item
	 */
	public void deleteTaskBook(BooksTaskItem item) {
		BookDownloadManager.deleteTaskBook(item);
	}

	@Override
	protected List<NameValuePair> getRequestHeaders() {
		return null;
	}

	@Override
	protected String getRequestUrl() {
		return "http://i.ifeng.com/gotonebook";
	}

	public interface OnDataHasChangedListener {
		public void onChanged();
	}
}
