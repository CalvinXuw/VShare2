package com.ifeng.vshare.requestor;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;

import com.ifeng.vshare.model.BaseVShareItem;
import com.ifeng.vshare.model.PointsColumnListItem;
import com.ifeng.vshare.model.PointsColumnListItem.PointsColumnItem;

/**
 * 积分商品栏目数据获取model
 * 
 * @author Calvin
 * 
 */
public class PointsColumnRequestor extends BaseVShareRequestor {

	/** 栏目列表 */
	public List<PointsColumnItem> mColumnList = new LinkedList<PointsColumnItem>();

	/**
	 * 构造
	 * 
	 * @param context
	 * @param listener
	 */
	public PointsColumnRequestor(Context context,
			OnModelProcessListener listener) {
		super(context, listener);
		setAutoParseClass(PointsColumnListItem.class);
	}

	@Override
	protected List<NameValuePair> getRequestHeaders() {
		return null;
	}

	@Override
	protected List<NameValuePair> getRequestParams() {
		return null;
	}

	@Override
	protected String getRequestUrl() {
		return "http://cmv.ifeng.com/Api/shopcidlist";
	}

	@Override
	protected void handleResult(BaseVShareItem item) {
		PointsColumnListItem columnListItem = (PointsColumnListItem) item;
		this.mColumnList.clear();
		if (columnListItem != null) {
			this.mColumnList.addAll(columnListItem.mColumnList);
		}
	}

	/**
	 * 获取栏目列表
	 * 
	 * @return
	 */
	public List<PointsColumnItem> getColumnList() {
		return mColumnList;
	}

}
