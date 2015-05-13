package com.juchao.upg.android.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.juchao.upg.android.R;
import com.juchao.upg.android.adapter.InspectionTaskListAdapter;
import com.juchao.upg.android.db.InspectionEquipmentDao;
import com.juchao.upg.android.db.InspectionTaskDao;
import com.juchao.upg.android.entity.InspectionTask;
import com.juchao.upg.android.entity.InspectionTaskEquipment;
import com.juchao.upg.android.entity.InspectionTaskList;
import com.juchao.upg.android.util.IntentUtil;

public class DjTaskListActivity extends BaseActivity implements OnClickListener , OnItemClickListener ,ListView.OnScrollListener{

	private int from;
	public static final int From_Equipment_Inspection = 1;
	public static final int From_Query_Inspection = 2;
    private ListView listView;
    private TextView headerLeft , headerTitle ,noResult;
    
    private InspectionTaskListAdapter adapter;
    
    private int curpage = 1;
	private static final int PAGE_SIZE = 10;
	protected static final String TAG = "DjTaskListActivity";
	public static final String REFRASH_ACTION = "UPG_TASKLIST_REFRASH_ACTION";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.dj_task_list_act);
       
		from = getIntent().getIntExtra("from", 0);
		
		headerLeft = (TextView)findViewById(R.id.header_left);
		headerTitle = (TextView)findViewById(R.id.header_title);
		headerLeft.setText("任务下载");
		headerTitle.setText("点检任务列表");
		
		noResult= (TextView)findViewById(R.id.noResult);
		listView = (ListView)findViewById(R.id.listView);
		
		listView.setOnScrollListener(this);
		mListFooterView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
						.inflate(R.layout.loadmore_bar, listView,false);
		mListFooterProgress = (ProgressBar) mListFooterView.findViewById(R.id.progress);
		mLoadingText = (TextView) mListFooterView.findViewById(R.id.loading_text);
		listView.addFooterView(mListFooterView, null, false);
		
		
		adapter = new InspectionTaskListAdapter(this , from);
		listView.setAdapter(adapter);
		
		if(from == From_Query_Inspection){
			headerLeft.setText("点检");
			listView.setOnItemClickListener(this);
		}else if(from == 0){
			headerLeft.setText("任务下载");
			listView.setOnItemClickListener(this);
		}else if(from == From_Equipment_Inspection){
			headerLeft.setText("点检");
		}
		
		headerLeft.setOnClickListener(this);
		
		curpage = 1;
		loadData();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(REFRASH_ACTION);
		registerReceiver(receiver, filter);
	}

	private BroadcastReceiver receiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action != null && action.equals(REFRASH_ACTION)){
				refrash();
			}
		}
	};
	
	private void refrash(){
		adapter.clearData();
		curpage = 1;
		loadData();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
	}
	private void loadData() {
		new AsyncTask<String, Void, List<InspectionTaskList>>() {
			protected void onPreExecute() {
				noResult.setVisibility(View.GONE);
				if(curpage == 1){
					setFooterViewVisibility(View.GONE);
				}
			};
			@Override
			protected List<InspectionTaskList> doInBackground(String... params) {
				List<InspectionTaskList> taskList = null;
				//获取任务列表
				InspectionTaskDao dao = new InspectionTaskDao(DjTaskListActivity.this);
				Log.d(TAG, "PAGE_SIZE" + PAGE_SIZE + "  , curpage : " +curpage);
				List<InspectionTask> list = dao.queryInspectionTaskList(PAGE_SIZE , curpage ,1);  //需要过滤过期的任务 ....待完善
				dao.closeDB();
				
				//根据任务列表，获取对应的设备列表
				if(list != null && list.size() > 0){
					taskList = new ArrayList<InspectionTaskList>();
					InspectionEquipmentDao eDao = new InspectionEquipmentDao(DjTaskListActivity.this);
					for(int i=0 ; i < list.size(); i++){
						List<InspectionTaskEquipment> mEquipmentList = eDao.queryEquipmentList(list.get(i).id,list.get(i).status);
						int equipmentItemNum=eDao.queryEquipmentItemNum(list.get(i).id,list.get(i).status);
						
						if(mEquipmentList != null && mEquipmentList.size() > 0){
							//任务列表的标题，即任务名称
							InspectionTaskList item = new InspectionTaskList();
							item.id = list.get(i).id;
							item.name = list.get(i).taskName;
							item.type = 1;
							item.taskIndex = i+1;
							item.deviceNum = mEquipmentList.size();
							item.itemNum = 0;
							item.style=list.get(i).status;
							item.intervalType=list.get(i).intervalType;
							taskList.add(item);
							//该任务下的设备列表
							int itemNum = 0;
							
							for(InspectionTaskEquipment entity : mEquipmentList){
								InspectionTaskList item2 = new InspectionTaskList();
								item2.id = entity.id;
								item2.equipmentId = entity.equipmentId;
								item2.name = entity.equipmentName;
								item2.type = 2;
								item2.taskName = item.name;
								item2.taskId= list.get(i).id;
								item2.style=list.get(i).status;
								item2.intervalType=list.get(i).intervalType;
								itemNum +=  eDao.queryItemSizeById(list.get(i).id,entity.id , 0,list.get(i).status);
								taskList.add(item2);
							}
							try{
								Log.d(TAG, "itemNum : " +itemNum);
								taskList.get(taskList.size() - mEquipmentList.size() -1 ).itemNum = itemNum;
								//taskList.get(taskList.size() - mEquipmentList.size() - 1).itemNum = equipmentItemNum;
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					}
					eDao.closeDB();
				}
				
				return taskList;
			}
			
			protected void onPostExecute(List<InspectionTaskList> result) {
				
				if(result != null && result.size() > 0){
					Log.d(TAG, "result.size() > " +result.size());
					adapter.addData(result);
				}else{
					if(curpage == 1){
						noResult.setVisibility(View.VISIBLE);
					}
				}
				
				if(result == null || result.size() < PAGE_SIZE){
					setFooterViewVisibility(View.GONE);
				}else{
					if(curpage == 1 && result.size() > 10){
						setFooterViewVisibility(View.VISIBLE);
					}
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
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
			//进入设备对应的项目列表界面
			try{
				InspectionTaskList tempTaskEntity = (InspectionTaskList)parent.getAdapter().getItem(position);
				if( tempTaskEntity != null && tempTaskEntity.type == InspectionTaskListAdapter.TYPE_2){
					long taskId=tempTaskEntity.taskId;
					int style=tempTaskEntity.style;
					Bundle bundle = new Bundle();
					bundle.putLong("inspectionTaskEquipmentId", tempTaskEntity.id);
					bundle.putString("inspectionTaskEquipmentName", tempTaskEntity.name);
					bundle.putLong("inspectionTaskId", taskId);
					bundle.putLong("equipmentId", tempTaskEntity.equipmentId);
					bundle.putString("taskName", tempTaskEntity.taskName);
					bundle.putInt("style", tempTaskEntity.style);
					IntentUtil.startActivityFromMain(DjTaskListActivity.this, DjTaskItemListActivity.class, bundle);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		int last = view.getLastVisiblePosition();
		
		if(null != adapter){
			int adapterCnt = adapter.getCount()-1;
			if(last >= adapterCnt && mListFooterView.getVisibility()==View.VISIBLE){
					Log.d(TAG, "curpage : " +curpage);
					++curpage;
					loadData();
			}
		}
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
	}
}
