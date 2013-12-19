package com.ifeng.vshare.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ifeng.util.Utility;
import com.ifeng.util.imagecache.ImageFetcher;
import com.ifeng.util.ui.OnSingleClickListener;
import com.ifeng.vshare.R;
import com.ifeng.vshare.activity.PointsDetailActivity;
import com.ifeng.vshare.model.PointsListItem.PointsItem;
import com.ifeng.vshare.requestor.PointsListRequestor;
import com.ifeng.vshare.requestor.PointsListRequestor.PointsListViewItem;

/**
 * 积分商城列表适配器
 * 
 * @author Calvin
 * 
 */
public class PointsListAdapter extends BaseAdapter {

	/** activity */
	private Activity mActivity;
	/** 图片加载器 */
	private ImageFetcher mImageFetcher;
	/** 数据model */
	private PointsListRequestor mPointsRequestor;

	/**
	 * 构造
	 * 
	 * @param activity
	 * @param imageFetcher
	 * @param requestor
	 */
	public PointsListAdapter(Activity activity, ImageFetcher imageFetcher,
			PointsListRequestor requestor) {
		mActivity = activity;
		mImageFetcher = imageFetcher;
		mPointsRequestor = requestor;
	}

	@Override
	public int getCount() {
		return mPointsRequestor.getDataList().size();
	}

	@Override
	public Object getItem(int position) {
		return (PointsListViewItem) mPointsRequestor.getDataList()
				.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PointsListHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mActivity).inflate(
					R.layout.points_list_item, null);

			holder = new PointsListHolder();

			holder.mImageGroup = convertView
					.findViewById(R.id.layout_item_point_image);
			holder.mImage = (ImageView) convertView
					.findViewById(R.id.image_item_point);
			holder.mTitle = (TextView) convertView
					.findViewById(R.id.text_item_point_name);
			holder.mProductId = (TextView) convertView
					.findViewById(R.id.text_item_point_id);
			holder.mGotonePoint = (TextView) convertView
					.findViewById(R.id.text_item_point_gotone);

			convertView.setTag(holder);
			// 适配计算 视频item原尺寸为 720*200
			int width = (int) (Utility.getScreenWidth(mActivity) / 720 * (200 - 40));
			int height = (int) (Utility.getScreenWidth(mActivity) / 720 * (200));
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.mImageGroup
					.getLayoutParams();
			params.width = width;
			params.height = height;
			holder.mImageGroup.setLayoutParams(params);
		} else {
			holder = (PointsListHolder) convertView.getTag();
		}

		PointsItem item = ((PointsListViewItem) getItem(position))
				.getProducts().get(0);

		mImageFetcher.loadImage(item.mImg, holder.mImage);
		holder.mTitle.setText(item.mTitle);
		holder.mProductId.setText(String.format(
				mActivity.getString(R.string.product_id), item.mProductId));
		holder.mGotonePoint.setText(item.mGotonePoints);
		convertView.setOnClickListener(new OnProductItemClick(item));

		return convertView;
	}

	/**
	 * PointsListHolder Holder
	 * 
	 * @author Calvin
	 * 
	 */
	private class PointsListHolder {

		/** image group */
		private View mImageGroup;
		/** image */
		private ImageView mImage;
		/** title */
		private TextView mTitle;
		/** productid */
		private TextView mProductId;
		/** gotone point */
		private TextView mGotonePoint;

	}

	/**
	 * 商品子项点击事件
	 * 
	 * @author Calvin
	 * 
	 */
	private class OnProductItemClick extends OnSingleClickListener {

		/** 积分商品item */
		private PointsItem mItem;

		/**
		 * 构造
		 * 
		 * @param item
		 */
		public OnProductItemClick(PointsItem item) {
			mItem = item;
		}

		@Override
		public void onSingleClick(View v) {
			mActivity.startActivity(PointsDetailActivity.getIntent(mActivity,
					mItem.mId));
		}

	}
}