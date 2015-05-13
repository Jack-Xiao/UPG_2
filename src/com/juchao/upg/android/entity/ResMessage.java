package com.juchao.upg.android.entity;

import java.util.List;

public class ResMessage  extends BaseResult{

	
	public List<Message> data;
	
	public static class Message{
		public long id;
		public String msgType;
		public String message;
		public String createTime;
	}
}
