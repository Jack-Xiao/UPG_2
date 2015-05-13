package com.juchao.upg.android.entity;

import java.util.List;

public class MaintenaceTaskEquipment {

	public Long id;  //设备Id
	public Long maintenaceTaskId; //维修任务Id
	public Long equipmentId; //设备资料Id
	public String equipmentName; //设备名称
	public String type; //类型
	public List<MaintenaceTaskEquipmentProblem> maintenaceTaskEquipmentProblems;
	public int maintenaceState; 
	
	public int timeList; //维修所用时间   单位分钟   
	
	
	
}
   