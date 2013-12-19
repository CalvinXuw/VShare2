package com.ifeng.vshare.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.ifeng.BaseApplicaion;
import com.ifeng.util.logging.Log;

/**
 * 大讲堂管理的db
 * 
 * @author Calvin
 * 
 */
public class LectureDBHelper extends SQLiteOpenHelper {

	/** log tag. */
	protected final String TAG = getClass().getSimpleName();

	/** if enabled, logcat will output the log. */
	protected static final boolean DEBUG = true & BaseApplicaion.DEBUG;

	/** db name */
	private static final String DATABASE_NAME = "lecture.db";

	/** 当前数据库版本 */
	private static final int DATABASE_VERSION = 1;

	/** 版本1 */
	private static final int DATABASE_VERSON_1 = 1;

	/** 感兴趣的管理表 */
	public static final String TABLE_LECTURE_INTEREST = "lectureinterest";

	/** 书名 */
	public static final String COLUMN_LECTUREID = "lectureid";

	/** 感兴趣的讲堂活动表 */
	private static final String CREATE_TABLE_LECTURE_INTEREST = "create table "
			+ TABLE_LECTURE_INTEREST + " (" + BaseColumns._ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_LECTUREID
			+ " integer);";

	/**
	 * 构造
	 * 
	 * @param context
	 */
	public LectureDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.beginTransaction();
		try {
			db.execSQL(CREATE_TABLE_LECTURE_INTEREST);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.d(TAG, "occuring error when create tables");
			Log.e(TAG, e);
		} finally {
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (DEBUG) {
			Log.d(TAG, "upgrage from " + oldVersion + " to " + newVersion);
		}

		if (oldVersion > newVersion) {
			if (DEBUG) {
				Log.i(TAG, "upgrade lecture database from version "
						+ oldVersion + " ,but current version is " + newVersion
						+ " , so destroying all old data");
			}

			oldVersion = 0;
		}

		// 逐步升级到目标version
		for (int version = oldVersion + 1; version <= newVersion; version++) {
			upgradeTo(db, version);
		}
	}

	/**
	 * 升级到指定版本的db
	 * 
	 * @param db
	 * @param newVersion
	 */
	private void upgradeTo(SQLiteDatabase db, int newVersion) {
		if (DEBUG) {
			Log.d(TAG, "upgrage to " + newVersion);
		}

		switch (newVersion) {
		case DATABASE_VERSON_1:
			upgradeToVersion1(db);
			break;
		}
	}

	/**
	 * 升级到版本1
	 * 
	 * @param db
	 */
	private void upgradeToVersion1(SQLiteDatabase db) {
		db.beginTransaction();
		try {
			db.execSQL("drop table if exists " + TABLE_LECTURE_INTEREST);

			db.execSQL(TABLE_LECTURE_INTEREST);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.d(TAG, "occuring error when create tables");
			Log.e(TAG, e);
		} finally {
			db.endTransaction();
		}
	}
}
