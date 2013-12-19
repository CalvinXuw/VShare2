package com.ifeng.vshare.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ifeng.util.Utility;
import com.ifeng.vshare.R;
import com.ifeng.vshare.model.LobbyListItem.LobbyItem;
import com.ifeng.vshare.requestor.LobbyListRequestor;
import com.ifeng.vshare.requestor.LobbyListRequestor.LobbyType;

/**
 * 休息厅的adapter
 * 
 * @author Calvin
 * 
 */
public class LobbyListAdapter extends BaseAdapter {

	/** activity */
	private Activity mActivity;
	/** 数据model */
	private LobbyListRequestor mLobbyRequestor;

	/**
	 * 构造
	 * 
	 * @param activity
	 * @param requestor
	 */
	public LobbyListAdapter(Activity activity, LobbyListRequestor requestor) {
		mActivity = activity;
		mLobbyRequestor = requestor;
	}

	@Override
	public int getCount() {
		return mLobbyRequestor.getDataList().size();
	}

	@Override
	public Object getItem(int position) {
		return (LobbyItem) mLobbyRequestor.getDataList().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LobbyListViewHolder holder = null;
		if (convertView == null) {
			convertView = mActivity.getLayoutInflater().inflate(
					R.layout.lobby_list_item, null);
			holder = new LobbyListViewHolder();

			holder.mName = (TextView) convertView
					.findViewById(R.id.text_item_lobby_name);
			holder.mInfo = (TextView) convertView
					.findViewById(R.id.text_item_lobby_info);
			holder.mDistance = (TextView) convertView
					.findViewById(R.id.text_item_lobby_distance);
			holder.mAddr = (TextView) convertView
					.findViewById(R.id.text_item_lobby_addr);
			holder.mTel = (TextView) convertView
					.findViewById(R.id.text_item_lobby_tel);

			convertView.setTag(holder);
		} else {
			holder = (LobbyListViewHolder) convertView.getTag();
		}

		final LobbyItem lobbyItem = (LobbyItem) getItem(position);

		holder.mInfo.setText(lobbyItem.mInfo);
		holder.mTel.setText("电话：" + lobbyItem.mTel);
		holder.mAddr.setText(lobbyItem.mAddress);

		// 根据类型不同，显示机场或者火车站小图标
		SpannableString spannableString = new SpannableString("/type "
				+ lobbyItem.mName);
		int size = (int) (19 * Utility.getScaledDensity(mActivity) + 0.5f);

		if (lobbyItem.getLobbyType() == LobbyType.Airport) {
			Drawable drawable = mActivity.getResources().getDrawable(
					R.drawable.image_vip_airport);
			drawable.setBounds(0, 0, size, size);
			spannableString.setSpan(new ImageSpan(drawable,
					ImageSpan.ALIGN_BOTTOM), 0, 5,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		} else {
			Drawable drawable = mActivity.getResources().getDrawable(
					R.drawable.image_vip_railwaystation);
			drawable.setBounds(0, 0, size, size);
			spannableString.setSpan(new ImageSpan(drawable,
					ImageSpan.ALIGN_BOTTOM), 0, 5,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		holder.mName.setText(spannableString);

		String distance = null;
		if (lobbyItem.mDistance == -1) {
			distance = mActivity.getString(R.string.unknow);
			holder.mDistance.setVisibility(View.GONE);
		} else {
			distance = lobbyItem.mDistance > 1000 ? (int) lobbyItem.mDistance
					/ 1000 + "km" : (int) lobbyItem.mDistance + "m";
		}
		holder.mDistance.setText(distance);

		convertView.setBackgroundResource(R.color.background_listitem_nor);

		return convertView;
	}

	/**
	 * 休息厅页面的布局容器
	 * 
	 * @author Calvin
	 * 
	 */
	private class LobbyListViewHolder {

		/** 名称 */
		public TextView mName;
		/** 描述 */
		public TextView mInfo;
		/** 电话 */
		public TextView mTel;
		/** 距离 */
		public TextView mDistance;
		/** 位置 */
		public TextView mAddr;
	}
}