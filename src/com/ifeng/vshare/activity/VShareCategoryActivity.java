package com.ifeng.vshare.activity;

import com.ifeng.vshare.VShareActivity;

/**
 * 一级菜单activity基类
 * 
 * @author Calvin
 * 
 */
public class VShareCategoryActivity extends VShareActivity {

	@Override
	public void finish() {
		// 重新唤醒indexactivity
		//startActivity(new Intent(this, VShareMainActivity.class));
		super.finish();
	}
}
