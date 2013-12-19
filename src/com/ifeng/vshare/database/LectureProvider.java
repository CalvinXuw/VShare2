package com.ifeng.vshare.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.ifeng.BaseApplicaion;

/**
 * lecture db provider
 * 
 * @author Calvin
 * 
 */
public class LectureProvider extends ContentProvider {

	/** log tag. */
	private final String TAG = getClass().getSimpleName();

	/** if enabled, logcat will output the log. */
	private static final boolean DEBUG = true & BaseApplicaion.DEBUG;

	/** MIME type for the lecture interest */
	private static final String LECTURE_INTEREST_TYPE = "vnd.android.cursor.lectureinterest";

	/** URI matcher used to recognize URIs sent by applications */
	private static UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	/** URI matcher constant for the URI of 大讲堂感兴趣列表 */
	private static final int LECTURE_INTEREST = 1;

	/** AUTHORITY. */
	public static final String AUTHORITY = "com.ifeng.vshare.lecture";

	/** uri 书籍管理 */
	public static final Uri LECTURE_INTEREST_URI = Uri.parse("content://"
			+ AUTHORITY + "/lectureinterest");

	static {
		sURIMatcher.addURI(AUTHORITY, "lectureinterest", LECTURE_INTEREST);
	}

	/** DBHelper */
	private LectureDBHelper mDbHelper;

	@Override
	public boolean onCreate() {
		mDbHelper = new LectureDBHelper(getContext());
		return false;
	}

	@Override
	public String getType(Uri uri) {
		int match = sURIMatcher.match(uri);
		switch (match) {
		case LECTURE_INTEREST:
			return LECTURE_INTEREST_TYPE;
		}
		throw new IllegalArgumentException("unknow uri:" + uri);
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		String table = getTableNameFromUri(uri);
		long rowId = mDbHelper.getWritableDatabase()
				.insert(table, null, values);
		if (rowId == -1) {
			Log.d(TAG, "insert into lecture database failed");
			return null;
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return ContentUris.withAppendedId(uri, rowId);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		String table = getTableNameFromUri(uri);
		int count = mDbHelper.getWritableDatabase().delete(table, selection,
				selectionArgs);

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		String table = getTableNameFromUri(uri);
		int count = mDbHelper.getWritableDatabase().update(table, values,
				selection, selectionArgs);

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		String table = getTableNameFromUri(uri);
		Cursor cursor = mDbHelper.getReadableDatabase().query(table,
				projection, selection, selectionArgs, null, null, sortOrder);
		return cursor;
	}

	/**
	 * 从uri中通过matcher匹配获取tablename
	 * 
	 * @param uri
	 * @return
	 */
	private String getTableNameFromUri(Uri uri) {
		String table = null;

		int match = sURIMatcher.match(uri);
		switch (match) {
		case LECTURE_INTEREST:
			table = LectureDBHelper.TABLE_LECTURE_INTEREST;
			break;
		default:
			throw new IllegalArgumentException("unknow uri:" + uri);
		}
		return table;
	}

}
