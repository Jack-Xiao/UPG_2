package com.juchao.upg.android.task;

import android.content.Context;
import android.os.AsyncTask;

import com.juchao.upg.android.db.MaintenaceEquipmentDao;
import com.juchao.upg.android.db.MaintenaceTaskDao;
import com.juchao.upg.android.entity.MaintenaceTask;
import com.juchao.upg.android.entity.MaintenaceTaskEquipment;
import com.juchao.upg.android.entity.MaintenaceTaskEquipmentProblem;
import com.juchao.upg.android.entity.ResMaintenaceTaskEquipment;
import com.juchao.upg.android.net.NetAccessor;
import com.juchao.upg.android.util.ConstantUtil;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;
import com.juchao.upg.android.util.Log;
  
/**
 * 下载维修任务
 * @author xiao-jiang
 *  
 */
public class DownloadMaintenaceTask extends AsyncTask<Long, Void, Integer>{
	private static final String TAG=DownloadMaintenaceTask.class.getSimpleName();
	private Context mContext;
	private TaskCallBack mCallBack;
	
	public DownloadMaintenaceTask(Context context){
		this(context,null);
	}
	
	public DownloadMaintenaceTask(Context context, TaskCallBack callBack) {
		this.mContext=context;
		this.mCallBack=callBack;
	}
	
	protected void onPreExecute(){
		super.onPreExecute();
		if(mCallBack !=null){
			this.mCallBack.preExecute();
		}
	}
	
	@Override
	protected void onPostExecute(Integer result){
		super.onPostExecute(result);
		if(mCallBack !=null){
			mCallBack.callBackResult(result);
		}
	}

	public static final int SUCCESS = 0;
	public static final int FAIL = -1;
	public interface TaskCallBack {
		public void preExecute();
		public void callBackResult(int code);
	}
	@Override
	protected Integer doInBackground(Long... params) {
		long taskId=params[0];
		int code = FAIL;
		
		ResMaintenaceTaskEquipment result = null;
		String token=DefaultShared.getString(Constants.KEY_TOKEN, "");
		
		result=NetAccessor.downloadMaintenaceTask(mContext,token,taskId);
		if(result !=null && result.code == 0 && result.maintenaceTaskEquipments !=null &&
				result.maintenaceTaskEquipments.size() >0 ){
			MaintenaceEquipmentDao dao=new MaintenaceEquipmentDao(mContext);  
			long insertNum = 0;
			for(MaintenaceTaskEquipment entity:result.maintenaceTaskEquipments){
				//--++ @20141104
				//insertNum = dao.insertEquipment(entity);
				insertNum = dao.insertEquipment(entity,taskId,
						ConstantUtil.WxEffectConfirmTask.WxDownLoadTaskState);
				Log.d(TAG, "insertNum : + " +insertNum);

                for(MaintenaceTaskEquipmentProblem problem : entity.maintenaceTaskEquipmentProblems){
                        String strId=problem.problemPicId;
                    if(String.valueOf(strId) == "null" && strId !="") continue;
                        String [] ids= strId.split(",");
                    for (String pid : ids){
                    	if(pid =="") continue;
                        NetAccessor.DownloadPic(mContext, token, pid);
                    }
                }
			}
			dao.closeDB();
			MaintenaceTaskDao mMaintenaceTaskDao= new MaintenaceTaskDao(mContext);
			MaintenaceTask mMaintenaceTask=mMaintenaceTaskDao.queryMaintenaceTaskByid(taskId);
			if(mMaintenaceTask !=null){
				mMaintenaceTask.isDownloaded=true;
				mMaintenaceTaskDao.updateMaintenaceTask(mMaintenaceTask);
				code=SUCCESS;
				Log.d(TAG, "downloadMaintenaceTask is success");
			}
			mMaintenaceTaskDao.closeDB();
		}
		return code;
	}
}
