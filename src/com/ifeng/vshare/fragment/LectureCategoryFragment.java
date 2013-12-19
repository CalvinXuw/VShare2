package com.ifeng.vshare.fragment;

import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ifeng.util.model.AbstractModel;
import com.ifeng.util.ui.PageIndexView;
import com.ifeng.util.ui.PageIndexView.OnPageIndexSelected;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareApplication;
import com.ifeng.vshare.VShareApplication.OnCityLocationListener;
import com.ifeng.vshare.VShareFragment;
import com.ifeng.vshare.activity.LocationActivity.CityInfo;
import com.ifeng.vshare.model.LectureListItem.LectureItem;
import com.ifeng.vshare.requestor.LectureListRequestor;
import com.ifeng.vshare.ui.LecturePagerConnecter;

/**
 * 大讲堂pager页面
 * 
 * @author Calvin
 * 
 */
public class LectureCategoryFragment extends VShareFragment implements
		OnPageIndexSelected {

	/** key model requestor */
	private static final String KEY_MODEL_REQUESTOR = "requestor";

	/**
	 * 当前请求状态，用于控制 {@link LecturePagerLoadingFragment} 交互状态
	 * 
	 * @author Calvin
	 * 
	 */
	public enum LectureRequestorState {
		NORMAL, REQUESTING, FAILED
	}

	/** 当前页面请求状态 */
	private LectureRequestorState mCurrentRequestorState = LectureRequestorState.REQUESTING;

	/** 加载状态分页 */
	private LecturePagerLoadingFragment mLoadingFragment;

	/** 分页adapter */
	private LecturePagerAdapter mPagerAdapter;
	/** view pager */
	private ViewPager mPager;
	/** 页下索引控件 */
	private PageIndexView mIndexView;
	/** lecture requestor */
	private LectureListRequestor mLectureRequestor;

	/** 页数　标题 */
	private TextView mCountLabelView;
	/** 页数 */
	private TextView mCountView;

	/**
	 * 构造
	 */
	public LectureCategoryFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		((VShareApplication) getActivity().getApplication())
				.addLocationServiceListener(mLocationListener);
		super.onCreate(new Bundle());
	}

	@Override
	public void onDestroy() {
		((VShareApplication) getActivity().getApplication())
				.removeLocationServiceListener(mLocationListener);
		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.lecture_page, container, false);

		mPager = (ViewPager) layout.findViewById(R.id.pager_content);
		mIndexView = (PageIndexView) layout.findViewById(R.id.pageindex);
		mCountLabelView = (TextView) layout
				.findViewById(R.id.id_lecture_pagecount);
		mCountView = (TextView) layout
				.findViewById(R.id.text_lecture_pagecount);

		mIndexView.setOnPageIndexSelected(this);

		mPagerAdapter = new LecturePagerAdapter(getFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		mPager.setOnPageChangeListener(mPagerAdapter);

		((VShareApplication) getActivity().getApplication()).requestLocation();

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
	private class LecturePagerAdapter extends FragmentStatePagerAdapter
			implements OnPageChangeListener {

		/** COUNT */
		private int mCount;

		/**
		 * 构造
		 * 
		 * @param fm
		 */
		public LecturePagerAdapter(FragmentManager fm) {
			super(fm);
			mLoadingFragment = LecturePagerLoadingFragment
					.getInstance(new LecturePagerConnecter() {

						@Override
						public void writeToParcel(Parcel dest, int flags) {
						}

						@Override
						public int describeContents() {
							return 0;
						}

						@Override
						public void setCurrentState(LectureRequestorState state) {
							if (state == LectureRequestorState.REQUESTING) {
								if (mCount == 0) {
									((VShareApplication) getActivity()
											.getApplication())
											.requestLocation();
								} else {
									mLectureRequestor.requestNextPage();
								}
								mCurrentRequestorState = LectureRequestorState.REQUESTING;
								mLoadingFragment.refreshState();
							}
						}

						@Override
						public LectureRequestorState getCurrentState() {
							return mCurrentRequestorState;
						}
					});
		}

		@Override
		public int getCount() {
			// 如果还有需要加载的数据，添加一个请求加载页到列表；如果当前为首次请求也添加一个加载页
			if (hasNext() || mCount == 0) {
				return mCount + 1;
			}
			return mCount;
		}

		@Override
		public void notifyDataSetChanged() {
			mCount = mLectureRequestor.getDataList().size();
			if (mCount != 0) {
				mIndexView.setMax(mCount);
			}
			super.notifyDataSetChanged();
		}

		/**
		 * 是否有还有余下数据
		 * 
		 * @return
		 */
		private boolean hasNext() {
			return mLectureRequestor.isHasNext();
		}

		@Override
		public Fragment getItem(int position) {
			// 如果当前为最后一个页面时，且有余下数据或为首次加载时，配置一个加载页
			if (position == getCount() - 1 && (hasNext() || mCount == 0)) {
				if (mCurrentRequestorState == LectureRequestorState.NORMAL) {
					mLectureRequestor.requestNextPage();
					mCurrentRequestorState = LectureRequestorState.REQUESTING;
				}
				return mLoadingFragment;
			} else {
				LecturePagerSummaryFragment summaryFragment = LecturePagerSummaryFragment
						.getInstance((LectureItem) mLectureRequestor
								.getDataList().get(position));
				return summaryFragment;
			}
		}

		@Override
		public void onPageSelected(int arg0) {
			if (mIndexView.isInit()) {
				mIndexView.setCurrent(arg0);
			}
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	@Override
	public void onSelected(int which) {
		mPager.setCurrentItem(which);
	}

	@Override
	public void onSuccess(AbstractModel requestor) {
		mCurrentRequestorState = LectureRequestorState.NORMAL;
		mLoadingFragment.refreshState();
		mPagerAdapter.notifyDataSetChanged();

		mCountLabelView.setText(R.string.lecture_pagecount_label);
		mCountView.setText(String.format(getString(R.string.lecture_pagecount),
				mLectureRequestor.getCount()));

	}

	@Override
	public void onFailed(AbstractModel requestor, int errorCode) {
		mCurrentRequestorState = LectureRequestorState.FAILED;
		mLoadingFragment.refreshState();
		mPagerAdapter.notifyDataSetChanged();
	}

	/** 获取定位信息，获得结果之后再进行接口数据请求 */
	private OnCityLocationListener mLocationListener = new OnCityLocationListener() {

		@Override
		public void onLocation(CityInfo info) {
			mLectureRequestor.updateLocationInfo(info);
			mLectureRequestor.requestFirstPage();
		}
	};

	@Override
	protected void setupModel() {
		mLectureRequestor = new LectureListRequestor(getActivity(),
				((VShareApplication) getActivity().getApplication())
						.getLocationInfo(), this);
		mModelManageQueue.addTaskModel(KEY_MODEL_REQUESTOR, mLectureRequestor);
	}

	@Override
	public void onActionTrigger(int actionId) {
	}

	@Override
	protected void setImageCacheParams() {
		mImageSize = mWidth;
		mImageCacheDir = "lecturelist";
	}

}
