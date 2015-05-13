package com.juchao.upg.android.util;

import java.util.Map;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.juchao.upg.android.R;

/**
 * 启动Activity
 * @author xuxd
 *
 */
public class IntentUtil {
	
	/**
	 * 启动Activity ，不带参数
	 * @param activity
	 * @param cls
	 */
	public static void startActivity(Activity activity,Class<?> cls){
		Intent intent=new Intent();
		intent.setClass(activity,cls);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	/**
	 * 启动Activity ，不带参数
	 * @param activity
	 * @param cls
	 */
	public static void startActivityFromMain(Activity activity,Class<?> cls){
		Intent intent=new Intent();
		intent.setClass(activity,cls);
		activity.startActivity(intent);
	}
	/**
	 * 启动Activity ，带参数
	 * @param activity
	 * @param cls
	 * @param params
	 */
	public static void startActivity(Activity activity,Class<?> cls,Bundle bundle){
		Intent intent=new Intent();
		intent.setClass(activity,cls);
		intent.putExtras(bundle);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	public static void startActivityFromMain(Activity activity,Class<?> cls,Bundle bundle){
		Intent intent=new Intent();
		intent.setClass(activity,cls);
		intent.putExtras(bundle);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	public static void startActivityFromMain1(Activity activity,Class<?> cls,Bundle bundle){
		Intent intent=new Intent();
		intent.setClass(activity,cls);
		intent.putExtras(bundle);
		activity.startActivity(intent);
		//activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	/**
	 * 启动service
	 * @param activity
	 * @param cls
	 * @param params
	 */
	public static void startService(Activity activity,Class<?> cls,BasicNameValuePair...params){
		Intent intent=new Intent();
		intent.setClass(activity,cls);
		for(int i=0;i<params.length;i++){
			intent.putExtra(params[i].getName(), params[i].getValue());
		}
		activity.startService(intent);
	}
}
