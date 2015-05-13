package com.juchao.upg.android.task;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.juchao.upg.android.db.InspectionTaskDao;
import com.juchao.upg.android.entity.InspectionTask;
import com.juchao.upg.android.entity.ResInspectionTask;
import com.juchao.upg.android.net.NetAccessor;
import com.juchao.upg.android.net.NetIOUtils;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;

/**
 * GetInspectionDownloadListTask
 * 
 */
public class GetInspectionTaskList extends AsyncTask<String, Void, List<InspectionTask>> {
	private static final String TAG = GetInspectionTaskList.class.getSimpleName();
	private Context mContext;
	private ProgressDialog mDialog;
	private TaskCallBack mCallBack;
	
	public GetInspectionTaskList(Context context) {
		this(context, null);
	}
	
	public GetInspectionTaskList(Context context, TaskCallBack callback) {
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
	protected List<InspectionTask> doInBackground(String... params) {
		// TODO Auto-generated method stub
		
		int pageSize = Integer.parseInt(params[0]);
		int pageNum = Integer.parseInt(params[1]);
		
		//List<Long>taskIds = new ArrayList<Long>(); 
		List<String>taskIds = new ArrayList<String>();
		ResInspectionTask result = null;
		String token = DefaultShared.getString(Constants.KEY_TOKEN, "");
		
		InspectionTaskDao dao = new InspectionTaskDao(mContext);
		if(NetIOUtils.isNetworkOk(mContext) && pageNum == 1){
			result = NetAccessor.getInspectionTaskList(mContext, token);
			if(result != null && result.code == 0 
					&& result.tasks != null && result.tasks.size() > 0) {
				//倒序插入
				for(int i = result.tasks.size() -1 ; i >=0 ; i--){
					taskIds.add(result.tasks.get(i).id + "_" + result.tasks.get(i).status );
					dao.insertInspectionTask(result.tasks.get(i));
				}
				
				//删除不在下载任务列表中的本地任务
				dao.removeCancelInspectionTask(taskIds);
				
				
//				for(InspectionTask entity : result.tasks){
//					dao.insertInspectionTask(entity);
//				}
			}
		}
		
		List<InspectionTask> list = dao.queryInspectionTaskList(pageSize, pageNum , 0);
		
		dao.closeDB();
		return list;
	}
	
	@Override
	protected void onPostExecute(List<InspectionTask> result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(mCallBack != null) {
			mCallBack.callBackResult(result);
		}
	}
	
	public interface TaskCallBack {
		public void preExecute();
		public void callBackResult(List<InspectionTask> taskList);
	}
}
