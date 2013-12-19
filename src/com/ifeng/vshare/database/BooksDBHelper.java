package com.ifeng.vshare.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ifeng.BaseApplicaion;
import com.ifeng.util.logging.Log;

/**
 * 图书管理的db
 * 
 * @author Calvin
 * 
 */
public class BooksDBHelper extends SQLiteOpenHelper {

	/** log tag. */
	protected final String TAG = getClass().getSimpleName();

	/** if enabled, logcat will output the log. */
	protected static final boolean DEBUG = true & BaseApplicaion.DEBUG;

	/** db name */
	private static final String DATABASE_NAME = "books.db";

	/** 当前数据库版本 */
	private static final int DATABASE_VERSION = 1;

	/** 版本1 */
	private static final int DATABASE_VERSON_1 = 1;

	/** 书籍的管理表 */
	public static final String TABLE_BOOKTASKS = "booktasks";
	/** 书籍的阅读进度表 */
	public static final String TABLE_BOOKPROGRESS = "bookprogress";
	/** 书籍的阅读过的章节记录表 */
	public static final String TABLE_BOOKCHAPTER = "bookchapter";

	/** book id */
	public static final String COLUMN_BOOKID = "bookid";
	/** 书名 */
	public static final String COLUMN_BOOKNAME = "bookname";
	/** 作者 */
	public static final String COLUMN_AUTHOR = "bookauthor";
	/** 推荐短评 */
	public static final String COLUMN_SHORT_DESC = "bookshortdesc";
	/** 描述 */
	public static final String COLUMN_DESC = "bookdesc";
	/** 封面图片url */
	public static final String COLUMN_COVERIMG = "coverimg";
	/** epub下载地址 */
	public static final String COLUMN_EPUBURL = "epuburl";
	/** 对应的下载数据库中的id */
	public static final String COLUMN_DOWNLOADID = "downloadid";
	/** 任务进度 */
	public static final String COLUMN_TASKPROGRESS = "taskprogress";
	/** 状态 */
	public static final String COLUMN_STATUS = "status";
	/** 单本书阅读进度 */
	public static final String COLUMN_READPROGRESS = "readprogress";
	/** 已经阅读过的章节 */
	public static final String COLUMN_CHAPTER = "chapter";

	/** status 尚未获取到url */
	public static final int STATUS_NOURL = 1;
	/** status 准备下载 */
	public static final int STATUS_READY_TO_DOWNLOAD = 2;
	/** status 下载中 */
	public static final int STATUS_DOWNLOADING = 3;
	/** status 下载完成 */
	public static final int STATUS_DOWNLOADCOMPLETED = 4;
	/** status 解析中 */
	public static final int STATUS_PARSE = 5;
	/** status 完成 */
	public static final int STATUS_COMPLETED = 6;
	/** status 异常错误 */
	public static final int STATUS_ERROR = 7;

	/** 书籍管理表 */
	private static final String CREATE_TABLE_ALL_BOOKS = "create table "
			+ TABLE_BOOKTASKS + " (" + COLUMN_BOOKID + " integer primary key,"
			+ COLUMN_BOOKNAME + " text," + COLUMN_AUTHOR + " text,"
			+ COLUMN_SHORT_DESC + " text," + COLUMN_DESC + " text,"
			+ COLUMN_COVERIMG + " text," + COLUMN_EPUBURL + " text,"
			+ COLUMN_DOWNLOADID + " long," + COLUMN_TASKPROGRESS + " integer,"
			+ COLUMN_STATUS + " integer);";
	/** 阅读进度表 */
	private static final String CREATE_TABLE_BOOK_PROGRESS = "create table "
			+ TABLE_BOOKPROGRESS + " (" + COLUMN_BOOKID
			+ " integer primary key," + COLUMN_CHAPTER + " integer,"
			+ COLUMN_READPROGRESS + " integer);";
	/** 已阅读的章节表 */
	private static final String CREATE_TABLE_BOOK_READ_CHAPTER = "create table "
			+ TABLE_BOOKCHAPTER
			+ " ("
			+ COLUMN_BOOKID
			+ " integer,"
			+ COLUMN_CHAPTER + " integer);";

	/**
	 * 构造
	 * 
	 * @param context
	 */
	public BooksDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.beginTransaction();
		try {
			db.execSQL(CREATE_TABLE_ALL_BOOKS);
			db.execSQL(CREATE_TABLE_BOOK_PROGRESS);
			db.execSQL(CREATE_TABLE_BOOK_READ_CHAPTER);
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
				Log.i(TAG, "upgrade books database from version " + oldVersion
						+ " ,but current version is " + newVersion
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
			db.execSQL("drop table if exists " + TABLE_BOOKTASKS);
			db.execSQL("drop table if exists " + TABLE_BOOKPROGRESS);
			db.execSQL("drop table if exists " + TABLE_BOOKCHAPTER);

			db.execSQL(CREATE_TABLE_ALL_BOOKS);
			db.execSQL(CREATE_TABLE_BOOK_PROGRESS);
			db.execSQL(CREATE_TABLE_BOOK_READ_CHAPTER);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.d(TAG, "occuring error when create tables");
			Log.e(TAG, e);
		} finally {
			db.endTransaction();
		}
	}
}
