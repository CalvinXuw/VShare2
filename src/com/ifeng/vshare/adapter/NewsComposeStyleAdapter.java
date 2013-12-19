package com.ifeng.vshare.adapter;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ifeng.util.Utility;
import com.ifeng.util.imagecache.ImageFetcher;
import com.ifeng.util.ui.OnSingleClickListener;
import com.ifeng.vshare.R;
import com.ifeng.vshare.activity.NewsDetailActivity;
import com.ifeng.vshare.activity.NewsTopicActivity;
import com.ifeng.vshare.activity.PhotoDetailActivity;
import com.ifeng.vshare.activity.VideoPlayActivity;
import com.ifeng.vshare.model.NewsListItem.NewsItem;
import com.ifeng.vshare.model.NewsListItem.NewsItem.NewsType;
import com.ifeng.vshare.model.PhotosListItem.PhotosItem;
import com.ifeng.vshare.model.PhotosListItem.PhotosUrlItem;
import com.ifeng.vshare.requestor.NewsListRequestor.NewsListViewItem;

/**
 * 要闻部分组合样式adapter，包含资讯及图片两种样式
 * 
 * @author Calvin
 * 
 */
public abstract class NewsComposeStyleAdapter extends ComposeStyleAdapter {

	/** 资讯样式id */
	protected static final int NEWS_STYLE = 0;
	/** 图片样式id */
	protected static final int PHOTOS_STYLE = 1;

	/** activity */
	private Activity mActivity;
	/** image fetcher */
	private ImageFetcher mImageFetcher;

	/**
	 * 构造
	 * 
	 * @param activity
	 * @param imageFetcher
	 */
	public NewsComposeStyleAdapter(Activity activity,
			ImageFetcher imageFetcher) {
		mActivity = activity;
		mImageFetcher = imageFetcher;
	}

	@Override
	public int getSubStyleCount() {
		// 资讯、图片
		return 2;
	}

	@Override
	public View getSubStyleOrginView(int style) {
		View convertView = null;
		if (style == NEWS_STYLE) {
			convertView = getNewsStyleOrginView();
		} else if (style == PHOTOS_STYLE) {
			convertView = getPhotosStyleOrginView();
		}
		return convertView;
	}

	@Override
	public View getSubStyleItemView(View converView, int style, Object itemRes) {
		if (style == NEWS_STYLE) {
			getNewsStyleItemView(converView, (NewsListViewItem) itemRes);
		} else if (style == PHOTOS_STYLE) {
			getPhotosStyleItemView(converView, (PhotosItem) itemRes);
		}
		return converView;
	}

	/**
	 * 获取资讯样式的空白子视图
	 * 
	 * @return
	 */
	private View getNewsStyleOrginView() {
		View convertView = mActivity.getLayoutInflater().inflate(
				R.layout.news_list_item, null);
		NewsListViewHolder holder = new NewsListViewHolder();

		holder.mItemView = (ViewGroup) convertView
				.findViewById(R.id.layout_item);
		holder.mLeftGroup = convertView
				.findViewById(R.id.layout_item_news_left);
		holder.mRightGroup = convertView
				.findViewById(R.id.layout_item_news_right);

		holder.mSingleNewImage = (ImageView) convertView
				.findViewById(R.id.image_item_news_image);
		holder.mSingleNewImageGroup = convertView
				.findViewById(R.id.layout_item_news_image);
		holder.mLeftTitle = (TextView) convertView
				.findViewById(R.id.text_item_news_title_left);
		holder.mLeftTime = (TextView) convertView
				.findViewById(R.id.text_item_news_time_left);
		holder.mLeftSource = (TextView) convertView
				.findViewById(R.id.text_item_news_source_left);
		holder.mLeftVideoTag = convertView
				.findViewById(R.id.image_item_news_type_left);
		holder.mRightTitle = (TextView) convertView
				.findViewById(R.id.text_item_news_title_right);
		holder.mRightTime = (TextView) convertView
				.findViewById(R.id.text_item_news_time_right);
		holder.mRightSource = (TextView) convertView
				.findViewById(R.id.text_item_news_source_right);
		holder.mRightVideoTag = convertView
				.findViewById(R.id.image_item_news_type_right);

		convertView.setTag(holder);

		// 适配计算 视频item原尺寸为 720*180 Density=2，同时为了保证小屏幕正常显示，高度不低于设计稿
		RelativeLayout.LayoutParams oldParams = (RelativeLayout.LayoutParams) holder.mItemView
				.getLayoutParams();
		oldParams.height = (int) (Utility.getScreenWidth(mActivity) / 720 * 180);
		if (Utility.getScreenWidth(mActivity) < 720) {
			oldParams.height = (int) (90 * Utility.getDensity(mActivity));
		}
		holder.mItemView.setLayoutParams(oldParams);

		return convertView;
	}

	/**
	 * 获取图片样式的空白子视图
	 * 
	 * @return
	 */
	private View getPhotosStyleOrginView() {
		View convertView = LayoutInflater.from(mActivity).inflate(
				R.layout.photos_list_item, null);
		PhotosListHolder holder = new PhotosListHolder();
		holder.mTitle = (TextView) convertView
				.findViewById(R.id.text_item_image_title);
		holder.mTime = (TextView) convertView
				.findViewById(R.id.text_item_image_time);
		holder.mCount = (TextView) convertView
				.findViewById(R.id.text_item_image_count);

		holder.mGroupThree = (ViewGroup) convertView
				.findViewById(R.id.layout_item_image_three);
		holder.mGroupTwo = (ViewGroup) convertView
				.findViewById(R.id.layout_item_image_two);
		holder.mGroupOne = (ViewGroup) convertView
				.findViewById(R.id.layout_item_image_one);

		holder.mGroupThreeImage1 = (ImageView) convertView
				.findViewById(R.id.image_item_image_three_image1);
		holder.mGroupThreeImage2 = (ImageView) convertView
				.findViewById(R.id.image_item_image_three_image2);
		holder.mGroupThreeImage3 = (ImageView) convertView
				.findViewById(R.id.image_item_image_three_image3);

		holder.mGroupTwoImage1 = (ImageView) convertView
				.findViewById(R.id.image_item_image_two_image1);
		holder.mGroupTwoImage2 = (ImageView) convertView
				.findViewById(R.id.image_item_image_two_image2);

		holder.mGroupOneImage = (ImageView) convertView
				.findViewById(R.id.image_item_image_one_image1);

		convertView.setTag(holder);

		LinearLayout.LayoutParams layoutParams = null;
		// 适配计算 三张图组合原尺寸为 720*145
		layoutParams = (LayoutParams) holder.mGroupThree.getLayoutParams();
		layoutParams.height = (int) (Utility.getScreenWidth(mActivity) / 720 * (145 + 20));
		holder.mGroupThree.setLayoutParams(layoutParams);
		// 适配计算 两张图组合原尺寸为 720*220
		layoutParams = (LayoutParams) holder.mGroupTwo.getLayoutParams();
		layoutParams.height = (int) (Utility.getScreenWidth(mActivity) / 720 * (220 + 20));
		holder.mGroupTwo.setLayoutParams(layoutParams);
		// 适配计算 一张图组合原尺寸为 720*450
		layoutParams = (LayoutParams) holder.mGroupOne.getLayoutParams();
		layoutParams.height = (int) (Utility.getScreenWidth(mActivity) / 720 * (450 + 20));
		holder.mGroupOne.setLayoutParams(layoutParams);

		return convertView;
	}

	/**
	 * 填充资讯样式itemview内容信息
	 * 
	 * @param convertView
	 * @param newsListViewItem
	 * @return
	 */
	private View getNewsStyleItemView(View convertView,
			NewsListViewItem newsListViewItem) {
		if (newsListViewItem == null) {
			return convertView;
		}
		NewsListViewHolder holder = (NewsListViewHolder) convertView.getTag();

		// 若为两条新闻的子项
		if (newsListViewItem.getNews().size() > 1) {
			holder.mRightGroup.setVisibility(View.VISIBLE);
			holder.mSingleNewImageGroup.setVisibility(View.GONE);

			NewsItem rightItem = newsListViewItem.getNews().get(1);

			holder.mRightTitle.setText(rightItem.mTitle);
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm",
					Locale.CHINA);
			holder.mRightTime.setText(dateFormat.format(
					rightItem.mCreatetime * 1000).toString());
			// 对于专题类型的新闻，在来源位置进行显示区分
			if (rightItem.getNewsType() == NewsType.TOPIC) {
				holder.mRightSource.setVisibility(View.VISIBLE);
				holder.mRightVideoTag.setVisibility(View.GONE);

				holder.mRightSource.setText(R.string.title_topic);
				holder.mRightSource.setTextColor(mActivity.getResources()
						.getColor(R.color.font_source_tips_topic));
				holder.mRightSource.setBackgroundColor(mActivity.getResources()
						.getColor(R.color.background_source_tips_topic));
			} else if (rightItem.getNewsType() == NewsType.VIDEO) {
				holder.mRightSource.setVisibility(View.GONE);
				holder.mRightVideoTag.setVisibility(View.VISIBLE);
			} else {
				holder.mRightSource.setVisibility(View.VISIBLE);
				holder.mRightVideoTag.setVisibility(View.GONE);

				holder.mRightSource.setText(rightItem.mSource);
				holder.mRightSource.setTextColor(mActivity.getResources()
						.getColor(R.color.font_list_tag));
				holder.mRightSource.setBackgroundColor(mActivity.getResources()
						.getColor(R.color.transparent));
			}

			holder.mRightGroup.setOnClickListener(new OnNewsItemClickListener(
					mActivity, rightItem));
		} else {
			// 单条新闻展示时，显示一张新闻缩略图
			holder.mRightGroup.setVisibility(View.GONE);
			holder.mSingleNewImageGroup.setVisibility(View.VISIBLE);
		}

		NewsItem leftItem = newsListViewItem.getNews().get(0);

		holder.mLeftTitle.setText(leftItem.mTitle);
		holder.mLeftTime.setText(DateFormat.format("MM-dd hh:mm",
				leftItem.mCreatetime * 1000).toString());

		if (leftItem.getNewsType() == NewsType.TOPIC) {
			holder.mLeftSource.setVisibility(View.VISIBLE);
			holder.mLeftVideoTag.setVisibility(View.GONE);

			holder.mLeftSource.setText(R.string.title_topic);
			holder.mLeftSource.setTextColor(mActivity.getResources().getColor(
					R.color.font_source_tips_topic));
			holder.mLeftSource.setBackgroundColor(mActivity.getResources()
					.getColor(R.color.background_source_tips_topic));
		} else if (leftItem.getNewsType() == NewsType.VIDEO) {
			holder.mLeftSource.setVisibility(View.GONE);
			holder.mLeftVideoTag.setVisibility(View.VISIBLE);
		} else {
			holder.mLeftSource.setVisibility(View.VISIBLE);
			holder.mLeftVideoTag.setVisibility(View.GONE);

			holder.mLeftSource.setText(leftItem.mSource);
			holder.mLeftSource.setTextColor(mActivity.getResources().getColor(
					R.color.font_list_tag));
			holder.mLeftSource.setBackgroundColor(mActivity.getResources()
					.getColor(R.color.transparent));
		}

		// 对于不存在缩略图的新闻则隐藏缩略图
		int imageSize = (int) (Utility.getScreenWidth(mActivity)
				/ Utility.getDensity(mActivity) / 3);
		if (!TextUtils.isEmpty(leftItem.mImg)) {
			mImageFetcher.loadImage(leftItem.mImg, holder.mSingleNewImage,
					imageSize);
		} else {
			holder.mSingleNewImageGroup.setVisibility(View.GONE);
		}

		holder.mLeftGroup.setOnClickListener(new OnNewsItemClickListener(
				mActivity, leftItem));

		return convertView;
	}

	/**
	 * 填充图片样式itemview内容
	 * 
	 * @param convertView
	 * @param item
	 * @return
	 */
	private View getPhotosStyleItemView(View convertView, final PhotosItem item) {
		if (item == null) {
			return convertView;
		}
		final PhotosListHolder holder = (PhotosListHolder) convertView.getTag();
		holder.mTitle.setText(item.mTitle);
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm",
				Locale.CHINA);
		holder.mTime.setText(dateFormat.format(item.mCreatetime * 1000)
				.toString());
		holder.mCount.setText(String.format(
				mActivity.getString(R.string.photo_count), item.mCount));

		/*
		 * 分别针对有多张图片，有两张图片和只有一张图片的情况，而layout当中每种情况分配了一组ViewGroup，其中高度为指定高度，
		 * 图片scaleType为填充
		 */
		List<PhotosUrlItem> urls = item.mImgs;
		if (urls.size() > 2) {
			int imageSize = (int) (Utility.getScreenWidth(mActivity)
					/ Utility.getDensity(mActivity) / 3);
			mImageFetcher.loadImage(urls.get(0).mUrl, holder.mGroupThreeImage1,
					imageSize);
			mImageFetcher.loadImage(urls.get(1).mUrl, holder.mGroupThreeImage2,
					imageSize);
			mImageFetcher.loadImage(urls.get(2).mUrl, holder.mGroupThreeImage3,
					imageSize);

			holder.mGroupThree.setVisibility(View.VISIBLE);
			holder.mGroupTwo.setVisibility(View.GONE);
			holder.mGroupOne.setVisibility(View.GONE);
		} else if (urls.size() > 1) {
			int imageSize = (int) (Utility.getScreenWidth(mActivity)
					/ Utility.getDensity(mActivity) / 2);
			mImageFetcher.loadImage(urls.get(0).mUrl, holder.mGroupTwoImage1,
					imageSize);
			mImageFetcher.loadImage(urls.get(1).mUrl, holder.mGroupTwoImage2,
					imageSize);

			holder.mGroupThree.setVisibility(View.GONE);
			holder.mGroupTwo.setVisibility(View.VISIBLE);
			holder.mGroupOne.setVisibility(View.GONE);
		} else if (urls.size() > 0) {
			int imageSize = (int) (Utility.getScreenWidth(mActivity)
					/ Utility.getDensity(mActivity) * 1.5);
			holder.mGroupOneImage
					.setImageResource(R.drawable.default_image_large_light);
			mImageFetcher.loadImage(urls.get(0).mUrl, holder.mGroupOneImage,
					imageSize);

			holder.mGroupThree.setVisibility(View.GONE);
			holder.mGroupTwo.setVisibility(View.GONE);
			holder.mGroupOne.setVisibility(View.VISIBLE);
		}

		convertView.setOnClickListener(new OnSingleClickListener() {

			@Override
			public void onSingleClick(View v) {
				mActivity.startActivity(PhotoDetailActivity.getIntent(
						mActivity, item.mTitle, item.mId));
			}
		});

		return convertView;
	}

	/**
	 * 资讯列表页面的布局容器
	 * 
	 * @author Calvin
	 * 
	 */
	private class NewsListViewHolder {
		/** item view */
		public ViewGroup mItemView;
		/** 左item */
		public View mLeftGroup;
		/** 右item */
		public View mRightGroup;

		/*
		 * 左边item位置
		 */

		/** 单条情况下，展示出现的image */
		public ImageView mSingleNewImage;
		/** 单条情况下，展示出现的image group */
		public View mSingleNewImageGroup;
		/** 左 标题 */
		public TextView mLeftTitle;
		/** 左 时间 */
		public TextView mLeftTime;
		/** 左 来源 */
		public TextView mLeftSource;
		/** 左 视频标记 */
		public View mLeftVideoTag;

		/*
		 * 右边item位置
		 */
		/** 右 标题 */
		public TextView mRightTitle;
		/** 右 时间 */
		public TextView mRightTime;
		/** 右 来源 */
		public TextView mRightSource;
		/** 左 视频标记 */
		public View mRightVideoTag;
	}

	/**
	 * PhotoList Holder 由于内容问题，导致图片大小比例均不相同，故采取极端措施，将图片Item分为三组，应对不同数量的图片类型
	 * 
	 * @author Calvin
	 * 
	 */
	private class PhotosListHolder {
		/** 标题 */
		private TextView mTitle;
		/** 时间 */
		private TextView mTime;
		/** 数量 */
		private TextView mCount;

		/** 组1 包含三个ImageView */
		private ViewGroup mGroupThree;
		/** 组2 包含两个ImageView */
		private ViewGroup mGroupTwo;
		/** 组3 包含一个ImageView */
		private ViewGroup mGroupOne;

		/** left */
		private ImageView mGroupThreeImage1;
		/** middle */
		private ImageView mGroupThreeImage2;
		/** right */
		private ImageView mGroupThreeImage3;

		/** left */
		private ImageView mGroupTwoImage1;
		/** right */
		private ImageView mGroupTwoImage2;

		/** middle */
		private ImageView mGroupOneImage;
	}

	/**
	 * 资讯单条item点击事件
	 * 
	 * @author Calvin
	 * 
	 */
	public static class OnNewsItemClickListener extends OnSingleClickListener {

		/** activity */
		private Activity mActivity;
		/** item中获取信息 */
		private NewsItem mItem;

		/**
		 * 构造
		 * 
		 * @param item
		 */
		public OnNewsItemClickListener(Activity activity, NewsItem item) {
			mActivity = activity;
			mItem = item;
		}

		@Override
		public void onSingleClick(View v) {
			if (mItem.getNewsType() == NewsType.TOPIC) {
				mActivity.startActivity(NewsTopicActivity.getIntent(mActivity,
						mItem.mTopicId));
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
