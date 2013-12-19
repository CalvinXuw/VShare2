package com.ifeng.vshare.adapter;

import android.app.Activity;

import com.ifeng.util.imagecache.ImageFetcher;
import com.ifeng.vshare.model.NewsListItem.NewsItem.NewsType;
import com.ifeng.vshare.requestor.NewsListRequestor;
import com.ifeng.vshare.requestor.NewsListRequestor.NewsListViewItem;
import com.ifeng.vshare.requestor.PhotoListRequestor;

/**
 * 资讯adapter
 * 
 * @author Calvin
 * 
 */
public class NewsListAdapter extends NewsComposeStyleAdapter {

	/** 数据model */
	private NewsListRequestor mNewsRequestor;

	/**
	 * 构造
	 * 
	 * @param activity
	 * @param imageFetcher
	 * @param requestor
	 */
	public NewsListAdapter(Activity activity, ImageFetcher imageFetcher,
			NewsListRequestor requestor) {
		super(activity, imageFetcher);
		mNewsRequestor = requestor;
	}

	@Override
	public int getCount() {
		return mNewsRequestor.getDataList().size();
	}

	@Override
	public Object getItem(int position) {
		NewsListViewItem listViewItem = (NewsListViewItem) mNewsRequestor
				.getDataList().get(position);
		if (listViewItem.getNews().get(0).getNewsType() == NewsType.IMAGE) {
			return PhotoListRequestor.convertNewsItem(listViewItem.getNews()
					.get(0));
		}
		return listViewItem;
	}

	@Override
	public int getItemSubStyle(int position) {
		try {
			NewsListViewItem listViewItem = (NewsListViewItem) mNewsRequestor
					.getDataList().get(position);
			if (listViewItem.getNews().get(0).getNewsType() == NewsType.IMAGE) {
				return PHOTOS_STYLE;
			}
			return NEWS_STYLE;
		} catch (Exception e) {
			return NEWS_STYLE;
		}
	}

}