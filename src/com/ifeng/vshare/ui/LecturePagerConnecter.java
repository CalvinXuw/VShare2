package com.ifeng.vshare.ui;

import android.os.Parcelable;

import com.ifeng.vshare.fragment.LectureCategoryFragment.LectureRequestorState;

/**
 * 大讲堂滑动页面卡之中，加载fragment与pager的连接器
 * 
 * @author Calvin
 * 
 */
public interface LecturePagerConnecter extends Parcelable {

	/**
	 * 获取pager当前的请求状态
	 * 
	 * @return
	 */
	public LectureRequestorState getCurrentState();

	/**
	 * 向pager更改当前的请求状态
	 * 
	 * @param state
	 */
	public void setCurrentState(LectureRequestorState state);
}