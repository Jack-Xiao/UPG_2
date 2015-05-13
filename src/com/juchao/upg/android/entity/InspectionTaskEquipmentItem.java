package com.juchao.upg.android.entity;

import java.io.Serializable;


/**
 *  点检任务设备项目
 * @author xuxd
 *
 */
public class InspectionTaskEquipmentItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2248685278414270876L;
	
	/**
	 * 点检项目id
	 */
	public Long id;
	/**
	 * 点检设备id
	 */
	public Long inspectionTaskEquipmentId;
	
	/**
	 * 项目资料id
	 */
	public Long equipmentSpotcheckId;

	/**
	 * 项目名称
	 */
	public String equipmentSpotcheckName;
	
	/**
	 * 处理结果（值只能为：OK 或  NG）
	 */
	public String result;
	
	/**
	 * 故障描述（问题描述 ， result 为 OK则不填，为NG则必填）
	 */
	public String faultDescribe;
	
	/**
	 * 点检状态    1：未点检    ； 2：已点检，未提交  ；  3：完成
	 */
	public int state;
	
	/**
	 * 图片Id (多个用逗号分隔，如： 123,333,22)
	 */
	public String imageIds;
	
	/**
	 * 图片名称 (多个用逗号分隔，如： name1,name2,name3)
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
	 * 检查方法 （不需保存数据库）
	 * length="1000"
	 */
	public String checkMethod;
	/**
	 * 判断标准（不需保存数据库）
	 * length="1000"
	 */
	public String judgementStandard;
	
	/**
	 * 所属的设备ID（不需保存数据库）
	 */
	public long equipmentId;
	
	/**
	 * 预计耗时（秒）
	 */
	public long costPlanTime;
	
	
	//以下字段暂时没用到
	/**
	 * 负责人
	 */
	public Long headId;	
		
	/**
	 * 点检人
	 */
	public Long checkerId;
	
	/**
	 * 采集终端编号
	 */
	public String terminalNumber;
	
	/**
	 * 任务类型   0：定常     1：自定义    2：特种设备
	 */
	public int style;
}
