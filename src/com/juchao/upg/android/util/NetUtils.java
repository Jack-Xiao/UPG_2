/**
 * 2009-12-5
 */
package com.juchao.upg.android.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;


public class NetUtils {

	/**
	 * 检查网络是否已连接
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivity.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			return true;
		}
		return false;
	}	
	
	
	/**
	 * 检测网络连接时网络类型
	 * @param context
	 * @return
	 */
	public static int getNetConectionedType(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
		        .getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		int netConectionType = -1;

		if (activeNetInfo != null && activeNetInfo.isConnected()) {
			return activeNetInfo.getType();
		}
		return netConectionType;
	}
	
	
	
	/**
	 * 打开wifi开关
	 * @param context
	 */
	public static void openWifi(Context context)
	{
		WifiManager  wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(true);
	}
	
	/**
	 * 关闭wifi开关
	 * @param context
	 */
	public static void closeWifi(Context context)
	{
		WifiManager  wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(false);
	}

}
