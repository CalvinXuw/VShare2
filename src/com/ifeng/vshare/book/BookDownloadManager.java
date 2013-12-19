package com.ifeng.vshare.book;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.text.TextUtils;
import android.util.SparseArray;
import android.widget.Toast;

import com.ifeng.util.download.DownloadInfo;
import com.ifeng.util.download.DownloadManager;
import com.ifeng.util.download.Downloads;
import com.ifeng.util.model.AbstractModel;
import com.ifeng.util.model.AbstractModel.OnModelProcessListener;
import com.ifeng.vshare.R;
import com.ifeng.vshare.database.BooksDBHelper;
import com.ifeng.vshare.database.BooksProvider;
import com.ifeng.vshare.database.dao.BooksManageDao;
import com.ifeng.vshare.database.dao.BooksManageDao.BooksTaskItem;
import com.ifeng.vshare.database.dao.BooksReadChapterDao;
import com.ifeng.vshare.database.dao.BooksReadProgressDao;
import com.ifeng.vshare.model.BookListItem.BookItem;
import com.ifeng.vshare.requestor.BookDownloadUrlRequestor;

/**
 * 图书下载管理类，其中包括完成对图书下载地址的获取以及图书epub文件的解析工作
 * 
 * @author Calvin
 * 
 */
public class BookDownloadManager {

	/** 单一id的图书任务状态变化通知 */
	public static final String NOTIFICATION_BOOKSTASK_ID = "com.ifeng.vhsare.booksmanager.id";
	/** 图书列表更新状态变化通知 */
	public static final String NOTIFICATION_BOOKSTASK_LIST = "com.ifeng.vhsare.booksmanager.list";
	/** key 图书id */
	public static final String BOOKID = "id";
	/** key 图书task */
	public static final String BOOKTASK = "task";

	/** 静态实例 */
	private static BookDownloadManager sBooksDownloadManager;
	/** 保护异常 */
	private static NullPointerException sUnInitException = new NullPointerException(
			"the BooksDownloadManager have to be inited befor use it");

	/** context */
	private Context mContext;

	/** 管理epub地址获取requestor的sparsearray */
	private SparseArray<BookDownloadUrlRequestor> mUrlRequestorSparseArray;
	/** 管理epub解析analyser的sparsearray */
	private SparseArray<BookEpubAnalyser> mEpubAnalyserSparseArray;

	/** 数据库变化监听 bookdb */
	private BooksObserver mBooksObserver;
	/** 数据库变化监听 downloaddb */
	private DownloadsObserver mDownloadsObserver;

	/** downloaddb dao */
	private DownloadManager mDownloadManager;

	/** 本地书籍列表 */
	private LinkedList<BooksTaskItem> mBooksTaskItems;

	/**
	 * 初始化下载服务
	 * 
	 * @param context
	 */
	public static void init(Context context) {
		if (sBooksDownloadManager == null) {
			synchronized (BookDownloadManager.class) {
				sBooksDownloadManager = new BookDownloadManager(context);
			}
		}
	}

	/**
	 * 停止图书下载服务
	 */
	public static void release() {
		if (sBooksDownloadManager != null) {
			synchronized (sBooksDownloadManager) {
				sBooksDownloadManager.cancel();
				sBooksDownloadManager = null;
			}
		}
	}

	/**
	 * 添加一本书籍
	 * 
	 * @param item
	 */
	public static void addTaskBook(BookItem item) {
		if (sBooksDownloadManager != null) {
			sBooksDownloadManager.addTaskBookInternal(item);
		} else {
			throw sUnInitException;
		}
	}

	/**
	 * 下载任务重试
	 * 
	 * @param item
	 */
	public static void retryTaskBook(BooksTaskItem item) {
		if (sBooksDownloadManager != null) {
			sBooksDownloadManager.retryTaskBookInternal(item);
		} else {
			throw sUnInitException;
		}
	}

	/**
	 * 删除下载任务
	 * 
	 * @param item
	 */
	public static void deleteTaskBook(BooksTaskItem item) {
		if (sBooksDownloadManager != null) {
			sBooksDownloadManager.deleteTaskBookInternal(item);
		} else {
			throw sUnInitException;
		}
	}

	/**
	 * 刷新当前任务列表
	 */
	public static void refreshCurrentTask() {
		if (sBooksDownloadManager != null) {
			sBooksDownloadManager.updateDownloadDbdata();
			sBooksDownloadManager.updateBooksDbData();
		} else {
			throw sUnInitException;
		}
	}

	/**
	 * 获取本地任务列表
	 * 
	 * @return
	 */
	public static synchronized List<BooksTaskItem> getCurrentTasks() {
		if (sBooksDownloadManager != null) {
			return sBooksDownloadManager.mBooksTaskItems;
		} else {
			throw sUnInitException;
		}
	}

	/**
	 * 获取指定id的图书任务
	 * 
	 * @param bookId
	 * @return
	 */
	public static BooksTaskItem getTaskById(int bookId) {
		if (sBooksDownloadManager != null) {
			for (BooksTaskItem booksTaskItem : sBooksDownloadManager.mBooksTaskItems) {
				if (booksTaskItem.mBookId == bookId) {
					return booksTaskItem;
				}
			}
			return null;
		} else {
			throw sUnInitException;
		}

	}

	/**
	 * 添加一本书籍
	 * 
	 * @param item
	 */
	private void addTaskBookInternal(BookItem item) {
		BooksManageDao.getInstance(mContext).insertBook(item);
	}

	/**
	 * 下载任务重试
	 * 
	 * @param item
	 */
	private void retryTaskBookInternal(BooksTaskItem item) {
		BooksManageDao.getInstance(mContext).updateRetryStatus(item.mBookId);
	}

	/**
	 * 删除下载任务
	 * 
	 * @param item
	 */
	private void deleteTaskBookInternal(BooksTaskItem item) {
		BooksManageDao.getInstance(mContext).deleteByBookId(item.mBookId);
		BooksReadChapterDao.getInstance(mContext).deleteChapterRecordByBookId(
				item.mBookId);
		BooksReadProgressDao.getInstance(mContext).deleteProgressByBookId(
				item.mBookId);

		if (item.mStatus == BooksDBHelper.STATUS_COMPLETED
				|| item.mStatus == BooksDBHelper.STATUS_DOWNLOADING
				|| item.mStatus == BooksDBHelper.STATUS_DOWNLOADCOMPLETED
				|| item.mStatus == BooksDBHelper.STATUS_PARSE) {
			mDownloadManager.remove(item.mDownloadId);
		}

		BookDownloadUrlRequestor urlRequestor = mUrlRequestorSparseArray
				.get(item.mBookId);
		if (urlRequestor != null) {
			urlRequestor.cancel();
		}

		BookEpubAnalyser analyser = mEpubAnalyserSparseArray.get(item.mBookId);
		if (analyser != null) {
			analyser.cancel();
		}
	}

	/**
	 * 构造
	 * 
	 * @param context
	 */
	private BookDownloadManager(Context context) {
		mContext = context;

		// 初始化数据
		mBooksTaskItems = new LinkedList<BooksTaskItem>();
		mUrlRequestorSparseArray = new SparseArray<BookDownloadUrlRequestor>();
		mEpubAnalyserSparseArray = new SparseArray<BookEpubAnalyser>();

		mDownloadManager = new DownloadManager(mContext.getContentResolver(),
				mContext.getPackageName());

		mBooksObserver = new BooksObserver();
		mDownloadsObserver = new DownloadsObserver();
		// 注册observer
		mContext.getContentResolver().registerContentObserver(
				Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI, true,
				mDownloadsObserver);
		mContext.getContentResolver().registerContentObserver(
				BooksProvider.BOOKS_MANAGER_URI, true, mBooksObserver);
	}

	/**
	 * 取消当前尚未完成的任务
	 */
	private void cancel() {
		// 清除尚未完成的请求任务
		for (int i = 0; i < mUrlRequestorSparseArray.size(); i++) {
			BookDownloadUrlRequestor requestor = mUrlRequestorSparseArray
					.get(mUrlRequestorSparseArray.keyAt(i));
			if (requestor != null) {
				requestor.cancel();
			}
		}
		// 清楚尚未解析完成的任务
		for (int i = 0; i < mEpubAnalyserSparseArray.size(); i++) {
			BookEpubAnalyser analyser = mEpubAnalyserSparseArray
					.get(mEpubAnalyserSparseArray.keyAt(i));
			if (analyser != null) {
				analyser.cancel();
			}
		}

		// 注销observer
		mContext.getContentResolver().unregisterContentObserver(mBooksObserver);
		mContext.getContentResolver().unregisterContentObserver(
				mDownloadsObserver);
	}

	/**
	 * 更新bookdb数据，通知ui回调刷新界面
	 */
	private synchronized void updateBooksDbData() {
		BooksManageDao booksManageDao = BooksManageDao.getInstance(mContext);
		mBooksTaskItems.clear();
		mBooksTaskItems.addAll(booksManageDao.queryAllBooks());
		// 遍历任务，处理各个状态
		for (BooksTaskItem taskItem : mBooksTaskItems) {
			// 如果为尚未获取到url的状态，且任务队列中不存在相同任务
			if (taskItem.mStatus == BooksDBHelper.STATUS_NOURL) {
				if (mUrlRequestorSparseArray.get(taskItem.mBookId) == null) {
					BookDownloadUrlRequestor requestor = new BookDownloadUrlRequestor(
							mContext, taskItem.mBookId,
							new DownloadUrlRequestorListener(taskItem.mBookId,
									taskItem.mBookName));
					mUrlRequestorSparseArray.put(taskItem.mBookId, requestor);
					requestor.request();
				}
			}

			// 如果当前url已经获取，且尚未加入到下载队列之中
			if (taskItem.mStatus == BooksDBHelper.STATUS_READY_TO_DOWNLOAD) {
				long downloadId = mDownloadManager.enqueueBookDownloadTask(
						taskItem.mEpubUrl, taskItem.mBookName,
						taskItem.mBookName + "_" + taskItem.mAuthor, false);
				BooksManageDao.getInstance(mContext).updateDownloadIdByBookId(
						taskItem.mBookId, downloadId);
			}

			// 如果当前下载已经完成，且尚未加入到解析任务
			if (taskItem.mStatus == BooksDBHelper.STATUS_DOWNLOADCOMPLETED) {
				if (mEpubAnalyserSparseArray.get(taskItem.mBookId) == null) {
					BookEpubAnalyser analyser = new BookEpubAnalyser(mContext,
							taskItem, new EpubAnalyseListener(taskItem.mBookId,
									taskItem.mBookName));
					mEpubAnalyserSparseArray.put(taskItem.mBookId, analyser);
					analyser.analyse();
					BooksManageDao.getInstance(mContext).updateParseStatus(
							taskItem.mBookId);
				}
			}

			// 修正解析状态
			if (taskItem.mStatus == BooksDBHelper.STATUS_PARSE
					&& mEpubAnalyserSparseArray.get(taskItem.mBookId) == null) {
				BooksManageDao.getInstance(mContext).updateProgress(
						taskItem.mBookId, 100, true);
			}

			// 检验文件是否存在
			if (taskItem.mStatus == BooksDBHelper.STATUS_COMPLETED) {
				DownloadInfo downloadInfo = mDownloadManager.getDownloadById(
						mContext, taskItem.mDownloadId);
				if (downloadInfo == null) {
					// 数据丢失，重新下载
					BooksManageDao.getInstance(mContext).updateErrorStatus(
							taskItem.mBookId);
				} else if (!new File(downloadInfo.mFileName).exists()
						|| !new File(downloadInfo.mFileName
								+ BookEpubAnalyser.UNZIP_DIR_TAG).exists()) {
					// 下载文件被删除，重新下载
					BooksManageDao.getInstance(mContext).updateErrorStatus(
							taskItem.mBookId);
				} else {
					// 记录配置文件标识
					taskItem.mManifestFileName = downloadInfo.mFileName
							+ BookEpubAnalyser.UNZIP_DIR_TAG + File.separator
							+ BookEpubAnalyser.MANIFEST_FILENAME;
					if (!new File(taskItem.mManifestFileName).exists()) {
						// 配置文件丢失
						BooksManageDao.getInstance(mContext).updateErrorStatus(
								taskItem.mBookId);
					}
				}
			}

			Intent broadCastIntent = new Intent(NOTIFICATION_BOOKSTASK_ID);
			broadCastIntent.putExtra(BOOKID, taskItem.mBookId);
			broadCastIntent.putExtra(BOOKTASK, taskItem);
			mContext.sendBroadcast(broadCastIntent);
		}

		mContext.sendBroadcast(new Intent(NOTIFICATION_BOOKSTASK_LIST));
	}

	/**
	 * 从downloaddb更新数据到bookdb
	 */
	private synchronized void updateDownloadDbdata() {
		for (BooksTaskItem taskItem : mBooksTaskItems) {
			// 循环下载中的BooksTaskItem
			if (taskItem.mStatus == BooksDBHelper.STATUS_DOWNLOADING) {
				DownloadInfo downloadInfo = mDownloadManager.getDownloadById(
						mContext, taskItem.mDownloadId);
				if (downloadInfo == null) {
					// 数据丢失，重新下载
					BooksManageDao.getInstance(mContext).updateErrorStatus(
							taskItem.mBookId);
					continue;
				}
				// 更新进度
				BooksManageDao.getInstance(mContext).updateProgress(
						taskItem.mBookId,
						(int) (downloadInfo.mCurrentBytes
								/ (float) downloadInfo.mTotalBytes * 100),
						false);
				// 如果下载完成，更改状态
				if (Downloads.isStatusSuccess(downloadInfo.mStatus)) {
					BooksManageDao.getInstance(mContext).updateProgress(
							taskItem.mBookId, 100, true);
					// 如果下载失败，更改状态
				} else if (Downloads.isStatusError(downloadInfo.mStatus)) {
					BooksManageDao.getInstance(mContext).updateErrorStatus(
							taskItem.mBookId);
				}
			}
		}
	}

	/**
	 * bookdatabase监听，更新界面
	 * 
	 * @author Calvin
	 * 
	 */
	private class BooksObserver extends ContentObserver {

		public BooksObserver() {
			super(new Handler(mContext.getMainLooper()));
		}

		@Override
		public void onChange(boolean selfChange) {
			updateBooksDbData();
		}
	}

	/**
	 * 处理url获取requestor的返回结果
	 * 
	 * @author Calvin
	 * 
	 */
	private class DownloadUrlRequestorListener implements
			OnModelProcessListener {

		/** book id */
		private int mBookId;
		/** book name */
		private String mBookName;

		/**
		 * 构造
		 * 
		 * @param bookId
		 */
		public DownloadUrlRequestorListener(int bookId, String bookName) {
			mBookId = bookId;
			mBookName = bookName;
		}

		@Override
		public void onSuccess(AbstractModel requestor) {
			BookDownloadUrlRequestor urlRequestor = (BookDownloadUrlRequestor) requestor;
			// url 地址验证
			if (!TextUtils.isEmpty(urlRequestor.getEpubUrl())
					&& !urlRequestor.getEpubUrl().equals("null")) {
				BooksManageDao.getInstance(mContext).updateEpubUrlByBookId(
						mBookId, urlRequestor.getEpubUrl());
			} else {
				// 验证失败，url为空或者为“null”
				BooksManageDao.getInstance(mContext).updateErrorStatus(mBookId);
			}

			mUrlRequestorSparseArray.delete(mBookId);
		}

		@Override
		public void onFailed(AbstractModel requestor, int errorCode) {
			// 若获取epub下载地址失败，则更新状态
			BooksManageDao.getInstance(mContext).updateErrorStatus(mBookId);
			Toast.makeText(
					mContext,
					String.format(
							mContext.getString(R.string.book_parsing_failed),
							mBookName), Toast.LENGTH_LONG).show();
			mUrlRequestorSparseArray.delete(mBookId);
		}

		@Override
		public void onProgress(AbstractModel model, int progress) {
			// do nothing
		}
	}

	/**
	 * epub图书解析监听回调
	 * 
	 * @author Calvin
	 * 
	 */
	private class EpubAnalyseListener implements OnModelProcessListener {

		/** book id */
		private int mBookId;
		/** book name */
		private String mBookName;

		/**
		 * 构造
		 * 
		 * @param bookId
		 */
		public EpubAnalyseListener(int bookId, String bookName) {
			mBookId = bookId;
			mBookName = bookName;
		}

		@Override
		public void onSuccess(AbstractModel analyser) {
			BooksManageDao.getInstance(mContext).updateCompletedStatus(mBookId);
			mEpubAnalyserSparseArray.delete(mBookId);
			Toast.makeText(
					mContext,
					String.format(
							mContext.getString(R.string.book_parsing_completed),
							mBookName), Toast.LENGTH_LONG).show();
		}

		@Override
		public void onFailed(AbstractModel analyser, int errorcode) {
			// 若获取epub解析失败，则更新状态
			BooksManageDao.getInstance(mContext).updateErrorStatus(mBookId);
			Toast.makeText(
					mContext,
					String.format(
							mContext.getString(R.string.book_parsing_failed),
							mBookName), Toast.LENGTH_LONG).show();
			mEpubAnalyserSparseArray.delete(mBookId);
		}

		@Override
		public void onProgress(AbstractModel model, int progress) {
			// do nothing
		}
	}

	/**
	 * 监听downloaddatabase，更新bookdatabase
	 * 
	 * @author Calvin
	 * 
	 */
	private class DownloadsObserver extends ContentObserver {

		public DownloadsObserver() {
			super(new Handler(mContext.getMainLooper()));
		}

		@Override
		public void onChange(boolean selfChange) {
			updateDownloadDbdata();
		}
	}
}
