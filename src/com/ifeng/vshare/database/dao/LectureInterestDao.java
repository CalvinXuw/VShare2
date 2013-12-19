package com.ifeng.vshare.database.dao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.ifeng.BaseApplicaion;
import com.ifeng.util.database.SQLiteTransaction;
import com.ifeng.util.logging.Log;
import com.ifeng.vshare.database.LectureDBHelper;
import com.ifeng.vshare.database.LectureProvider;

/**
 * 大讲堂活动感兴趣记录管理类
 * 
 * @author Calvin
 * 
 */
public class LectureInterestDao {

	/** log tag. */
	private final String TAG = getClass().getSimpleName();

	/** if enabled, logcat will output the log. */
	private static final boolean DEBUG = true & BaseApplicaion.DEBUG;

	/*
	 * 更新数据库字段时，需要重新更改索引
	 */
	/** column index row id */
	private static final int INDEX_ID = 0;
	/** column index lecture id */
	private static final int INDEX_LECTURE_ID = 1;

	/** instance */
	private static LectureInterestDao sLectureInterestDao;
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
	public static LectureInterestDao getInstance(Context context) {
		if (sLectureInterestDao == null) {
			synchronized (LectureInterestDao.class) {
				sLectureInterestDao = new LectureInterestDao(context);
			}
		}
		return sLectureInterestDao;
	}

	/**
	 * 构造
	 * 
	 * @param context
	 */
	private LectureInterestDao(Context context) {
		mContext = context;
		mExecutor = Executors.newSingleThreadExecutor();
	}

	/**
	 * 添加一个感兴趣的活动
	 * 
	 * @param bookId
	 * @param chapterId
	 */
	public void insertLectureInterestRecord(final int lectureId) {
		if (DEBUG) {
			Log.d(TAG, "add lecture interest record " + lectureId);
		}

		if (queryLectureInterestRecordExistByLectureId(lectureId)) {
			return;
		}

		final ContentValues lectureValues = new ContentValues();
		lectureValues.put(LectureDBHelper.COLUMN_LECTUREID, lectureId);

		runTransactionAsync(new SQLiteTransaction() {

			@Override
			protected boolean performTransaction(ContentResolver cr) {
				cr.insert(LectureProvider.LECTURE_INTEREST_URI, lectureValues);
				return true;
			}
		}, false);
	}

	/**
	 * 检验指定lectureId是否已经记录
	 * 
	 * @param lectureId
	 * @return
	 */
	public boolean queryLectureInterestRecordExistByLectureId(
			final int lectureId) {
		if (DEBUG) {
			Log.d(TAG, "check lecture interest record with " + lectureId);
		}

		boolean isExist = false;
		Cursor cursor = mContext.getContentResolver().query(
				LectureProvider.LECTURE_INTEREST_URI, null,
				LectureDBHelper.COLUMN_LECTUREID + "=?",
				new String[] { lectureId + "" }, null);
		if (cursor != null && cursor.moveToFirst()) {
			isExist = true;
		}

		if (cursor != null) {
			cursor.close();
		}

		return isExist;
	}

	/**
	 * 删除一条感兴趣的活动
	 * 
	 * @param bookId
	 */
	public void deleteLectureInterestRecordByLectureId(final int lectureId) {
		if (DEBUG) {
			Log.d(TAG, "delete lecture interest record of " + lectureId);
		}

		runTransactionAsync(new SQLiteTransaction() {

			@Override
			protected boolean performTransaction(ContentResolver cr) {
				cr.delete(LectureProvider.LECTURE_INTEREST_URI,
						LectureDBHelper.COLUMN_LECTUREID + "=?",
						new String[] { lectureId + "" });
				return true;
			}
		}, false);
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
}
