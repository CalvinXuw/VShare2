package com.ifeng.vshare.requestor;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

import com.ifeng.util.net.parser.AbstractIFItem;
import com.ifeng.vshare.model.VideoListItem.VideoItem;
import com.ifeng.vshare.model.VideoTopicListItem;

/**
 * 视频专题页面model
 * 
 * @author Calvin
 * 
 */
public class VideoTopicRequestor extends BaseVSharePageRequestor {

	/** key id */
	private static final String KEY_ID = "id";

	/** id */
	private int mId;

	/** 专题顶部数据源 */
	private VideoTopicTopItem mTopItem;

	public VideoTopicRequestor(Context context, int topicId,
			OnModelProcessListener listener) {
		super(context, listener);
		mId = topicId;
		setAutoParseClass(VideoTopicListItem.class);
	}

	@Override
	protected List<NameValuePair> getRequestParamsWithoutPagething() {
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair(KEY_ID, mId + ""));
		return params;
	}

	@Override
	protected void processPageResult(PageItem item) {
		if (getPageIndex() == FIRSTPAGE) {
			return;
		}

		// 对加载的条目进行去重操作
		VideoTopicListItem requestorItem = (VideoTopicListItem) item;
		List<AbstractIFItem> videos = requestorItem.mItems;
		if (videos != null && videos.size() > 0 && mItems != null
				&& mItems.size() > 0) {
			List<AbstractIFItem> toRemoveList = new LinkedList<AbstractIFItem>();
			for (AbstractIFItem nextPageItem : videos) {
				VideoItem videosItem = (VideoItem) nextPageItem;
				for (AbstractIFItem oldVideos : mItems) {
					if (videosItem.mId == ((VideoItem) oldVideos).mId) {
						toRemoveList.add(nextPageItem);
						break;
					}
				}
			}
			requestorItem.mItems.removeAll(toRemoveList);
		}
	}

	@Override
	protected void handlePageResult(PageItem item) {
		VideoTopicListItem requestorItem = (VideoTopicListItem) item;

		if (getPageIndex() == FIRSTPAGE) {
			mTopItem = new VideoTopicTopItem(requestorItem);
		}
	}

	@Override
	public List<?> getDataList() {
		return mItems;
	}

	/**
	 * 获取顶部头图数据
	 * 
	 * @return
	 */
	public VideoTopicTopItem getTopone() {
		return mTopItem;
	}

	@Override
	protected List<NameValuePair> getRequestHeaders() {
		return null;
	}

	@Override
	protected String getRequestUrl() {
		return "http://i.ifeng.com/gototopiclist";
	}

	/**
	 * 专题顶部数据item
	 * 
	 * @author Calvin
	 * 
	 */
	public class VideoTopicTopItem {
		/** 专题名 */
		public String mTitle;
		/** 专题缩略图 */
		public String mImg;
		/** 专题简介 */
		public String mInfo;
		/** 相关咨询数(编辑录入，不保证为真实数据) */
		public int mTotal;

		/**
		 * 转换
		 * 
		 * @param requestorItem
		 */
		private VideoTopicTopItem(VideoTopicListItem requestorItem) {
			if (requestorItem == null) {
				return;
			}
			mTitle = requestorItem.mTitle;
			mImg = requestorItem.mImg;
			mInfo = requestorItem.mInfo;
			mTotal = requestorItem.mTotal;
		}
	}
}
