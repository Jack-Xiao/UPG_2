package com.juchao.upg.android.task;

import java.util.List;
import android.content.Context;
import android.os.AsyncTask;

import com.juchao.upg.android.entity.AccountEquipment;
import com.juchao.upg.android.entity.AccountTask;
import com.juchao.upg.android.net.NetAccessor;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;
import com.juchao.upg.android.db.AccountTaskDao;


public class UploadMultAccountItemTask extends AsyncTask<Long,Void,Integer[]> {
	private static final String TAG = UploadMultAccountItemTask.class.getSimpleName();
	private Context mContext;
	private TaskCallBack mCallBack;
	private List<AccountTask> mtaskList;
	
	public UploadMultAccountItemTask(Context context,
			List<AccountTask> taskList, TaskCallBack taskCallBack) {
		this.mtaskList = taskList;
		this.mCallBack=taskCallBack;
		this.mContext=context;
	}
	

	public interface TaskCallBack {
		void callBackResult(Integer[] result);
		void preExecute();
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
		Integer [] result= new Integer[2];
		AccountTaskDao dao=new AccountTaskDao(mContext);
		String token = DefaultShared.getString(Constants.KEY_TOKEN, "");
		int totalFailNum = 0;
		int totalSuccessNum = 0;
		for(AccountTask task : mtaskList){
			List<AccountEquipment> itemList = dao.queryAccountTaskNeedUploadItemList(task.id);
			//int itemNum=dao.queryAllHasUploadEquipmentItemNum(task.id);
			int commitSuccessNum = 0;
			for(AccountEquipment item : itemList){
				int commitResult = NetAccessor.commitAccountItemData(mContext, token, item);
				if(commitResult == 0 || commitResult == 8){ //点检项目提交成功
					commitSuccessNum += 1;
					totalSuccessNum ++;
					dao.updateAccountItem(item.id);
				}else{
					totalFailNum ++;
				}
			}
			if(commitSuccessNum != 0 && commitSuccessNum == itemList.size()){
				dao.updateAccountTask(task);
			}
		}
		result[0] = totalSuccessNum;
		result[1] = totalFailNum;
		dao.closeDB();
		return result;
	}

	@Override
	protected void onPostExecute(Integer[] result) {
		super.onPostExecute(result);
		if(mCallBack != null) {
			mCallBack.callBackResult(result);
		}
	}
}
