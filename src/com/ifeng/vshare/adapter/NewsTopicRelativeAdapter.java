package com.ifeng.vshare.adapter;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ifeng.util.Utility;
import com.ifeng.util.imagecache.ImageFetcher;
import com.ifeng.util.ui.OnSingleClickListener;
import com.ifeng.vshare.R;
import com.ifeng.vshare.activity.NewsDetailActivity;
import com.ifeng.vshare.activity.PhotoDetailActivity;
import com.ifeng.vshare.activity.VideoPlayActivity;
import com.ifeng.vshare.model.NewsListItem.NewsItem;
import com.ifeng.vshare.model.NewsListItem.NewsItem.NewsType;
import com.ifeng.vshare.requestor.NewsTopicRequestor;

/**
 * 相关资讯的adapter
 * 
 * @author Calvin
 * 
 */
public class NewsTopicRelativeAdapter extends BaseAdapter {

	/** activity */
	private Activity mActivity;
	/** 图片加载器 */
	private ImageFetcher mImageFetcher;
	/** 数据model */
	private NewsTopicRequestor mTopicRequestor;

	/**
	 * 构造
	 * 
	 * @param activity
	 * @param requestor
	 */
	public NewsTopicRelativeAdapter(Activity activity,
			ImageFetcher imageFetcher, NewsTopicRequestor requestor) {
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
		return (NewsItem) mTopicRequestor.getDataList().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		NewsListViewHolder holder = null;
		if (convertView == null) {
			convertView = mActivity.getLayoutInflater().inflate(
					R.layout.topic_list_item, null);
			holder = new NewsListViewHolder();

			holder.mItemView = (ViewGroup) convertView
					.findViewById(R.id.layout_item);
			holder.mImage = (ImageView) convertView
					.findViewById(R.id.image_item_topic);
			holder.mImageGroup = convertView
					.findViewById(R.id.layout_item_topic_image);
			holder.mTitle = (TextView) convertView
					.findViewById(R.id.text_item_topic_title);
			holder.mTime = (TextView) convertView
					.findViewById(R.id.text_item_topic_time);
			holder.mType = (ImageView) convertView
					.findViewById(R.id.image_item_topic_type);

			convertView.setTag(holder);

			// 适配计算 视频item原尺寸为 720*180
			RelativeLayout.LayoutParams oldParams = (RelativeLayout.LayoutParams) holder.mItemView
					.getLayoutParams();
			oldParams.height = (int) (Utility.getScreenWidth(mActivity) / 720 * 180);
			holder.mItemView.setLayoutParams(oldParams);

		} else {
			holder = (NewsListViewHolder) convertView.getTag();
		}

		NewsItem relativeNewsItem = (NewsItem) getItem(position);

		holder.mTitle.setText(relativeNewsItem.mTitle);
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm",
				Locale.CHINA);
		holder.mTime.setText(dateFormat.format(
				relativeNewsItem.mCreatetime * 1000).toString());

		// 部分资源没有图片，则直接隐藏
		if (relativeNewsItem.mImgArray != null
				&& relativeNewsItem.mImgArray.size() > 0) {
			mImageFetcher.loadImage(relativeNewsItem.mImgArray.get(0).mUrl,
					holder.mImage,
					(int) (Utility.getScreenWidth(mActivity) / 3));
			holder.mImageGroup.setVisibility(View.VISIBLE);
		} else if (!TextUtils.isEmpty(relativeNewsItem.mImg)) {
			mImageFetcher.loadImage(relativeNewsItem.mImg, holder.mImage,
					(int) (Utility.getScreenWidth(mActivity) / 3));
			holder.mImageGroup.setVisibility(View.VISIBLE);
		} else {
			holder.mImageGroup.setVisibility(View.GONE);
		}

		// 根据类型不同，显示图片或者视频的小图标
		if (relativeNewsItem.getNewsType() == NewsType.IMAGE) {
			holder.mType
					.setBackgroundResource(R.drawable.image_topic_type_photo);
		} else if (relativeNewsItem.getNewsType() == NewsType.VIDEO) {
			holder.mType
					.setBackgroundResource(R.drawable.image_topic_type_video);
		} else if (relativeNewsItem.getNewsType() == NewsType.NEWS) {
			holder.mType.setBackgroundColor(mActivity.getResources().getColor(
					R.color.transparent));
		}

		// 根据不同的类型，跳转到不同的详情页
		convertView.setOnClickListener(new OnNewsItemClickListener(
				relativeNewsItem));

		return convertView;
	}

	/**
	 * 资讯列表页面的布局容器
	 * 
	 * @author Calvin
	 * 
	 */
	private class NewsListViewHolder {

		/** 容器 */
		public ViewGroup mItemView;
		/** 可能出现的image */
		public ImageView mImage;
		/** 可能出现的image group */
		public View mImageGroup;
		/** 标题 */
		public TextView mTitle;
		/** 时间 */
		public TextView mTime;
		/** type标识 */
		public View mType;
	}

	/**
	 * 资讯单条item点击事件
	 * 
	 * @author Calvin
	 * 
	 */
	private class OnNewsItemClickListener extends OnSingleClickListener {

		/** item中获取信息 */
		private NewsItem mItem;

		/**
		 * 构造
		 * 
		 * @param item
		 */
		public OnNewsItemClickListener(NewsItem relativeNewsItem) {
			mItem = relativeNewsItem;
		}

		@Override
		public void onSingleClick(View v) {
			// 根据类型不同，显示图片或者视频的小图标
			if (mItem.getNewsType() == NewsType.IMAGE) {
				mActivity.startActivity(PhotoDetailActivity.getIntent(
						mActivity, mItem.mTitle, mItem.mId));
			} else if (mItem.getNewsType() == NewsType.NEWS
					|| mItem.getNewsType() == NewsType.VIDEO) {
				mActivity.startActivity(NewsDetailActivity.getIntent(mActivity,
						mItem.mId, mItem.mTitle, mItem.mCommentKey));
			} else if (mItem.getNewsType() == NewsType.VIDEO) {
				// no use
				mActivity.startActivity(VideoPlayActivity.getIntent(mActivity,
						mItem.mTitle, mItem.mId));
			}
		}

	}
}
