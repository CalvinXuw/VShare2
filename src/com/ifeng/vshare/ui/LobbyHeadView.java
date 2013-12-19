package com.ifeng.vshare.ui;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ifeng.util.Utility;
import com.ifeng.util.ui.IIFAnimation.IFAnimation;
import com.ifeng.util.ui.OnSingleClickListener;
import com.ifeng.vshare.R;
import com.ifeng.vshare.requestor.LobbyListRequestor.LobbyType;

/**
 * LobbyDescriptionView 容器
 * 
 * @author Calvin
 * 
 */
public class LobbyHeadView {

	/** activity */
	private Activity mActivity;

	/** 内容View */
	private ViewGroup mContentView;
	/** 宣传图片 */
	private ImageView mHeadImage;
	/** 短介绍 */
	private TextView mShortDescription;
	/** 展开收起按钮 */
	private View mDetailControl;
	/** 展开收起标示 */
	private View mDetailSign;
	/** 详情数据容器 */
	private ViewGroup mDetailContainer;
	/** 详情数据 */
	private View mDetail;
	/** 城市名 */
	private TextView mCityName;

	/**
	 * 构造
	 * 
	 * @param inflater
	 */
	public LobbyHeadView(Activity activity) {
		mActivity = activity;
		LayoutInflater inflater = activity.getLayoutInflater();

		mContentView = (ViewGroup) inflater.inflate(R.layout.lobby_description,
				null);
		mHeadImage = (ImageView) mContentView
				.findViewById(R.id.image_head_lobby);
		mShortDescription = (TextView) mContentView
				.findViewById(R.id.text_head_lobby);
		mDetailControl = mContentView.findViewById(R.id.btn_lobby_description);
		mDetailSign = mContentView.findViewById(R.id.image_lobby_description);
		mDetailContainer = (ViewGroup) mContentView
				.findViewById(R.id.layout_lobby_description_container);
		mCityName = (TextView) mContentView
				.findViewById(R.id.text_section_lobby);

		mHeadImage.setLayoutParams(new LinearLayout.LayoutParams((int) Utility
				.getScreenWidth(mActivity), (int) (Utility
				.getScreenWidth(mActivity) / 720 * 200)));
	}

	/**
	 * 根据贵宾厅类型进行样式初始化
	 * 
	 * @param type
	 */
	public void initWithLobbyType(LobbyType type) {
		if (type == LobbyType.RailwayStation) {
			mHeadImage.setImageBitmap(BitmapFactory.decodeStream(mActivity
					.getResources().openRawResource(
							R.drawable.background_lobby_head_railway)));
			mShortDescription.setText(R.string.lobby_message_railway);
			mDetail = mActivity.getLayoutInflater().inflate(
					R.layout.lobby_description_railway, null);
		} else {
			mHeadImage.setImageBitmap(BitmapFactory.decodeStream(mActivity
					.getResources().openRawResource(
							R.drawable.background_lobby_head_airport)));
			mShortDescription.setText(R.string.lobby_message_airport);
			mDetail = mActivity.getLayoutInflater().inflate(
					R.layout.lobby_description_airport, null);
		}
		mDetailContainer.addView(mDetail, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		mDetailControl.setOnClickListener(new OnSingleClickListener() {

			@Override
			public void onSingleClick(View v) {
				// 初始化
				if (mDetailContainer.getVisibility() == View.GONE) {
					mDetailContainer.setVisibility(View.VISIBLE);
					mDetailSign
							.setBackgroundResource(R.drawable.image_lobby_detail_expand);
					return;
				}

				if (mDetail.getPaddingBottom() < 0) {
					mDetailSign
							.setBackgroundResource(R.drawable.image_lobby_detail_expand);
					new DescriptionExpandAnimation(0).startAnimation();
				} else {
					mDetailSign
							.setBackgroundResource(R.drawable.image_lobby_detail_shrink);
					new DescriptionExpandAnimation(-mDetail.getMeasuredHeight())
							.startAnimation();
				}
			}
		});

		mContentView.invalidate();
		mContentView.setOnClickListener(null);
	}

	/**
	 * 更新省市名称
	 * 
	 * @param cityName
	 */
	public void setCityName(String cityName) {
		mCityName.setText(cityName);
	}

	/**
	 * 获取HeadView
	 * 
	 * @return
	 */
	public View getContentView() {
		return mContentView;
	}

	/**
	 * 详情内容展开收起动画
	 * 
	 * @author Calvin
	 * 
	 */
	private class DescriptionExpandAnimation extends IFAnimation {

		/** 动画执行时间 */
		private static final int DURATION = 500;
		/** 起始位置 */
		private int mStartPadding;
		/** 终止位置 */
		private int mEndPadding;

		public DescriptionExpandAnimation(int end) {
			super(mContentView, DURATION);

			mStartPadding = mDetail.getPaddingBottom();
			mEndPadding = end;
		}

		@Override
		public void applyTransformation(float percent) {
			mDetail.setPadding(0, 0, 0,
					(int) (mStartPadding + (mEndPadding - mStartPadding)
							* percent));
		}

		@Override
		public void onAnimationFinished() {
		}

	}
}