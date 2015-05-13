package com.juchao.upg.android.task;

import android.content.Context;
import android.os.AsyncTask;
import com.juchao.upg.android.entity.ResAppUpdate;
import com.juchao.upg.android.net.NetAccessor;
import com.juchao.upg.android.util.Constants;

/**
 * GetInspectionDownloadListTask
 * 
 */
public class CheckUpdateAppTask extends AsyncTask<String, Void, ResAppUpdate> {
	private static final String TAG = CheckUpdateAppTask.class.getSimpleName();
	private Context mContext;
	private TaskCallBack mCallBack;
	private int resultCode;
	
	public CheckUpdateAppTask(Context context) {
		this(context, null);
	}
	
	public CheckUpdateAppTask(Context context, TaskCallBack callback) {
		this.mContext = context;
		this.mCallBack = callback;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		resultCode = Constants.REQUEST_FAIL;
		this.mCallBack.preCheckUpdate();
	}
	
	@Override
	protected ResAppUpdate doInBackground(String... params) {
		// TODO Auto-generated method stub
		
		ResAppUpdate mResAppUpdate = NetAccessor.getAppUpdateInfo(mContext);
		

		
		return mResAppUpdate;
	}
	
	@Override
	protected void onPostExecute(ResAppUpdate result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(mCallBack != null) {
			mCallBack.callBackResult(resultCode, result);
		}
	}
	
	public interface TaskCallBack {
		public void preCheckUpdate();
		public void callBackResult(int resultCode, ResAppUpdate mResAppUpdate);
	}
}
