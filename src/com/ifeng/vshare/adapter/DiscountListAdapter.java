package com.ifeng.vshare.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ifeng.util.ui.OnSingleClickListener;
import com.ifeng.vshare.R;
import com.ifeng.vshare.activity.DiscountDetailActivity;
import com.ifeng.vshare.model.DiscountListItem.DiscountItem;
import com.ifeng.vshare.requestor.DiscountListRequestor;

/**
 * 特惠商盟的adapter
 * 
 * @author Calvin
 * 
 */
public class DiscountListAdapter extends BaseAdapter {

	/** activity */
	private Activity mActivity;
	/** 数据model */
	private DiscountListRequestor mDiscountRequestor;

	public DiscountListAdapter(Activity activity,
			DiscountListRequestor requestor) {
		mActivity = activity;
		mDiscountRequestor = requestor;
	}

	@Override
	public int getCount() {
		return mDiscountRequestor.getDataList().size();
	}

	@Override
	public Object getItem(int position) {
		return (DiscountItem) mDiscountRequestor.getDataList().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DiscountListViewHolder holder = null;
		if (convertView == null) {
			convertView = mActivity.getLayoutInflater().inflate(
					R.layout.discount_list_item, null);
			holder = new DiscountListViewHolder();

			holder.mName = (TextView) convertView
					.findViewById(R.id.text_item_discount_name);
			holder.mTel = (TextView) convertView
					.findViewById(R.id.text_item_discount_tel);
			holder.mInfo = (TextView) convertView
					.findViewById(R.id.text_item_discount_info);
			holder.mDistance = (TextView) convertView
					.findViewById(R.id.text_item_discount_distance);

			convertView.setTag(holder);
		} else {
			holder = (DiscountListViewHolder) convertView.getTag();
		}

		final DiscountItem discountItem = (DiscountItem) getItem(position);

		holder.mName.setText(discountItem.mName);
		holder.mInfo.setText(discountItem.mInfo);
		holder.mTel.setText(discountItem.mTel);

		String distance = null;
		if (discountItem.mDistance == -1) {
			distance = mActivity.getString(R.string.unknow);
			holder.mDistance.setVisibility(View.GONE);
		} else {
			distance = discountItem.mDistance > 1000 ? (int) discountItem.mDistance
					/ 1000 + "km"
					: (int) discountItem.mDistance + "m";
		}
		holder.mDistance.setText(distance);

		convertView.setOnClickListener(new OnSingleClickListener() {

			@Override
			public void onSingleClick(View v) {
				mActivity.startActivity(DiscountDetailActivity.getIntent(
						mActivity, discountItem.mName, discountItem.mId));
			}
		});

		return convertView;
	}

	/**
	 * 特惠商盟页面的布局容器
	 * 
	 * @author Calvin
	 * 
	 */
	private class DiscountListViewHolder {

		/** 名称 */
		public TextView mName;
		/** 描述 */
		public TextView mInfo;
		/** 电话 */
		public TextView mTel;
		/** 距离 */
		public TextView mDistance;
	}
}