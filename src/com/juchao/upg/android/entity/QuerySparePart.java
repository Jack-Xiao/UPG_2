package com.juchao.upg.android.entity;

/**
 * 由后台传入的备品备件信息
 * @author xiao-jiang
 *
 */
public class QuerySparePart {

	private long id;
	//名称
	public String name;
	/*
	 * 备品备件编号
	 */
	public String modelNo;
	/*
	 * 备品备件数量
	 */
	public int currentStore;
	
}
