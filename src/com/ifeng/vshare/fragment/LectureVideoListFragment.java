package com.ifeng.vshare.fragment;

import java.io.Serializable;
import java.util.List;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ifeng.util.model.AbstractModel;
import com.ifeng.util.ui.IFRefreshListView;
import com.ifeng.util.ui.IFRefreshViewLayout;
import com.ifeng.util.ui.OnSingleClickListener;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareFragment;
import com.ifeng.vshare.model.LectureDetailItem.LectureDetailVideoItem;

/**
 * 分集视频列表页面Fragment
 * 
 * @author Calvin
 * 
 */
public class LectureVideoListFragment extends VShareFragment {

	/** key videolist */
	private static final String KEY_VIDEOLIST = "videolist";
	/** key callback */
	private static final String KEY_VIDEOCALLBACK = "callback";

	/**
	 * 获取实例
	 * 
	 * @param cid
	 * @param callback
	 * @return
	 */
	public static LectureVideoListFragment getInstance(
			List<LectureDetailVideoItem> videos, OnVideoListCallback callback) {
		LectureVideoListFragment listFragment = new LectureVideoListFragment();
		Bundle arg = new Bundle();
		arg.putSerializable(KEY_VIDEOLIST, (Serializable) videos);
		arg.putParcelable(KEY_VIDEOCALLBACK, callback);
		listFragment.setArguments(arg);
		return listFragment;
	}

	/** videos */
	private List<LectureDetailVideoItem> mVideos;
	/** callback */
	private OnVideoListCallback mCallback;
	/** current playitem */
	private int mCurrentPosition = -1;

	/** adapter */
	private VideosAdapter mVideosAdapter;
	/** 执行刷新的view */
	private IFRefreshViewLayout<IFRefreshListView> mRefreshView;
	/** RefreshListView */
	private IFRefreshListView mListView;

	/**
	 * 构造
	 */
	public LectureVideoListFragment() {
	}

	/**
	 * 播放下一集
	 */
	public void playNextOne() {
		if (mCallback != null) {
			if (mCurrentPosition + 1 < mVideos.size()) {
				mCurrentPosition++;
				mCallback.onVideoSelected(mCurrentPosition);
				if (mVideosAdapter != null) {
					mVideosAdapter.notifyDataSetChanged();
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		mVideos = (List<LectureDetailVideoItem>) getArguments()
				.getSerializable(KEY_VIDEOLIST);
		mCallback = getArguments().getParcelable(KEY_VIDEOCALLBACK);
		super.onCreate(new Bundle());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		/*
		 * 设置默认加载图片
		 */
		mImageFetcher.setLoadingImage(R.drawable.default_image_little);

		View layout = inflater.inflate(R.layout.base_fragment_list, container,
				false);

		mRefreshView = (IFRefreshViewLayout<IFRefreshListView>) layout
				.findViewById(R.id.layout_refresh);

		mListView = new IFRefreshListView(getActivity());
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				/*
				 * 在滑动状态在不加载图片，优化滚动卡顿
				 */
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
					mImageFetcher.setPauseWork(true);
				} else {
					mImageFetcher.setPauseWork(false);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});

		updateDatas();

		playNextOne();

		return layout;
	}

	@Override
	public void onSuccess(AbstractModel requestor) {
	}

	/**
	 * 更新数据
	 * 
	 * @param requestor
	 */
	private synchronized void updateDatas() {
		if (mVideosAdapter == null) {
			mRefreshView.setContentView(mListView);

			mVideosAdapter = new VideosAdapter();
			mListView.setAdapter(mVideosAdapter);
		}

		mVideosAdapter.notifyDataSetChanged();
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
		mImageCacheDir = "lecturevideolist";
	}

	/**
	 * 视频列表适配器
	 * 
	 * @author Calvin
	 * 
	 */
	private class VideosAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mVideos.size();
		}

		@Override
		public Object getItem(int position) {
			return mVideos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			VideosListHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.lecture_video_videolist_item, null);

				holder = new VideosListHolder();

				holder.mItemView = (ViewGroup) convertView
						.findViewById(R.id.layout_item);
				holder.mVideoImage = (ImageView) convertView
						.findViewById(R.id.image_item_video);
				holder.mVideoName = (TextView) convertView
						.findViewById(R.id.text_item_video_name);
				holder.mVideoDesc = (TextView) convertView
						.findViewById(R.id.text_item_video_desc);

				convertView.setTag(holder);
				// 适配计算 视频item原尺寸为 720*190
				holder.mItemView.setLayoutParams(new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT, (int) (mWidth
								* mDensity / 720 * 165)));

			} else {
				holder = (VideosListHolder) convertView.getTag();
			}

			LectureDetailVideoItem item = (LectureDetailVideoItem) getItem(position);

			mImageFetcher.loadImage(item.mVideoImage, holder.mVideoImage,
					mWidth / 3);
			holder.mVideoName.setText(item.mVideoTitle);
			holder.mVideoDesc.setText(item.mVideoDesc);

			if (mCurrentPosition != position) {
				holder.mItemView.setEnabled(true);
				holder.mItemView
						.setBackgroundResource(R.drawable.background_listitem);
				holder.mItemView
						.setOnClickListener(new OnSingleClickListener() {

							@Override
							public void onSingleClick(View v) {
								if (mCallback != null) {
									mCallback.onVideoSelected(position);
									mCurrentPosition = position;
									if (mVideosAdapter != null) {
										mVideosAdapter.notifyDataSetChanged();
									}
								}
							}
						});
			} else {
				holder.mItemView.setEnabled(false);
				holder.mItemView
						.setBackgroundResource(R.color.background_listitem_act);
				holder.mItemView.setOnClickListener(null);
			}

			return convertView;
		}

		/**
		 * VideosListHolder Holder
		 * 
		 * @author Calvin
		 * 
		 */
		private class VideosListHolder {
			/** item view供重新计算适配使用 */
			private ViewGroup mItemView;

			/** image */
			private ImageView mVideoImage;

			/** name */
			private TextView mVideoName;

			/** desc */
			private TextView mVideoDesc;
		}
	}

	/**
	 * 分集视频与讲堂视频Activity之间的回调
	 * 
	 * @author Calvin
	 * 
	 */
	public static interface OnVideoListCallback extends Parcelable {

		/**
		 * 选中视频播放
		 * 
		 * @param position
		 */
		public void onVideoSelected(int position);
	}
}
