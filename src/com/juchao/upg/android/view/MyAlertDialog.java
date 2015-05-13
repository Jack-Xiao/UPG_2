package com.juchao.upg.android.view;

import java.lang.reflect.Method;

import com.juchao.upg.android.MyApp;

import android.app.AlertDialog;
import android.content.Context;

public class MyAlertDialog extends AlertDialog {
	private Context context;

	protected MyAlertDialog(Context context, int theme) {
		super(context, theme);
		this.context=context;
		// TODO Auto-generated constructor stub
	}

	protected MyAlertDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		this.context=context;
		// TODO Auto-generated constructor stub
	}

	protected MyAlertDialog(Context context) {
		super(context);
		this.context=context;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		try {

			Object service = MyApp.application.getSystemService("statusbar");
			Class<?> statusbarManager = Class
					.forName("android.app.StatusBarManager");
			Method test = statusbarManager.getMethod("collapse");
			test.invoke(service);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
