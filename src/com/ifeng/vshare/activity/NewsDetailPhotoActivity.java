package com.ifeng.vshare.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.ifeng.thirdparty.photoview.PhotoViewAttacher;
import com.ifeng.thirdparty.photoview.PhotoViewAttacher.OnPhotoTapListener;
import com.ifeng.util.imagecache.ImageCache.ImageCacheParams;
import com.ifeng.util.imagecache.ImageFetcher;
import com.ifeng.util.imagecache.ImageWorker.ImageDrawableCallback;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareActivity;

/**
 * @author qianzy
 * @describe 显示图片点击后的大图
 */
public class NewsDetailPhotoActivity extends VShareActivity {

	/** key url */
	private static final String KEY_URL = "url";

	/**
	 * 获取intent
	 * 
	 * @param activity
	 * @param url
	 * @return
	 */
	public static Intent getIntent(Activity activity, String url) {
		Intent intent = new Intent(activity, NewsDetailPhotoActivity.class);
		intent.putExtra(KEY_URL, url);
		return intent;
	}

	/** image fetcher */
	private ImageFetcher mImageFetcher;
	/** 可放大的imageview */
	private ImageView mImageView;
	/** 缩放图片工具 */
	private PhotoViewAttacher mAttacher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_detail_zoomimage);

		mImageView = (ImageView) findViewById(R.id.zoomimage_news_detail);
		initBitmapFetcher();

		/*
		 * 设置默认加载图片
		 */
		mImageFetcher.setLoadingImage(R.drawable.default_image_large_dark);

		String url = getIntent().getExtras().getString(KEY_URL);
		mImageFetcher.loadImage(url, new ImageDrawableCallback() {

			@Override
			public void getImageDrawable(Drawable drawable) {
				mImageView.setImageBitmap(((BitmapDrawable) drawable)
						.getBitmap());
				mAttacher = new PhotoViewAttacher(mImageView);
				mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {

					@Override
					public void onPhotoTap(View view, float x, float y) {
						finish();
					}
				});
			}
		});

	}

	/**
	 * 初始化fetcher
	 */
	private void initBitmapFetcher() {
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int height = (int) (displayMetrics.heightPixels / displayMetrics.density);
		int width = (int) (displayMetrics.widthPixels / displayMetrics.density);
		int imageSize = Math.min(height, width);

		ImageCacheParams params = new ImageCacheParams(this, "newsdetailzoom");
		params.setMemCacheSizePercent(0.2f);

		mImageFetcher = new ImageFetcher(this, imageSize, "newsdetailzoom");
		mImageFetcher.addImageCache(getSupportFragmentManager(), params);
	}

	@Override
	public void onResume() {
		super.onResume();
		mImageFetcher.setExitTasksEarly(false);
	}

	@Override
	public void onPause() {
		super.onPause();
		mImageFetcher.setPauseWork(false);
		mImageFetcher.setExitTasksEarly(true);
		mImageFetcher.flushCache();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mImageFetcher.closeCache();
		mAttacher.cleanup();
	}
}
