package com.juchao.upg.android.entity;


public class ResLogin extends BaseResult{

	
	/**
	 * 
	 */
	
	public String token;
	public User user;
	
	public static class User{
		public long userId; //用户ID
		public String name; //用户姓名
		public String jobNumber; //职工编号
		public String sex; //性别
	}
}
