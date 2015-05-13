package com.juchao.upg.android.entity;

import java.util.Date;

public class MaintenaceTaskEquipmentProblem {
	
	public long id; // 待维修问题 id
	public long maintenaceTaskEquipmentId; //维修设备 id.
	public String maintenaceTaskEquipmentName;
	public Date startTime; //维修开始时间
	public Date endTime; //维修结束时间
	public Date checkTime; //维修确认时间
	public String state; //已确认  /  已完成 /  进行中 /   未开始
	public String problemDesc; //维修说明
	public String problemName; //维修问题名称
	
	//public String pro_Desc; //故障描述
	public String problemSuggestion; //故障意见
	public String problemMeasure; //故障对策
    //故障图片 多个以,分隔开
    public String problemPicId;
    
    public String faultSchemeType; // 对策类型
}
  