package com.ifeng.vshare.adapter;

import android.app.Activity;

import com.ifeng.util.imagecache.ImageFetcher;
import com.ifeng.vshare.model.PhotosListItem.PhotosItem;
import com.ifeng.vshare.requestor.PhotoListRequestor;

/**
 * 图片列表adapter
 * 
 * @author Calvin
 * 
 */
public class PhotoListAdapter extends NewsComposeStyleAdapter {

	/** 数据model */
	private PhotoListRequestor mPhotosRequestor;

	/**
	 * 构造
	 * 
	 * @param activity
	 * @param imageFetcher
	 */
	public PhotoListAdapter(Activity activity, ImageFetcher imageFetcher,
			PhotoListRequestor requestor) {
		super(activity, imageFetcher);
		mPhotosRequestor = requestor;
	}

	@Override
	public int getCount() {
		return mPhotosRequestor.getDataList().size();
	}

	@Override
	public Object getItem(int position) {
		return (PhotosItem) mPhotosRequestor.getDataList().get(position);
	}

	@Override
	public int getItemSubStyle(int position) {
		return PHOTOS_STYLE;
	}

}