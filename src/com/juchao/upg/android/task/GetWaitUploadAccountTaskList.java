package com.juchao.upg.android.task;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.juchao.upg.android.db.InspectionTaskDao;
import com.juchao.upg.android.entity.AccountTask;
import com.juchao.upg.android.entity.InspectionTask;
import com.juchao.upg.android.db.AccountTaskDao;

/**
 * 获取等待上传的盘点  任务列表
 * 
 */
public class GetWaitUploadAccountTaskList extends AsyncTask<String, Void, List<AccountTask>> {
	private static final String TAG = GetWaitUploadAccountTaskList.class.getSimpleName();
	private Context mContext;
	private ProgressDialog mDialog;
	private TaskCallBack mCallBack;
	
	public GetWaitUploadAccountTaskList(Context context) {
		this(context, null);
	}
	
	public GetWaitUploadAccountTaskList(Context context, TaskCallBack callback) {
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
	protected List<AccountTask> doInBackground(String... params) {
		int pageSize = Integer.parseInt(params[0]);
		int pageNum = Integer.parseInt(params[1]);
		
		AccountTaskDao dao = new AccountTaskDao(mContext);
		List<AccountTask> list = dao.queryAccountTaskList(pageSize, pageNum , 1);
		List<AccountTask> resultList = new ArrayList<AccountTask>();
		for(int i = 0 ; i < list.size(); i++){
			int waitUploadNum = dao.queryAccountWaitUploadItemNum(list.get(i).id);
			if(waitUploadNum > 0){
				list.get(i).waitUploadNum = waitUploadNum;
				resultList.add(list.get(i));
			}
		}
		dao.closeDB();
		return resultList;
	}
	
	@Override
	protected void onPostExecute(List<AccountTask> result) {
		super.onPostExecute(result);
		if(mCallBack != null) {
			mCallBack.callBackResult(result);
		}
	}
	
	public interface TaskCallBack {
		public void preExecute();
		public void callBackResult(List<AccountTask> taskList);
	}
}
