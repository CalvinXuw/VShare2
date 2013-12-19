package com.ifeng.vshare.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ifeng.vshare.R;
import com.ifeng.vshare.book.BookEpubAnalyser.ChapterItem;
import com.ifeng.vshare.database.dao.BooksReadChapterDao;
import com.ifeng.vshare.database.dao.BooksReadProgressDao;
import com.ifeng.vshare.database.dao.BooksReadProgressDao.BooksReadProgressItem;
import com.ifeng.vshare.model.BookChapterListItem.RemoteChapterItem;

/**
 * 图书详情信息的组合样式adapter
 * 
 * @author Calvin
 * 
 */
public abstract class BookComposeStyleAdapter extends ComposeStyleAdapter {

	/** 本地图书样式id */
	protected static final int LOCAL_STYLE = 0;
	/** 在线图书样式id */
	protected static final int REMOTE_STYLE = 1;

	/** activity */
	private Activity mActivity;

	/** bookId */
	private int mBookId;

	/**
	 * 构造
	 * 
	 * @param activity
	 * @param imageFetcher
	 */
	public BookComposeStyleAdapter(Activity activity, int bookId) {
		mActivity = activity;
		mBookId = bookId;
	}

	@Override
	public int getSubStyleCount() {
		// 本地、在线
		return 2;
	}

	@Override
	public View getSubStyleOrginView(int style) {
		View convertView = null;
		if (style == LOCAL_STYLE) {
			convertView = getLocalStyleOrginView();
		} else if (style == REMOTE_STYLE) {
			convertView = getRemoteStyleOrginView();
		}
		return convertView;
	}

	@Override
	public View getSubStyleItemView(View converView, int style, Object itemRes) {
		if (style == LOCAL_STYLE) {
			getLocalStyleItemView(converView, (ChapterItem) itemRes);
		} else if (style == REMOTE_STYLE) {
			getRemoteStyleItemView(converView, (RemoteChapterItem) itemRes);
		}
		return converView;
	}

	/**
	 * 获取本地图书样式的空白子视图
	 * 
	 * @return
	 */
	private View getLocalStyleOrginView() {
		View convertView = mActivity.getLayoutInflater().inflate(
				R.layout.book_chapter_list_item, null);
		LocalListViewHolder holder = new LocalListViewHolder();
		holder.mTitle = (TextView) convertView
				.findViewById(R.id.text_item_book_chapter_tital);
		holder.mMark = (ImageView) convertView
				.findViewById(R.id.image_item_book_chapter_mark);
		convertView.setTag(holder);

		return convertView;
	}

	/**
	 * 获取在线图书样式的空白子视图
	 * 
	 * @return
	 */
	private View getRemoteStyleOrginView() {
		View convertView = LayoutInflater.from(mActivity).inflate(
				R.layout.book_detail_list_item, null);
		RemoteListHolder holder = new RemoteListHolder();
		holder.mTitle = (TextView) convertView
				.findViewById(R.id.text_item_book_detail_title);
		convertView.setTag(holder);
		return convertView;
	}

	/**
	 * 填充本地图书样式itemview内容信息
	 * 
	 * @param convertView
	 * @param newsListViewItem
	 * @return
	 */
	private View getLocalStyleItemView(View convertView,
			ChapterItem chapterViewItem) {
		if (chapterViewItem == null) {
			return convertView;
		}
		LocalListViewHolder holder = (LocalListViewHolder) convertView.getTag();
		if (getCurrentChapter(mBookId) == chapterViewItem.mChapterId) {
			holder.mMark
					.setBackgroundResource(R.drawable.image_book_catalogue_ongoing);
		} else {
			if (BooksReadChapterDao.getInstance(mActivity)
					.queryRecordChapterExistByChapterId(mBookId,
							chapterViewItem.mChapterId)) {
				holder.mMark
						.setBackgroundResource(R.drawable.image_book_catalogue_readed);
			} else {
				holder.mMark
						.setBackgroundResource(R.drawable.image_book_catalogue_unread);
			}
		}

		holder.mTitle.setText(chapterViewItem.mChapterId + "."
				+ chapterViewItem.mTitle);
		return convertView;
	}

	/**
	 * 填充在线图书样式itemview内容
	 * 
	 * @param convertView
	 * @param item
	 * @return
	 */
	private View getRemoteStyleItemView(View convertView,
			final RemoteChapterItem item) {
		if (item == null) {
			return convertView;
		}
		RemoteListHolder holder = (RemoteListHolder) convertView.getTag();
		holder.mTitle.setText(item.mChapterName);
		return convertView;
	}

	/**
	 * 获取当前章节
	 * 
	 * @return
	 */
	public int getCurrentChapter(int bookId) {
		BooksReadProgressItem item = BooksReadProgressDao
				.getInstance(mActivity).queryProgressByBookId(bookId);
		if (item != null) {
			return item.mChapterId;
		}
		return -1;
	}

	/**
	 * 资讯列表页面的布局容器
	 * 
	 * @author Calvin
	 * 
	 */
	private class LocalListViewHolder {
		/** 章节标题 */
		private TextView mTitle;
		/** 章节状态标记 */
		private ImageView mMark;
	}

	private class RemoteListHolder {
		/** 章节标题 */
		private TextView mTitle;
	}
}