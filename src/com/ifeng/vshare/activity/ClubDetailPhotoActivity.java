package com.ifeng.vshare.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ifeng.util.ui.NavgationbarView;
import com.ifeng.util.ui.NavgationbarView.Style;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareActivity;
import com.ifeng.vshare.fragment.PhotoDetailFragment;
import com.ifeng.vshare.model.ClubDetailItem;

/**
 * 俱乐部详情跳转的查看活动图片的页面
 * 
 * @author Calvin
 * 
 */
public class ClubDetailPhotoActivity extends VShareActivity {

	/** key title */
	private static final String KEY_TITLE = "title";
	/** key clubdetail item */
	private static final String KEY_CLUBDETAIL_ITEM = "clubdetail_item";

	/**
	 * get intent
	 * 
	 * @param activity
	 * @param title
	 * @param item
	 * @return
	 */
	public static Intent getIntent(Activity activity, String title,
			ClubDetailItem item) {
		Intent intent = new Intent(activity, ClubDetailPhotoActivity.class);
		intent.putExtra(KEY_TITLE, title);
		intent.putExtra(KEY_CLUBDETAIL_ITEM, item);
		return intent;
	}

	@Override
	protected void onCreate(Bundle arg0) {

		super.onCreate(arg0);
		setContentView(R.layout.base_activity_single);

		NavgationbarView navgationbar = (NavgationbarView) findViewById(R.id.navgationbar);
		navgationbar.setStyle(Style.DARK);
		/*
		 * 设置导航条部分
		 */
		navgationbar.setTitle(getString(R.string.section_club_photos));
		navgationbar.setBackItem(this);

		PhotoDetailFragment photosDetailFragment = PhotoDetailFragment
				.getInstance(getIntent().getExtras().getString(KEY_TITLE),
						(ClubDetailItem) getIntent().getExtras()
								.getSerializable(KEY_CLUBDETAIL_ITEM));
		getSupportFragmentManager().beginTransaction()
				.add(R.id.layout_content, photosDetailFragment).commit();
	}
}
