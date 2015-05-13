package com.juchao.upg.android.ui;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.juchao.upg.android.R;
import com.juchao.upg.android.adapter.InspectionItemListAdapter;
import com.juchao.upg.android.db.InspectionEquipmentDao;
import com.juchao.upg.android.entity.InspectionTaskEquipmentItem;
import com.juchao.upg.android.util.IntentUtil;
import com.juchao.upg.android.view.ActivityInspectionItemDetailUtil;

public class DjTaskItemListActivity extends BaseActivity implements OnClickListener,OnItemClickListener {

    
    private ListView listView;
    private TextView headerLeft , headerTitle ,noResult;
    
    private InspectionItemListAdapter adapter;
    
	protected static final String TAG = "DjTaskListActivity";
	private  long inspectionTaskEquipmentId = 0;
	private String inspectionTaskEquipmentName = "";
	private long inspectionTaskId =0L;
	private int style;
	private String taskName;
	private long equipmentId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.dj_task_item_list_act);
       
		inspectionTaskEquipmentId = getIntent().getLongExtra("inspectionTaskEquipmentId", 0);
		inspectionTaskId=getIntent().getLongExtra("inspectionTaskId", 0L);
		Log.d(TAG, "inspectionTaskEquipmentId > " +inspectionTaskEquipmentId);
		inspectionTaskEquipmentName = getIntent().getStringExtra("inspectionTaskEquipmentName");
		style=getIntent().getIntExtra("style", 0);
		equipmentId=getIntent().getLongExtra("equipmentId", 0L);
		taskName=getIntent().getStringExtra("taskName");
		
		headerLeft = (TextView)findViewById(R.id.header_left);
		headerTitle = (TextView)findViewById(R.id.header_title);
		headerLeft.setText("任务下载");
		
		headerTitle.setText(inspectionTaskEquipmentName);
		
		noResult= (TextView)findViewById(R.id.noResult);
		listView = (ListView)findViewById(R.id.listView);
		
		adapter = new InspectionItemListAdapter(this);
		listView.setAdapter(adapter);
		headerLeft.setOnClickListener(this);
		
		//listView.setOnItemLongClickListener(mOnItemLongClick);
		listView.setOnItemClickListener(this);
		
		loadData(inspectionTaskId);
	}

	private void loadData(final long taskId) {
		new AsyncTask<String, Void, List<InspectionTaskEquipmentItem>>() {
			protected void onPreExecute() {
				noResult.setVisibility(View.GONE);
			};
			@Override
			protected List<InspectionTaskEquipmentItem> doInBackground(String... params) {
				
				InspectionEquipmentDao dao = new InspectionEquipmentDao(DjTaskItemListActivity.this);
				List<InspectionTaskEquipmentItem> itemList = dao.queryInspectionItemList(taskId,inspectionTaskEquipmentId , 0 ,style);
				dao.closeDB();
				
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
	
	private OnItemLongClickListener mOnItemLongClick = new OnItemLongClickListener(){

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			InspectionTaskEquipmentItem item = (InspectionTaskEquipmentItem) parent.getAdapter().getItem(position);
			
			Bundle bundle=new Bundle();
			
			bundle.putSerializable("inspectionItem", item);
			
			IntentUtil.startActivityFromMain(DjTaskItemListActivity.this,ActivityInspectionItemDetailUtil.class,bundle);
			
			return false;
		}
		
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		//InspectionTaskEquipmentItem item = (InspectionTaskEquipmentItem) parent.getAdapter().getItem(position);
		
		InspectionTaskEquipmentItem item=(InspectionTaskEquipmentItem)parent.getAdapter().getItem(position);
		
		InspectionEquipmentDao dao = new InspectionEquipmentDao(DjTaskItemListActivity.this);
		
		Bundle bundle=new Bundle();
		
		String scanCode=dao.GetBaseEquipmentSpotcheckProjectNo(item.equipmentSpotcheckId);
		dao.closeDB();
		
//		bundle.putLong("inspectionTaskId", inspectionTaskId);
//		bundle.putLong("inspectionTaskEquipmentId", inspectionTaskEquipmentId);
//		bundle.putLong("equipmentId", equipmentId);
//		bundle.putString("mEquipmentName", mEquipmentName);
//		bundle.putString("taskName", taskName);
//		bundle.putInt("style",style);
		
		bundle.putSerializable("inspectionItem", item);
		bundle.putLong("inspectionTaskId", inspectionTaskId);
		bundle.putString("mEquipmentName", inspectionTaskEquipmentName);
		bundle.putString("scanCode", scanCode);
		bundle.putString("taskName", taskName);
		bundle.putInt("style", style);
		bundle.putLong("equipmentId", equipmentId);
		IntentUtil.startActivityFromMain1(DjTaskItemListActivity.this,DjTaskItemQueryDetailActivity.class,bundle);
		
	}
}
