package com.ifeng.vshare.fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.ifeng.util.logging.Log;
import com.ifeng.util.model.AbstractModel;
import com.ifeng.util.ui.IFRefreshListView;
import com.ifeng.util.ui.IFRefreshViewLayout;
import com.ifeng.util.ui.OnSingleClickListener;
import com.ifeng.util.ui.StateView;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareFragment;
import com.ifeng.vshare.activity.BookReadActivity;
import com.ifeng.vshare.adapter.BookComposeStyleAdapter;
import com.ifeng.vshare.book.BookEpubAnalyser.ChapterItem;
import com.ifeng.vshare.database.BooksDBHelper;
import com.ifeng.vshare.database.dao.BooksManageDao;
import com.ifeng.vshare.database.dao.BooksManageDao.BooksTaskItem;
import com.ifeng.vshare.database.dao.BooksReadChapterDao;
import com.ifeng.vshare.database.dao.BooksReadProgressDao;
import com.ifeng.vshare.database.dao.BooksReadProgressDao.BooksReadProgressItem;
import com.ifeng.vshare.model.BookChapterListItem.RemoteChapterItem;
import com.ifeng.vshare.model.BookListItem.BookItem;
import com.ifeng.vshare.requestor.BookChapterRequestor;
import com.ifeng.vshare.requestor.BookChapterRequestor.OnDataHasChangedListener;
import com.ifeng.vshare.ui.BookDetailHeadView;
import com.ifeng.vshare.ui.state.ErrorRequestState;

/**
 * 图书章节页面 fragment
 * 
 * @author qianzy
 * @time 2013-6-18 上午10:50:47
 */
public class BookDetailFragment extends VShareFragment {

	/** key info */
	private static final String BOOK_INFO = "info";
	private static final String IS_DOWNLOAD = "is_download";

	/** 图书信息 */
	private BookDetailHeadView mBookDetailHeader;
	/** 执行刷新的view */
	private IFRefreshViewLayout<IFRefreshListView> mRefreshView;
	/** RefreshListView */
	private IFRefreshListView mBookChapterListView;
	/** state view 当前状态 */
	private StateView mStateView;

	/**
	 * 获取实例
	 * 
	 * @param item
	 * @return
	 */
	public static BookDetailFragment getInstance(Object item, boolean isDownload) {
		BookDetailFragment booksChapterFragment = new BookDetailFragment();
		Bundle arg = new Bundle();
		arg.putSerializable(BOOK_INFO, (Serializable) item);
		arg.putBoolean(IS_DOWNLOAD, isDownload);
		booksChapterFragment.setArguments(arg);
		return booksChapterFragment;
	}

	/** key model requestor */
	private static final String KEY_MODEL_REQUESTOR = "requestor";

	/** 图书管理类item */
	private BooksTaskItem mBooksTaskItem;
	private BookItem mBooksItem;

	/** 章节列表 */
	private List<ChapterItem> mLocalChapters;
	/** 章节列表 */
	private List<RemoteChapterItem> mBooksChapters;
	/** adapter */
	private BookChapterAdapter mBookChapterAdapter;

	/** 是否已经下载完成 */
	private boolean mIsDownload;

	/** 图书章节请求 */
	private BookChapterRequestor mBooksChapterRequestor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mIsDownload = getArguments().getBoolean(IS_DOWNLOAD);
		if (mIsDownload) {
			mBooksTaskItem = (BooksTaskItem) getArguments().getSerializable(
					BOOK_INFO);
		} else {
			mBooksItem = (BookItem) getArguments().getSerializable(BOOK_INFO);
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mainView = inflater.inflate(R.layout.base_fragment_list,
				container, false);
		mImageFetcher.setLoadingImage(R.drawable.default_image_little);
		mStateView = (StateView) mainView.findViewById(R.id.stateview);

		mRefreshView = (IFRefreshViewLayout<IFRefreshListView>) mainView
				.findViewById(R.id.layout_refresh);

		mBookChapterListView = new IFRefreshListView(getActivity());

		mBookDetailHeader = new BookDetailHeadView(getActivity(), mImageFetcher);

		if (mIsDownload) {
			initContentView();
		} else {
			mStateView.setVisibility(View.VISIBLE);
			mStateView.setState(mProcessingRequestState);
			mBooksChapterRequestor.request();
		}

		return mainView;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mBookChapterAdapter != null) {
			mBookChapterAdapter.notifyDataSetChanged();
		}

		if (mImageFetcher != null) {
			mImageFetcher.clearMemoryCache();
			System.gc();
		}
	}

	/**
	 * 如果找不到图书的配置文件，则做清空数据的回退操作
	 */
	private void clearBookTaskWhenError() {
		BooksManageDao.getInstance(getActivity()).deleteByBookId(
				mBooksTaskItem.mBookId);
		BooksReadChapterDao.getInstance(getActivity())
				.deleteChapterRecordByBookId(mBooksTaskItem.mBookId);
		BooksReadProgressDao.getInstance(getActivity()).deleteProgressByBookId(
				mBooksTaskItem.mBookId);
		getActivity().finish();
	}

	/**
	 * 图书章节adapter
	 * 
	 * @author Calvin
	 * 
	 */
	public class BookChapterAdapter extends BookComposeStyleAdapter {

		/**
		 * 构造
		 * 
		 * @param activity
		 * @param bookId
		 */
		public BookChapterAdapter(int bookId) {
			super(getActivity(), bookId);
		}

		@Override
		public int getCount() {
			if (mIsDownload) {
				return mLocalChapters.size();
			} else {
				return mBooksChapters.size();
			}
		}

		@Override
		public Object getItem(int position) {
			if (mIsDownload) {
				return mLocalChapters.get(position);
			} else {
				return mBooksChapters.get(position);
			}
		}

		@Override
		public int getItemSubStyle(int position) {
			if (mIsDownload) {
				return LOCAL_STYLE;
			} else {
				return REMOTE_STYLE;
			}
		}

	}

	/**
	 * 获取当前章节
	 * 
	 * @return
	 */
	public int getCurrentChapter(int bookId) {
		BooksReadProgressItem item = BooksReadProgressDao.getInstance(
				getActivity()).queryProgressByBookId(bookId);
		if (item != null) {
			return item.mChapterId;
		}
		return -1;
	}

	/***
	 * @param fileName
	 * @return
	 * @throws Exception
	 *             manifestFileName文件不存在或者被更改
	 * @describe 反序列化得到list
	 */
	private List<ChapterItem> readObject(String fileName) throws Exception {
		FileInputStream fos = new FileInputStream(new File(fileName));
		ObjectInputStream ois = new ObjectInputStream(fos);
		List<ChapterItem> chapterItems = (List<ChapterItem>) ois.readObject();
		return chapterItems;
	}

	/*
	 * 增加图书详情顶部的图书文字信息等
	 */

	/**
	 * 填充图书内容信息
	 */
	public void initContentView() {
		if (mIsDownload) {
			mBookDetailHeader.initWithBookTaskItem(mBooksTaskItem);
			mBookChapterAdapter = new BookChapterAdapter(mBooksTaskItem.mBookId);

			continueRead();
		} else {
			mBookDetailHeader.initWithBookItem(mBooksItem);
			mBookChapterAdapter = new BookChapterAdapter(mBooksItem.mId);

			downloadBook();
		}

		mRefreshView.setContentView(mBookChapterListView);

		mBookChapterListView.addHeaderView(mBookDetailHeader.getContentView(),
				null, false);
		mBookChapterListView.setAdapter(mBookChapterAdapter);
		mBookChapterListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mIsDownload) {
					getActivity().startActivity(
							BookReadActivity.getIntent(getActivity(),
									mBooksTaskItem, (int) id));
				}
			}

		});
	}

	/**
	 * 继续阅读
	 */
	private void continueRead() {
		mIsDownload = true;
		try {
			mLocalChapters = readObject(mBooksTaskItem.mManifestFileName);
		} catch (Exception e) {
			// 异常情况，如果图书被用户删除或更改，则清除当前数据返回书籍列表页面
			if (DEBUG) {
				Log.e(TAG, "manifest file has been changed by user");
			}
			clearBookTaskWhenError();
			return;
		}

		mBookDetailHeader.setBookTaskState(
				getString(R.string.book_detail_read),
				new OnSingleClickListener() {

					@Override
					public void onSingleClick(View v) {
						int currentChapterIndex = 0;
						for (int i = 0; i < mLocalChapters.size(); i++) {
							if (mLocalChapters.get(i).mChapterId == getCurrentChapter(mBooksTaskItem.mBookId)) {
								currentChapterIndex = i;
								break;
							}
						}
						getActivity().startActivity(
								BookReadActivity.getIntent(getActivity(),
										mBooksTaskItem, currentChapterIndex));
					}
				});
		mBookChapterAdapter.notifyDataSetChanged();
	}

	/**
	 * 下载书籍
	 */
	private void downloadBook() {
		mBookDetailHeader.setBookTaskState(
				getString(R.string.book_detail_download),
				new OnSingleClickListener() {

					@Override
					public void onSingleClick(View v) {
						mBooksChapterRequestor.addTaskBook(mBooksItem);
					}
				});
	}

	/**
	 * 界面刷新回调
	 */
	private OnDataHasChangedListener mDataHasChangedListener = new OnDataHasChangedListener() {

		@Override
		public void onChanged() {
			refreshByBookTaskState();
		}
	};

	/**
	 * 刷新界面
	 */
	private void refreshByBookTaskState() {
		mBooksTaskItem = mBooksChapterRequestor.getLocalBookItem();
		switch (mBooksTaskItem.mStatus) {
		case BooksDBHelper.STATUS_NOURL:
		case BooksDBHelper.STATUS_READY_TO_DOWNLOAD:
		case BooksDBHelper.STATUS_DOWNLOADING:
			mBookDetailHeader.setBookTaskState(
					getString(R.string.book_detail_downlaoding), null);
			break;
		case BooksDBHelper.STATUS_DOWNLOADCOMPLETED:
		case BooksDBHelper.STATUS_PARSE:
			mBookDetailHeader.setBookTaskState(
					getString(R.string.book_detail_analyse), null);
			break;
		// 错误状态显示重试
		case BooksDBHelper.STATUS_ERROR:
			mBookDetailHeader.setBookTaskState(
					getString(R.string.book_detail_retry),
					new OnSingleClickListener() {

						@Override
						public void onSingleClick(View v) {
							mBooksChapterRequestor
									.retryTaskBook(mBooksTaskItem);
						}
					});
			break;
		// 完成状态可点击阅读
		case BooksDBHelper.STATUS_COMPLETED:
			continueRead();
			break;
		}
	}

	/*
	 * 增加获取在线图书的详情信息
	 */

	@Override
	public void onSuccess(AbstractModel requestor) {
		// 请求完成，结束等待页面
		mStateView.dismiss();

		BookChapterRequestor urlRequest = mBooksChapterRequestor;
		mBooksChapters = urlRequest.getChapters();

		List<RemoteChapterItem> removeList = new LinkedList<RemoteChapterItem>();
		for (RemoteChapterItem booksChapterItem : mBooksChapters) {
			if (TextUtils.isEmpty(booksChapterItem.mChapterName)) {
				removeList.add(booksChapterItem);
			}
		}
		mBooksChapters.removeAll(removeList);

		initContentView();
	}

	@Override
	public void onFailed(AbstractModel requestor, int errorCode) {
		mStateView.setState(mErrorRequestState);
	}

	@Override
	public void onActionTrigger(int actionId) {
		if (actionId == ErrorRequestState.STATE_ACTION_ERROR_RETRY) {
			mStateView.setState(mProcessingRequestState);
			mBooksChapterRequestor.request();
		}
	}

	@Override
	protected void setImageCacheParams() {
		mImageSize = mWidth / 2;
		mImageCacheDir = "bookdetail";
	}

	@Override
	protected void setupModel() {
		if (mIsDownload) {
			return;
		}
		mBooksChapterRequestor = new BookChapterRequestor(getActivity(),
				mBooksItem.mId, this, mDataHasChangedListener);
		mModelManageQueue.addTaskModel(KEY_MODEL_REQUESTOR,
				mBooksChapterRequestor);
	}

}
