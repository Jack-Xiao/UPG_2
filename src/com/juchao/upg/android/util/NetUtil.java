package com.juchao.upg.android.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class NetUtil {

	static public NetworkInfo getActiveNetInfo(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		return connectivityManager.getActiveNetworkInfo();
	}
	
	static public boolean isOk(Context context) {
		NetworkInfo networkInfo = getActiveNetInfo(context);
		return  (networkInfo != null && networkInfo.isAvailable());
	}
	
	static public boolean isNetworkConnected(Context context) {
		NetworkInfo networkInfo = getActiveNetInfo(context);
		//有的时候网络有链接，但是不可用。
		return (networkInfo != null && networkInfo.isConnected() );
	}
	
	static public boolean isConnectedOrConnecting(Context context) {
		NetworkInfo networkInfo = getActiveNetInfo(context);
		return (networkInfo != null && networkInfo.isConnectedOrConnecting());
	}
	
	static public boolean isConnect2G(Context context){
		
		NetworkInfo networkInfo = getActiveNetInfo(context);
		
		if (networkInfo == null || !networkInfo.isConnectedOrConnecting())return false;;
		
		int type = networkInfo.getType();
		int subType = networkInfo.getSubtype();
		 if(type==ConnectivityManager.TYPE_MOBILE){
			 switch(subType){  
			 case TelephonyManager.NETWORK_TYPE_GPRS:  
	                return true; // ~ 100 kbps  
	         case TelephonyManager.NETWORK_TYPE_CDMA:  
	        	 return true; // ~ 14-64 kbps 
	         case TelephonyManager.NETWORK_TYPE_1xRTT:  
	                    return true;  // ~ 50-100 kbps  
	         case TelephonyManager.NETWORK_TYPE_EDGE:  
	                    return true; // ~ 50-100 kbps  
	         default:
	                	return false;
			 }
		 }else{
			 return false;
		 }
		
	}
}
