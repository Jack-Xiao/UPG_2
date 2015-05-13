package com.juchao.upg.android.entity;

public class BaseOperationNotice {
	
	//需知Id
	public long id;
	//需知内容
	public String notice;
	//设备Id
	public long equipmentId;
	// 存放点检类型值  1、年度;2、 季度;3、 月度;4、周常;5、 日常;6、维修;7、自定义
	public int type;
}