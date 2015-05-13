package com.juchao.upg.android.task;

import com.juchao.upg.android.db.AccountTaskDao;
import com.juchao.upg.android.entity.AccountEquipment;
import com.juchao.upg.android.entity.ResAccountTaskEquipment;
import com.juchao.upg.android.net.NetAccessor;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;
import com.juchao.upg.android.util.Log;

import android.content.Context;
import android.os.AsyncTask;

public class DownloadAccountTask extends AsyncTask<Long, Void, Integer> {
	private static final String TAG=DownloadAccountTask.class.getSimpleName();
	private Context mContext;
	private TaskCallBack mCallBack;
	
	public static final int SUCCESS = 0;
	public static final int FAIL = -1;
	public interface TaskCallBack {
		public void preExecute();
		public void callBackResult(int code);
	}
	
	
	public DownloadAccountTask(Context context) {
		this(context, null);
	}
	
	public DownloadAccountTask(Context context, TaskCallBack callback) {
		this.mContext = context;
		this.mCallBack = callback;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		if(mCallBack != null) {
			this.mCallBack.preExecute();
		}
	}
	
	@Override
	protected Integer doInBackground(Long... params) {
		
		long taskId = params[0];
		int code = FAIL;
		
		ResAccountTaskEquipment result = null;
		String token = DefaultShared.getString(Constants.KEY_TOKEN, "");
		result = NetAccessor.downloadAccountTask(mContext, token, taskId);
		if(result != null && result.code == 0 
				&& result.equAccount != null && result.equAccount.size() > 0) {
			AccountTaskDao dao = new AccountTaskDao(mContext);
			long insertNum = 0;
			for(AccountEquipment entity : result.equAccount){
				insertNum = dao.insertEquipment(entity);
				Log.d(TAG, "insertNum : " +insertNum);
			}
			dao.updateAccountTask(taskId);
			dao.closeDB();
			code=SUCCESS;
		}
		return code;
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(mCallBack != null) {
			mCallBack.callBackResult(result);
		}
	}
	
}
