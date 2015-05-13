package com.juchao.upg.android.ui;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.juchao.upg.android.db.AccountTaskDao;
import com.juchao.upg.android.R;
import com.juchao.upg.android.entity.AccountEquipment;
import com.juchao.upg.android.adapter.WaitUploadAccountItemListAdapter;

public class PdTaskUploadItemListActivity extends BaseActivity implements OnClickListener{
    private ListView listView;
    private TextView headerLeft , headerTitle ,noResult;
    private WaitUploadAccountItemListAdapter adapter;
	protected static final String TAG = PdTaskUploadItemListActivity.class.getSimpleName();
	private long taskId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pd_task_item_list_act);
		
		taskId=getIntent().getLongExtra("taskId", 0L);
		headerLeft = (TextView)findViewById(R.id.header_left);
		headerTitle = (TextView)findViewById(R.id.header_title);
		headerLeft.setText("待上传数据");
		headerTitle.setText("待上传的盘点项目");
		
		noResult= (TextView)findViewById(R.id.noResult);
		listView = (ListView)findViewById(R.id.listView);
		
		adapter = new WaitUploadAccountItemListAdapter(this);
		listView.setAdapter(adapter);
		headerLeft.setOnClickListener(this);
		
		loadData();
	}
	
	private void loadData() {
		new AsyncTask<String,Void,List<AccountEquipment>>(){
			@Override
			protected void onPreExecute(){
				noResult.setVisibility(View.GONE);
			}

			@Override
			protected List<AccountEquipment> doInBackground(String... params) {
				List<AccountEquipment> itemList=null;
				try{
					AccountTaskDao dao=new AccountTaskDao(PdTaskUploadItemListActivity.this);
					itemList=dao.queryAccountTaskNeedUploadItemList(taskId);
					dao.closeDB();
				}catch(Exception e){
					e.printStackTrace();
				}
				return itemList;
			}
			
			@Override
			protected void onPostExecute(List<AccountEquipment> result){
				if(result !=null && result.size() > 0){
					adapter.addData(result);
					//adapter.setData(result);
				}else{
					noResult.setVisibility(View.VISIBLE);
				}
			};
		}.execute();
	}

	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		case R.id.header_left:  //返回
			finishActivity();
			break;
		}
	}
}
