package com.ifeng.vshare.activity;

import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.ifeng.util.ui.NavgationbarView;
import com.ifeng.util.ui.SlideTabbarView;
import com.ifeng.util.ui.SlideTabbarView.OnTabSelectedListener;
import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareActivity;
import com.ifeng.vshare.adapter.PagerSliderConnectAdapter;
import com.ifeng.vshare.fragment.LobbyListFragment;
import com.ifeng.vshare.requestor.LobbyListRequestor.LobbyType;

/**
 * 贵宾厅页面
 * 
 * @author Calvin
 * 
 */
public class LobbyActivity extends VShareActivity {

	@SuppressLint("ResourceAsColor")
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		createSinglePage();
	}

	/**
	 * 单一机场页面
	 */
	private void createSinglePage() {
		setContentView(R.layout.base_activity_single);

		NavgationbarView navgationbar = (NavgationbarView) findViewById(R.id.navgationbar);

		/*
		 * 设置导航条部分
		 */
		navgationbar.setTitle(getString(R.string.title_lobby));
		navgationbar.setBackItem(this);

		getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.layout_content,
						LobbyListFragment.getInstance(LobbyType.Airport)).commit();
	}

	/**
	 * 火车站、机场分页数据
	 */
	@SuppressLint("ResourceAsColor")
	private void createMultiplePage() {
		setContentView(R.layout.base_activity_multiple);

		NavgationbarView navgationbar = (NavgationbarView) findViewById(R.id.navgationbar);
		SlideTabbarView slideTabbar = (SlideTabbarView) findViewById(R.id.tab_slidebar);
		final ViewPager pager = (ViewPager) findViewById(R.id.pager_content);

		/*
		 * 设置导航条部分
		 */
		navgationbar.setTitle(getString(R.string.title_lobby));
		navgationbar.setBackItem(this);

		/*
		 * 处理分页数据
		 */
		LinkedList<Fragment> subPageFragments = new LinkedList<Fragment>();
		subPageFragments.add(LobbyListFragment.getInstance(LobbyType.Airport));
		subPageFragments.add(LobbyListFragment
				.getInstance(LobbyType.RailwayStation));

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
				getSupportFragmentManager(), subPageFragments, slideTabbar);

		pager.setAdapter(mAdapter);
		pager.setOnPageChangeListener(mAdapter);
		slideTabbar
				.addTabsByTabs(new OnTabSelectedListener() {
					@Override
					public void onSelected(int which) {
						pager.setCurrentItem(which);
					}
				}, getString(R.string.title_airport),
						getString(R.string.title_railway));
	}
}
