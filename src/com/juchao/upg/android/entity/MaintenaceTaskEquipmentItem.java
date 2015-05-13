package com.juchao.upg.android.entity;

import java.io.Serializable;

public class MaintenaceTaskEquipmentItem implements Serializable{
	
	private static final long serialVersionUID =121212134L;
	/**
	 * 维修项目id
	 */
	public Long id;
	
	public Long maintenaceTaskId;

	/**
	 * 任务名字
	 */
	public String taskName;
	
	/**
	 * 设备Id 
	 */
	public long equipmentId;
	
	/**
	 * 设备名称
	 */
	public String equipmentName;
	
	/**
	 * 设备编号
	 */
	public String equipmentNum;
	
	/**
	 * 设备对应的二维码 （设备条码）
	 */
	public String equipmentTDC;
	
	/**
	 * 项目资料 id
	 * 
	 */
	public Long equipmentSpotcheckId;
	/**
	 * 项目名称
	 */
	public String equipmentSpotcheckName;
	/**
	 * 处理结果(值只能为：OK 或 NG)
	 */
	public String result;
	/**
	 * 故障描述
	 */
	public String faultDescribe;
	
	/**
	 * 维修状态： 1:已维修 未确认    2: 已效果确认 
	 */
	public int state;
	/**
	 * 图片Id （多个用逗号分隔， 如  123,233,22)
	 */
	public String imageIds;
	/**
	 * 图片名称（多个用逗号分隔 ，  如 :  name1,name2,name3）
	 */
	public String imageNames;
	/**
	 * 耗时(秒)
	 */
	public int costTime;
	/**
	 * 开始时间
	 */
	public String startTime;
	/**
	 * 结束时间
	 */
	public String endTime;
	/**
	 * 检查方法（不需要保存数据库）
	 * length = "1000"
	 */
	public String checkMethod;
	/**
	 * 判断标准（不需保存数据库）
	 * length = "1000"
	 */
	public String judgementStandard;
	
	
	// 设备编号（二维码信息扫描的结果） @20141120
	public String managementOrgNum;
	
	
	/**
	 * 问题名称
	 */
	public String problemName; 
	
	/**
	 * 问题描述
	 */
	public String problemDescribe;
	/**
	 * 备件id
	 */
	public String sparePartIds;
	
	/**
	 * 问题Id
	 */
	public long problemId;
	
	
	//以下字段作为备用
	/**
	 * 负责人
	 */
	public Long headId;
	
	/**
	 * 维修人
	 */
	public Long checkerId;
	/**
	 * 维修终端编号 
	 */
	public String terminalNumber;
	
	/**
	 * 问题的完成状态
	 * 0- 未完成
	 * 1- 已完成，需要上传。
	 */
	public int uploadState;
	
	public String sparepartContent;
}
