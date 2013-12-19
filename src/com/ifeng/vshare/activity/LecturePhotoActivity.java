package com.ifeng.vshare.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ifeng.util.ui.NavgationbarView;
import com.ifeng.util.ui.NavgationbarView.Style;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareActivity;
import com.ifeng.vshare.fragment.PhotoDetailFragment;
import com.ifeng.vshare.model.LectureDetailItem;
import com.ifeng.vshare.model.LectureDetailItem.LectureDetailPhotoItem;

/**
 * lecture详情页活动大图 activity页面
 * 
 * @author Calvin
 * 
 */
public class LecturePhotoActivity extends VShareActivity {

	/** key title */
	private static final String KEY_TITLE = "title";
	/** key lecturedetail item */
	private static final String KEY_LECTUREDETAIL_ITEM = "lecturedetail_item";

	/**
	 * 获取跳转intent
	 * 
	 * @param activity
	 * @param tite
	 * @param item
	 * @return
	 */
	public static Intent getIntent(Activity activity, String tite,
			LectureDetailItem item) {
		Intent intent = new Intent(activity, LecturePhotoActivity.class);
		intent.putExtra(KEY_TITLE, tite);
		intent.putExtra(KEY_LECTUREDETAIL_ITEM, item);

		for (LectureDetailPhotoItem urlItem : item.mActivityPhotos) {
			urlItem.mImage += "?";
		}

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
		navgationbar
				.setTitle(getString(R.string.section_lecture_acitivty_photo));
		navgationbar.setBackItem(this);

		PhotoDetailFragment photosDetailFragment = PhotoDetailFragment
				.getInstance(getIntent().getExtras().getString(KEY_TITLE),
						(LectureDetailItem) getIntent().getExtras()
								.getSerializable(KEY_LECTUREDETAIL_ITEM));
		getSupportFragmentManager().beginTransaction()
				.add(R.id.layout_content, photosDetailFragment).commit();
	}
}
