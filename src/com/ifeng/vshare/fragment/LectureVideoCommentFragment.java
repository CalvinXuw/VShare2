package com.ifeng.vshare.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ifeng.util.model.AbstractModel;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareFragment;

/**
 * 讲堂视频页面的评论分页
 * 
 * @author Calvin
 * 
 */
public class LectureVideoCommentFragment extends VShareFragment {

	/** key comment name */
	private static final String KEY_LECTURE_NAME = "name";
	/** key comment tag */
	private static final String KEY_COMMENT_TAG = "comment";

	/**
	 * 获取实例
	 * 
	 * @param name
	 * @param string
	 * @return
	 */
	public static LectureVideoCommentFragment getInstance(String name,
			String commentTag) {
		LectureVideoCommentFragment instance = new LectureVideoCommentFragment();
		Bundle extra = new Bundle();
		extra.putString(KEY_COMMENT_TAG, commentTag);
		extra.putString(KEY_LECTURE_NAME, name);
		instance.setArguments(extra);
		return instance;
	}

	/**
	 * 构造
	 */
	public LectureVideoCommentFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.lecture_video_comment,
				container, false);
		// 初始化评论组件
		CommentListFragment commentListFragment = CommentListFragment
				.getInstance(getString(R.string.lecture_comment_title_comment),
						getArguments().getString(KEY_COMMENT_TAG), false);
		CommentBarFragment commentBarFragment = CommentBarFragment.getInstance(
				getArguments().getString(KEY_LECTURE_NAME), getArguments()
						.getString(KEY_COMMENT_TAG));

		getChildFragmentManager().beginTransaction()
				.add(R.id.layout_commentlist, commentListFragment).commit();
		getChildFragmentManager().beginTransaction()
				.add(R.id.layout_commentbar, commentBarFragment).commit();
		return layout;
	}

	@Override
	public void onSuccess(AbstractModel model) {

	}

	@Override
	public void onFailed(AbstractModel model, int errorCode) {

	}

	@Override
	public void onActionTrigger(int actionId) {

	}

	@Override
	protected void setImageCacheParams() {

	}

}
