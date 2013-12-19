package com.ifeng.vshare.fragment;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.ifeng.thirdparty.photoview.PhotoViewAttacher;
import com.ifeng.util.Utility;
import com.ifeng.util.imagecache.ImageWorker;
import com.ifeng.util.imagecache.ImageWorker.ImageDrawableCallback;
import com.ifeng.util.model.AbstractModel;
import com.ifeng.util.ui.RoundProgressImageView;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareFragment;
import com.ifeng.vshare.model.ClubDetailItem;
import com.ifeng.vshare.model.ClubDetailItem.ClubPhotoItem;
import com.ifeng.vshare.model.LectureDetailItem;
import com.ifeng.vshare.model.LectureDetailItem.LectureDetailPhotoItem;
import com.ifeng.vshare.model.PhotosDetailItem;
import com.ifeng.vshare.model.PhotosDetailItem.PhotosDetailInnerItem;

/**
 * 多张图片的滑动展示fragment，其中入口包括图片列表页面以及俱乐部页面和大讲堂活动页面。
 * 
 * @author qianzy
 * @time 2013-6-6 下午3:35:57
 */
public class PhotoDetailFragment extends VShareFragment {

	/** key come from */
	private static final String KEY_COMEFROM = "come_from";
	/** key title */
	private static final String KEY_TITLE = "title";
	/** key photodetail item */
	private static final String KEY_PHOTODETAIL_ITEM = "photodetail_item";
	/** key clubdetail item */
	private static final String KEY_CLUBDETAIL_ITEM = "clubdetail_item";
	/** key lecturedetail item */
	private static final String KEY_LECTUREDETAIL_ITEM = "lecturedetail_item";

	/**
	 * 来源
	 * 
	 * @author Calvin
	 * 
	 */
	private enum ComeFromType implements Serializable {
		PHOTOLIST, CLUB, LECTURE
	}

	/**
	 * 获取来源自图片详情activity的实例
	 * 
	 * @param title
	 * @param item
	 * @return
	 */
	public static PhotoDetailFragment getInstance(String title,
			PhotosDetailItem item) {
		PhotoDetailFragment detailFragment = new PhotoDetailFragment();
		Bundle arg = new Bundle();
		arg.putSerializable(KEY_COMEFROM, ComeFromType.PHOTOLIST);
		arg.putString(KEY_TITLE, title);
		arg.putSerializable(KEY_PHOTODETAIL_ITEM, item);
		detailFragment.setArguments(arg);
		return detailFragment;
	}

	/**
	 * 获取来源自club详情activity的实例
	 * 
	 * @param title
	 * @param item
	 * @return
	 */
	public static PhotoDetailFragment getInstance(String title,
			ClubDetailItem item) {
		PhotoDetailFragment detailFragment = new PhotoDetailFragment();
		Bundle arg = new Bundle();
		arg.putSerializable(KEY_COMEFROM, ComeFromType.CLUB);
		arg.putString(KEY_TITLE, title);
		arg.putSerializable(KEY_CLUBDETAIL_ITEM, item);
		detailFragment.setArguments(arg);
		return detailFragment;
	}

	/**
	 * 获取来源自lecture详情activity的实例
	 * 
	 * @param title
	 * @param item
	 * @return
	 */
	public static PhotoDetailFragment getInstance(String title,
			LectureDetailItem item) {
		PhotoDetailFragment detailFragment = new PhotoDetailFragment();
		Bundle arg = new Bundle();
		arg.putSerializable(KEY_COMEFROM, ComeFromType.LECTURE);
		arg.putString(KEY_TITLE, title);
		arg.putSerializable(KEY_LECTUREDETAIL_ITEM, item);
		detailFragment.setArguments(arg);
		return detailFragment;
	}

	/** 来源 */
	private ComeFromType mComeFromType;
	/** 标题 */
	private String mTitle;
	/** 图片详情数据源 */
	private PhotosDetailItem mPhotosDetailItem;
	/** club详情数据源 */
	private ClubDetailItem mClubDetailItem;
	/** lecture详情数据源 */
	private LectureDetailItem mLectureDetailItem;

	/** 图片详情文字 */
	private View mMessageView;
	/** 标题 */
	private TextView mMessageTitle;
	/** 索引 */
	private TextView mMessageIndex;
	/** 介绍文字 */
	private TextView mMessage;

	/**
	 * 构造
	 */
	public PhotoDetailFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mComeFromType = (ComeFromType) getArguments().getSerializable(
				KEY_COMEFROM);
		if (mComeFromType == null) {
			throw new IllegalArgumentException("must set a comefrom type");
		}

		if (mComeFromType == ComeFromType.PHOTOLIST) {
			mTitle = getArguments().getString(KEY_TITLE);
			mPhotosDetailItem = (PhotosDetailItem) getArguments()
					.getSerializable(KEY_PHOTODETAIL_ITEM);
		} else if (mComeFromType == ComeFromType.CLUB) {
			mTitle = getArguments().getString(KEY_TITLE);
			mClubDetailItem = (ClubDetailItem) getArguments().getSerializable(
					KEY_CLUBDETAIL_ITEM);
		} else if (mComeFromType == ComeFromType.LECTURE) {
			mTitle = getArguments().getString(KEY_TITLE);
			mLectureDetailItem = (LectureDetailItem) getArguments()
					.getSerializable(KEY_LECTUREDETAIL_ITEM);
		}

		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater
				.inflate(R.layout.photos_detail, container, false);

		// 图片提示文字
		mMessageView = layout.findViewById(R.id.layout_photo_message);
		mMessageTitle = (TextView) layout
				.findViewById(R.id.text_photo_detail_title);
		mMessageIndex = (TextView) layout
				.findViewById(R.id.text_photo_detail_index);
		mMessage = (TextView) layout
				.findViewById(R.id.text_photo_detail_message);

		List<String> urls = new LinkedList<String>();
		List<String> messages = new LinkedList<String>();
		if (mComeFromType == ComeFromType.PHOTOLIST) {
			List<PhotosDetailInnerItem> photoItems = mPhotosDetailItem.mPhotoItems;
			for (int i = 0; i < photoItems.size(); i++) {
				urls.add(photoItems.get(i).mImg);
				messages.add(!TextUtils.isEmpty(photoItems.get(i).mInfo) ? photoItems
						.get(i).mInfo : "");
			}
			mMessageView.setVisibility(View.VISIBLE);
		} else if (mComeFromType == ComeFromType.CLUB) {
			List<ClubPhotoItem> photoItems = mClubDetailItem.mPhotos;
			for (int i = 0; i < photoItems.size(); i++) {
				urls.add(photoItems.get(i).mUrl);
			}
			mMessageView.setVisibility(View.GONE);
		} else if (mComeFromType == ComeFromType.LECTURE) {
			List<LectureDetailPhotoItem> photoItems = mLectureDetailItem.mActivityPhotos;
			for (int i = 0; i < photoItems.size(); i++) {
				urls.add(photoItems.get(i).mImage);
			}
			mMessageView.setVisibility(View.GONE);
		}

		ViewPager pager = (ViewPager) layout.findViewById(R.id.pager_content);
		PhotosPagerAdapter adapter = new PhotosPagerAdapter(
				getFragmentManager(), urls, messages);
		pager.setAdapter(adapter);
		pager.setOnPageChangeListener(adapter);
		if (adapter.getCount() > 0) {
			adapter.onPageSelected(0);
		}

		return layout;
	}

	/**
	 * 图片pager adapter，其中 {@link FragmentStatePagerAdapter}和
	 * {@link FragmentPagerAdapter}不同，在fragment不显示于界面时，
	 * {@link FragmentStatePagerAdapter}会调用fragment的destory方法将其销毁，而
	 * {@link FragmentPagerAdapter}则只调用destoryview方法，如果需要大量页卡时会产生严重的内存占用现象。
	 * 
	 * @author Calvin
	 * 
	 */
	private class PhotosPagerAdapter extends FragmentStatePagerAdapter
			implements OnPageChangeListener {

		/** 资源url */
		private List<String> mUrls;
		/** 图片描述 */
		private List<String> mMessageString;

		public PhotosPagerAdapter(FragmentManager fm, List<String> urls,
				List<String> message) {
			super(fm);
			mUrls = urls;
			mMessageString = message;
		}

		@Override
		public int getCount() {
			return mUrls.size();
		}

		@Override
		public Fragment getItem(int position) {
			PhotosSinglePageFragment singlePageFragment = new PhotosSinglePageFragment();
			Bundle argBundle = new Bundle();
			argBundle.putString(PhotosSinglePageFragment.KEY_URL,
					mUrls.get(position));
			singlePageFragment.setArguments(argBundle);
			return singlePageFragment;
		}

		@Override
		public void onPageSelected(int arg0) {
			mMessageTitle.setText(mTitle);
			mMessageIndex.setText((arg0 + 1) + "/" + mUrls.size());
			if (mMessageString == null || mMessageString.size() == 0) {
				return;
			}
			mMessage.setMovementMethod(new ScrollingMovementMethod());

			// 计算描述文字行数，并约束最大行数
			Rect bounds = new Rect();
			Paint paint = new Paint();
			paint.setTextSize((15 + 1) * mDensity);
			paint.getTextBounds(mMessageString.get(arg0), 0, mMessageString
					.get(arg0).length(), bounds);
			int lineCount = (int) Math.ceil(bounds.width() * 1.1
					/ (mWidth * mDensity));
			if (lineCount > 5) {
				lineCount = 5;
				mMessage.setMovementMethod(new ScrollingMovementMethod());
			} else {
				mMessage.setMovementMethod(null);
			}

			// 计算描述textview高度
			LinearLayout.LayoutParams oldText = (LinearLayout.LayoutParams) mMessage
					.getLayoutParams();
			oldText.height = (int) (lineCount * (15 * 1.5 * mDensity) + 10 * mDensity);
			mMessage.setLayoutParams(oldText);
			mMessage.invalidate();

			mMessage.setText(mMessageString.get(arg0));

			// 计算底部文字view高度
			RelativeLayout.LayoutParams oldContainer = (LayoutParams) mMessageView
					.getLayoutParams();
			oldContainer.height = (int) (lineCount * (15 * 1.5 * mDensity) + 45 * mDensity);
			mMessageView.setLayoutParams(oldContainer);
			mMessageView.invalidate();
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	/**
	 * 单页图片
	 * 
	 * @author Calvin
	 * 
	 */
	public static class PhotosSinglePageFragment extends VShareFragment {

		/** key url */
		public static final String KEY_URL = "url";
		/** url */
		private String mImageUrl;
		/** 缩放图片attacher */
		private PhotoViewAttacher mZoomAttacher;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			mImageUrl = getArguments().getString(KEY_URL);
			super.onCreate(savedInstanceState);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View layoutView = inflater.inflate(R.layout.photos_detail_page,
					container, false);

			final RoundProgressImageView imageView = (RoundProgressImageView) layoutView
					.findViewById(R.id.image_photos_detail);
			imageView.setImageResource(R.drawable.default_image_large_dark);
			mImageFetcher.loadImage(mImageUrl, new ImageDrawableCallback() {

				@Override
				public void getImageDrawable(Drawable drawable) {
					/*
					 * 为了保证缺省图.9的正常显示，初始layout为scaletype=fitXy，height=200dip，
					 * 获取到图片后需要进行layoutparam以及scaletype修正
					 */
					imageView.setLayoutParams(new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.FILL_PARENT,
							LinearLayout.LayoutParams.FILL_PARENT));
					imageView.setScaleType(ScaleType.FIT_CENTER);
					ImageWorker.setFadeInDrawable(imageView, drawable);

					mZoomAttacher = new PhotoViewAttacher(imageView);
				}

				@Override
				public void updateProgress(int progress) {
					imageView.setProgress(progress);
				}
			});
			return layoutView;
		}

		@Override
		public void onSuccess(AbstractModel requestor) {
		}

		@Override
		public void onFailed(AbstractModel requestor, int errorCode) {
		}

		@Override
		public void onActionTrigger(int actionId) {
		}

		@Override
		protected void setImageCacheParams() {
			mImageSize = mWidth;
			mImageCacheDir = "photodetail" + File.separator
					+ Utility.getMD5(mImageUrl);
		}

		@Override
		public void onDestroy() {
			if (mZoomAttacher != null) {
				mZoomAttacher.cleanup();
			}
			super.onDestroy();
		}
	}

	/*
	 * 以下方法为requestor或stateview的接口，本类中不做实现
	 */

	@Override
	public void onSuccess(AbstractModel requestor) {
	}

	@Override
	public void onFailed(AbstractModel requestor, int errorCode) {
	}

	@Override
	public void onActionTrigger(int actionId) {
	}

	@Override
	protected void setImageCacheParams() {
		mImageSize = mWidth;
		mImageCacheDir = "photodetail";
	}
}
