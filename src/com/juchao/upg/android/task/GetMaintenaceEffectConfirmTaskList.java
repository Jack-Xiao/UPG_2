package com.juchao.upg.android.task;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import com.juchao.upg.android.db.MaintenaceTaskDao;
import com.juchao.upg.android.entity.MaintenaceTask;
import com.juchao.upg.android.entity.ResMaintenaceTask;
import com.juchao.upg.android.util.ConstantUtil;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;

public class GetMaintenaceEffectConfirmTaskList extends AsyncTask<String,Void,List<MaintenaceTask>> {

	private static final String TAG=GetMaintenaceEffectConfirmTaskList.class.getSimpleName();
	private Context mContext;
	private ProgressDialog mDialog;
	private TaskCallBack mCallBack;
	
	public GetMaintenaceEffectConfirmTaskList(Context context){
		this(context,null);
	}
	public GetMaintenaceEffectConfirmTaskList(Context context, TaskCallBack callback){
		this.mContext=context;
		this.mCallBack=callback;
	}
	
	protected void onPostExecute(List<MaintenaceTask> result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(mCallBack != null) {
			mCallBack.callBackResult(result);
		}
	}
	
	protected void onPreExecute(){
		super.onPreExecute();
		if(mCallBack != null) {
			this.mCallBack.preExecute();
		}
	}
	
	@Override
	protected List<MaintenaceTask> doInBackground(String... params) {
		int pageSize= Integer.parseInt(params[0]);
		int pageNum=Integer.parseInt(params[1]);
		
		ArrayList <Long> maintenaceIdlist=new ArrayList<Long>();
		ResMaintenaceTask result=null;
		 String token=DefaultShared.getString(Constants.KEY_TOKEN, "");
		 MaintenaceTaskDao dao=new MaintenaceTaskDao(mContext);
		 if(com.juchao.upg.android.net.NetIOUtils.isNetworkOk(mContext) && pageNum == 1){
			 result=com.juchao.upg.android.net.NetAccessor.getMaintenaceEffectConfirmTaskList(mContext,token);
			 if(result !=null && result.code == 0 && result.tasks !=null 
					 && result.tasks.size() > 0){
				 //倒序插入
				 for(int i = result.tasks.size() - 1;i>=0;i--){
					 maintenaceIdlist.add(result.tasks.get(i).id);
					 dao.insertMaintenaceTask(result.tasks.get(i),
							 ConstantUtil.WxEffectConfirmTask.WxEffectConfirmTaskState);
				 }
				 dao.removeCancelTask(maintenaceIdlist,1);
			 }
		 }
		 List<MaintenaceTask> list=dao.queryMaintenaceTaskList(pageSize,pageNum,
				 Constants.WX_HAS_COMMIT_TASK_STATE_SET,
				 ConstantUtil.WxEffectConfirmTask.WxEffectConfirmTaskState);
		 dao.closeDB();
		 return list;
	}
	
	public interface TaskCallBack {
		public void preExecute();
		public void callBackResult(List<MaintenaceTask> taskList);
	}
}
