package com.juchao.upg.android.task;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.juchao.upg.android.db.InspectionTaskDao;
import com.juchao.upg.android.entity.InspectionTask;

/**
 * 获取等待上传的点检任务列表
 * 
 */
public class GetWaitUploadTaskList extends AsyncTask<String, Void, List<InspectionTask>> {
	private static final String TAG = GetWaitUploadTaskList.class.getSimpleName();
	private Context mContext;
	private ProgressDialog mDialog;
	private TaskCallBack mCallBack;
	
	public GetWaitUploadTaskList(Context context) {
		this(context, null);
	}
	
	public GetWaitUploadTaskList(Context context, TaskCallBack callback) {
		this.mContext = context;
		this.mCallBack = callback;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(mCallBack != null) {
			this.mCallBack.preExecute();
		}
	}
	
	@Override
	protected List<InspectionTask> doInBackground(String... params) {
		int pageSize = Integer.parseInt(params[0]);
		int pageNum = Integer.parseInt(params[1]);
		
		InspectionTaskDao dao = new InspectionTaskDao(mContext);
		List<InspectionTask> list = dao.queryInspectionTaskList(pageSize, pageNum , 1);
		List<InspectionTask> resultList = new ArrayList<InspectionTask>();
		for(int i = 0 ; i < list.size() ; i++){
			int waitUploadNum = dao.queryInspectionItemNum(list.get(i).id,list.get(i).status);
			if(waitUploadNum > 0){ 
				list.get(i).waitUploadNum = waitUploadNum;
				resultList.add(list.get(i));
			}
		}
		dao.closeDB();
		return resultList;
	}
	
	@Override
	protected void onPostExecute(List<InspectionTask> result) {
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
