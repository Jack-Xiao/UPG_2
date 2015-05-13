package com.juchao.upg.android.task;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.juchao.upg.android.db.MaintenaceTaskDao;
import com.juchao.upg.android.entity.MaintenaceTask;
import com.juchao.upg.android.entity.ResMaintenaceTask;
import com.juchao.upg.android.util.ConstantUtil;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;

public class GetMaintenaceTaskList extends AsyncTask<String,Void,List<MaintenaceTask>> {
	private static final String TAG=GetMaintenaceTaskList.class.getSimpleName();
	private Context mContext;
	private ProgressDialog mDialog;
	private TaskCallBack mCallBack;
	
	public GetMaintenaceTaskList(Context context){
		this(context,null);
	}
	public GetMaintenaceTaskList(Context context, TaskCallBack callback){
		this.mContext=context;
		this.mCallBack=callback;
	}   
	protected void onPreExecute(){
		super.onPreExecute();
		if(mCallBack !=null){
			this.mCallBack.preExecute();
		}
	}
	@Override
	protected void onPostExecute(List<MaintenaceTask> result){
		super.onPostExecute(result);
		if(mCallBack !=null){
			mCallBack.callBackResult(result);
		}
	}
	
	public interface TaskCallBack {
		public void preExecute();
		public void callBackResult(List<MaintenaceTask> taskList);
	}

	@Override
	protected List<MaintenaceTask> doInBackground(String... params) {
		 int pageSize=Integer.parseInt(params[0]);
		 int pageNum=Integer.parseInt(params[1]);
		 
		 ArrayList <Long> maintenaceIdlist=new ArrayList<Long>();
		 
		 ResMaintenaceTask result=null;
		 StringBuffer sb=new StringBuffer();
		 String token=DefaultShared.getString(Constants.KEY_TOKEN, "");
		 MaintenaceTaskDao dao=new MaintenaceTaskDao(mContext);
		 if(com.juchao.upg.android.net.NetIOUtils.isNetworkOk(mContext) && pageNum == 1){
			 result=com.juchao.upg.android.net.NetAccessor.getMaintenaceTaskList(mContext,token);
			 if(result !=null && result.code == 0 && result.tasks !=null 
					 && result.tasks.size() > 0){
				      
				 //倒序插入
				 for(int i = result.tasks.size() - 1;i>=0;i--){
					 dao.insertMaintenaceTask(result.tasks.get(i),ConstantUtil.WxEffectConfirmTask.WxDownLoadTaskState);
					 maintenaceIdlist.add(result.tasks.get(i).id);
				 }
				 dao.removeCancelTask(maintenaceIdlist,0);
			 }
		 }
		 List<MaintenaceTask> list=dao.queryMaintenaceTaskList(pageSize,pageNum,0,ConstantUtil.WxEffectConfirmTask.WxDownLoadTaskState);
		 dao.closeDB();
		 return list;
	}
}
