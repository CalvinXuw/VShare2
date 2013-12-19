package com.ifeng.vshare.requestor;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

import com.ifeng.vshare.model.PointsListItem;
import com.ifeng.vshare.model.PointsListItem.PointsItem;

/**
 * 积分页面model
 * 
 * @author Calvin
 * 
 */
public class PointsListRequestor extends BaseVSharePageRequestor {

	/** key cid */
	private static final String KEY_CID = "cid";

	/** 子栏目cid */
	private int mCid;
	/** 分栏数据 */
	private List<PointsListViewItem> mPointsListViewItems;

	/**
	 * 构造方法，由栏目类型进行初始化
	 * 
	 * @param context
	 * @param column
	 * @param listener
	 */
	public PointsListRequestor(Context context, int cid,
			OnModelProcessListener listener) {
		super(context, listener);
		setAutoParseClass(PointsListItem.class);
		mPointsListViewItems = new LinkedList<PointsListViewItem>();
		mCid = cid;
	}

	@Override
	protected List<NameValuePair> getRequestParamsWithoutPagething() {
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair(KEY_CID, mCid + ""));
		return params;
	}

	@Override
	protected void handlePageResult(PageItem item) {
		/*
		 * 对数据进行排版
		 */
		synchronized (mDataLock) {
			mPointsListViewItems.clear();
			PointsListViewItem listViewItem = null;
			int columnCount = 1;
			int currentCount = 0;
			for (int i = 0; i < mItems.size(); i++) {
				if (i == 0 || currentCount == columnCount) {
					listViewItem = new PointsListViewItem();
					mPointsListViewItems.add(listViewItem);
					currentCount = 0;
				}

				listViewItem.addProduct((PointsItem) mItems.get(i));
				currentCount++;
			}
		}
	}

	@Override
	public List<?> getDataList() {
		synchronized (mDataLock) {
			return mPointsListViewItems;
		}
	}

	@Override
	protected List<NameValuePair> getRequestHeaders() {
		return null;
	}

	@Override
	protected String getRequestUrl() {
		return "http://cmv.ifeng.com/Api/shoplist";
	}

	/**
	 * 提供列表展示的数据源
	 * 
	 * @author Calvin
	 * 
	 */
	public class PointsListViewItem {
		/** 视频 */
		private LinkedList<PointsItem> mProducts;

		/**
		 * 构造
		 */
		public PointsListViewItem() {
			mProducts = new LinkedList<PointsItem>();
		}

		/**
		 * 为页面展示提供数据
		 * 
		 * @return
		 */
		public LinkedList<PointsItem> getProducts() {
			return mProducts;
		}

		/**
		 * 添加一条商品
		 * 
		 * @param item
		 */
		private void addProduct(PointsItem item) {
			mProducts.add(item);
		}
	}
}
