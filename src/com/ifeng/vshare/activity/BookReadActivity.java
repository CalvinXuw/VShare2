package com.ifeng.vshare.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.ifeng.util.Utility;
import com.ifeng.util.logging.Log;
import com.ifeng.util.ui.BookPageWidget;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareActivity;
import com.ifeng.vshare.book.BookEpubAnalyser.ChapterItem;
import com.ifeng.vshare.book.BookPageFactory;
import com.ifeng.vshare.book.BookPageFactory.ChapterProgressCallback;
import com.ifeng.vshare.database.dao.BooksManageDao.BooksTaskItem;
import com.ifeng.vshare.database.dao.BooksReadChapterDao;
import com.ifeng.vshare.database.dao.BooksReadProgressDao;
import com.ifeng.vshare.database.dao.BooksReadProgressDao.BooksReadProgressItem;

/***
 * @author qianzy
 * @time 2013-6-19 下午2:32:10
 * @describe 图书阅读类
 */
public class BookReadActivity extends VShareActivity {

	/** key book info */
	private static final String BOOK_ITEM = "bookitem";
	/** key chapter index */
	private static final String CHAPTER_INDEX = "chapterindex";

	/**
	 * 获取图书阅读页面的intent
	 * 
	 * @param activity
	 * @param item
	 * @return
	 */
	public static Intent getIntent(Activity activity, BooksTaskItem bookinfo,
			int chapterIndex) {
		Intent intent = new Intent(activity, BookReadActivity.class);
		intent.putExtra(BOOK_ITEM, bookinfo);
		intent.putExtra(CHAPTER_INDEX, chapterIndex);
		return intent;
	}

	/** 正文字体大小 */
	private static final int FONT_SIZE_CONTENT = 16;
	/** 信息文字字体大小，章节、书名、进度等 */
	private static final int FONT_SIZE_INFO = 12;

	/** 图书信息 */
	private BooksTaskItem mBookInfo;
	/** 章节列表 */
	private List<ChapterItem> mChapters;
	/** 当前章节索引 */
	private int mCurrentChapterIndex;

	/** 绘制当前页面以及下一个页面 */
	public Canvas mCurPageCanvas, mNextPageCanvas;
	private Bitmap mCurPageBitmap, mNextPageBitmap;

	/** 翻页效果绘制类 */
	private BookPageWidget mPageWidget;
	private BookPageFactory mPagefactory;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		int screenWidth = (int) Utility.getScreenWidth(this);
		int screenHeight = (int) Utility.getScreenHeight(this);

		mBookInfo = (BooksTaskItem) getIntent().getExtras().getSerializable(
				BOOK_ITEM);
		mCurrentChapterIndex = getIntent().getExtras().getInt(CHAPTER_INDEX);

		try {
			mChapters = readObject(mBookInfo.mManifestFileName);
		} catch (Exception e) {
			// 异常情况，如果图书被用户删除或更改，则清除当前数据返回书籍列表页面
			if (DEBUG) {
				Log.e(TAG, "manifest file has been changed by user");
			}
			return;
		}

		mCurPageBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
				Bitmap.Config.ARGB_8888);
		mNextPageBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
				Bitmap.Config.ARGB_8888);
		mCurPageCanvas = new Canvas(mCurPageBitmap);
		mNextPageCanvas = new Canvas(mNextPageBitmap);

		mPageWidget = new BookPageWidget(this, screenWidth, screenHeight);// 页面
		setContentView(mPageWidget);
		mPageWidget.setBitmaps(mCurPageBitmap, mCurPageBitmap);
		mPageWidget.setOnTouchListener(mOnPageDargListener);

		// 书工厂
		mPagefactory = new BookPageFactory(screenWidth, screenHeight,
				FONT_SIZE_CONTENT, FONT_SIZE_INFO, this);
		mPagefactory.setBackgroundBitmap(BitmapFactory.decodeResource(
				this.getResources(), R.drawable.background_book));
		mPagefactory.setFontColor(getResources().getColor(
				R.color.font_list_desc));
		mPagefactory.setChapterProgressCallback(new ChapterProgressCallback() {

			@Override
			public int getChapterStartProgress() {
				return getProgressByChapterIndex(mCurrentChapterIndex);
			}

			@Override
			public int getBookTotalLength() {
				return getBookTotalProgress();
			}
		});

		openBook(mCurrentChapterIndex, getChapterProgressWithDbProgress());
		mPagefactory.drawPage(mCurPageCanvas);
	}

	@Override
	protected void onDestroy() {
		if (mCurPageBitmap != null && !mCurPageBitmap.isRecycled()) {
			mCurPageBitmap.recycle();
		}
		if (mNextPageBitmap != null && !mNextPageBitmap.isRecycled()) {
			mNextPageBitmap.recycle();
		}
		mPagefactory.release();
		super.onDestroy();
	}

	private OnTouchListener mOnPageDargListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent e) {
			if (v == mPageWidget) {

				if (e.getAction() == MotionEvent.ACTION_DOWN) {
					mPageWidget.abortAnimation();
					mPageWidget.calcCornerXY(e.getX(), e.getY());
					mPagefactory.drawPage(mCurPageCanvas);
					/** 左翻 */
					if (mPageWidget.DragToRight()) {
						if (mPagefactory.isFirstPage()) {
							int prePosition = mCurrentChapterIndex - 1;
							if (prePosition < 0) {
								Toast.makeText(getApplicationContext(),
										R.string.book_read_first,
										Toast.LENGTH_SHORT).show();
								return mPageWidget.doTouchEvent(e, true);
							}
							// 此处begin并无实际意义，具体begin位置会在turnPreChapter中重算
							openBook(prePosition,
									getChapterTotalProgress(prePosition));
							mCurrentChapterIndex--;
							mPagefactory.turnPreChapter();
						} else {
							mPagefactory.turePrePage();
						}
						mPagefactory.drawPage(mNextPageCanvas);
					} else {// 右翻
						if (mPagefactory.isLastPage()) {
							int nextPosition = mCurrentChapterIndex + 1;
							if (nextPosition > mChapters.size() - 1) {
								Toast.makeText(getApplicationContext(),
										R.string.book_read_last,
										Toast.LENGTH_SHORT).show();
								return mPageWidget.doTouchEvent(e, true);
							}
							openBook(nextPosition, 0);
							mCurrentChapterIndex++;
							mPagefactory.turnNextChapter();
						} else {
							mPagefactory.tureNextPage();
						}
						mPagefactory.drawPage(mNextPageCanvas);
					}
					mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
				} else if (e.getAction() == MotionEvent.ACTION_UP
						|| e.getAction() == MotionEvent.ACTION_MOVE) {
					/** 左翻 */
					if (mPageWidget.DragToRight()) {
						if (mPagefactory.isFirstPage()) {
							int prePosition = mCurrentChapterIndex - 1;
							if (prePosition < 0) {
								return mPageWidget.doTouchEvent(e, true);
							}
						}
						mPagefactory.drawPage(mNextPageCanvas);
					} else {// 右翻
						if (mPagefactory.isLastPage()) {
							int nextPosition = mCurrentChapterIndex + 1;
							if (nextPosition > mChapters.size() - 1) {
								return mPageWidget.doTouchEvent(e, true);
							}
						}
					}
				}
				return mPageWidget.doTouchEvent(e, false);
			}
			return false;
		}
	};

	/***
	 * @param fileName
	 * @return
	 * @throws Exception
	 * @describe 反序列化得到list
	 */
	public List<ChapterItem> readObject(String fileName) throws Exception {
		FileInputStream fos = new FileInputStream(new File(fileName));
		ObjectInputStream ois = new ObjectInputStream(fos);
		List<ChapterItem> chapterItems = (List<ChapterItem>) ois.readObject();
		return chapterItems;
	}

	@Override
	protected void onPause() {
		// 不放在onDestory中，实际上onDestory的调用要晚于上一个页面的onResume的调用，导致页面无法正常刷新显示
		BooksReadProgressDao.getInstance(this).insertProgress(
				mBookInfo.mBookId,
				mChapters.get(mCurrentChapterIndex).mChapterId,
				mPagefactory.getCurrentPageProgress());
		super.onPause();
	}

	/**
	 * @describe 打开图书
	 * @param pageIndex
	 * @param begin
	 */
	public void openBook(int pageIndex, int begin) {
		try {
			mPagefactory.openbook(mChapters.get(pageIndex).mFileName, begin);
			// 添加已读章节
			BooksReadChapterDao.getInstance(this).insertChapterRecord(
					mBookInfo.mBookId, mChapters.get(pageIndex).mChapterId);
		} catch (IOException e) {
			if (DEBUG) {
				Log.e(TAG, e);
			}
			Toast.makeText(this, "打开电子书失败", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		mPagefactory.setChapterName(String.format(
				getString(R.string.book_chapter_format),
				mChapters.get(pageIndex).mChapterId));
		mPagefactory.setBookName(mBookInfo.mBookName);
	}

	/**
	 * 获取整本书的长度
	 * 
	 * @return
	 */
	private int getBookTotalProgress() {
		int totalLen = 0;
		for (int i = 0; i < mChapters.size(); i++) {
			File chapterFile = new File(mChapters.get(i).mFileName);
			if (chapterFile.exists()) {
				totalLen += chapterFile.length();
			}
		}
		return totalLen;
	}

	/**
	 * 获取某一章的长度
	 * 
	 * @param pageIndex
	 * @return
	 */
	private int getChapterTotalProgress(int pageIndex) {
		int chapterLength = (int) new File(mChapters.get(pageIndex).mFileName)
				.length();
		return chapterLength;
	}

	/**
	 * 
	 * 计算当前章节在全本之中的起始进度
	 * 
	 * @param chapterIndex
	 * @param totalProgress
	 * @return
	 */
	private int getProgressByChapterIndex(int chapterIndex) {
		int totalLen = 0;
		for (int i = 0; i < chapterIndex; i++) {
			File chapterFile = new File(mChapters.get(i).mFileName);
			if (chapterFile.exists()) {
				totalLen += chapterFile.length();
			}
		}
		return totalLen;
	}

	/**
	 * 通过数据库中记录的阅读进度反查当前章节的续读进度
	 * 
	 * @return
	 */
	private int getChapterProgressWithDbProgress() {

		BooksReadProgressItem item = BooksReadProgressDao.getInstance(this)
				.queryProgressByBookId(mBookInfo.mBookId);

		if (item != null
				&& mChapters.get(mCurrentChapterIndex).mChapterId == item.mChapterId) {
			return item.mProgress;
		}
		return 0;
	}
}