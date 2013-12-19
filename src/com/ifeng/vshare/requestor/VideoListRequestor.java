package com.ifeng.vshare.requestor;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

import com.ifeng.vshare.model.VideoListItem;
import com.ifeng.vshare.model.VideoListItem.VideoItem;

/**
 * 视频页面model
 * 
 * @author Calvin
 * 
 */
public class VideoListRequestor extends BaseVSharePageRequestor {

	/** key cid */
	private static final String KEY_CID = "cid";

	/** value cid of history */
	private static final int VALUE_CID_HISTRY = 17933;
	/** value cid of person */
	private static final int VALUE_CID_PERSON = 17934;
	/** value cid culture */
	private static final int VALUE_CID_CULTURE = 17935;
	/** value cid discover */
	private static final int VALUE_CID_DISCOVER = 17936;
	/** value cid of society */
	private static final int VALUE_CID_SOCIETY = 17937;

	/** 视频子栏目标题 注意更换 */
	public static final String[] COLUMN_TITLE = { "历史", "人物", "探索", "社会", "文化" };
	/** 视频子栏目标题对应cid 注意更换 */
	public static final int[] COLUMN_ID = { VALUE_CID_HISTRY, VALUE_CID_PERSON,
			VALUE_CID_DISCOVER, VALUE_CID_SOCIETY, VALUE_CID_CULTURE };

	/** 子栏目cid */
	private int mCid;

	/** 头图数据 */
	private VideoItem mToponeItem;
	/** 分栏后的数据 */
	private List<VideosListViewItem> mVideosListViewItems;

	/**
	 * 构造方法，由栏目类型进行初始化
	 * 
	 * @param context
	 * @param column
	 * @param listener
	 */
	public VideoListRequestor(Context context, int cid,
			OnModelProcessListener listener) {
		super(context, listener);
		setAutoParseClass(VideoListItem.class);
		mVideosListViewItems = new LinkedList<VideosListViewItem>();
		mCid = cid;
	}

	@Override
	protected List<NameValuePair> getRequestParamsWithoutPagething() {
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair(KEY_CID, mCid + ""));
		return params;
	}

	@Override
	protected void handlePageResult(PageItem item) {
		VideoListItem requestorItem = (VideoListItem) item;

		if (getPageIndex() == FIRSTPAGE) {
			mToponeItem = requestorItem.mTopItem;
		}

		/*
		 * 对数据进行排版
		 */
		synchronized (mDataLock) {
			mVideosListViewItems.clear();
			VideosListViewItem listViewItem = null;
			int columnCount = 1;
			int currentCount = 0;
			for (int i = 0; i < mItems.size(); i++) {
				if (i == 0 || currentCount == columnCount) {
					listViewItem = new VideosListViewItem();
					mVideosListViewItems.add(listViewItem);
					currentCount = 0;
				}

				listViewItem.addVideo((VideoItem) mItems.get(i));
				currentCount++;
			}
		}
	}

	@Override
	public List<?> getDataList() {
		synchronized (mDataLock) {
			return mVideosListViewItems;
		}
	}

	/**
	 * 获取顶部头图数据
	 * 
	 * @return
	 */
	public VideoItem getTopone() {
		return mToponeItem;
	}

	@Override
	protected List<NameValuePair> getRequestHeaders() {
		return null;
	}

	@Override
	protected String getRequestUrl() {
		return "http://i.ifeng.com/gotonenewslist";
	}

	/**
	 * 提供列表展示的数据源
	 * 
	 * @author Calvin
	 * 
	 */
	public class VideosListViewItem {
		/** 视频 */
		private LinkedList<VideoItem> mVideos;

		/**
		 * 构造
		 */
		public VideosListViewItem() {
			mVideos = new LinkedList<VideoItem>();
		}

		/**
		 * 为页面展示提供数据
		 * 
		 * @return
		 */
		public LinkedList<VideoItem> getVideos() {
			return mVideos;
		}

		/**
		 * 添加一条视频
		 * 
		 * @param item
		 */
		private void addVideo(VideoItem item) {
			mVideos.add(item);
		}
	}
}
