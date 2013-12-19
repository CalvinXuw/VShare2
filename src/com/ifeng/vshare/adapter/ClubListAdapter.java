package com.ifeng.vshare.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ifeng.util.Utility;
import com.ifeng.util.imagecache.ImageFetcher;
import com.ifeng.util.ui.OnSingleClickListener;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareApplication;
import com.ifeng.vshare.activity.ClubDetailActivity;
import com.ifeng.vshare.activity.LobbyActivity;
import com.ifeng.vshare.activity.ServiceActivity;
import com.ifeng.vshare.model.ClubListItem.ClubItem;
import com.ifeng.vshare.requestor.ClubListRequestor;

/**
 * 休息厅的adapter
 * 
 * @author Calvin
 * 
 */
public class ClubListAdapter extends SectionAdapter {

	/** sectionid 会员服务 */
	private final static int SECTION_SERVICE = 0;
	/** sectionid 移动贵宾厅 */
	private final static int SECTION_LOBBY = 1;
	/** sectionid */
	private final static int SECTION_LOCALCLUB = 2;

	/** activity */
	private Activity mActivity;
	/** 图片加载器 */
	private ImageFetcher mImageFetcher;
	/** 数据model */
	private ClubListRequestor mClubListRequestor;

	/**
	 * 构造
	 * 
	 * @param activity
	 * @param imageFetcher
	 * @param requestor
	 */
	public ClubListAdapter(Activity activity, ImageFetcher imageFetcher,
			ClubListRequestor requestor) {
		mActivity = activity;
		mImageFetcher = imageFetcher;
		mClubListRequestor = requestor;
	}

	@Override
	public int sectionCount() {
		return 3;
	}

	@Override
	public int getCountWithSection(int sectionId) {
		switch (sectionId) {
		case SECTION_SERVICE:
			return 1;
		case SECTION_LOBBY:
			return 1;// mLobbySwitchRequestor.isLobbyOn() ? 1 : 0;
		case SECTION_LOCALCLUB:
			return mClubListRequestor.getDataList().size();
		}
		return 0;
	}

	@Override
	public Object getItemWithSection(int sectionId, int position) {
		switch (sectionId) {
		case SECTION_LOCALCLUB:
			return (ClubItem) mClubListRequestor.getDataList().get(position);
		}
		return null;
	}

	@Override
	public String getSectionName(int sectionId) {
		switch (sectionId) {
		case SECTION_SERVICE:
			return mActivity.getString(R.string.section_club_service);
		case SECTION_LOBBY:
			return mActivity.getString(R.string.section_club_lobby);
		case SECTION_LOCALCLUB:
			return ((VShareApplication) mActivity.getApplication())
					.getLocationInfo().mShortCut
					+ mActivity.getString(R.string.section_club_local);
		}
		return null;
	}

	@Override
	public View getViewWithSection(int sectionId, int position, View convertView) {
		ClubListViewHolder holder = null;
		if (convertView == null) {
			convertView = mActivity.getLayoutInflater().inflate(
					R.layout.club_list_item, null);
			holder = new ClubListViewHolder();

			holder.mName = (TextView) convertView
					.findViewById(R.id.text_item_club_name);
			holder.mImage = (ImageView) convertView
					.findViewById(R.id.image_item_club);

			convertView.setTag(holder);

			// 适配计算 图片原尺寸为 720*320
			holder.mImage.setLayoutParams(new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.FILL_PARENT, (int) (Utility
							.getScreenWidth(mActivity) / 720 * 320)));
		} else {
			holder = (ClubListViewHolder) convertView.getTag();
		}

		switch (sectionId) {
		// 会员服务，跳转至会员服务activity
		case SECTION_SERVICE:
			holder.mImage.setImageBitmap(BitmapFactory.decodeStream(mActivity
					.getResources().openRawResource(
							R.drawable.background_club_service)));
			holder.mName.setText(getSectionName(sectionId));
			convertView.setOnClickListener(new OnSingleClickListener() {

				@Override
				public void onSingleClick(View v) {
					mActivity.startActivity(new Intent(mActivity,
							ServiceActivity.class));
				}
			});
			break;
		// 贵宾厅，跳转至贵宾厅activity
		case SECTION_LOBBY:
			holder.mImage.setImageBitmap(BitmapFactory.decodeStream(mActivity
					.getResources().openRawResource(
							R.drawable.background_club_lobby)));
			holder.mName.setText(getSectionName(sectionId));
			convertView.setOnClickListener(new OnSingleClickListener() {

				@Override
				public void onSingleClick(View v) {
					mActivity.startActivity(new Intent(mActivity,
							LobbyActivity.class));
				}
			});
			break;
		// 俱乐部，跳转至俱乐部详情
		case SECTION_LOCALCLUB:
			final ClubItem clubItem = (ClubItem) getItemWithSection(
					SECTION_LOCALCLUB, position);
			holder.mName.setText(clubItem.mName);
			mImageFetcher.loadImage(clubItem.mImg, holder.mImage);
			convertView.setOnClickListener(new OnSingleClickListener() {

				@Override
				public void onSingleClick(View v) {
					mActivity.startActivity(ClubDetailActivity.getIntent(
							mActivity, clubItem.mName, clubItem.mId));
				}
			});
			break;
		}

		return convertView;
	}

	/**
	 * 俱乐部页面的布局容器
	 * 
	 * @author Calvin
	 * 
	 */
	private class ClubListViewHolder {

		/** 名称 */
		public TextView mName;
		/** 图片 */
		public ImageView mImage;
	}
}