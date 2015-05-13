package com.juchao.upg.android.task;

import android.content.Context;
import android.os.AsyncTask;

import com.juchao.upg.android.db.InspectionEquipmentDao;
import com.juchao.upg.android.db.MaintenaceEquipmentDao;
import com.juchao.upg.android.entity.MaintenaceTaskEquipmentItem;
import com.juchao.upg.android.net.NetAccessor;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;

public class UploadMaintenaceItemTask extends AsyncTask<Long, Void, Integer[]>{
	private static final String TAG= UploadMaintenaceItemTask.class.getSimpleName();
	private Context mContext;
	private TaskCallBack mCallBack;
	private MaintenaceTaskEquipmentItem maintenaceItem;
	private int from;
	
	public UploadMaintenaceItemTask(Context context, MaintenaceTaskEquipmentItem maintenaceItem,int from,
			TaskCallBack callback){
		this.mContext=context;
		this.mCallBack=callback;
		this.maintenaceItem=maintenaceItem;
		this.from=from;
	}
	
	@Override
	protected void onPreExecute(){
		super.onPreExecute();
		if(mCallBack !=null){
			this.mCallBack.preExecute();
		}
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
		public void callBackResult(Integer[] result);
	}
	@Override
	protected Integer[] doInBackground(Long... params) {
		Integer[] result = new Integer[2]; 
		String token = DefaultShared.getString(Constants.KEY_TOKEN, "");
		int commitResult=0;
		if(from ==com.juchao.upg.android.ui.WxTaskEquipmentListActivity.From_EffectConfirm_Maintenace){
			maintenaceItem.state= 4;
			commitResult = NetAccessor.confirmMaintenaceItemData(mContext,token,maintenaceItem);
		}
		else
		{
			maintenaceItem.state= 3;
		    commitResult = NetAccessor.commitMaintenaceItemData(mContext, token,  maintenaceItem);
		}
			MaintenaceEquipmentDao dao = new MaintenaceEquipmentDao(mContext);
			//test: @20141108此处暂时设定全部提交成功。
			//commitResult = 0;
			if(commitResult == 0){
				result[0] = commitResult;
				//更新维修问题 的状态（完成）
				dao.updateMaintenaceItem(maintenaceItem.id,from);
			}else{
				result[0] = -1;
			}
			result[1] = dao.commitCheckAfter(maintenaceItem.equipmentId);
			
			dao.closeDB();
		
		
		return result;
	}
}
