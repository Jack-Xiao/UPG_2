package com.juchao.upg.android;

import com.juchao.upg.android.crash.CrashHandler;
import com.juchao.upg.android.receiver.TickUpdate;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;

import android.app.Application;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MyApp extends Application {

	public static MyApp application;

	public static boolean IsIData = false; 

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());

		application = this;
		// BaseInit();
		TickUpdate.register(this);

		getPhoneInfo();
	}


	private void getPhoneInfo() {

		String mtype = android.os.Build.MODEL; // 手机型号 ---- xiaomi == A4
		
//		TelephonyManager tmManager = (TelephonyManager) this
//				.getSystemService(TELEPHONY_SERVICE);
//		String mtyb = android.os.Build.BRAND;// 手机品牌
//		String imei = tmManager.getDeviceId();
//		String imsi = tmManager.getSubscriberId();
//		String numer = tmManager.getLine1Number(); // 手机号码
//		String serviceName = tmManager.getSimOperatorName(); // 运营商
		if(String.valueOf(mtype).equals("A4")){
			IsIData = true;
		}
	}

	private void BaseInit() {
		// PERIOD = DefaultShared.getLong(Constants.KEY_REMIND_INTERVAL, 0L);
		DefaultShared.putInt(Constants.KEY_REMIND_INTERVAL, 1000 * 60);
	}
}
