package com.ifeng.vshare.activity;

import java.io.Serializable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ifeng.util.ui.NavgationbarView;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareActivity;
import com.ifeng.vshare.database.dao.BooksManageDao.BooksTaskItem;
import com.ifeng.vshare.fragment.BookDetailFragment;
import com.ifeng.vshare.model.BookListItem.BookItem;

/**
 * 图书章节显示的activity
 * 
 * @author qianzy
 * @time 2013-6-18 上午10:43:26
 */
public class BookDetailActivity extends VShareActivity {
	/** key info */
	private static final String BOOK_INFO = "info";
	private static final String ISDOWNLOAD = "id_download";

	/**
	 * 获取跳转intent
	 * 
	 * @param activity
	 * @param item
	 * @return
	 */
	public static Intent getIntent(Context activity, BookItem item) {
		Intent intent = new Intent(activity, BookDetailActivity.class);
		intent.putExtra(BOOK_INFO, (Serializable) item);
		intent.putExtra(ISDOWNLOAD, false);
		return intent;
	}

	/**
	 * 获取跳转intent
	 * 
	 * @param activity
	 * @param item
	 * @return
	 */
	public static Intent getIntent(Context activity, BooksTaskItem item) {
		Intent intent = new Intent(activity, BookDetailActivity.class);
		intent.putExtra(BOOK_INFO, (Serializable) item);
		intent.putExtra(ISDOWNLOAD, true);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_activity_single);
		NavgationbarView navgationbar = (NavgationbarView) findViewById(R.id.navgationbar);
		/*
		 * 设置导航条部分
		 */
		navgationbar.setBackItem(this);
		navgationbar.setTitle(getString(R.string.title_book));
		BookDetailFragment bookChapterFragment;
		boolean isDownload = getIntent().getBooleanExtra(ISDOWNLOAD, false);
		if (isDownload) {
			BooksTaskItem booksTaskItem = (BooksTaskItem) getIntent()
					.getSerializableExtra(BOOK_INFO);
			bookChapterFragment = BookDetailFragment.getInstance(
					booksTaskItem, isDownload);
		} else {
			BookItem booksItem = (BookItem) getIntent().getSerializableExtra(
					BOOK_INFO);
			bookChapterFragment = BookDetailFragment.getInstance(booksItem,
					isDownload);
		}

		getSupportFragmentManager().beginTransaction()
				.add(R.id.layout_content, bookChapterFragment).commit();

	}

	@Override
	public void finish() {
		// 由推送进入详情页面，需要返回主菜单
		if (sActivityStack.size() == 1) {
			startActivity(new Intent(this, HeadlineCategoryActivity.class));
		}
		super.finish();
	}

}
