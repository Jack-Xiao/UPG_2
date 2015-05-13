package com.juchao.upg.android.entity;


public class ResAppUpdate extends BaseResult{

	
	/**
	 * 
	 */
	public int localVersionCode;
	
	public int versionCode; //最新版本号
	public String versionName;  //版本名
	public String url;  //最新版本下载路径
	public String log;  //更新日志
}
