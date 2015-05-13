package com.juchao.upg.android.task;

import android.content.Context;
import android.os.AsyncTask;

import com.juchao.upg.android.entity.AccountEquipment;
import com.juchao.upg.android.net.NetAccessor;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;

public class UploadAccountEquipmentTask extends AsyncTask<Long,Void,Integer[]>{
	private long taskId;
	private AccountEquipment item;
	private Context mContext;
	private TaskCallBack mCallBack;
	
	public UploadAccountEquipmentTask(long taskId, Context context,
			AccountEquipment entity, TaskCallBack taskCallBack) {
		this.mContext=context;
		this.mCallBack=taskCallBack;
		this.item=entity;
		this.taskId=taskId;
	}

	public interface TaskCallBack {
		public void preExecute();
		public void callBackResult(Integer[] result);
	}
	@Override
	protected void onPreExecute(){
		super.onPreExecute();
		if(mCallBack != null) {
			this.mCallBack.preExecute();
		}
	}

	@Override
	protected Integer[] doInBackground(Long... params) {
		Integer [] result=new Integer[2];
		String token = DefaultShared.getString(Constants.KEY_TOKEN, "");
		int commitResult = NetAccessor.commitAccountItemData(mContext,token,item);
		com.juchao.upg.android.db.AccountTaskDao dao=new com.juchao.upg.android.db.AccountTaskDao(mContext);
		if(commitResult == 0 || commitResult == 8 ){
			result[0]=commitResult;
			dao.updateAccountTaskItemUploadStatus(item);
		}else{
			result[0] = commitResult;
		}
		dao.closeDB();
		return result;
	}
	
	@Override
	protected void onPostExecute(Integer [] result){
		super.onPostExecute(result);
		if(mCallBack != null) {
			mCallBack.callBackResult(result);
		}  
	}
}
