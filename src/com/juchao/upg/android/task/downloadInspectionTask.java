package com.juchao.upg.android.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.juchao.upg.android.db.InspectionEquipmentDao;
import com.juchao.upg.android.db.InspectionTaskDao;
import com.juchao.upg.android.entity.InspectionTask;
import com.juchao.upg.android.entity.InspectionTaskEquipment;
import com.juchao.upg.android.entity.ResInspectionTaskEquipment;
import com.juchao.upg.android.net.NetAccessor;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;

/**
 * 下载点检任务
 * 
 */
public class downloadInspectionTask extends AsyncTask<Long, Void, Integer> {
	private static final String TAG = downloadInspectionTask.class.getSimpleName();
	private Context mContext;
	private TaskCallBack mCallBack;
	private int style;
	
	public downloadInspectionTask(Context context) {
		this(context,0, null);
	}
	
	public downloadInspectionTask(Context context,int style, TaskCallBack callback) {
		this.mContext = context;
		this.mCallBack = callback;
		this.style=style;
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
		
		ResInspectionTaskEquipment result = null;
		String token = DefaultShared.getString(Constants.KEY_TOKEN, "");
		result = NetAccessor.downloadInspectionTask(mContext, token, taskId,style);
		if(result != null && result.code == 0 
				&& result.inspectionTaskEquipments != null && result.inspectionTaskEquipments.size() > 0) {
			InspectionEquipmentDao dao = new InspectionEquipmentDao(mContext);
			long insertNum = 0;
			for(InspectionTaskEquipment entity : result.inspectionTaskEquipments){
				entity.style=style;
				insertNum=dao.insertEquipment(entity, taskId);
				Log.d(TAG, "insertNum : " +insertNum);
			}
			dao.closeDB();
			
			InspectionTaskDao mInspectionTaskDao = new InspectionTaskDao(mContext);
			InspectionTask mInspectionTask = mInspectionTaskDao.queryInspectionTaskById(taskId,style);
			if(mInspectionTask != null){
				mInspectionTask.isDownloaded = true;
				mInspectionTaskDao.updateInspectionTask(mInspectionTask);
				code = SUCCESS;
				
				Log.d(TAG, "downloadInspectionTast is success ");
			}
			mInspectionTaskDao.closeDB();
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
	
	public static final int SUCCESS = 0;
	public static final int FAIL = -1;
	public interface TaskCallBack {
		public void preExecute();
		public void callBackResult(int code);
	}
}
