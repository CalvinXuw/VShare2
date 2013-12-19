package com.ifeng.vshare.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ifeng.vshare.R;
import com.ifeng.vshare.model.ServiceListItem.ServiceItem;
import com.ifeng.vshare.requestor.ServiceListRequestor;

/**
 * 会员专享服务的adapter
 * 
 * @author Calvin
 * 
 */
public class ServiceListAdapter extends BaseAdapter {

	/** activity */
	private Activity mActivity;
	/** 数据model */
	private ServiceListRequestor mServiceRequestor;

	/**
	 * 构造
	 * 
	 * @param activity
	 * @param requestor
	 */
	public ServiceListAdapter(Activity activity, ServiceListRequestor requestor) {
		mActivity = activity;
		mServiceRequestor = requestor;
	}

	@Override
	public int getCount() {
		return mServiceRequestor.getDataList().size();
	}

	@Override
	public Object getItem(int position) {
		return (ServiceItem) mServiceRequestor.getDataList().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ServiceListViewHolder holder = null;
		if (convertView == null) {
			convertView = mActivity.getLayoutInflater().inflate(
					R.layout.service_list_item, null);
			holder = new ServiceListViewHolder();

			holder.mName = (TextView) convertView
					.findViewById(R.id.text_item_service_name);
			holder.mInfo = (TextView) convertView
					.findViewById(R.id.text_item_service_info);
			holder.mDiamond = convertView
					.findViewById(R.id.image_item_service_diamond);
			holder.mGolden = convertView
					.findViewById(R.id.image_item_service_golden);
			holder.mSilver = convertView
					.findViewById(R.id.image_item_service_silver);

			convertView.setTag(holder);
		} else {
			holder = (ServiceListViewHolder) convertView.getTag();
		}

		ServiceItem serviceItem = (ServiceItem) getItem(position);

		holder.mName.setText(serviceItem.mName);
		holder.mInfo.setText(serviceItem.mInfo);

		// 根据类型不同，显示小图标
		holder.mDiamond.setVisibility(serviceItem.mDiamond == 1 ? View.VISIBLE
				: View.GONE);
		holder.mGolden.setVisibility(serviceItem.mGolden == 1 ? View.VISIBLE
				: View.GONE);
		holder.mSilver.setVisibility(serviceItem.mSilver == 1 ? View.VISIBLE
				: View.GONE);

		convertView.setOnClickListener(null);

		return convertView;
	}

	/**
	 * 服务列表页面的布局容器
	 * 
	 * @author Calvin
	 * 
	 */
	private class ServiceListViewHolder {

		/** 名称 */
		public TextView mName;
		/** 描述 */
		public TextView mInfo;
		/** diamond标识 */
		public View mDiamond;
		/** golden标识 */
		public View mGolden;
		/** silver标识 */
		public View mSilver;

	}
}