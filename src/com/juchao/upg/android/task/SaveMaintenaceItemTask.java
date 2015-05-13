package com.juchao.upg.android.task;

import android.content.Context;
import android.os.AsyncTask;

import com.juchao.upg.android.db.MaintenaceEquipmentDao;
import com.juchao.upg.android.entity.InspectionTaskEquipmentItem;
import com.juchao.upg.android.entity.MaintenaceTaskEquipmentItem;
import com.juchao.upg.android.ui.WxTaskEquipmentListActivity;

public class SaveMaintenaceItemTask extends AsyncTask<String,Void,Integer[]> {

	private static final String TAG= SaveMaintenaceItemTask.class.getSimpleName();
	private Context mContext;
	private TaskCallBack mCallBack;
	private MaintenaceTaskEquipmentItem maintenaceItem;
	private int from;
	public SaveMaintenaceItemTask(Context context,MaintenaceTaskEquipmentItem maintenaceItem ,
			TaskCallBack callback,int from) {
		this.mContext = context;
		this.mCallBack = callback;
		this.maintenaceItem = maintenaceItem;
		this.from=from;
	}
	@Override 
	protected void onPreExecute(){
		super.onPreExecute();
	}
	@Override
	protected void onPostExecute(Integer[] result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(mCallBack != null) {
			mCallBack.callBackResult(result);
		}
	}
	@Override
	protected Integer[] doInBackground(String... params) {
		MaintenaceEquipmentDao dao=new MaintenaceEquipmentDao(mContext);
		if(from == WxTaskEquipmentListActivity.From_Equipment_Maintenace ){
			from =3;
		}else if(from == WxTaskEquipmentListActivity.From_EffectConfirm_Maintenace){
			from = 4;
		}else{}
		Integer [] result=dao.saveMaintenaceItem(maintenaceItem,from);
		dao.closeDB();
		return result;
	}
	public interface TaskCallBack {
		public void callBackResult(Integer[] result);
	}

}
