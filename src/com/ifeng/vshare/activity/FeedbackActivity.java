package com.ifeng.vshare.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ifeng.util.model.AbstractModel;
import com.ifeng.util.net.requestor.IRequestModelErrorCode;
import com.ifeng.util.ui.NavgationbarView;
import com.ifeng.util.ui.OnSingleClickListener;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareActivity;
import com.ifeng.vshare.VShareFragment;
import com.ifeng.vshare.requestor.BaseVShareRequestor.VShareRequestResult;
import com.ifeng.vshare.requestor.FeedbackRequestor;

/**
 * 意见反馈
 * 
 * @author Calvin
 * 
 */
public class FeedbackActivity extends VShareActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.base_activity_single);

		NavgationbarView navgationbar = (NavgationbarView) findViewById(R.id.navgationbar);

		/*
		 * 设置导航条部分
		 */
		navgationbar.setTitle(getString(R.string.title_feedback));
		navgationbar.setBackItem(this);

		getSupportFragmentManager().beginTransaction()
				.add(R.id.layout_content, new FeedFragment()).commit();
	}

	/**
	 * 意见反馈
	 * 
	 * @author Calvin
	 * 
	 */
	public static class FeedFragment extends VShareFragment {

		/** 反馈请求key */
		private static final String KEY_REQUESTOR = "requestor";
		/** 反馈请求 */
		private FeedbackRequestor mFeedbackRequestor;

		/** 反馈联系人 */
		private EditText mFeedbackContact;
		/** 反馈信息 */
		private EditText mFeedbackContent;
		/** 提交按钮 */
		private Button mHandUpBtn;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View layout = inflater.inflate(R.layout.feedback, container, false);

			mFeedbackContact = (EditText) layout
					.findViewById(R.id.edit_feedback_contact);
			mFeedbackContent = (EditText) layout
					.findViewById(R.id.edit_feedback_content);
			mHandUpBtn = (Button) layout.findViewById(R.id.btn_feedback_handup);

			mFeedbackRequestor = new FeedbackRequestor(getActivity(), this);
			mModelManageQueue.addTaskModel(KEY_REQUESTOR, mFeedbackRequestor);

			mHandUpBtn.setOnClickListener(mClickListener);

			return layout;
		}

		/**
		 * 提交
		 */
		private OnSingleClickListener mClickListener = new OnSingleClickListener() {

			@Override
			public void onSingleClick(View v) {
				String contact = mFeedbackContact.getText().toString();
				String content = mFeedbackContent.getText().toString();

				if (TextUtils.isEmpty(content)
						|| TextUtils.isEmpty(content.trim())) {
					Toast.makeText(getActivity(),
							R.string.feedback_handup_empty, Toast.LENGTH_LONG)
							.show();
					return;
				}

				if (content.trim().length() >= 255) {
					content = content.substring(0, 254);
					Toast.makeText(getActivity(),
							R.string.feedback_handup_over, Toast.LENGTH_LONG)
							.show();
				}

				mHandUpBtn.setEnabled(false);
				mHandUpBtn.setBackgroundColor(getResources().getColor(
						R.color.btn_disable));
				mHandUpBtn.setText(R.string.feedback_handuping);
				mFeedbackRequestor.handUpFeedback(contact, content);
			}
		};

		@Override
		public void onSuccess(AbstractModel model) {
			if (mFeedbackRequestor.getLastRequestResult() != VShareRequestResult.SUCCESS) {
				onFailed(model, IRequestModelErrorCode.ERROR_CODE_SERVICE_ERROR);
				return;
			}

			mFeedbackContact.setText("");
			mFeedbackContent.setText("");
			mHandUpBtn.setEnabled(true);
			mHandUpBtn.setBackgroundResource(R.drawable.btn_blue);
			mHandUpBtn.setText(R.string.feedback_handup);
			Toast.makeText(getActivity(), R.string.feedback_handup_success,
					Toast.LENGTH_LONG).show();
		};

		@Override
		public void onFailed(AbstractModel model, int errorCode) {
			mHandUpBtn.setEnabled(true);
			mHandUpBtn.setBackgroundResource(R.drawable.btn_blue);
			mHandUpBtn.setText(R.string.feedback_handup);
			Toast.makeText(getActivity(), R.string.network_bad,
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onActionTrigger(int actionId) {

		}
	}
}
