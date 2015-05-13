package com.juchao.upg.android.entity;

/**
 * 点检任务列表
 * @author 
 *
 */
public class InspectionTaskList {

	public long id;  //type：1 点检任务ID ， 2.点检设备ID
	public String name;  //type：1任务名称 ，2.设备名称
	public int type;  //type : 1任务 ，2.设备
	
	
	//当type==1 时
	public int taskIndex;
	public int deviceNum;
	public int itemNum;
	
	//当type==2 时
	public long equipmentId;
	public String taskName; //点检任务的名称
	
	public long taskId; //点检任务Id.
	
	//任务类型
	public int style;
	
	//
	public int intervalType;
}
