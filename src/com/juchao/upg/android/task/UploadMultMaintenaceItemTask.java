package com.juchao.upg.android.task;

import java.util.List;

import com.juchao.upg.android.db.MaintenaceTaskDao;
import com.juchao.upg.android.entity.MaintenaceTask;
import com.juchao.upg.android.entity.MaintenaceTaskEquipmentItem;
import com.juchao.upg.android.net.NetAccessor;
import com.juchao.upg.android.task.downloadInspectionTask.TaskCallBack;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;

import android.content.Context;
import android.os.AsyncTask;

/**
 * 上传维修任务
 * 
 * @author xiao-jiang
 * 
 */
public class UploadMultMaintenaceItemTask extends
		AsyncTask<Long, Void, Integer[]> {

	private static final String TAG = UploadMultMaintenaceItemTask.class
			.getSimpleName();
	private Context mContext;
	private TaskCallBack mCallBack;
	private List<MaintenaceTask> taskList;

	public UploadMultMaintenaceItemTask(Context context,
			List<MaintenaceTask> taskList, TaskCallBack callback) {
		this.mContext = context;
		this.mCallBack = callback;
		this.taskList = taskList;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (mCallBack != null) {
			this.mCallBack.preExecute();
		}
	}

	@Override
	protected Integer[] doInBackground(Long... params) {
		Integer [] result = new Integer[2];
		MaintenaceTaskDao dao=new MaintenaceTaskDao(mContext);
		String token=DefaultShared.getString(Constants.KEY_TOKEN,"");
		int totalFailNum = 0;
		int totalSuccessNum = 0;
		
		for(MaintenaceTask task: taskList){
			//List<MaintenaceTaskEquipmentItem> itemList=dao.queryMaintenaceItemList(task.id);
			List<MaintenaceTaskEquipmentItem> itemList=dao.queryMaintenaceItemList(task.id,null);
			int commitSuccessNum = 0;
			
			for(MaintenaceTaskEquipmentItem item: itemList){
				int commitResult=NetAccessor.commitMaintenaceItemData(mContext,token,item);	
					if(commitResult == 0 || commitResult == 8){
						commitSuccessNum += 1;
						totalSuccessNum +=1;
						dao.updateMaintenaceItem(item.id,null);
					}else{
						totalFailNum ++;
					}
			}
			if(commitSuccessNum == itemList.size()){
					task.state = 1;
					dao.updateMaintenaceTask(task);
			}
		}
		
		
		result[0] = totalSuccessNum;
		result[1] = totalFailNum;
		dao.closeDB();
		return result;
	}
	@Override
	protected void onPostExecute(Integer[] result){
		super.onPostExecute(result);
		if(mCallBack !=null){
			mCallBack.callBackResult(result);
		}
	}
	
	public interface TaskCallBack{
		public void preExecute();
		/**
		 * 
		 * @param result result[0]:总共成功数，
		 *  			 result[1]:总共失败数
		 */
		public void callBackResult(Integer[] result);
	}
}
