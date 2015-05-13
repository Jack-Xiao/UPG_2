package com.juchao.upg.android.ui;

import java.lang.reflect.Method;

import com.juchao.upg.android.MyApp;
import com.juchao.upg.android.util.HadwareControll;
import com.juchao.upg.android.util.IntentUtil;
import com.juchao.upg.android.util.scan.qr_codescan.MipcaActivityCapture;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class BaseActivity extends Activity {
	protected final static int SCANNIN_GREQUEST_CODE = 1;
	public ProgressBar mListFooterProgress;
	public View mListFooterView;
	public TextView mLoadingText;
	protected String scanningCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		forbiddenNotification();
	}

	private void forbiddenNotification() {
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// notificationManager.

	}

	protected void finishActivity() {
		BaseActivity.this.finish();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		try {

			Object service = getSystemService("statusbar");
			Class<?> statusbarManager = Class
					.forName("android.app.StatusBarManager");
			Method test = statusbarManager.getMethod("collapse");
			test.invoke(service);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 设置底部加载行的可见性
	 * 
	 * @param visibility
	 */
	public void setFooterViewVisibility(int visibility) {
		if (visibility == View.GONE || visibility == View.INVISIBLE) {
			mListFooterView.setVisibility(View.GONE);
			mListFooterProgress.setVisibility(View.GONE);
			mLoadingText.setVisibility(View.GONE);
		} else if (visibility == View.VISIBLE) {
			mListFooterView.setVisibility(View.VISIBLE);
			mListFooterProgress.setVisibility(View.VISIBLE);
			mLoadingText.setVisibility(View.VISIBLE);
		}
	}

	protected void beginScanCode(BaseActivity activity) {
		judegScan(true);
	}

	protected void judegScan(boolean needScan) {
		if (MyApp.IsIData) {
			HadwareControll.Open();
		} else {
			if (needScan) {
				Intent intent = new Intent(this, MipcaActivityCapture.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
			}
		}
	}

}
