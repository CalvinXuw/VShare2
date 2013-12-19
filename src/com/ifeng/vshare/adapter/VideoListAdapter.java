package com.ifeng.vshare.adapter;

import java.util.List;

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
import com.ifeng.vshare.activity.VideoTopicActivity;
import com.ifeng.vshare.model.VideoListItem.VideoItem;
import com.ifeng.vshare.model.VideoListItem.VideoItem.VideoType;
import com.ifeng.vshare.requestor.VideoListRequestor;
import com.ifeng.vshare.requestor.VideoListRequestor.VideosListViewItem;

/**
 * 视频列表适配器
 * 
 * @author Calvin
 * 
 */
public class VideoListAdapter extends BaseAdapter {

	/** activity */
	private Activity mActivity;
	/** 图片加载器 */
	private ImageFetcher mImageFetcher;
	/** 数据model */
	private VideoListRequestor mVideosRequestor;

	/**
	 * 构造
	 * 
	 * @param activity
	 * @param imageFetcher
	 * @param requestor
	 */
	public VideoListAdapter(Activity activity, ImageFetcher imageFetcher,
			VideoListRequestor requestor) {
		mActivity = activity;
		mImageFetcher = imageFetcher;
		mVideosRequestor = requestor;
	}

	@Override
	public int getCount() {
		return mVideosRequestor.getDataList().size();
	}

	@Override
	public Object getItem(int position) {
		return mVideosRequestor.getDataList().get(position);
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
			holder.mTopicSign = convertView
					.findViewById(R.id.text_item_video_topic);

			convertView.setTag(holder);
			// 适配计算 视频item原尺寸为 720*190
			holder.mItemView.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT, (int) (Utility
							.getScreenWidth(mActivity) / 720 * 190)));

		} else {
			holder = (VideosListHolder) convertView.getTag();
		}

		VideosListViewItem item = (VideosListViewItem) getItem(position);
		List<VideoItem> videos = item.getVideos();
		VideoItem videosItem = videos.get(0);

		mImageFetcher.loadImage(videosItem.mImg, holder.mVideoImage,
				(int) (Utility.getScreenWidth(mActivity) / 3));
		holder.mVideoName.setText(videosItem.mTitle);
		holder.mVideoDesc.setText(videosItem.mDesc);
		holder.mVideoTime.setText(videosItem.mTimeLength);
		holder.mItemView.setOnClickListener(new OnVideoItemClick(mActivity,
				videosItem));

		if (videosItem.getVideoType() == VideoType.TOPIC) {
			holder.mTopicSign.setVisibility(View.VISIBLE);
		} else {
			holder.mTopicSign.setVisibility(View.GONE);
		}

		return convertView;
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

		/** 专题标识 */
		private View mTopicSign;
	}

	/**
	 * 视频子项点击事件
	 * 
	 * @author Calvin
	 * 
	 */
	public static class OnVideoItemClick extends OnSingleClickListener {

		/** activity */
		private Activity mActivity;
		/** 视频item */
		private VideoItem mItem;

		/**
		 * 构造
		 * 
		 * @param item
		 */
		public OnVideoItemClick(Activity activity, VideoItem item) {
			mActivity = activity;
			mItem = item;
		}

		@Override
		public void onSingleClick(View v) {
			if (mItem.getVideoType() == VideoType.TOPIC) {
				mActivity.startActivity(VideoTopicActivity.getIntent(mActivity,
						mItem.mTopicId));
			} else {
				mActivity.startActivity(VideoPlayActivity.getIntent(mActivity,
						mItem.mTitle, mItem.mId));
			}
		}

	}
}