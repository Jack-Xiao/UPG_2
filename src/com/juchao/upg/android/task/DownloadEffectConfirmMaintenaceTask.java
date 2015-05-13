package com.juchao.upg.android.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.juchao.upg.android.db.InspectionTaskDao;
import com.juchao.upg.android.db.MaintenaceEquipmentDao;
import com.juchao.upg.android.db.MaintenaceTaskDao;
import com.juchao.upg.android.entity.InspectionTask;
import com.juchao.upg.android.entity.MaintenaceTask;
import com.juchao.upg.android.entity.MaintenaceTaskEquipment;
import com.juchao.upg.android.entity.ResMaintenaceTaskEquipment;
import com.juchao.upg.android.net.NetAccessor;
import com.juchao.upg.android.util.ConstantUtil;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;

public class DownloadEffectConfirmMaintenaceTask extends AsyncTask<Long,Void,Integer> {
	private static final String TAG = downloadInspectionTask.class.getSimpleName();
	private Context mContext;
	private TaskCallBack mCallBack;
	
	public DownloadEffectConfirmMaintenaceTask(Context context) {
		this(context, null);
	}
	
	public DownloadEffectConfirmMaintenaceTask(Context context, TaskCallBack callback) {
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
		
		ResMaintenaceTaskEquipment result = null;
		String token = DefaultShared.getString(Constants.KEY_TOKEN, "");
		result=NetAccessor.downloadMaintenaceEffectConfirmTask(mContext, token, taskId);
		if(result != null && result.code == 0 
				&& result.maintenaceTaskEquipments != null && result.maintenaceTaskEquipments.size() > 0) {
			MaintenaceEquipmentDao dao = new MaintenaceEquipmentDao(mContext);
			long insertNum = 0;
			for(MaintenaceTaskEquipment entity : result.maintenaceTaskEquipments ){
				insertNum = dao.insertEquipment(entity,taskId,ConstantUtil.WxEffectConfirmTask.WxEffectConfirmTaskState);
				Log.d(TAG, "insertNum : " +insertNum);
			}
			dao.closeDB();
			 
			MaintenaceTaskDao mInspectionTaskDao = new MaintenaceTaskDao(mContext);
			MaintenaceTask mInspectionTask = mInspectionTaskDao.queryMaintenaceTaskByid(taskId);
			if(mInspectionTask != null){
				mInspectionTask.isDownloaded = true;
				mInspectionTask.state=4;
				mInspectionTaskDao.updateMaintenaceTask(mInspectionTask);
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
