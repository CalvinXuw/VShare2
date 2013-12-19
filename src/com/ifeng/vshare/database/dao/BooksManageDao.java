package com.ifeng.vshare.database.dao;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.ifeng.BaseApplicaion;
import com.ifeng.util.database.SQLiteTransaction;
import com.ifeng.util.logging.Log;
import com.ifeng.vshare.R;
import com.ifeng.vshare.database.BooksDBHelper;
import com.ifeng.vshare.database.BooksProvider;
import com.ifeng.vshare.model.BookListItem.BookItem;

/**
 * 书籍管理类
 * 
 * @author Calvin
 * 
 */
public class BooksManageDao {
	/** log tag. */
	private final String TAG = getClass().getSimpleName();

	/** if enabled, logcat will output the log. */
	private static final boolean DEBUG = true & BaseApplicaion.DEBUG;

	/*
	 * 更新数据库字段时，需要重新更改索引
	 */
	/** column index bookid */
	private static final int INDEX_BOOKID = 0;
	/** column index book name */
	private static final int INDEX_BOOKNAME = 1;
	/** column index auther */
	private static final int INDEX_AUTHOR = 2;
	/** column index book short desc */
	private static final int INDEX_SHORT_DESC = 3;
	/** column index book desc */
	private static final int INDEX_DESC = 4;
	/** column index cover img url */
	private static final int INDEX_COVERIMG = 5;
	/** column index epub url */
	private static final int INDEX_EPUBURL = 6;
	/** column index download id */
	private static final int INDEX_DOWNLOADID = 7;
	/** column index taskprogress */
	private static final int INDEX_PROGRESS = 8;
	/** column index status */
	private static final int INDEX_STATUS = 9;

	/** instance */
	private static BooksManageDao sBooksManageDao;
	/** context */
	private Context mContext;
	/** 用于执行数据库操作 */
	private ExecutorService mExecutor;

	/**
	 * 获取一个实例，单例
	 * 
	 * @param context
	 * @return
	 */
	public static BooksManageDao getInstance(Context context) {
		if (sBooksManageDao == null) {
			synchronized (BooksManageDao.class) {
				sBooksManageDao = new BooksManageDao(context);
			}
		}
		return sBooksManageDao;
	}

	/**
	 * 构造
	 * 
	 * @param context
	 */
	private BooksManageDao(Context context) {
		mContext = context;
		mExecutor = Executors.newSingleThreadExecutor();
	}

	/**
	 * 添加一本书
	 * 
	 * @param booksItem
	 */
	public boolean insertBook(BookItem booksItem) {
		if (DEBUG) {
			Log.d(TAG, "add a book id " + booksItem.mId + " name "
					+ booksItem.mTitle);
		}

		if (queryBookExistByBookId(booksItem.mId)) {
			return false;
		}

		final ContentValues bookValues = new ContentValues();
		bookValues.put(BooksDBHelper.COLUMN_BOOKID, booksItem.mId);
		bookValues.put(BooksDBHelper.COLUMN_BOOKNAME, booksItem.mTitle);
		bookValues.put(
				BooksDBHelper.COLUMN_AUTHOR,
				TextUtils.isEmpty(booksItem.mAuthor)
						|| booksItem.mAuthor.equals("null") ? mContext
						.getString(R.string.book_unknow_author)
						: booksItem.mAuthor);
		bookValues.put(BooksDBHelper.COLUMN_SHORT_DESC, booksItem.mShortDesc);
		bookValues.put(BooksDBHelper.COLUMN_DESC, booksItem.mDesc);
		bookValues.put(BooksDBHelper.COLUMN_COVERIMG, booksItem.mImg);
		bookValues.put(BooksDBHelper.COLUMN_TASKPROGRESS, 0);
		bookValues.put(BooksDBHelper.COLUMN_STATUS, BooksDBHelper.STATUS_NOURL);

		runTransactionAsync(new SQLiteTransaction() {

			@Override
			protected boolean performTransaction(ContentResolver cr) {
				cr.insert(BooksProvider.BOOKS_MANAGER_URI, bookValues);
				return true;
			}
		}, false);
		return true;
	}

	/**
	 * 更新已存在的book任务的epub download url
	 * 
	 * @param bookId
	 * @param url
	 * @return
	 */
	public boolean updateEpubUrlByBookId(final int bookId, String url) {
		if (DEBUG) {
			Log.d(TAG, "add epub download url to " + bookId);
		}

		if (!queryBookExistByBookId(bookId)) {
			return false;
		}

		final ContentValues urlValues = new ContentValues();
		urlValues.put(BooksDBHelper.COLUMN_EPUBURL, url);
		urlValues.put(BooksDBHelper.COLUMN_STATUS,
				BooksDBHelper.STATUS_READY_TO_DOWNLOAD);

		runTransactionAsync(new SQLiteTransaction() {

			@Override
			protected boolean performTransaction(ContentResolver cr) {
				cr.update(BooksProvider.BOOKS_MANAGER_URI, urlValues,
						BooksDBHelper.COLUMN_BOOKID + "=?",
						new String[] { bookId + "" });
				return true;
			}
		}, false);
		return true;
	}

	/**
	 * 更新指定booktask的downloadid
	 * 
	 * @param bookId
	 * @param downloadId
	 * @return
	 */
	public boolean updateDownloadIdByBookId(final int bookId, long downloadId) {
		if (DEBUG) {
			Log.d(TAG, "add download id to " + bookId);
		}

		if (!queryBookExistByBookId(bookId)) {
			return false;
		}

		final ContentValues urlValues = new ContentValues();
		urlValues.put(BooksDBHelper.COLUMN_DOWNLOADID, downloadId);
		urlValues.put(BooksDBHelper.COLUMN_STATUS,
				BooksDBHelper.STATUS_DOWNLOADING);

		runTransactionAsync(new SQLiteTransaction() {

			@Override
			protected boolean performTransaction(ContentResolver cr) {
				cr.update(BooksProvider.BOOKS_MANAGER_URI, urlValues,
						BooksDBHelper.COLUMN_BOOKID + "=?",
						new String[] { bookId + "" });
				return true;
			}
		}, false);
		return true;
	}

	/**
	 * 更新下载进度
	 * 
	 * @param bookId
	 * @param downloadProgress
	 * @param isFinish
	 * @return
	 */
	public boolean updateProgress(final int bookId, int downloadProgress,
			boolean isFinish) {
		if (DEBUG) {
			Log.d(TAG, "update booktask progress to " + downloadProgress
					+ " to " + bookId);
		}

		if (!queryBookExistByBookId(bookId)) {
			return false;
		}

		final ContentValues progressValues = new ContentValues();
		progressValues.put(BooksDBHelper.COLUMN_TASKPROGRESS, downloadProgress);
		if (isFinish) {
			progressValues.put(BooksDBHelper.COLUMN_STATUS,
					BooksDBHelper.STATUS_DOWNLOADCOMPLETED);
		} else {
			progressValues.put(BooksDBHelper.COLUMN_STATUS,
					BooksDBHelper.STATUS_DOWNLOADING);
		}
		runTransactionAsync(new SQLiteTransaction() {

			@Override
			protected boolean performTransaction(ContentResolver cr) {
				cr.update(BooksProvider.BOOKS_MANAGER_URI, progressValues,
						BooksDBHelper.COLUMN_BOOKID + "=?",
						new String[] { bookId + "" });
				return true;
			}
		}, false);
		return true;
	}

	/**
	 * 更新任务为解析状态
	 * 
	 * @param bookId
	 * @return
	 */
	public boolean updateParseStatus(final int bookId) {
		if (DEBUG) {
			Log.d(TAG, "update booktask status to parse " + bookId);
		}
		return updateStatus(bookId, BooksDBHelper.STATUS_PARSE);
	}

	/**
	 * 更新任务为错误状态
	 * 
	 * @param bookId
	 * @return
	 */
	public boolean updateErrorStatus(final int bookId) {
		if (DEBUG) {
			Log.d(TAG, "update booktask status to error " + bookId);
		}
		return updateStatus(bookId, BooksDBHelper.STATUS_ERROR);
	}

	/**
	 * 更新任务为等待重试状态
	 * 
	 * @param bookId
	 * @return
	 */
	public boolean updateRetryStatus(final int bookId) {
		if (DEBUG) {
			Log.d(TAG, "update booktask status to retry " + bookId);
		}
		return updateStatus(bookId, BooksDBHelper.STATUS_NOURL);
	}

	/**
	 * 更新任务为完成状态
	 * 
	 * @param bookId
	 * @return
	 */
	public boolean updateCompletedStatus(final int bookId) {
		if (DEBUG) {
			Log.d(TAG, "update booktask status to completed " + bookId);
		}
		return updateStatus(bookId, BooksDBHelper.STATUS_COMPLETED);
	}

	/**
	 * 更新任务status
	 * 
	 * @param bookId
	 * @param status
	 * @return
	 */
	private boolean updateStatus(final int bookId, int status) {
		if (!queryBookExistByBookId(bookId)) {
			return false;
		}

		final ContentValues statusValues = new ContentValues();
		statusValues.put(BooksDBHelper.COLUMN_STATUS, status);

		runTransactionAsync(new SQLiteTransaction() {

			@Override
			protected boolean performTransaction(ContentResolver cr) {
				cr.update(BooksProvider.BOOKS_MANAGER_URI, statusValues,
						BooksDBHelper.COLUMN_BOOKID + "=?",
						new String[] { bookId + "" });
				return true;
			}
		}, false);
		return true;
	}

	/**
	 * 删除一本图书的记录
	 * 
	 * @param bookId
	 */
	public void deleteByBookId(final int bookId) {
		if (DEBUG) {
			Log.d(TAG, "delete booktask by id " + bookId);
		}

		runTransactionAsync(new SQLiteTransaction() {

			@Override
			protected boolean performTransaction(ContentResolver cr) {
				cr.delete(BooksProvider.BOOKS_MANAGER_URI,
						BooksDBHelper.COLUMN_BOOKID + "=?",
						new String[] { bookId + "" });
				return true;
			}
		}, false);
	}

	/**
	 * 获取指定booktask的downloadid
	 * 
	 * @param bookId
	 * @return
	 */
	public long queryBookDownlaodIdByBookId(int bookId) {
		if (DEBUG) {
			Log.d(TAG, "query booktask downloadid by id " + bookId);
		}

		long downloadId = -1;
		Cursor cursor = mContext.getContentResolver().query(
				BooksProvider.BOOKS_MANAGER_URI,
				new String[] { BooksDBHelper.COLUMN_DOWNLOADID },
				BooksDBHelper.COLUMN_BOOKID + "=?",
				new String[] { bookId + "" }, null);
		if (cursor != null && cursor.moveToFirst()) {
			downloadId = cursor.getLong(INDEX_DOWNLOADID);
		}

		if (cursor != null) {
			cursor.close();
		}

		return downloadId;
	}

	/**
	 * 获取所有booktask信息
	 * 
	 * @param bookId
	 * @return
	 */
	public List<BooksTaskItem> queryAllBooks() {
		if (DEBUG) {
			Log.d(TAG, "get all book tasks");
		}

		List<BooksTaskItem> items = new LinkedList<BooksTaskItem>();
		Cursor cursor = mContext.getContentResolver().query(
				BooksProvider.BOOKS_MANAGER_URI, null, null, null, null);
		if (cursor == null) {
			return items;
		}

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			BooksTaskItem item = new BooksTaskItem();
			items.add(item);

			item.mBookId = cursor.getInt(INDEX_BOOKID);
			item.mBookName = cursor.getString(INDEX_BOOKNAME);
			item.mAuthor = cursor.getString(INDEX_AUTHOR);
			item.mShortDesc = cursor.getString(INDEX_SHORT_DESC);
			item.mDesc = cursor.getString(INDEX_DESC);
			item.mCoverImg = cursor.getString(INDEX_COVERIMG);
			item.mEpubUrl = cursor.getString(INDEX_EPUBURL);
			item.mDownloadId = cursor.getLong(INDEX_DOWNLOADID);
			item.mProgress = cursor.getInt(INDEX_PROGRESS);
			item.mStatus = cursor.getInt(INDEX_STATUS);
		}

		if (cursor != null) {
			cursor.close();
		}

		return items;
	}

	/**
	 * 检验指定bookId是否已经存在
	 * 
	 * @param bookId
	 * @return
	 */
	public boolean queryBookExistByBookId(final int bookId) {
		if (DEBUG) {
			Log.d(TAG, "check book exist " + bookId);
		}

		boolean isExist = false;
		Cursor cursor = mContext.getContentResolver().query(
				BooksProvider.BOOKS_MANAGER_URI,
				new String[] { BooksDBHelper.COLUMN_BOOKID },
				BooksDBHelper.COLUMN_BOOKID + "=?",
				new String[] { bookId + "" }, null);
		if (cursor != null && cursor.moveToFirst()) {
			isExist = true;
		}

		if (cursor != null) {
			cursor.close();
		}

		return isExist;
	}

	/**
	 * 异步执行数据库的更新，删除，增加操作，由于查找需要返回数据，所以不采用本方法
	 * 
	 * @param transaction
	 * @param async
	 */
	private void runTransactionAsync(final SQLiteTransaction transaction,
			boolean async) {
		if (async) {
			mExecutor.execute(new Runnable() {

				@Override
				public void run() {
					transaction.run(mContext.getContentResolver());
				}
			});
		} else {
			transaction.run(mContext.getContentResolver());
		}
	}

	/**
	 * 书籍管理表中的书籍task item
	 * 
	 * @author Calvin
	 * 
	 */
	public static class BooksTaskItem implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7848247691596519145L;
		/** book id */
		public int mBookId;
		/** book name */
		public String mBookName;
		/** book author */
		public String mAuthor;
		/** book short desc */
		public String mShortDesc;
		/** book desc */
		public String mDesc;
		/** book cover image url */
		public String mCoverImg;
		/** book epub file url */
		public String mEpubUrl;
		/** download id in download database */
		public long mDownloadId;
		/** task process progress */
		public int mProgress;
		/** task status */
		public int mStatus;
		/** manifest file name */
		public String mManifestFileName;
	}
}
