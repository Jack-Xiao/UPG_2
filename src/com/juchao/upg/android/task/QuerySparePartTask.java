package com.juchao.upg.android.task;

import java.util.List;

import com.juchao.upg.android.db.InspectionEquipmentDao;
import com.juchao.upg.android.db.InspectionTaskDao;
import com.juchao.upg.android.entity.InspectionTask;
import com.juchao.upg.android.entity.InspectionTaskEquipment;
import com.juchao.upg.android.entity.QuerySparePart;
import com.juchao.upg.android.entity.ResBaseSparePart;
import com.juchao.upg.android.entity.ResInspectionTaskEquipment;
import com.juchao.upg.android.entity.ResQuerySparePart;
import com.juchao.upg.android.net.NetAccessor;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;
import com.juchao.upg.android.util.ToastUtil;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class QuerySparePartTask extends AsyncTask<String [],Void,List<QuerySparePart>> {
	private static final String TAG=QuerySparePartTask.class.getSimpleName();
	private Context mContext;
	private TaskCallBack mCallBack;
	
	public QuerySparePartTask(Context context,TaskCallBack callback){
		this.mContext = context;
		this.mCallBack = callback;
	}
	@Override
	protected void onPreExecute(){
		super.onPreExecute();
		if(mCallBack !=null){
			this.mCallBack.preExecute();
		}
	}
	
	@Override
	protected List<QuerySparePart> doInBackground(String[]... params) {
		//long taskId = params[0];
		ResQuerySparePart result = null;
		String equipmentId=params[0][0].toString();
		String keyword=params[0][1].toString();

		String token = DefaultShared.getString(Constants.KEY_TOKEN, "");
		result = NetAccessor.querySparePart(mContext, token, equipmentId+"",keyword);
		if(result !=null && result.code == 0 && result.part !=null && result.part.size() > 0){
			return result.part;
			
		}else{ 
			return null;
		}
		
		/*if(result != null && result.code == 0 
				&& result.inspectionTaskEquipments != null && result.inspectionTaskEquipments.size() > 0) {
			InspectionEquipmentDao dao = new InspectionEquipmentDao(mContext);
			long insertNum = 0;
			for(InspectionTaskEquipment entity : result.inspectionTaskEquipments){
				insertNum = dao.insertEquipment(entity);
				Log.d(TAG, "insertNum : " +insertNum);
			}
			dao.closeDB();
			
			InspectionTaskDao mInspectionTaskDao = new InspectionTaskDao(mContext);
			InspectionTask mInspectionTask = mInspectionTaskDao.queryInspectionTaskById(taskId);
			if(mInspectionTask != null){
				mInspectionTask.isDownloaded = true;
				mInspectionTaskDao.updateInspectionTask(mInspectionTask);
				code = SUCCESS;
				
				Log.d(TAG, "downloadInspectionTast is success ");
			}
			mInspectionTaskDao.closeDB();
		}*/
	}
	
	@Override
	protected void onPostExecute(List <QuerySparePart> taskList){
		super.onPostExecute(taskList);
		if(mCallBack !=null){
			mCallBack.callBackResult(taskList);
		}
	}

	public static final int SUCCESS = 0;
	public static final int FAIL = -1;
	public static final int NO_RESULT=1;
	
	public interface TaskCallBack {
		public void preExecute();
		public void callBackResult(List<QuerySparePart> taskList);
	}
}
