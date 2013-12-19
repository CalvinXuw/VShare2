package com.ifeng.vshare.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ifeng.util.Utility;
import com.ifeng.util.imagecache.ImageFetcher;
import com.ifeng.util.ui.OnSingleClickListener;
import com.ifeng.vshare.R;
import com.ifeng.vshare.activity.VideoPlayActivity;
import com.ifeng.vshare.model.VideoListItem.VideoItem;
import com.ifeng.vshare.requestor.VideoTopicRequestor;

/**
 * 相关视频的adapter
 * 
 * @author Calvin
 * 
 */
public class VideoTopicRelativeAdapter extends BaseAdapter {

	/** activity */
	private Activity mActivity;
	/** 图片加载器 */
	private ImageFetcher mImageFetcher;
	/** 数据model */
	private VideoTopicRequestor mTopicRequestor;

	/**
	 * 构造
	 * 
	 * @param activity
	 * @param imageFetcher
	 * @param requestor
	 */
	public VideoTopicRelativeAdapter(Activity activity,
			ImageFetcher imageFetcher, VideoTopicRequestor requestor) {
		mActivity = activity;
		mImageFetcher = imageFetcher;
		mTopicRequestor = requestor;
	}

	@Override
	public int getCount() {
		return mTopicRequestor.getDataList().size();
	}

	@Override
	public Object getItem(int position) {
		return mTopicRequestor.getDataList().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		VideosListHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mActivity).inflate(
					R.layout.video_list_item, null);

			holder = new VideosListHolder();

			holder.mItemView = (ViewGroup) convertView
					.findViewById(R.id.layout_item);
			holder.mVideoImage = (ImageView) convertView
					.findViewById(R.id.image_item_video);
			holder.mVideoName = (TextView) convertView
					.findViewById(R.id.text_item_video_name);
			holder.mVideoDesc = (TextView) convertView
					.findViewById(R.id.text_item_video_desc);
			holder.mVideoTime = (TextView) convertView
					.findViewById(R.id.text_item_video_time);

			convertView.setTag(holder);
			// 适配计算 视频item原尺寸为 720*190
			holder.mItemView.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT, (int) (Utility
							.getScreenWidth(mActivity) / 720 * 190)));

		} else {
			holder = (VideosListHolder) convertView.getTag();
		}

		VideoItem item = (VideoItem) getItem(position);

		mImageFetcher.loadImage(item.mImg, holder.mVideoImage,
				(int) (Utility.getScreenWidth(mActivity) / 3));
		holder.mVideoName.setText(item.mTitle);
		holder.mVideoDesc.setText(item.mDesc);
		holder.mVideoTime.setText(item.mTimeLength);
		holder.mItemView.setOnClickListener(new OnVideoItemClick(item));

		return convertView;
	}

	/**
	 * 视频子项点击事件
	 * 
	 * @author Calvin
	 * 
	 */
	private class OnVideoItemClick extends OnSingleClickListener {

		/** 视频item */
		private VideoItem mItem;

		/**
		 * 构造
		 * 
		 * @param item
		 */
		public OnVideoItemClick(VideoItem item) {
			mItem = item;
		}

		@Override
		public void onSingleClick(View v) {
			mActivity.startActivity(VideoPlayActivity.getIntent(mActivity,
					mItem.mTitle, mItem.mId));
		}

	}

	/**
	 * VideosListHolder Holder
	 * 
	 * @author Calvin
	 * 
	 */
	private class VideosListHolder {
		/** item view供重新计算适配使用 */
		private ViewGroup mItemView;

		/** image */
		private ImageView mVideoImage;

		/** name */
		private TextView mVideoName;

		/** desc */
		private TextView mVideoDesc;

		/** time */
		private TextView mVideoTime;
	}
}