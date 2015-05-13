package com.juchao.upg.android.task;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.juchao.upg.android.db.InspectionEquipmentDao;
import com.juchao.upg.android.db.InspectionTaskDao;
import com.juchao.upg.android.entity.InspectionTask;
import com.juchao.upg.android.entity.InspectionTaskEquipmentItem;
import com.juchao.upg.android.net.NetAccessor;
import com.juchao.upg.android.ui.DjTaskUploadItemListActivity;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;

/**
 * 下载点检任务
 * 
 */
public class UploadMultInspectionItemTask extends AsyncTask<Long, Void, Integer[]> {
	private static final String TAG = UploadMultInspectionItemTask.class.getSimpleName();
	private Context mContext;
	private TaskCallBack mCallBack;
	private List<InspectionTask> taskList;
	
	public UploadMultInspectionItemTask(Context context,List<InspectionTask> taskList , 
			TaskCallBack callback ) {
		this.mContext = context;
		this.mCallBack = callback;
		this.taskList = taskList;
	}

	@Override
	protected void onPreExecute() { 
		super.onPreExecute();
		if(mCallBack != null) {
			this.mCallBack.preExecute();
		}
	}
	
	@Override
	protected Integer[] doInBackground(Long... params) {
		//Integer[] result = new Integer[2];
		Integer[] result = new Integer[3];
		result[2] = 0;
		InspectionTaskDao dao = new InspectionTaskDao(mContext);
		String token = DefaultShared.getString(Constants.KEY_TOKEN, "");
		int totalFailNum = 0;
		int totalSuccessNum = 0;
		for(InspectionTask task : taskList){
			
			List<InspectionTaskEquipmentItem> itemList = dao.queryInspectionItemList(task.id,task.status);
			int itemNum=dao.queryAllHasUploadEquipmentItemNum(task.id,task.status);
			int commitSuccessNum = 0; 
			for(InspectionTaskEquipmentItem item : itemList){ 
				int commitResult = NetAccessor.commitInspectionItemData(mContext, token, item); 
				if(commitResult == 0 || commitResult == 8){ //点检项目提交成功 
					commitSuccessNum += 1; 
					totalSuccessNum ++;  
					dao.updateInspectionItem(item.id,item.style);
				}else if(commitResult == 9){
					result[2] = 1;
					dao.restoreInspectionItem(item.id,item.style,item.inspectionTaskEquipmentId,task.id);
				}
				else{
					totalFailNum  ++;
				}
			}
			
			if(commitSuccessNum == itemNum){//如果改任务提交完成 //提交成功的数量等于所有需要提交的任务数量， 
				//更新下状态 (已完成)
				task.state = 1;
				dao.updateInspectionTask(task);
			}
		}
		
		result[0] = totalSuccessNum;
		result[1] = totalFailNum;
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
		/**
		 * 
		 * @param result result[0]:总共成功数，result[1]：总共失败数
		 */
		public void callBackResult(Integer[] result);
	}
}
