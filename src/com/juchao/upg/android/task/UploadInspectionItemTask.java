package com.juchao.upg.android.task;

import android.content.Context;
import android.os.AsyncTask;

import com.juchao.upg.android.db.InspectionEquipmentDao;
import com.juchao.upg.android.entity.InspectionTaskEquipmentItem;
import com.juchao.upg.android.net.NetAccessor;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;

/**
 * 下载点检任务
 * 
 */
public class UploadInspectionItemTask extends AsyncTask<Long, Void, Integer[]> {
	private static final String TAG = UploadInspectionItemTask.class.getSimpleName();
	private Context mContext;
	private TaskCallBack mCallBack;
	private InspectionTaskEquipmentItem inspectionItem;
	private long inspectionTaskId;
	
	public UploadInspectionItemTask(long inspectionTaskId,Context context,InspectionTaskEquipmentItem inspectionItem , 
			TaskCallBack callback ) {
		this.mContext = context;
		this.mCallBack = callback;
		this.inspectionItem = inspectionItem;
		this.inspectionTaskId=inspectionTaskId;
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
	protected Integer[] doInBackground(Long... params) {
		Integer[] result = new Integer[2]; 
		String token = DefaultShared.getString(Constants.KEY_TOKEN, "");
		int commitResult = NetAccessor.commitInspectionItemData(mContext, token, inspectionItem);
		InspectionEquipmentDao dao = new InspectionEquipmentDao(mContext);
		if(commitResult >=0){
			result[0] = commitResult;
			//更新点检项目的状态（完成）
			if( commitResult !=9)
			dao.updateInspectionItem(inspectionItem.id,inspectionItem.style);
		}else{
			result[0] = -1;
		}
		result[1] = dao.commitCheckAfter(inspectionTaskId,inspectionItem.inspectionTaskEquipmentId,inspectionItem.style);
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
		public void preExecute();
		public void callBackResult(Integer[] result);
	}
}
