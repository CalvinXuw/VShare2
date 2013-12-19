package com.ifeng.vshare.fragment;

import java.io.File;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ifeng.util.Utility;
import com.ifeng.util.imagecache.ImageWorker;
import com.ifeng.util.imagecache.ImageWorker.ImageDrawableCallback;
import com.ifeng.util.model.AbstractModel;
import com.ifeng.util.ui.OnSingleClickListener;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareFragment;
import com.ifeng.vshare.activity.LectureDetailActivity;
import com.ifeng.vshare.model.LectureListItem.LectureItem;

/**
 * 大讲堂概要信息fragment
 * 
 * @author Calvin
 * 
 */
public class LecturePagerSummaryFragment extends VShareFragment {

	/** key item */
	private static final String KEY_ITEM = "item";

	/** 讲堂item */
	private LectureItem mLectureItem;

	/**
	 * 获取实例
	 * 
	 * @param item
	 * @return
	 */
	public static LecturePagerSummaryFragment getInstance(LectureItem item) {
		LecturePagerSummaryFragment summaryFragment = new LecturePagerSummaryFragment();
		Bundle arg = new Bundle();
		arg.putSerializable(KEY_ITEM, item);
		summaryFragment.setArguments(arg);
		return summaryFragment;
	}

	/**
	 * 构造
	 */
	public LecturePagerSummaryFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mLectureItem = (LectureItem) getArguments().getSerializable(KEY_ITEM);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.lecture_page_summary,
				container, false);

		TextView title = (TextView) layout
				.findViewById(R.id.text_lecture_title);
		TextView time = (TextView) layout.findViewById(R.id.text_lecture_time);
		TextView location = (TextView) layout
				.findViewById(R.id.text_lecture_location);
		TextView summary = (TextView) layout
				.findViewById(R.id.text_lecture_summary);
		TextView type = (TextView) layout.findViewById(R.id.text_lecture_type);
		final ImageView image = (ImageView) layout
				.findViewById(R.id.image_lecture);
		ViewGroup imageGroup = (ViewGroup) layout
				.findViewById(R.id.layout_lecture_image);

		title.setText(mLectureItem.mName);
		time.setText(mLectureItem.mTime);
		location.setText(mLectureItem.mAddr);
		summary.setText(mLectureItem.mSummary);
		switch (mLectureItem.getLectureState()) {
		case NOTICE:
			type.setText(R.string.lecture_type_notice);
			type.setBackgroundResource(R.drawable.image_lecture_type_notice);
			break;
		case GOING:
			type.setText(R.string.lecture_type_ongoing);
			type.setBackgroundResource(R.drawable.image_lecture_type_ongoing);
			break;
		case HISTORY:
			type.setText(R.string.lecture_type_history);
			type.setBackgroundResource(R.drawable.image_lecture_type_history);
			break;
		}

		LinearLayout.LayoutParams oldParams = (LinearLayout.LayoutParams) imageGroup
				.getLayoutParams();
		// 屏幕适配调整，原设计图比例为640*450，屏幕为720 Density=2
		oldParams.height = (int) (mWidth * mDensity / 720 * 450);
		imageGroup.setLayoutParams(oldParams);
		image.setImageResource(R.drawable.default_image_large_light);
		mImageFetcher.loadImage(mLectureItem.mImg, new ImageDrawableCallback() {

			@Override
			public void getImageDrawable(Drawable drawable) {
				/*
				 * 为了保证缺省图.9的正常显示，初始layout为scaletype=fitXy 获取到图片后需要进行scaletype修正
				 */
				image.setScaleType(ScaleType.CENTER_CROP);
				ImageWorker.setFadeInDrawable(image, drawable);
			}
		});

		image.setOnClickListener(new OnSingleClickListener() {

			@Override
			public void onSingleClick(View v) {
				startActivity(LectureDetailActivity.getIntent(getActivity(),
						mLectureItem.mId));
			}
		});

		return layout;
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
		mImageCacheDir = "lecturelist" + File.separator
				+ Utility.getMD5(mLectureItem.mName + mLectureItem.mId);
	}

}
