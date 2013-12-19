package com.ifeng.vshare.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ifeng.util.imagecache.ImageCache;
import com.ifeng.util.model.AbstractModel;
import com.ifeng.util.model.AbstractModel.OnModelProcessListener;
import com.ifeng.util.ui.NavgationbarView;
import com.ifeng.util.ui.OnSingleClickListener;
import com.ifeng.util.ui.expandcard.CardView.Card;
import com.ifeng.util.ui.expandcard.CardView.DetailFragment;
import com.ifeng.util.ui.expandcard.ExpandCardView;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareActivity;
import com.ifeng.vshare.VShareApplication;
import com.ifeng.vshare.VShareApplication.OnCityLocationListener;
import com.ifeng.vshare.activity.LocationActivity.CityInfo;
import com.ifeng.vshare.requestor.ClientUpdateRequertor;

/**
 * 首页，其中采用卡片的展示效果，为了减少驻留内存，在跳转到其他页面时需要销毁首页，同时在子页面返回时需要重构主页
 * 
 * @author Calvin
 * 
 */
public class VShareMainActivity extends VShareActivity {

	/** key model requestor */
	private static final String KEY_MODEL_REQUESTOR = "requestor";

	/** 卡片viewcontainer */
	private ExpandCardView mExpandcard;

	/** 定位等待progress */
	private ProgressBar mLocationing;
	/** 定位结果 */
	private TextView mLocationCity;

	/** 上一次点击返回键的时间记录 */
	private long mLastBackKeyDown;

	/** 客户端更新检测requestor */
	private ClientUpdateRequertor mClientUpdateRequertor;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		setContentView(R.layout.index);

		// 导航条设置
		NavgationbarView navgationbar = (NavgationbarView) findViewById(R.id.navgationbar);

		navgationbar.setMiddleItem(getLayoutInflater().inflate(
				R.layout.nav_logo, null));

		View locationNavItem = getLayoutInflater().inflate(
				R.layout.nav_location, null);
		navgationbar.setLeftItem(locationNavItem);
		mLocationing = (ProgressBar) locationNavItem
				.findViewById(R.id.progress_nav_location);
		mLocationCity = (TextView) locationNavItem
				.findViewById(R.id.text_nav_location);
		locationNavItem.setOnClickListener(new OnSingleClickListener() {

			@Override
			public void onSingleClick(View v) {
				VShareMainActivity.this.startActivity(new Intent(
						VShareMainActivity.this, LocationActivity.class));
			}
		});
		processLocation();

		View settingItem = getLayoutInflater().inflate(R.layout.nav_setting,
				null);
		navgationbar.setRightItem(settingItem);
		settingItem.setOnClickListener(new OnSingleClickListener() {

			@Override
			public void onSingleClick(View v) {
				VShareMainActivity.this.startActivity(new Intent(
						VShareMainActivity.this, SettingActivity.class));
			}
		});

		// 设置卡片
		mExpandcard = (ExpandCardView) findViewById(R.id.expandcard);
		initCardView();

		// 客户端更新检测
		mClientUpdateRequertor = new ClientUpdateRequertor(this,
				mClientUpdateListener);
		mModelManageQueue.addTaskModel(KEY_MODEL_REQUESTOR,
				mClientUpdateRequertor);
		mClientUpdateRequertor.checkUpdateAuto();
	}

	@Override
	protected void onResume() {
		loadLocationFromApplication();
		super.onResume();
	}

	/**
	 * 初始化卡片
	 */
	private void initCardView() {
		Card headline = new Card(getString(R.string.title_headline),
				CardDetailFragment.getIntance(
						R.drawable.background_card_headline_full, 0),
				R.drawable.background_card_headline_shrink);
		Card video = new Card(getString(R.string.title_video),
				CardDetailFragment.getIntance(
						R.drawable.background_card_video_full, 1),
				R.drawable.background_card_video_shrink);
		Card lecture = new Card(getString(R.string.title_lecture),
				CardDetailFragment.getIntance(
						R.drawable.background_card_lecture_full, 2),
				R.drawable.background_card_lecture_shrink);
		Card point = new Card(getString(R.string.title_point),
				CardDetailFragment.getIntance(
						R.drawable.background_card_point_full, 3),
				R.drawable.background_card_point_shrink);
		Card club = new Card(getString(R.string.title_club),
				CardDetailFragment.getIntance(
						R.drawable.background_card_club_full, 4),
				R.drawable.background_card_club_shrink);
		Card vip = new Card(getString(R.string.title_discount),
				CardDetailFragment.getIntance(
						R.drawable.background_card_vip_full, 5),
				R.drawable.background_card_vip_shrink);
		mExpandcard.addCards(headline, video, lecture, point, club, vip);
	}

	/**
	 * 处理定位相关操作
	 */
	private void processLocation() {
		((VShareApplication) getApplication())
				.addLocationServiceListener(mLocationListener);
		((VShareApplication) getApplication()).requestLocation();
	}

	/**
	 * 从application中加载位置信息
	 */
	private void loadLocationFromApplication() {
		CityInfo locationCity = ((VShareApplication) getApplication())
				.getLocationInfo();
		mLocationing.setVisibility(View.GONE);
		mLocationCity.setText(locationCity.mShortCut);
	}

	/** 客户端更新请求结果处理回调 */
	private OnModelProcessListener mClientUpdateListener = new OnModelProcessListener() {

		@Override
		public void onSuccess(AbstractModel requestor) {
			// 由ClientUpdateRequertor控制显示更新对话框
			((ClientUpdateRequertor) requestor).showUpdateDialog();
		}

		@Override
		public void onFailed(AbstractModel requestor, int errorCode) {
			// do nothing~
		}

		@Override
		public void onProgress(AbstractModel model, int progress) {
			// do nothing
		}
	};

	@Override
	protected void onDestroy() {
		((VShareApplication) getApplication())
				.removeLocationServiceListener(mLocationListener);
		ImageCache.clearMemoryCache();
		super.onDestroy();
	}

	@Override
	public void startActivity(Intent intent) {
		// 为了性能优化，跳转至其他页面时销毁掉主页
		// finish();
		super.startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 如果卡片处于展开状态，那么点击返回键时，卡片收起
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (mExpandcard != null && mExpandcard.isExpand()) {
				mExpandcard.shrink();
				return true;
			} else {
				// 连续点击退出客户端
				long now = System.currentTimeMillis();
				if (now - mLastBackKeyDown < 3000) {
					finish();
					return true;
				} else {
					mLastBackKeyDown = now;
					Toast.makeText(this, R.string.exit_hint, Toast.LENGTH_SHORT)
							.show();
					return true;
				}

			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 卡片展开页面
	 * 
	 * @author Calvin
	 * 
	 */
	public static class CardDetailFragment extends DetailFragment {

		/** intent列表 */
		private final Class<?>[] classs = { HeadlineCategoryActivity.class,
				VideoCategoryActivity.class, LectureCategoryActivity.class,
				PointsCategoryActivity.class, ClubCategoryActivity.class,
				DiscountCategoryActivity.class };

		/** 图片res */
		private static final String KEY_IMG_RES = "img_res";
		/** index */
		private static final String KEY_INDEX = "index";

		/** 图片资源id */
		private int mImageRes;
		/** intent索引 */
		private int mIndex;

		/**
		 * 获取实例
		 * 
		 * @param resId
		 * @param index
		 * @return
		 */
		public static CardDetailFragment getIntance(int resId, int index) {
			CardDetailFragment cardDetailFragment = new CardDetailFragment();
			Bundle arg = new Bundle();
			arg.putInt(KEY_IMG_RES, resId);
			arg.putInt(KEY_INDEX, index);
			cardDetailFragment.setArguments(arg);

			return cardDetailFragment;
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			mImageRes = getArguments().getInt(KEY_IMG_RES);
			mIndex = getArguments().getInt(KEY_INDEX);

			super.onCreate(savedInstanceState);
		}

		/**
		 * 构造
		 */
		public CardDetailFragment() {

		}

		@Override
		protected View createViewForParent(LayoutInflater inflater,
				ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.index_detailcard, container,
					false);

			ImageView image = (ImageView) view.findViewById(R.id.image_index);
			image.setImageBitmap(BitmapFactory.decodeStream(getResources()
					.openRawResource(mImageRes)));
			view.setOnClickListener(new OnSingleClickListener() {

				@Override
				public void onSingleClick(View v) {
					getActivity().startActivity(
							new Intent(getActivity(), classs[mIndex]));
				}
			});
			return view;
		}
	}

	/** 获取定位信息 */
	private OnCityLocationListener mLocationListener = new OnCityLocationListener() {

		@Override
		public void onLocation(CityInfo info) {
			loadLocationFromApplication();
		}
	};

}
