package com.ifeng.vshare.fragment;

import java.io.Serializable;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.ifeng.util.Utility;
import com.ifeng.util.model.AbstractModel;
import com.ifeng.util.ui.OnSingleClickListener;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareFragment;
import com.ifeng.vshare.activity.NewsDetailActivity;
import com.ifeng.vshare.requestor.CommentPublishRequestor;
import com.ifeng.vshare.ui.CommentBarPagerConnecter;

/**
 * 评论条fragment
 * 
 * @author Calvin
 * 
 */
public class CommentBarFragment extends VShareFragment {

	/** key comment title */
	private static final String KEY_COMMENT_TITLE = "title";

	/** key comment tag */
	private static final String KEY_COMMENT_TAG = "tag";

	/** key comment comefrom type */
	private static final String KEY_COMEFROM = "comefrom";

	/** key news detail callback */
	private static final String KEY_NEWSDETAIL_CALLBACK = "newsdetailcallback";

	/**
	 * 获取一个评论条实例，来源自大讲堂
	 * 
	 * @param title
	 * @param commentTag
	 * @return
	 */
	public static CommentBarFragment getInstance(String title, String commentTag) {
		CommentBarFragment commentBarFragment = new CommentBarFragment();
		Bundle arg = new Bundle();
		arg.putString(KEY_COMMENT_TITLE, title);
		arg.putString(KEY_COMMENT_TAG, commentTag);
		arg.putSerializable(KEY_COMEFROM, ComeFromType.LECTURE);
		commentBarFragment.setArguments(arg);
		return commentBarFragment;
	}

	/**
	 * 获取一个评论条实例，来源自资讯详情
	 * 
	 * @param title
	 * @param commentTag
	 * @param callback
	 * @return
	 */
	public static CommentBarFragment getInstance(String title,
			String commentTag, CommentBarPagerConnecter callback) {
		CommentBarFragment commentBarFragment = new CommentBarFragment();
		Bundle arg = new Bundle();
		arg.putString(KEY_COMMENT_TITLE, title);
		arg.putString(KEY_COMMENT_TAG, commentTag);
		arg.putParcelable(KEY_NEWSDETAIL_CALLBACK, callback);
		arg.putSerializable(KEY_COMEFROM, ComeFromType.NEWS);
		commentBarFragment.setArguments(arg);
		return commentBarFragment;
	}

	/**
	 * 来源
	 * 
	 * @author Calvin
	 * 
	 */
	private enum ComeFromType implements Serializable {
		NEWS, LECTURE
	}

	/**
	 * 评论状态
	 * 
	 * @author Calvin
	 * 
	 */
	private enum CommentState {
		NORMAL, SENDING, SENDED
	}

	/** key model requestor */
	private static final String KEY_MODEL_REQUESTOR = "requestor";

	/** 当前软键盘状态 */
	private boolean mCurrentSoftKeyboardIsShow;
	/** 当前评论状态 */
	private CommentState mCurrentState = CommentState.NORMAL;

	/** 调用来源 */
	private ComeFromType mComeFromType;
	/** comment requestor */
	private CommentPublishRequestor mCommentRequestor;

	/** 收起状态附着在fragment上的view */
	private CommentBarViewHolder mRealCommentBarViewHolder;
	/** 键盘展开状态附着在window上的view */
	private CommentBarViewHolder mWindowCommentBarViewHolder;

	/** window layoutparam */
	private WindowManager.LayoutParams mWindowCommentBarViewParams;
	/** 当前是否添加到了window上 */
	private boolean mAddToWindow;

	/*
	 * ComeFromType news detail
	 */
	/** 页面变化回调 */
	private CommentBarPagerConnecter mCommentBarPagerConnecter;

	/**
	 * 空构造
	 */
	public CommentBarFragment() {
		setNeedImageFetcher(false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mComeFromType = (ComeFromType) getArguments().getSerializable(
				KEY_COMEFROM);
		switch (mComeFromType) {
		case NEWS:
			mCommentBarPagerConnecter = (CommentBarPagerConnecter) getArguments()
					.getParcelable(KEY_NEWSDETAIL_CALLBACK);
			break;
		case LECTURE:
			break;
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRealCommentBarViewHolder = new CommentBarViewHolder();
		mWindowCommentBarViewHolder = new CommentBarViewHolder();

		mRealCommentBarViewHolder.mCommentLayout = inflater.inflate(
				R.layout.comment_bar, container, false);
		mWindowCommentBarViewHolder.mCommentLayout = inflater.inflate(
				R.layout.comment_bar, null);

		mRealCommentBarViewHolder.mCommentLayout.getViewTreeObserver()
				.addOnGlobalLayoutListener(mSoftKeyboardListener);

		mRealCommentBarViewHolder.mCommentEditText = (EditText) mRealCommentBarViewHolder.mCommentLayout
				.findViewById(R.id.edit_comment_bar);
		mRealCommentBarViewHolder.mCommentBarActionBtn = (TextView) mRealCommentBarViewHolder.mCommentLayout
				.findViewById(R.id.btn_comment_bar);
		mWindowCommentBarViewHolder.mCommentEditText = (EditText) mWindowCommentBarViewHolder.mCommentLayout
				.findViewById(R.id.edit_comment_bar);
		mWindowCommentBarViewHolder.mCommentBarActionBtn = (TextView) mWindowCommentBarViewHolder.mCommentLayout
				.findViewById(R.id.btn_comment_bar);

		mRealCommentBarViewHolder.mCommentEditText
				.setInputType(InputType.TYPE_NULL);
		mRealCommentBarViewHolder.mCommentEditText
				.setOnTouchListener(mCommentEditOnClickListener);

		RelativeLayout.LayoutParams windowCommentBarParams = new LayoutParams(
				LayoutParams.FILL_PARENT,
				(int) (Utility.getDensity(getActivity()) * 55));
		windowCommentBarParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		RelativeLayout windowCommentBarContainer = new RelativeLayout(
				getActivity());
		mWindowCommentBarViewHolder.mCommentLayout
				.setLayoutParams(windowCommentBarParams);
		windowCommentBarContainer
				.addView(mWindowCommentBarViewHolder.mCommentLayout);
		windowCommentBarContainer
				.setOnClickListener(mSoftKeyboardDismissClickListener);
		mWindowCommentBarViewHolder.mCommentLayout = windowCommentBarContainer;

		return mRealCommentBarViewHolder.mCommentLayout;
	}

	@Override
	public void onSuccess(AbstractModel requestor) {
		if (mCommentRequestor.getCommentResult()) {
			Toast.makeText(getActivity(), R.string.comment_ontheway,
					Toast.LENGTH_LONG).show();
			mCurrentState = CommentState.SENDED;
		} else {
			Toast.makeText(getActivity(), R.string.comment_failed,
					Toast.LENGTH_LONG).show();
			mCurrentState = CommentState.NORMAL;
		}

		Utility.hideInputMethod(getActivity(),
				mWindowCommentBarViewHolder.mCommentLayout);
		updateBtnState(mRealCommentBarViewHolder);
		updateBtnState(mWindowCommentBarViewHolder);
	}

	@Override
	public void onFailed(AbstractModel requestor, int errorCode) {
		Toast.makeText(getActivity(), R.string.network_bad, Toast.LENGTH_LONG)
				.show();
		mCurrentState = CommentState.NORMAL;

		updateBtnState(mRealCommentBarViewHolder);
		updateBtnState(mWindowCommentBarViewHolder);
	}

	/**
	 * 外部刷新按钮状态
	 */
	public void updateBtnState() {
		updateBtnState(mRealCommentBarViewHolder);
		updateBtnState(mWindowCommentBarViewHolder);
	}

	/**
	 * 更新按钮状态
	 */
	private void updateBtnState(CommentBarViewHolder holder) {
		if (mCurrentSoftKeyboardIsShow) {
			updateByCommentState(holder);
		} else {
			// 键盘隐藏时，资讯页面需要额外的切换页面功能
			switch (mComeFromType) {
			case NEWS:
				if (mCommentBarPagerConnecter.getCurrentPage() == NewsDetailActivity.PAGE_INDEX_CONTENT) {
					holder.mCommentBarActionBtn
							.setText(R.string.comment_allcomment);
					holder.mCommentBarActionBtn
							.setBackgroundResource(R.drawable.btn_blue);
					holder.mCommentBarActionBtn
							.setOnClickListener(new OnSingleClickListener() {

								@Override
								public void onSingleClick(View v) {
									mCommentBarPagerConnecter
											.onPageSelected(NewsDetailActivity.PAGE_INDEX_COMMENT);
								}
							});
				} else {
					holder.mCommentBarActionBtn
							.setText(R.string.comment_newsdetail);
					holder.mCommentBarActionBtn
							.setBackgroundResource(R.drawable.btn_blue);
					holder.mCommentBarActionBtn
							.setOnClickListener(new OnSingleClickListener() {

								@Override
								public void onSingleClick(View v) {
									mCommentBarPagerConnecter
											.onPageSelected(NewsDetailActivity.PAGE_INDEX_CONTENT);
								}
							});
				}
				break;
			case LECTURE:
				updateByCommentState(holder);
				break;
			}
		}
	}

	/**
	 * 根据评论状态更改按钮展现
	 */
	private void updateByCommentState(CommentBarViewHolder holder) {
		switch (mCurrentState) {
		case NORMAL:
			holder.mCommentBarActionBtn.setText(R.string.comment_handup);
			holder.mCommentBarActionBtn
					.setBackgroundResource(R.drawable.btn_blue);
			holder.mCommentBarActionBtn
					.setOnClickListener(new HandupCommentOnClickListener(holder));
			break;
		case SENDING:
			holder.mCommentBarActionBtn.setText(R.string.comment_sending);
			holder.mCommentBarActionBtn.setBackgroundColor(getResources()
					.getColor(R.color.btn_disable));
			holder.mCommentBarActionBtn.setOnClickListener(null);
			break;
		case SENDED:
			holder.mCommentBarActionBtn.setText(R.string.comment_sended);
			holder.mCommentBarActionBtn.setBackgroundColor(getResources()
					.getColor(R.color.btn_disable));
			holder.mCommentBarActionBtn.setOnClickListener(null);
			break;
		}
	}

	/** 提交评论事件监听 */
	private class HandupCommentOnClickListener extends OnSingleClickListener {

		private CommentBarViewHolder mHolder;

		public HandupCommentOnClickListener(CommentBarViewHolder holder) {
			mHolder = holder;
		}

		@Override
		public void onSingleClick(View v) {
			if (!TextUtils.isEmpty(mHolder.mCommentEditText.getText()
					.toString().trim())) {
				mCommentRequestor.sendAComment(
						getArguments().getString(KEY_COMMENT_TITLE),
						mHolder.mCommentEditText.getText().toString().trim());
				mHolder.mCommentEditText.setText("");
				// Utility.hideInputMethod(getActivity(), v);
				mCurrentState = CommentState.SENDING;
				updateBtnState(mHolder);
			}
		}
	};

	/** 软键盘监听事件 */
	private OnGlobalLayoutListener mSoftKeyboardListener = new OnGlobalLayoutListener() {
		@Override
		public void onGlobalLayout() {
			Rect r = new Rect();
			// r will be populated with the coordinates of your view
			// that area still visible.
			getView().getWindowVisibleDisplayFrame(r);

			int heightDiff = getView().getRootView().getHeight()
					- (r.bottom - r.top);
			if (heightDiff > 100) { // if more than 100 pixels, its
									// probably a keyboard...
				mCurrentSoftKeyboardIsShow = true;

				if (mAddToWindow) {
					mWindowCommentBarViewParams.height = (int) (Utility
							.getScreenHeight(getActivity()) - heightDiff);
					mWindowCommentBarViewParams.y = 0;
					((WindowManager) getActivity().getSystemService(
							Context.WINDOW_SERVICE)).updateViewLayout(
							mWindowCommentBarViewHolder.mCommentLayout,
							mWindowCommentBarViewParams);
				}
			} else {
				mCurrentSoftKeyboardIsShow = false;

				if (mAddToWindow) {
					WindowManager windowManager = (WindowManager) getActivity()
							.getSystemService(Context.WINDOW_SERVICE);
					windowManager
							.removeView(mWindowCommentBarViewHolder.mCommentLayout);
					mAddToWindow = false;

					mRealCommentBarViewHolder.mCommentEditText
							.setText(mWindowCommentBarViewHolder.mCommentEditText
									.getText());
				}
			}

			updateBtnState(mRealCommentBarViewHolder);
			updateBtnState(mWindowCommentBarViewHolder);
		}
	};

	/**
	 * 唤起键盘
	 */
	private OnTouchListener mCommentEditOnClickListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, android.view.MotionEvent event) {
			if (!mAddToWindow) {
				mWindowCommentBarViewParams = new WindowManager.LayoutParams();
				mWindowCommentBarViewParams.gravity = Gravity.TOP
						| Gravity.LEFT;
				mWindowCommentBarViewParams.x = 0;
				mWindowCommentBarViewParams.y = 0;
				mWindowCommentBarViewParams.height = (int) Utility
						.getScreenHeight(getActivity())
						- Utility.getStatusBarHeight(getActivity());
				mWindowCommentBarViewParams.width = WindowManager.LayoutParams.FILL_PARENT;
				mWindowCommentBarViewParams.format = PixelFormat.RGBA_8888;
				WindowManager windowManager = (WindowManager) getActivity()
						.getSystemService(Context.WINDOW_SERVICE);
				windowManager.addView(
						mWindowCommentBarViewHolder.mCommentLayout,
						mWindowCommentBarViewParams);

				mAddToWindow = true;
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						Utility.showInputMethod(getActivity(),
								mWindowCommentBarViewHolder.mCommentEditText);

					}
				}, 200);
			}
			return true;
		};
	};

	/**
	 * 监听操作软键盘消失的点击事件
	 */
	private OnSingleClickListener mSoftKeyboardDismissClickListener = new OnSingleClickListener() {

		@Override
		public void onSingleClick(View v) {
			Utility.hideInputMethod(getActivity(),
					mWindowCommentBarViewHolder.mCommentLayout);
		}
	};

	@Override
	public void onActionTrigger(int actionId) {

	}

	@Override
	protected void setImageCacheParams() {

	}

	@Override
	protected void setupModel() {
		mCommentRequestor = new CommentPublishRequestor(getActivity(),
				getArguments().getString(KEY_COMMENT_TAG), this);
		mModelManageQueue.addTaskModel(KEY_MODEL_REQUESTOR, mCommentRequestor);
	}

	/**
	 * 评论条view holder
	 * 
	 * @author Calvin
	 * 
	 */
	private class CommentBarViewHolder {

		private View mCommentLayout;
		/** 评论文字框 */
		private EditText mCommentEditText;
		/** 评论动作按钮 */
		private TextView mCommentBarActionBtn;
	}
}
