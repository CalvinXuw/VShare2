package com.ifeng.vshare.requestor;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

import com.ifeng.util.net.parser.AbstractIFItem;
import com.ifeng.vshare.model.NewsListItem.NewsItem;
import com.ifeng.vshare.model.NewsListItem.NewsPhotoUrlItem;
import com.ifeng.vshare.model.PhotosListItem;
import com.ifeng.vshare.model.PhotosListItem.PhotosItem;
import com.ifeng.vshare.model.PhotosListItem.PhotosUrlItem;

/**
 * 图片页面model
 * 
 * @author Calvin
 * 
 */
public class PhotoListRequestor extends BaseVSharePageRequestor {

	/** key cid */
	private static final String KEY_CID = "cid";
	/** value cid */
	private static final String VALUE_CID = "17931";

	/**
	 * 由资讯item转化为图片item
	 * 
	 * @param newsItem
	 * @return
	 */
	public static PhotosItem convertNewsItem(NewsItem newsItem) {
		PhotosItem photosItem = new PhotosItem();
		photosItem.mId = newsItem.mId;
		photosItem.mTitle = newsItem.mTitle;
		photosItem.mCreatetime = newsItem.mCreatetime;
		photosItem.mImgs = new LinkedList<PhotosUrlItem>();
		for (NewsPhotoUrlItem urlItem : newsItem.mImgArray) {
			PhotosUrlItem photosUrlItem = new PhotosUrlItem();
			photosUrlItem.mUrl = urlItem.mUrl;
			photosItem.mImgs.add(photosUrlItem);
		}
		return photosItem;
	}

	public PhotoListRequestor(Context context, OnModelProcessListener listener) {
		super(context, listener);
		setAutoParseClass(PhotosListItem.class);
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
		PhotosListItem requestorItem = (PhotosListItem) item;
		List<AbstractIFItem> photos = requestorItem.mItems;
		if (photos != null && photos.size() > 0 && mItems != null
				&& mItems.size() > 0) {
			List<AbstractIFItem> toRemoveList = new LinkedList<AbstractIFItem>();
			for (AbstractIFItem nextPageItem : photos) {
				PhotosItem photoItem = (PhotosItem) nextPageItem;
				for (AbstractIFItem oldPhotos : mItems) {
					if (photoItem.mId == ((PhotosItem) oldPhotos).mId) {
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
	}

	@Override
	public List<?> getDataList() {
		return mItems;
	}

	@Override
	protected List<NameValuePair> getRequestHeaders() {
		return null;
	}

	@Override
	protected String getRequestUrl() {
		return "http://i.ifeng.com/gotonenewslist";
	}

}
