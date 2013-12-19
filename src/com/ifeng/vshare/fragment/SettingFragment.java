package com.ifeng.vshare.fragment;

import java.io.File;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ifeng.ipush.client.Ipush;
import com.ifeng.util.Utility;
import com.ifeng.util.model.AbstractModel;
import com.ifeng.util.net.requestor.RequestDataCache;
import com.ifeng.util.ui.OnSingleClickListener;
import com.ifeng.util.ui.SwitchButton;
import com.ifeng.util.ui.SwitchButton.OnSwitcherSwitchListener;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareFragment;
import com.ifeng.vshare.activity.AboutActivity;
import com.ifeng.vshare.activity.FeedbackActivity;
import com.ifeng.vshare.config.UserConfig;
import com.ifeng.vshare.model.ClientUpdateItem;
import com.ifeng.vshare.requestor.ClientUpdateRequertor;
import com.ifeng.vshare.ui.DialogManager;

/**
 * 设置页面fragment
 * 
 * @author Calvin
 * 
 */
public class SettingFragment extends VShareFragment {

	/** key model requestor */
	private static final String KEY_MODEL_REQUESTOR = "requestor";

	/*
	 * 缓存部分
	 */
	/** 缓存大小 */
	private TextView mCacheSize;
	/** 缓存目录 */
	private File mCacheDir;

	/*
	 * 更新部分
	 */
	/** 版本名 */
	private TextView mVersion;
	/** 更新new标识 */
	private View mUpdateTag;
	/** 更新按钮 */
	private Button mUpdateBtn;
	/** clientupdate requestor */
	private ClientUpdateRequertor mClientUpdateRequertor;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mainView = inflater.inflate(R.layout.setting, container, false);

		// push服务开关
		SwitchButton pushSwitchButton = (SwitchButton) mainView
				.findViewById(R.id.switch_setting_push);

		// 网路环境检测开关
		SwitchButton networkCheckSwitchButton = (SwitchButton) mainView
				.findViewById(R.id.switch_setting_network);

		// 清除缓存
		View clearCacheButton = mainView
				.findViewById(R.id.btn_setting_clearcache);
		mCacheSize = (TextView) mainView
				.findViewById(R.id.text_setting_cachesize);
		mCacheDir = RequestDataCache.getExternalCacheDir(getActivity());

		// 版本更新
		mVersion = (TextView) mainView.findViewById(R.id.text_setting_version);
		mUpdateTag = mainView.findViewById(R.id.text_setting_new);
		mUpdateBtn = (Button) mainView.findViewById(R.id.btn_setting_update);

		// 关于
		mainView.findViewById(R.id.btn_setting_about).setOnClickListener(
				mAboutClickListener);

		// 反馈
		mainView.findViewById(R.id.btn_setting_feedback).setOnClickListener(
				mFeedbackClickListener);

		// 交互及界面信息注入
		pushSwitchButton.setOnSwitcherSwitchListener(mPushSwitchListener);
		networkCheckSwitchButton
				.setOnSwitcherSwitchListener(mNetworkCheckSwitchListener);
		clearCacheButton.setOnClickListener(mCacheClearListener);

		mVersion.setText(Utility.getAppVersionName(getActivity()));
		mCacheSize.setText(Utility.convertFileSize(Utility
				.getFileSize(mCacheDir)));

		pushSwitchButton
				.setSwitch(Ipush.getState(getActivity()) == Ipush.PUSHSERVICE_STATE_RUNNING);
		// 不进行提醒，即默认允许加载
		networkCheckSwitchButton.setSwitch(!UserConfig.getInstance(
				getActivity()).isAllowAccessPictureWithoutWifi(false));

		mClientUpdateRequertor.checkUpdate();
		return mainView;
	}

	/** 推送开关 */
	private OnSwitcherSwitchListener mPushSwitchListener = new OnSwitcherSwitchListener() {

		@Override
		public void onSwitch(boolean isOn) {
			if (!isOn) {
				Ipush.stopService(getActivity());
			} else {
				Ipush.resumeService(getActivity());
			}
		}
	};

	/** 网络环境检测 */
	private OnSwitcherSwitchListener mNetworkCheckSwitchListener = new OnSwitcherSwitchListener() {

		@Override
		public void onSwitch(boolean isOn) {
			UserConfig.getInstance(getActivity())
					.setAllowAccessPictureWithoutWifi(!isOn);
			UserConfig.getInstance(getActivity())
					.setAllowAccessVideoWithoutWifi(!isOn);
		}
	};

	/** 清除缓存 */
	private OnSingleClickListener mCacheClearListener = new OnSingleClickListener() {

		@Override
		public void onSingleClick(View v) {
			DialogManager.getInstance().createDialog(
					getString(R.string.setting_clear),
					getString(R.string.setting_clear_message),
					new DialogManager.DialogStateCallback() {

						@Override
						public void onClick(int which) {
							if (which == DialogInterface.BUTTON_POSITIVE) {
								clearCacheAsync();
							}
						}

						@Override
						public void onCancel() {

						}
					}, true, getString(R.string.dialog_confirm),
					getString(R.string.dialog_cancel));
		}
	};

	/** 意见反馈 */
	private OnSingleClickListener mFeedbackClickListener = new OnSingleClickListener() {

		@Override
		public void onSingleClick(View v) {
			startActivity(new Intent(getActivity(), FeedbackActivity.class));
		}
	};

	/** 关于 */
	private OnSingleClickListener mAboutClickListener = new OnSingleClickListener() {

		@Override
		public void onSingleClick(View v) {
			startActivity(new Intent(getActivity(), AboutActivity.class));
		}
	};

	/** 更新提示 */
	private OnSingleClickListener mUpdateListener = new OnSingleClickListener() {

		@Override
		public void onSingleClick(View v) {
			mClientUpdateRequertor.showUpdateDialog();
		}
	};

	/**
	 * 清理缓存
	 */
	private void clearCacheAsync() {
		Toast.makeText(getActivity(), R.string.setting_clear_begin,
				Toast.LENGTH_SHORT).show();
		final Handler mainThreadHandler = new Handler(getActivity()
				.getMainLooper());
		new Thread() {
			@Override
			public void run() {
				deleteCacheFile(mCacheDir);
				mainThreadHandler.post(new Runnable() {

					@Override
					public void run() {
						// 当前页面已经退出
						if (getActivity() == null) {
							return;
						}
						Toast.makeText(getActivity(),
								R.string.setting_clear_finish,
								Toast.LENGTH_SHORT).show();
						mCacheSize.setText(Utility.convertFileSize(Utility
								.getFileSize(mCacheDir)));
					}
				});
			}
		}.start();
	}

	/**
	 * 删除缓存文件
	 * 
	 * @param file
	 */
	private void deleteCacheFile(File file) {
		try {
			if (file.exists()) {
				if (file.isFile()) {
					file.delete();
				} else if (file.isDirectory()) {
					File files[] = file.listFiles();
					for (int i = 0; i < files.length; i++) {
						this.deleteCacheFile(files[i]);
					}
				}
				if (file != mCacheDir) {
					file.delete();
				}
			} else {

			}
		} catch (Exception e) {
		}
	}

	@Override
	public void onSuccess(AbstractModel requestor) {
		// 已经经过了版本校验和更新检测间隔筛查，直接显示对话框即可
		final ClientUpdateItem updateItem = ((ClientUpdateRequertor) requestor)
				.getClientUpdateItem();

		if (updateItem == null) {
			return;
		}
		mUpdateBtn.setOnClickListener(mUpdateListener);
		mUpdateBtn.setVisibility(View.VISIBLE);
		mUpdateTag.setVisibility(View.VISIBLE);
		mVersion.setVisibility(View.GONE);
	}

	@Override
	public void onFailed(AbstractModel requestor, int errorCode) {
		// do nothing~
	}

	@Override
	public void onActionTrigger(int actionId) {
	}

	@Override
	protected void setImageCacheParams() {
	}

	@Override
	protected void setupModel() {
		mClientUpdateRequertor = new ClientUpdateRequertor(getActivity(), this);
		mModelManageQueue.addTaskModel(KEY_MODEL_REQUESTOR,
				mClientUpdateRequertor);
	}

}
