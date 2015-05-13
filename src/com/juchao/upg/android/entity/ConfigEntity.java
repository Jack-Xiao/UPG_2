package com.juchao.upg.android.entity;

import java.util.ArrayList;

public class ConfigEntity {

	public int id;
	public String text;
	public boolean showRed;
	
	
	public ConfigEntity(int id, String text, boolean showRed) {
		super();
		this.id = id;
		this.text = text;
		this.showRed = showRed;
	}
	
	public static ArrayList<ConfigEntity> getConfigList(){
		ArrayList<ConfigEntity> list =new ArrayList<ConfigEntity>();
		list.add(new ConfigEntity(0,"服务器地址" , false));
		list.add(new ConfigEntity(1,"消息提醒" , false));
//		list.add(new ConfigEntity(2,"设备注册" , false));
		list.add(new ConfigEntity(3,"基础数据更新" , false));
		list.add(new ConfigEntity(4,"软件更新" , false));
		list.add(new ConfigEntity(5,"关于" , false));
		
		return list; 
	}
	
}
