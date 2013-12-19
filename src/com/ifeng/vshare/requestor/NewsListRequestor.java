package com.ifeng.vshare.requestor;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

import com.ifeng.util.net.parser.AbstractIFItem;
import com.ifeng.vshare.model.NewsListItem;
import com.ifeng.vshare.model.NewsListItem.NewsItem;
import com.ifeng.vshare.model.NewsListItem.NewsItem.NewsType;

/**
 * 资讯页面model
 * 
 * @author Calvin
 * 
 */
public class NewsListRequestor extends BaseVSharePageRequestor {

	/** key cid */
	private static final String KEY_CID = "cid";
	/** value cid */
	private static final String VALUE_CID = "17930";

	/** 头图数据 */
	private NewsItem mTopItem;
	/** 分栏后的数据 */
	private List<NewsListViewItem> mNewsListViewItems;

	public NewsListRequestor(Context context, OnModelProcessListener listener) {
		super(context, listener);
		setAutoParseClass(NewsListItem.class);
		mNewsListViewItems = new LinkedList<NewsListViewItem>();
	}

	@Override
	protected List<NameValuePair> getRequestParamsWithoutPagething() {
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair(KEY_CID, VALUE_CID));
		return params;
	}

	@Override
	protected void processPageResult(PageItem item) {
		if (getPageIndex() == FIRSTPAGE) {
			return;
		}

		// 对加载的条目进行去重操作
		NewsListItem requestorItem = (NewsListItem) item;
		List<AbstractIFItem> news = requestorItem.mItems;
		if (news != null && news.size() > 0 && mItems != null
				&& mItems.size() > 0) {
			List<AbstractIFItem> toRemoveList = new LinkedList<AbstractIFItem>();
			for (AbstractIFItem nextPageItem : news) {
				NewsItem newsItem = (NewsItem) nextPageItem;
				for (AbstractIFItem oldNews : mItems) {
					if (newsItem.mId == ((NewsItem) oldNews).mId) {
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
		NewsListItem requestorItem = (NewsListItem) item;

		if (getPageIndex() == FIRSTPAGE) {
			mTopItem = requestorItem.mTopItem;
		}

		/*
		 * 对数据进行排版
		 */
		synchronized (mDataLock) {
			mNewsListViewItems.clear();
			NewsListViewItem listViewItem = null;
			// 两列
			int columnCount = 2;
			int currentCount = 0;
			for (int i = 0; i < mItems.size(); i++) {
				if (i == 0
						|| currentCount == columnCount
						|| (i + 1) % 5 == 0
						|| ((NewsItem) mItems.get(i)).getNewsType() == NewsType.IMAGE) {
					listViewItem = new NewsListViewItem();
					mNewsListViewItems.add(listViewItem);
					currentCount = 0;
				}

				listViewItem.addNew((NewsItem) mItems.get(i));
				currentCount++;

				if ((i + 1) % 5 == 0) {
					currentCount++;
				}
			}

			// 单列
			// int columnCount = 1;
			// int currentCount = 0;
			// for (int i = 0; i < mItems.size(); i++) {
			// if (i == 0 || currentCount == columnCount) {
			// listViewItem = new NewsListViewItem();
			// mNewsListViewItems.add(listViewItem);
			// currentCount = 0;
			// }
			//
			// listViewItem.addNew((NewsItem) mItems.get(i));
			// currentCount++;
			// }
		}
	}

	@Override
	public List<?> getDataList() {
		synchronized (mDataLock) {
			return mNewsListViewItems;
		}
	}

	/**
	 * 获取顶部头图数据
	 * 
	 * @return
	 */
	public NewsItem getTopone() {
		return mTopItem;
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
	public class NewsListViewItem {
		/** 新闻 */
		private LinkedList<NewsItem> mNews;

		/**
		 * 构造
		 */
		public NewsListViewItem() {
			mNews = new LinkedList<NewsItem>();
		}

		/**
		 * 为页面展示提供数据
		 * 
		 * @return
		 */
		public LinkedList<NewsItem> getNews() {
			return mNews;
		}

		/**
		 * 添加一条新闻
		 * 
		 * @param item
		 */
		private void addNew(NewsItem item) {
			mNews.add(item);
		}
	}
}
