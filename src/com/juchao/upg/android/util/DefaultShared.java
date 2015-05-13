package com.juchao.upg.android.util;

import java.util.Map;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.juchao.upg.android.MyApp;


/**
 * 
 * 默认的SharedPreferences
 * 支持数据类型存储 
 * 位置 packagename.xml
 * @author xuxd
 */
public final class DefaultShared {

	private static final SharedPreferences spf = PreferenceManager
			.getDefaultSharedPreferences(MyApp.application.getBaseContext());

	public static void putBoolean(String key, boolean valeu) {
		spf.edit().putBoolean(key, valeu).commit();
	}

	public static void putFloat(String key, float valeu) {
		spf.edit().putFloat(key, valeu).commit();
	}

	public static void putInt(String key, int valeu) {
		spf.edit().putInt(key, valeu).commit();
	}

	public static void putLong(String key, long valeu) {
		spf.edit().putLong(key, valeu).commit();
	}

	public static void putString(String key, String valeu) {
		spf.edit().putString(key, valeu).commit();
	}
	
	/**
	 * SharedPreferences支持数据类型
	 * @param params
	 */
	public static void putMap(Map<String,?> params)
	{
		if(params == null || params.size() == 0)
			return;
		Editor edit = spf.edit();
		for(Map.Entry<String, ?> entry : params.entrySet())
		{
			if(entry.getValue() instanceof Boolean)
			{
				edit.putBoolean(entry.getKey(), (Boolean)entry.getValue());
			}else if(entry.getValue() instanceof Float)
			{
				edit.putFloat(entry.getKey(), (Float)entry.getValue());
			}else if(entry.getValue() instanceof Integer)
			{
				edit.putInt(entry.getKey(), (Integer)entry.getValue());
			}else if(entry.getValue() instanceof Long)
			{
				edit.putLong(entry.getKey(), (Long)entry.getValue());
			}else if(entry.getValue() instanceof String)
			{
				edit.putString(entry.getKey(), (String)entry.getValue());
			}
		}
		edit.commit();
	}

	public static boolean getBoolean(String key, boolean defValue) {
		return spf.getBoolean(key, defValue);
	}

	public static float getFloat(String key, float defValue) {

		return spf.getFloat(key, defValue);
	}

	public static int getInt(String key, int defValue) {
		return spf.getInt(key, defValue);
	}

	public static long getLong(String key, long defValue) {
		return spf.getLong(key, defValue);
	}

	public static String getString(String key, String defValue) {
		return spf.getString(key, defValue);
	}
	
	public static boolean isContainKey(String key){
		return spf.contains(key);
	}
}