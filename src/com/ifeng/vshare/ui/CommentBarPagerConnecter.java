package com.ifeng.vshare.ui;

import android.os.Parcelable;

/**
 * 评论条和pager的连接器
 * 
 * @author Calvin
 * 
 */
public interface CommentBarPagerConnecter extends Parcelable {
	/**
	 * 被选取的页码
	 * 
	 * @param which
	 */
	public void onPageSelected(int which);

	/**
	 * 获取当前页码
	 * 
	 * @return
	 */
	public int getCurrentPage();
}