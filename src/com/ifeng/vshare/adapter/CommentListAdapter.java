package com.ifeng.vshare.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.ifeng.vshare.R;
import com.ifeng.vshare.model.CommentListItem.CommentItem;
import com.ifeng.vshare.requestor.CommentListRequestor;

/**
 * 评论列表的adapter
 * 
 * @author Calvin
 * 
 */
public class CommentListAdapter extends SectionAdapter {

	/** sectionid title */
	private final static int SECTION_TITLE = 0;
	/** sectionid comment */
	private final static int SECTION_COMMENT = 1;

	/** activity */
	private Activity mActivity;
	/** 数据model */
	private CommentListRequestor mCommentListRequestor;
	/** 评论标题 */
	private String mCommentTitle;
	/** 是否需要展示标题 */
	private boolean mNeedDisplayTitle;

	/**
	 * 构造
	 * 
	 * @param activity
	 * @param requestor
	 */
	public CommentListAdapter(Activity activity,
			CommentListRequestor requestor, String title) {
		mActivity = activity;
		mCommentListRequestor = requestor;
		mCommentTitle = title;
		mNeedDisplayTitle = mCommentTitle != null;
	}

	@Override
	public int sectionCount() {
		return 2;
	}

	@Override
	public int getCountWithSection(int sectionId) {
		switch (sectionId) {
		case SECTION_TITLE:
			return 1;
		case SECTION_COMMENT:
			return mCommentListRequestor.getComments().size();
		}
		return 0;
	}

	@Override
	public Object getItemWithSection(int sectionId, int position) {
		switch (sectionId) {
		case SECTION_COMMENT:
			return (CommentItem) mCommentListRequestor.getComments().get(
					position);
		}
		return null;
	}

	@Override
	public String getSectionName(int sectionId) {
		switch (sectionId) {
		case SECTION_TITLE:
			return mCommentTitle;
		case SECTION_COMMENT:
			return null;
		}
		return null;
	}

	@Override
	public View getViewWithSection(int sectionId, int position, View convertView) {
		CommentListViewHolder holder = null;
		if (convertView == null) {
			convertView = mActivity.getLayoutInflater().inflate(
					R.layout.comment_list_item, null);
			holder = new CommentListViewHolder();

			holder.mName = (TextView) convertView
					.findViewById(R.id.text_item_comment_name);
			holder.mTime = (TextView) convertView
					.findViewById(R.id.text_item_comment_time);
			holder.mContent = (TextView) convertView
					.findViewById(R.id.text_item_comment_cotent);
			holder.mCommentTitle = (TextView) convertView
					.findViewById(R.id.text_item_comment_title);
			holder.mCommentCount = (TextView) convertView
					.findViewById(mNeedDisplayTitle ? R.id.text_item_comment_hastitle_count
							: R.id.text_item_comment_notitle_count);
			holder.mSection = convertView
					.findViewById(mNeedDisplayTitle ? R.id.layout_item_comment_section_hastitle
							: R.id.layout_item_comment_section_notitle);
			holder.mComment = convertView
					.findViewById(R.id.layout_item_content);

			convertView.setTag(holder);
		} else {
			holder = (CommentListViewHolder) convertView.getTag();
		}

		if (sectionId == SECTION_TITLE) {
			holder.mCommentTitle.setText(mCommentTitle);
			holder.mCommentCount.setText(String.format(
					mActivity.getString(R.string.comment_count),
					mCommentListRequestor.getCommentCount()));
			holder.mSection.setVisibility(View.VISIBLE);
			holder.mComment.setVisibility(View.GONE);
		} else {
			CommentItem commentItem = (CommentItem) getItemWithSection(
					SECTION_COMMENT, position);

			holder.mName.setText(commentItem.mUserName);
			holder.mTime.setText(commentItem.mCommentDate);
			holder.mContent.setText(commentItem.mCommentContent);
			holder.mSection.setVisibility(View.GONE);
			holder.mComment.setVisibility(View.VISIBLE);
		}

		convertView.setOnClickListener(null);
		return convertView;
	}

	/**
	 * 评论列表页面的布局容器
	 * 
	 * @author Calvin
	 * 
	 */
	private class CommentListViewHolder {

		/** 用户名 */
		public TextView mName;
		/** 发表时间 */
		public TextView mTime;
		/** 评论内容 */
		public TextView mContent;
		/** comment标题 */
		public TextView mCommentTitle;
		/** comment count */
		public TextView mCommentCount;

		/** section group */
		public View mSection;
		/** comment group */
		public View mComment;
	}
}