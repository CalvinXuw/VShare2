package com.ifeng.vshare.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ifeng.util.model.AbstractModel;
import com.ifeng.util.net.requestor.IRequestModelErrorCode;
import com.ifeng.util.ui.NavgationbarView;
import com.ifeng.util.ui.NavgationbarView.Style;
import com.ifeng.util.ui.StateView;
import com.ifeng.util.ui.StateView.State.OnStateActionListener;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareActivity;
import com.ifeng.vshare.fragment.PhotoDetailFragment;
import com.ifeng.vshare.requestor.BaseVShareRequestor;
import com.ifeng.vshare.requestor.BaseVShareRequestor.VShareRequestResult;
import com.ifeng.vshare.requestor.PhotoDetailRequestor;
import com.ifeng.vshare.ui.state.ErrorRequestState;
import com.ifeng.vshare.ui.state.ProcessingRequestState;

/**
 * 图片详情Activity
 * 
 * @author qianzy
 * @time 2013-6-6 下午4:54:33
 */
public class PhotoDetailActivity extends VShareActivity implements
		OnStateActionListener {

	/** key photo id */
	private static final String KEY_PHOTO_ID = "photo_id";
	/** key photo title */
	private static final String KEY_PHOTO_TITLE = "photo_title";

	/**
	 * get intent
	 * 
	 * @param activity
	 * @param title
	 * @param id
	 * @return
	 */
	public static Intent getIntent(Context activity, String title, int id) {
		Intent intent = new Intent(activity, PhotoDetailActivity.class);
		intent.putExtra(KEY_PHOTO_TITLE, title);
		intent.putExtra(KEY_PHOTO_ID, id);
		return intent;
	}

	/** key model requestor */
	private static final String KEY_MODEL_REQUESTOR = "requestor";

	/** 图片详情数据requestor */
	private PhotoDetailRequestor mPhotosDetailRequestor;
	/** stateview */
	private StateView mStateView;
	/** process state */
	private ProcessingRequestState mProcessState;
	/** error state */
	private ErrorRequestState mErrorState;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.base_activity_single);

		// 设置导航条
		NavgationbarView navgationbar = (NavgationbarView) findViewById(R.id.navgationbar);
		navgationbar.setStyle(Style.DARK);
		navgationbar.setTitle(getString(R.string.title_photo));
		navgationbar.setBackItem(this);

		// 设置状态页面
		mStateView = (StateView) findViewById(R.id.stateview);
		mProcessState = new ProcessingRequestState(this,
				ProcessingRequestState.Style.DARK);
		mErrorState = new ErrorRequestState(this, ErrorRequestState.Style.DARK,
				this);

		mStateView.setState(mProcessState);

		mPhotosDetailRequestor = new PhotoDetailRequestor(this, getIntent()
				.getExtras().getInt(KEY_PHOTO_ID), this);
		mModelManageQueue.addTaskModel(KEY_MODEL_REQUESTOR,
				mPhotosDetailRequestor);
		mPhotosDetailRequestor.request();
	}

	@Override
	public void onActionTrigger(int actionId) {
		if (actionId == ErrorRequestState.STATE_ACTION_ERROR_RETRY) {
			mStateView.setState(mProcessState);
			mPhotosDetailRequestor.request();
		}
	}

	@Override
	public void onSuccess(AbstractModel requestor) {
		// 筛除服务器返回错误
		if (((BaseVShareRequestor) requestor).getLastRequestResult() != VShareRequestResult.SUCCESS) {
			onFailed(requestor, IRequestModelErrorCode.ERROR_CODE_SERVICE_ERROR);
			return;
		} else {
			PhotoDetailFragment detailFragment = PhotoDetailFragment
					.getInstance(
							getIntent().getExtras().getString(KEY_PHOTO_TITLE),
							((PhotoDetailRequestor) requestor).getDetailItem());
			getSupportFragmentManager().beginTransaction()
					.add(R.id.layout_content, detailFragment).commit();
			// 请求完成，结束等待页面
			mStateView.dismiss();
		}
	}

	@Override
	public void onFailed(AbstractModel requestor, int errorCode) {
		mStateView.setState(mErrorState);
	}

	@Override
	public void finish() {
		// 由推送进入详情页面，需要返回主菜单
		if (sActivityStack.size() == 1) {
			startActivity(new Intent(this, HeadlineCategoryActivity.class));
		}
		super.finish();
	}
}
