package com.ifeng.vshare.requestor;

import java.util.List;

import org.apache.http.NameValuePair;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.ifeng.util.net.parser.AbstractIFItem;
import com.ifeng.vshare.book.BookDownloadManager;
import com.ifeng.vshare.database.dao.BooksManageDao.BooksTaskItem;
import com.ifeng.vshare.model.BookChapterListItem;
import com.ifeng.vshare.model.BookChapterListItem.RemoteChapterItem;
import com.ifeng.vshare.model.BookListItem.BookItem;

/**
 * 获取未下载图书章节列表
 * 
 * @author qianzy
 * @time 2013-7-04 09:45:21
 */
public class BookChapterRequestor extends BaseIfengOpenbookRequestor {

	/** 章节列表 */
	private List<RemoteChapterItem> mChapterItems;

	/** bookdb数据变化后的ui回调 */
	private OnDataHasChangedListener mOnDataHasChangedListener;

	/** 图书列表状态监听 */
	private DownloadBooksChangedReceiver mDownloadBooksChangedReceiver;

	/** 图书下载任务item */
	private BooksTaskItem mBooksTaskItem;

	/**
	 * 构造
	 * 
	 * @param context
	 * @param bookId
	 * @param listener
	 */
	public BookChapterRequestor(Context context, int bookId,
			OnModelProcessListener listener,
			OnDataHasChangedListener dataHasChangedListener) {
		super(context, bookId, listener);
		mOnDataHasChangedListener = dataHasChangedListener;
		setAutoParseClass(BookChapterListItem.class);

		mDownloadBooksChangedReceiver = new DownloadBooksChangedReceiver();
		IntentFilter broadcastFilter = new IntentFilter(
				BookDownloadManager.NOTIFICATION_BOOKSTASK_ID);
		mContext.registerReceiver(mDownloadBooksChangedReceiver,
				broadcastFilter);
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
					BookDownloadManager.NOTIFICATION_BOOKSTASK_ID)
					&& mIntent.getExtras().getInt(BookDownloadManager.BOOKID) == mBookId) {
				mBooksTaskItem = (BooksTaskItem) mIntent.getExtras()
						.getSerializable(BookDownloadManager.BOOKTASK);
				if (mOnDataHasChangedListener != null) {
					mOnDataHasChangedListener.onChanged();
				}
			}
		}
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
		return "http://inf.read.ifeng.com/readInf/chapterList.htm";
	}

	@Override
	protected void handleResult(AbstractIFItem item) {
		BookChapterListItem requestItem = (BookChapterListItem) item;
		mChapterItems = requestItem.getChapters();
	}

	public List<RemoteChapterItem> getChapters() {
		return mChapterItems;
	}

	/**
	 * 获取本地图书数据
	 * 
	 * @return
	 */
	public BooksTaskItem getLocalBookItem() {
		return mBooksTaskItem;
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

	public interface OnDataHasChangedListener {
		public void onChanged();
	}
}
