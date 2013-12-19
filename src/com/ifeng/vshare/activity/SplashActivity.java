package com.ifeng.vshare.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.ifeng.vshare.R;
import com.ifeng.vshare.VShareActivity;
import com.ifeng.vshare.requestor.InitRequestor;

/**
 * 欢迎页面
 * 
 * @author Calvin
 * 
 */
public class SplashActivity extends VShareActivity {

	/** 初始化接口 */
	private static final String KEY_REQUESTOR = "init";

	/** 背景图view */
	private ImageView mLayoutView;

	@Override
	protected void onCreate(Bundle arg0) {
		if (sActivityStack.size() > 0) {
			clearStack();
		}

		super.onCreate(arg0);

		setContentView(R.layout.splash);
		mLayoutView = (ImageView) findViewById(R.id.image_splash);
		mLayoutView.setImageBitmap(BitmapFactory.decodeStream(getResources()
				.openRawResource(R.drawable.background_splash)));

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				finish();
				startActivity(new Intent(SplashActivity.this,
						VShareMainActivity.class));
			}
		}, 1000);

		InitRequestor initRequestor = new InitRequestor(this, this);
		mModelManageQueue.addTaskModel(KEY_REQUESTOR, initRequestor);
		initRequestor.request();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 禁止返回
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		mLayoutView.setImageBitmap(null);
		super.onDestroy();
	}
}
