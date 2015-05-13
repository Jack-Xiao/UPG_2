package com.juchao.upg.android.entity;

public class ResEquipmentSparePart1 {

	/**
	 * @hibernate.id
	 * generator-class="com.jc.common.diy.GenerateLongPrimaryKey"
	 */
	public Long id;
	
	/**
	 * 生产设备
	 * @hibernate.many-to-one column="equid"
	 */
	public Long equid;
	
	/**
	 * 备品备件
	 * @hibernate.many-to-one column="spapartid"
	 */
	public Long spapartid;
	
	//备品备件需要更改的数量。
	public int number;
	
	public String name;
}
