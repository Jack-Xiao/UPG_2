package com.juchao.upg.android.entity;

import java.util.List;

/**
 * 点检任务设备
 * @author 
 */
public class InspectionTaskEquipment {

	/**
	 * 点检设备id
	 */
	public Long id;
	/**
	 * 定检任务
	 */
	public Long inspectionTaskId;
	/**
	 * 设备资料id
	 */
	public Long equipmentId;
	/**
	 * 设备名称
	 */
	public String equipmentName;
	/**
	 * 类别 日常点检  / 年检
	 */	
	public String type;	
	/**
	 * 点检状态：
	 * 
	 * 0 : 未完成 ， 1：已完成
	 */	
	public int inspectionState;	
	/**
	 * 点检持续时间
	 */	
	public int timeList;//单位 分钟

	/**
	 * 点检项目列表
	 */
	public List<InspectionTaskEquipmentItem> inspectionTaskEquipmentItems;
	
	/**
	 * 该点检设备下的点检项目数
	 */
//	public int itemSize;
	
	/**
	 * 该设备属于任务的style 
	 */
	public int style;
}
