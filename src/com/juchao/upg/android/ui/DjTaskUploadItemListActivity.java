package com.juchao.upg.android.ui;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.juchao.upg.android.R;
import com.juchao.upg.android.adapter.WaitUploadInspectionItemListAdapter;
import com.juchao.upg.android.db.InspectionTaskDao;
import com.juchao.upg.android.entity.InspectionTaskEquipmentItem;

public class DjTaskUploadItemListActivity extends BaseActivity implements OnClickListener {

    
    private ListView listView;
    private TextView headerLeft , headerTitle ,noResult;
    private WaitUploadInspectionItemListAdapter adapter;
	protected static final String TAG = DjTaskUploadItemListActivity.class.getSimpleName();
	private long inspectionTaskId = 0;
	private int style; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.dj_task_item_list_act);
       
		inspectionTaskId = getIntent().getLongExtra("inspectionTaskId", 0);
		style=getIntent().getIntExtra("style", 0);
		
		Log.d(TAG, "inspectionTaskId > " +inspectionTaskId);
		headerLeft = (TextView)findViewById(R.id.header_left);
		headerTitle = (TextView)findViewById(R.id.header_title);
		headerLeft.setText("待上传数据");
		headerTitle.setText("待上传的点检项目");
		
		noResult= (TextView)findViewById(R.id.noResult);
		listView = (ListView)findViewById(R.id.listView);
		
		adapter = new WaitUploadInspectionItemListAdapter(this);
		listView.setAdapter(adapter);
		headerLeft.setOnClickListener(this);
		
		loadData();
	}

	private void loadData() {
		new AsyncTask<String, Void, List<InspectionTaskEquipmentItem>>() {
			protected void onPreExecute() { //在 doInBackground..  之后执行
				noResult.setVisibility(View.GONE);
			};
			@Override
			protected List<InspectionTaskEquipmentItem> doInBackground(String... params) {
				List<InspectionTaskEquipmentItem> itemList = null;
				try{
					InspectionTaskDao dao = new InspectionTaskDao(DjTaskUploadItemListActivity.this);
					itemList = dao.queryInspectionItemList(inspectionTaskId,style);
					dao.closeDB();
				}catch(Exception e){
					e.printStackTrace();
				}
				
				return itemList;
			}
			
			protected void onPostExecute(List<InspectionTaskEquipmentItem> result) {
				if(result != null && result.size() > 0){
					adapter.addData(result);
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
