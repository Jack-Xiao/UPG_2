package com.juchao.upg.android.task;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.juchao.upg.android.entity.AccountTask;
import com.juchao.upg.android.entity.InspectionTask;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;
import com.juchao.upg.android.entity.*;
import com.juchao.upg.android.db.AccountTaskDao;
import com.juchao.upg.android.net.NetIOUtils;
import com.juchao.upg.android.net.NetAccessor;

public class GetAccountTaskList extends
		AsyncTask<String, Void, List<AccountTask>> {
	private Context mContext;
	private TaskCallBack mCallBack;

	public GetAccountTaskList(Context context) {
		this(context, null);
	}

	public GetAccountTaskList(Context context, TaskCallBack callback) {
		this.mContext = context;
		this.mCallBack = callback;
	}

	protected void onPreExecute() {
		super.onPreExecute();
		if (mCallBack != null) {
			this.mCallBack.preExecute();
		}
	}

	@Override
	protected List<AccountTask> doInBackground(String... params) {
		int pageSize = Integer.parseInt(params[0]);
		int pageNum = Integer.parseInt(params[1]);

			ResAccountTask result = null;
			List<Long> taskIds = new ArrayList<Long>();
			String token = DefaultShared.getString(Constants.KEY_TOKEN, "");
			AccountTaskDao dao = new AccountTaskDao(mContext);
			if (NetIOUtils.isNetworkOk(mContext) && pageNum == 1) {
				result = NetAccessor.getAccountTaskList(mContext, token);
				if (result != null && result.code == 0 && result.tasks != null
						&& result.tasks.size() > 0) {
					for (int i = result.tasks.size() - 1; i >= 0; i--) {
						taskIds.add(result.tasks.get(i).id);
						dao.insertAccountTask(result.tasks.get(i));
					}
					dao.removeFinishAccountTask(taskIds);
				}
			}
			List<AccountTask> list = dao.queryAccountTaskList(pageSize,
					pageNum, 0);
			dao.closeDB();
		return list;
	}

	@Override
	protected void onPostExecute(List<AccountTask> result) {
		super.onPostExecute(result);
		if (mCallBack != null) {
			mCallBack.callBackResult(result);
		}
	}

	public interface TaskCallBack {
		public void preExecute();
		public void callBackResult(List<AccountTask> taskList);
	}
}
