package com.ifeng.vshare.ui;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.ifeng.util.Utility;
import com.ifeng.util.imagecache.ImageFetcher;
import com.ifeng.util.ui.ViewController;
import com.ifeng.vshare.R;
import com.ifeng.vshare.database.dao.BooksManageDao.BooksTaskItem;
import com.ifeng.vshare.model.BookListItem.BookItem;

/**
 * 阅读详情页面书籍信息
 * 
 * @author xuwei
 * 
 */
public class BookDetailHeadView extends ViewController {

	/** 图片加载器 */
	private ImageFetcher mImageFetcher;
	/** 书封面 */
	private ImageView mBookCover;
	/** 书名 */
	private TextView mBookName;
	/** 作者 */
	private TextView mAuthor;
	/** 书籍简介 */
	private TextView mBookDesc;
	/** 当前书籍任务状态，下载中、可下载、可阅读等 */
	private TextView mBookTaskState;

	/**
	 * 构造
	 * 
	 * @param activity
	 */
	public BookDetailHeadView(Activity activity, ImageFetcher imageFetcher) {
		super(activity);
		mImageFetcher = imageFetcher;
	}

	@Override
	protected View init() {
		View layout = (ViewGroup) getLayoutInflater().inflate(
				R.layout.book_detail, null);
		mBookCover = (ImageView) layout.findViewById(R.id.image_book_detail);
		mBookName = (TextView) layout.findViewById(R.id.text_book_detail_title);
		mAuthor = (TextView) layout.findViewById(R.id.text_book_detail_author);
		mBookTaskState = (TextView) layout
				.findViewById(R.id.text_book_detail_download);
		mBookDesc = (TextView) layout.findViewById(R.id.text_book_detail_desc);

		// 适配计算 item原尺寸为 720*220
		LinearLayout.LayoutParams params = (LayoutParams) mBookCover
				.getLayoutParams();
		params.height = (int) (Utility.getScreenWidth(getActivity()) / 720 * 220);
		mBookCover.setLayoutParams(params);

		return layout;
	}

	/**
	 * 由远端数据进行初始化界面
	 * 
	 * @param item
	 */
	public void initWithBookItem(BookItem item) {
		mImageFetcher.loadImage(item.mImg, mBookCover);
		mBookName.setText(item.mTitle);
		mAuthor.setText(TextUtils.isEmpty(item.mAuthor)
				|| item.mAuthor.equals("null") ? getActivity().getString(
				R.string.book_unknow_author) : item.mAuthor);
		mBookDesc.setText(item.mDesc);
	}

	/**
	 * 由本地图书任务进行界面初始化
	 * 
	 * @param item
	 */
	public void initWithBookTaskItem(BooksTaskItem item) {
		mImageFetcher.loadImage(item.mCoverImg, mBookCover);
		mBookName.setText(item.mBookName);
		mAuthor.setText(TextUtils.isEmpty(item.mAuthor)
				|| item.mAuthor.equals("null") ? getActivity().getString(
				R.string.book_unknow_author) : item.mAuthor);
		mBookDesc.setText(item.mDesc);
	}

	/**
	 * 设置当前书籍状态信息，更新到界面上
	 * 
	 * @param state
	 * @param action
	 */
	public void setBookTaskState(String state, OnClickListener action) {
		mBookTaskState.setText(state);
		mBookTaskState.setOnClickListener(action);
	}
}