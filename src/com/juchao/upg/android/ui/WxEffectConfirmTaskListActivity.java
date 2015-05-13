package com.juchao.upg.android.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.juchao.upg.android.R;
import com.juchao.upg.android.adapter.MaintenaceTaskListAdapter;
import com.juchao.upg.android.db.MaintenaceEquipmentDao;
import com.juchao.upg.android.db.MaintenaceTaskDao;
import com.juchao.upg.android.entity.MaintenaceTask;
import com.juchao.upg.android.entity.MaintenaceTaskEquipment;
import com.juchao.upg.android.entity.MaintenaceTaskList;
import com.juchao.upg.android.util.ConstantUtil;
import com.juchao.upg.android.util.IntentUtil;
import com.juchao.upg.android.util.Log;
import com.juchao.upg.android.util.ConstantUtil.WxEffectConfirmTask;

public class WxEffectConfirmTaskListActivity extends BaseActivity implements OnClickListener,OnItemClickListener,OnScrollListener {


	private int from;
	public static final int From_Equipment_Maintenace = 1;
	public static final int From_Query_Maintenace = 2;
	public static final int FROM_EffectConfirm_Maintenace=3;
	private ListView listView;
	private TextView headerLeft, headerTitle,noResult;
	    
	private MaintenaceTaskListAdapter adapter;
	private int curpage = 1;
	private static final int PAGE_SIZE = 10;
	protected static final String TAG="WxEffectConfirmTaskListActivity";
	public static final String REFRASH_ACTION="UPG_TASKLIST_REFRASH_ACTION";
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.wx_task_download_act);
		from=getIntent().getIntExtra("from", 0);
		headerLeft = (TextView)findViewById(R.id.header_left);
		headerTitle=(TextView)findViewById(R.id.header_title);
		
		headerLeft.setText("待维修");
		headerTitle.setText("维修任务列表");
		
		noResult=(TextView)findViewById(R.id.wx_noResult);
		listView=(ListView)findViewById(R.id.wx_listview);
		listView.setOnScrollListener(this);
		mListFooterView=((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.loadmore_bar,listView,false);
		mListFooterProgress=(ProgressBar)mListFooterView.findViewById(R.id.progress);
		mLoadingText=(TextView)mListFooterView.findViewById(R.id.loading_text);
		listView.addFooterView(mListFooterView,null,false);
		
		adapter=new MaintenaceTaskListAdapter(this,FROM_EffectConfirm_Maintenace,TAG);
		listView.setAdapter(adapter);
		headerLeft.setText("效果确认");
		headerLeft.setOnClickListener(this);
		
		curpage=1;
		loadData();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(REFRASH_ACTION);
		registerReceiver(receiver, filter);
		
	}
	
	private BroadcastReceiver receiver= new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			String action= intent.getAction();
			if(action !=null && action.equals(REFRASH_ACTION)){
				refrash();
			}
		}
	};
	
	private void refrash(){
		adapter.clearData();
		curpage= 1;
		loadData();
	}
	
	protected void onResume(){
		super.onResume();
	}
	private void loadData(){
		new AsyncTask<String, Void, List<MaintenaceTaskList>>(){
			protected void onPreExecute(){
				noResult.setVisibility(View.GONE);
				if(curpage == 1){
					setFooterViewVisibility(View.GONE);
				}
			};
			
			@Override
			protected List<MaintenaceTaskList> doInBackground(String... params) {
				List<MaintenaceTaskList> taskList = null;
				//获取任务列表
				MaintenaceTaskDao dao=new MaintenaceTaskDao(WxEffectConfirmTaskListActivity.this);
				Log.d(TAG, "PAGE_SIZE " +PAGE_SIZE + " , curpage : " + curpage);
				List<MaintenaceTask> list=dao.queryMaintenaceTaskList(PAGE_SIZE, curpage, 6,
						ConstantUtil.WxEffectConfirmTask.WxEffectConfirmTaskState); //需要过滤过期的任务 ....待完善
				dao.closeDB();
				
				//根据任务列表， 获取对应的设备列表
				if(list !=null && list.size() > 0 ) {
					taskList = new ArrayList<MaintenaceTaskList>();
					MaintenaceEquipmentDao eDao=new MaintenaceEquipmentDao(WxEffectConfirmTaskListActivity.this);
					for(int i =0 ; i < list.size(); i++ ){
						List<MaintenaceTaskEquipment> mEquipmentList=eDao.queryEquipmentList(list.get(i).id,0);
						int problNums=eDao.queryProblemNum(list.get(i).id,1);
						if (problNums == 0){
							continue;
						}
						if(mEquipmentList !=null && mEquipmentList.size() > 0 ){
							//任务列表的标题，即任务名称
							MaintenaceTaskList item=new MaintenaceTaskList();
							
							item.id=list.get(i).id;
							item.name=list.get(i).taskName;
							item.type = 1;
							item.taskIndex = i +1;
							item.deviceNum=mEquipmentList.size();
							item.itemNum = problNums;
							item.problNum=problNums;
							taskList.add(item);
							//该任务下的设备列表
							int itemNum = 0;
							for(MaintenaceTaskEquipment entity : mEquipmentList){
								MaintenaceTaskList item2=new MaintenaceTaskList();
								item2.id=entity.id;
								item2.equipmentId=entity.equipmentId;
								item2.equipmentName=entity.equipmentName;
								item2.type=2;
								item2.taskName= item.name;
								item2.maintenaceTaskId=item.id;
								itemNum +=eDao.queryItemSizeById(entity.id, 0);
								taskList.add(item2);
							}
							try{
								Log.d(TAG, "itemNum : " + itemNum);
								//taskList.get(taskList.size() - mEquipmentList.size() -1).itemNum = itemNum;
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					}
					eDao.closeDB();
				}
				return taskList;
			}
			@Override
			protected void onPostExecute(List<MaintenaceTaskList> result){
				if(result !=null && result.size() > 0){
					Log.d(TAG, "result.size() > " + result.size());
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
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		int last=view.getLastVisiblePosition();
		if(null !=adapter){
			int adapterCnt=adapter.getCount() - 1;
			if(last >= adapterCnt && mListFooterView.getVisibility() == View.VISIBLE){
				Log.d(TAG, "curpage : " + curpage);
				++curpage;
				loadData();
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// 进入设备对应的项目列表界面
		try{
			MaintenaceTaskList tempTaskEntity = (MaintenaceTaskList)parent.getAdapter().getItem(position);
			if(tempTaskEntity != null && tempTaskEntity.type == MaintenaceTaskListAdapter.TYPE_2){
				Bundle bundle = new Bundle();
				bundle.putLong("maintenaceTaskEquipmentId", tempTaskEntity.id);
				bundle.putString("maintenaceTaskEquipment", tempTaskEntity.name);
				IntentUtil.startActivityFromMain(WxEffectConfirmTaskListActivity.this, WxEquipmentProblListActivity.class,bundle);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.header_left:
			finishActivity();
			break;
		}
	}
	
	protected void onDestroy(){
		super.onDestroy();
		unregisterReceiver(receiver);
	}
}
