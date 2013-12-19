package com.ifeng.vshare.activity;

import java.util.LinkedList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.ifeng.util.model.AbstractModel;
import com.ifeng.util.net.requestor.IRequestModelErrorCode;
import com.ifeng.util.ui.NavgationbarView;
import com.ifeng.util.ui.SlideTabbarView;
import com.ifeng.util.ui.SlideTabbarView.OnTabSelectedListener;
import com.ifeng.util.ui.StateView;
import com.ifeng.util.ui.StateView.State.OnStateActionListener;
import com.ifeng.vshare.R;
import com.ifeng.vshare.adapter.PagerSliderConnectAdapter;
import com.ifeng.vshare.fragment.PointsListFragment;
import com.ifeng.vshare.model.PointsColumnListItem.PointsColumnItem;
import com.ifeng.vshare.requestor.BaseVShareRequestor;
import com.ifeng.vshare.requestor.BaseVShareRequestor.VShareRequestResult;
import com.ifeng.vshare.requestor.PointsColumnRequestor;
import com.ifeng.vshare.ui.state.ErrorRequestState;
import com.ifeng.vshare.ui.state.ProcessingRequestState;

/**
 * 积分商城activity
 * 
 * @author Calvin
 * 
 */
public class PointsCategoryActivity extends VShareCategoryActivity implements
		OnStateActionListener {

	/** key model requestor */
	private static final String KEY_MODEL_REQUESTOR = "requestor";

	/** 栏目列表请求 */
	private PointsColumnRequestor mPointsColumnRequestor;
	/** stateview */
	private StateView mStateView;
	/** process state */
	private ProcessingRequestState mProcessState;
	/** error state */
	private ErrorRequestState mErrorState;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		setContentView(R.layout.base_activity_multiple);

		NavgationbarView navgationbar = (NavgationbarView) findViewById(R.id.navgationbar);

		/*
		 * 设置导航条部分
		 */
		navgationbar.setTitle(getString(R.string.title_pointmarket));
		navgationbar.setBackItem(this);

		// 设置状态页面
		mStateView = (StateView) findViewById(R.id.stateview);
		mStateView.setVisibility(View.VISIBLE);
		mProcessState = new ProcessingRequestState(this);
		mErrorState = new ErrorRequestState(this, this);

		mStateView.setState(mProcessState);

		mPointsColumnRequestor = new PointsColumnRequestor(this, this);
		mModelManageQueue.addTaskModel(KEY_MODEL_REQUESTOR,
				mPointsColumnRequestor);
		mPointsColumnRequestor.request();
	}

	/**
	 * 更新界面
	 */
	private void updateUI() {
		List<PointsColumnItem> columnItems = mPointsColumnRequestor
				.getColumnList();

		if (columnItems.size() == 0) {
			return;
		}

		String[] titles = new String[columnItems.size()];
		for (int i = 0; i < columnItems.size(); i++) {
			titles[i] = columnItems.get(i).mColumnName;
		}

		SlideTabbarView slideTabbar = (SlideTabbarView) findViewById(R.id.tab_slidebar);
		final ViewPager pager = (ViewPager) findViewById(R.id.pager_content);
		/*
		 * 处理分页数据
		 */
		LinkedList<Fragment> subVideoColumns = new LinkedList<Fragment>();
		for (int i = 0; i < columnItems.size(); i++) {
			PointsListFragment pointsListFragment = PointsListFragment
					.getInstance(columnItems.get(i).mCid);
			subVideoColumns.add(pointsListFragment);
		}

		/*
		 * 设置tab滑块和pager，连接起两个控件
		 */
		slideTabbar.setHintBackground(R.drawable.background_slidebar_hint);
		slideTabbar.setNormalTextSizeFromDimen(getResources()
				.getDimensionPixelSize(R.dimen.font_slidebar));
		slideTabbar.setActiveTextSizeFromDimen(getResources()
				.getDimensionPixelSize(R.dimen.font_slidebar));
		slideTabbar.setBackgroundResource(R.drawable.background_slidebar);
		slideTabbar.setNormalTextColor(R.color.font_slidebar_nag);
		slideTabbar.setActiveTextColor(R.color.font_slidebar_pos);

		PagerSliderConnectAdapter mAdapter = new PagerSliderConnectAdapter(
				getSupportFragmentManager(), subVideoColumns, slideTabbar);

		pager.setAdapter(mAdapter);
		pager.setOnPageChangeListener(mAdapter);

		if (titles.length <= 4) {
			slideTabbar.addTabsByTabs(new OnTabSelectedListener() {
				@Override
				public void onSelected(int which) {
					pager.setCurrentItem(which);
				}
			}, titles);
		} else {
			slideTabbar.addTabsByTitles(100, new OnTabSelectedListener() {
				@Override
				public void onSelected(int which) {
					pager.setCurrentItem(which);
				}
			}, titles);
		}

	}

	@Override
	public void onActionTrigger(int actionId) {
		if (actionId == ErrorRequestState.STATE_ACTION_ERROR_RETRY) {
			mStateView.setState(mProcessState);
			mPointsColumnRequestor.request();
		}
	}

	@Override
	public void onSuccess(AbstractModel requestor) {
		// 筛除服务器返回错误
		if (((BaseVShareRequestor) requestor).getLastRequestResult() != VShareRequestResult.SUCCESS) {
			onFailed(requestor, IRequestModelErrorCode.ERROR_CODE_SERVICE_ERROR);
			return;
		} else {
			// 请求完成，结束等待页面
			mStateView.dismiss();
		}

		updateUI();
	}

	@Override
	public void onFailed(AbstractModel requestor, int errorCode) {
		mStateView.setState(mErrorState);
	}
}
