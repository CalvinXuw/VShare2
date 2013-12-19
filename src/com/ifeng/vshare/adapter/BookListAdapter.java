package com.ifeng.vshare.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ifeng.util.Utility;
import com.ifeng.util.imagecache.ImageFetcher;
import com.ifeng.util.ui.OnSingleClickListener;
import com.ifeng.vshare.R;
import com.ifeng.vshare.activity.BookDetailActivity;
import com.ifeng.vshare.database.BooksDBHelper;
import com.ifeng.vshare.database.dao.BooksManageDao.BooksTaskItem;
import com.ifeng.vshare.model.BookListItem.BookItem;
import com.ifeng.vshare.requestor.BookListRequestor;
import com.ifeng.vshare.ui.DialogManager;
import com.ifeng.vshare.ui.DialogManager.DialogStateCallback;

/**
 * 图书列表的adapter
 * 
 * @author Calvin
 * 
 */
public class BookListAdapter extends SectionAdapter {

	/** sectionid 本地 */
	private final static int SECTION_LOCAL = 0;
	/** sectionid 网络 */
	private final static int SECTION_REMOTE = 1;

	/** activity */
	private Activity mActivity;
	/** 图片加载器 */
	private ImageFetcher mImageFetcher;
	/** 数据model */
	private BookListRequestor mBookListRequestor;

	/**
	 * 构造
	 * 
	 * @param activity
	 * @param imageFetcher
	 * @param requestor
	 */
	public BookListAdapter(Activity activity, ImageFetcher imageFetcher,
			BookListRequestor requestor) {
		mActivity = activity;
		mImageFetcher = imageFetcher;
		mBookListRequestor = requestor;
	}

	@Override
	public int sectionCount() {
		return 2;
	}

	@Override
	public int getCountWithSection(int sectionId) {
		switch (sectionId) {
		case SECTION_LOCAL:
			return mBookListRequestor.getLocalBookItems().size();
		case SECTION_REMOTE:
			return mBookListRequestor.getRemoteBookItems().size();
		}
		return 0;
	}

	@Override
	public Object getItemWithSection(int sectionId, int position) {
		switch (sectionId) {
		case SECTION_LOCAL:
			return mBookListRequestor.getLocalBookItems().get(position);
		case SECTION_REMOTE:
			return mBookListRequestor.getRemoteBookItems().get(position);
		}
		return null;
	}

	@Override
	public String getSectionName(int sectionId) {
		switch (sectionId) {
		case SECTION_LOCAL:
			return mActivity.getString(R.string.section_book_local);
		case SECTION_REMOTE:
			return mActivity.getString(R.string.section_book_remote);
		}

		return null;
	}

	@Override
	public View getViewWithSection(int sectionId, int position, View convertView) {
		BookListViewHolder holder = null;
		if (convertView == null) {
			convertView = mActivity.getLayoutInflater().inflate(
					R.layout.book_list_item, null);
			holder = new BookListViewHolder();

			holder.mLocalSection = convertView
					.findViewById(R.id.layout_item_book_section_local);
			holder.mRemoteSection = convertView
					.findViewById(R.id.layout_item_book_section_remote);
			holder.mItemContent = (ViewGroup) convertView
					.findViewById(R.id.layout_item);

			holder.mName = (TextView) convertView
					.findViewById(R.id.text_item_book_title);
			holder.mAuthor = (TextView) convertView
					.findViewById(R.id.text_item_book_author);
			holder.mImage = (ImageView) convertView
					.findViewById(R.id.image_item_book);
			holder.mDesc = (TextView) convertView
					.findViewById(R.id.text_item_book_desc);

			holder.mProgressText = (TextView) convertView
					.findViewById(R.id.text_item_book_progress);
			holder.mProgress = (ProgressBar) convertView
					.findViewById(R.id.progress_item_book);
			holder.mTrash = (Button) convertView
					.findViewById(R.id.btn_item_book_trash);
			holder.mDownload = (Button) convertView
					.findViewById(R.id.btn_item_book_download);
			holder.mRetry = (Button) convertView
					.findViewById(R.id.btn_item_book_retry);

			holder.mDivider = convertView.findViewById(R.id.divider_booklist);

			convertView.setTag(holder);

			// 适配计算 item原尺寸为 720*260
			holder.mItemContent.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT, (int) (Utility
							.getScreenWidth(mActivity) / 720 * 260)));
		} else {
			holder = (BookListViewHolder) convertView.getTag();
		}

		// 分配视图处理
		if (sectionId == SECTION_LOCAL) {
			setLocalBookItemView(position, holder);
		} else if (sectionId == SECTION_REMOTE) {
			setRemoteBookItemView(position, holder);
		}

		if (sectionId == SECTION_LOCAL
				&& position == getCountWithSection(SECTION_LOCAL) - 1) {
			holder.mDivider.setVisibility(View.GONE);
		} else {
			holder.mDivider.setVisibility(View.VISIBLE);
		}

		return convertView;
	}

	/**
	 * 设置本地图书管理视图
	 * 
	 * @param position
	 * @param holder
	 */
	private void setLocalBookItemView(int position, BookListViewHolder holder) {
		if (position == 0) {
			holder.mLocalSection.setVisibility(View.VISIBLE);
			holder.mRemoteSection.setVisibility(View.GONE);
		} else {
			holder.mLocalSection.setVisibility(View.GONE);
			holder.mRemoteSection.setVisibility(View.GONE);
		}

		final BooksTaskItem item = (BooksTaskItem) getItemWithSection(
				SECTION_LOCAL, position);
		holder.mName.setText(item.mBookName);
		holder.mAuthor.setText(TextUtils.isEmpty(item.mAuthor)
				|| item.mAuthor.equals("null") ? mActivity
				.getString(R.string.book_unknow_author) : item.mAuthor);
		holder.mDesc.setText(item.mShortDesc);
		mImageFetcher.loadImage(item.mCoverImg, holder.mImage);
		switch (item.mStatus) {
		// 未取到url的状态时，显示等待条和删除
		case BooksDBHelper.STATUS_NOURL:
			holder.mProgressText.setVisibility(View.GONE);
			holder.mProgress.setVisibility(View.VISIBLE);
			holder.mTrash.setVisibility(View.VISIBLE);
			holder.mRetry.setVisibility(View.GONE);
			holder.mDownload.setVisibility(View.GONE);
			holder.mItemContent.setOnClickListener(null);
			break;
		// 准备下载时仅显示等待条
		case BooksDBHelper.STATUS_READY_TO_DOWNLOAD:
			holder.mProgressText.setVisibility(View.GONE);
			holder.mProgress.setVisibility(View.VISIBLE);
			holder.mTrash.setVisibility(View.GONE);
			holder.mRetry.setVisibility(View.GONE);
			holder.mDownload.setVisibility(View.GONE);
			holder.mItemContent.setOnClickListener(null);
			break;
		// 下载时显示进度文字和删除
		case BooksDBHelper.STATUS_DOWNLOADING:
			holder.mProgressText.setVisibility(View.VISIBLE);
			holder.mProgress.setVisibility(View.GONE);
			holder.mTrash.setVisibility(View.VISIBLE);
			holder.mRetry.setVisibility(View.GONE);
			holder.mDownload.setVisibility(View.GONE);

			holder.mProgressText.setText(item.mProgress + "%");
			holder.mItemContent.setOnClickListener(null);
			break;
		// 下载结束后仅显示等待条
		case BooksDBHelper.STATUS_DOWNLOADCOMPLETED:
			holder.mProgressText.setVisibility(View.GONE);
			holder.mProgress.setVisibility(View.VISIBLE);
			holder.mTrash.setVisibility(View.GONE);
			holder.mRetry.setVisibility(View.GONE);
			holder.mDownload.setVisibility(View.GONE);
			holder.mItemContent.setOnClickListener(null);
			break;
		// 解析状态时，显示解析提示
		case BooksDBHelper.STATUS_PARSE:
			holder.mProgressText.setVisibility(View.VISIBLE);
			holder.mProgress.setVisibility(View.GONE);
			holder.mTrash.setVisibility(View.GONE);
			holder.mRetry.setVisibility(View.GONE);
			holder.mDownload.setVisibility(View.GONE);

			holder.mProgressText.setText(R.string.book_parsing);
			holder.mItemContent.setOnClickListener(null);
			break;
		// 错误状态显示重试
		case BooksDBHelper.STATUS_ERROR:
			holder.mProgressText.setVisibility(View.GONE);
			holder.mProgress.setVisibility(View.GONE);
			holder.mTrash.setVisibility(View.VISIBLE);
			holder.mRetry.setVisibility(View.VISIBLE);
			holder.mDownload.setVisibility(View.GONE);
			holder.mItemContent.setOnClickListener(null);
			break;
		// 完成状态可点击阅读
		case BooksDBHelper.STATUS_COMPLETED:
			holder.mProgressText.setVisibility(View.GONE);
			holder.mProgress.setVisibility(View.GONE);
			holder.mTrash.setVisibility(View.VISIBLE);
			holder.mRetry.setVisibility(View.GONE);
			holder.mDownload.setVisibility(View.GONE);
			// 跳转到书籍阅读页
			holder.mItemContent.setOnClickListener(new OnSingleClickListener() {

				@Override
				public void onSingleClick(View v) {
					mActivity.startActivity(BookDetailActivity.getIntent(
							mActivity, item));
				}
			});

			break;
		}

		holder.mTrash.setOnClickListener(new OnSingleClickListener() {

			@Override
			public void onSingleClick(View v) {
				DialogManager.getInstance().createDialog(
						mActivity.getString(R.string.dialog_title),
						mActivity.getString(R.string.book_dialog_delete_msg),
						new DialogStateCallback() {

							@Override
							public void onClick(int which) {
								if (which == Dialog.BUTTON_POSITIVE) {
									mBookListRequestor.deleteTaskBook(item);
								}
							}

							@Override
							public void onCancel() {

							}
						}, true, mActivity.getString(R.string.dialog_confirm),
						mActivity.getString(R.string.dialog_cancel));
			}
		});

		holder.mRetry.setOnClickListener(new OnSingleClickListener() {

			@Override
			public void onSingleClick(View v) {
				mBookListRequestor.retryTaskBook(item);
			}
		});
	}

	/**
	 * 设置远端图书视图
	 * 
	 * @param position
	 * @param holder
	 */
	private void setRemoteBookItemView(int position, BookListViewHolder holder) {
		if (position == 0) {
			holder.mLocalSection.setVisibility(View.GONE);
			holder.mRemoteSection.setVisibility(View.VISIBLE);
		} else {
			holder.mLocalSection.setVisibility(View.GONE);
			holder.mRemoteSection.setVisibility(View.GONE);
		}

		final BookItem item = (BookItem) getItemWithSection(SECTION_REMOTE,
				position);
		holder.mName.setText(item.mTitle);
		holder.mAuthor.setText(TextUtils.isEmpty(item.mAuthor)
				|| item.mAuthor.equals("null") ? mActivity
				.getString(R.string.book_unknow_author) : item.mAuthor);
		holder.mDesc.setText(item.mShortDesc);
		mImageFetcher.loadImage(item.mImg, holder.mImage);

		holder.mProgressText.setVisibility(View.GONE);
		holder.mProgress.setVisibility(View.GONE);
		holder.mTrash.setVisibility(View.GONE);
		holder.mRetry.setVisibility(View.GONE);
		holder.mDownload.setVisibility(View.VISIBLE);
		holder.mItemContent.setOnClickListener(new OnSingleClickListener() {

			@Override
			public void onSingleClick(View v) {
				mActivity.startActivity(BookDetailActivity.getIntent(mActivity,
						item));
			}
		});

		holder.mDownload.setOnClickListener(new OnSingleClickListener() {

			@Override
			public void onSingleClick(View v) {
				mBookListRequestor.addTaskBook(item);
			}
		});
	}

	/**
	 * 书籍页面的布局容器
	 * 
	 * @author Calvin
	 * 
	 */
	private class BookListViewHolder {

		/** 本地section */
		public View mLocalSection;
		/** 远端section */
		public View mRemoteSection;
		/** item view */
		public ViewGroup mItemContent;

		/** 名称 */
		public TextView mName;
		/** 作者 */
		public TextView mAuthor;
		/** 图片 */
		public ImageView mImage;
		/** 描述 */
		public TextView mDesc;

		/** progress text */
		public TextView mProgressText;
		/** progress bar */
		public ProgressBar mProgress;
		/** trash btn */
		public Button mTrash;
		/** download btn */
		public Button mDownload;
		/** retry btn */
		public Button mRetry;

		/** divider */
		public View mDivider;
	}
}
