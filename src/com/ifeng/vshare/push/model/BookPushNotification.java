package com.ifeng.vshare.push.model;

import android.content.Intent;

import com.ifeng.vshare.activity.BookDetailActivity;
import com.ifeng.vshare.model.BookListItem.BookItem;

/**
 * 书籍类推送item
 * 
 * @author Calvin
 * 
 */
public class BookPushNotification extends AbstractPushNotification {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7042609764294405326L;
	/** bookitem */
	public BookItem mBooksItem;

	/**
	 * 构造，并完成intent部分数据解析
	 * 
	 * @param pushItemModel
	 */
	public BookPushNotification(AbstractPushNotification pushItemModel) {
		super(pushItemModel);
		mBooksItem = new BookItem();
		mBooksItem.parseData(getExtraInfo());
	}

	@Override
	public PushNotificationType getPushNotificationType() {
		return PushNotificationType.BOOK;
	}

	@Override
	public Intent getActionIntent() {
		return BookDetailActivity.getIntent(getContext(), mBooksItem);
	}
}
