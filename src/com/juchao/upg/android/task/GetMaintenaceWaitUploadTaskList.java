package com.juchao.upg.android.task;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.juchao.upg.android.db.MaintenaceTaskDao;
import com.juchao.upg.android.entity.MaintenaceTask;
import com.juchao.upg.android.task.GetMaintenaceWaitUploadTaskList.TaskCallBack;
import com.juchao.upg.android.ui.WxTaskUploadActivity;
import com.juchao.upg.android.util.ConstantUtil;

/**
 * 获取等待上传的维修任务列表
 * @author xiao-jiang
 *
 */
public class GetMaintenaceWaitUploadTaskList extends AsyncTask<String, Void, List<MaintenaceTask>>{
	private static final String TAG= GetWaitUploadTaskList.class.getSimpleName();
	private Context mContext;
	private ProgressDialog mDialog;
	private TaskCallBack mCallBack;
	
	public GetMaintenaceWaitUploadTaskList(Context context){
		this(context,null);
	}
	public GetMaintenaceWaitUploadTaskList(Context context, TaskCallBack callback){
		this.mContext=context;
		this.mCallBack=callback;
	}
	
	@Override
	protected void onPreExecute(){
		super.onPreExecute();
		if(mCallBack !=null){
			this.mCallBack.preExecute();
		}
	}
	
	@Override
	protected List<MaintenaceTask> doInBackground(String... params) {
		int pageSize = Integer.parseInt(params[0]);
		int pageNum = Integer.parseInt(params[1]);
		
		MaintenaceTaskDao dao= new MaintenaceTaskDao(mContext);
		List<MaintenaceTask> list = dao.queryMaintenaceTaskList(pageSize, pageNum, 5,
				ConstantUtil.WxEffectConfirmTask.WxDownLoadTaskState);
		
		/**List<MaintenaceTask> resultList= new ArrayList<MaintenaceTask>();
		for(int i =0 ;i < list.size(); i++){
			int waitUploadNum = dao.queryMaintenaceItemNum(list.get(i).id);
			if(waitUploadNum > 0){
				list.get(i).waitUploadNum = waitUploadNum;
				resultList.add(list.get(i));
			}
		}*/
		dao.closeDB();
		return list;
	}
	
	@Override 
	protected void onPostExecute(List<MaintenaceTask> result){
		super.onPostExecute(result);
		if(mCallBack !=null){
			mCallBack.callBackResult(result);
		}
	}
	
	public interface TaskCallBack{
		public void preExecute();
		public void callBackResult(List<MaintenaceTask> taskList);
	}
}
