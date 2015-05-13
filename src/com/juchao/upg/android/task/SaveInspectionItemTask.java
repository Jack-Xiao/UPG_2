package com.juchao.upg.android.task;

import android.content.Context;
import android.os.AsyncTask;

import com.juchao.upg.android.db.InspectionEquipmentDao;
import com.juchao.upg.android.entity.InspectionTaskEquipmentItem;

/**
 * GetInspectionDownloadListTask
 * 
 */
public class SaveInspectionItemTask extends AsyncTask<String, Void, Integer[]> {
	private static final String TAG = SaveInspectionItemTask.class.getSimpleName();
	private Context mContext;
	private TaskCallBack mCallBack;
	private InspectionTaskEquipmentItem inspectionItem;
	private long inspectionTaskId;
	public SaveInspectionItemTask(long inspectionTaskId,Context context,InspectionTaskEquipmentItem inspectionItem ,
			TaskCallBack callback) {
		this.mContext = context;
		this.mCallBack = callback;
		this.inspectionItem = inspectionItem;
		this.inspectionTaskId=inspectionTaskId;  
	}  

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}
	
	@Override
	protected Integer[] doInBackground(String... params) {
		
		
		InspectionEquipmentDao dao = new InspectionEquipmentDao(mContext);
		Integer[] result = dao.saveInspectionItem(inspectionTaskId,inspectionItem);
		dao.closeDB();
		
		return result;
	}
	
	@Override
	protected void onPostExecute(Integer[] result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(mCallBack != null) {
			mCallBack.callBackResult(result);
		}
	}
	
	public interface TaskCallBack {
		public void callBackResult(Integer[] result);
	}
}
