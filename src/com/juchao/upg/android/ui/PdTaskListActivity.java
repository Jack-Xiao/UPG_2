package com.juchao.upg.android.ui;

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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.juchao.upg.android.R;
import com.juchao.upg.android.adapter.AccountTaskListAdapter;
import com.juchao.upg.android.db.AccountTaskDao;
import com.juchao.upg.android.entity.AccountTask;
import com.juchao.upg.android.entity.AccountTaskList;
import com.juchao.upg.android.util.IntentUtil;

public class PdTaskListActivity extends BaseActivity implements OnClickListener , OnItemClickListener ,ListView.OnScrollListener {

	public static int FROM_QUERY=1;
	public static int FROM_ACCOUNT=2;
	private int from;
	private TextView headerLeft,headerTitle,noResult;
	private ListView listView;
	private static final int PAGE_SIZE=10;
	public static final String REFRASH_ACTION="UPG_ACCOUNTTASK_REFRASH_ACITON";
	
	//public static final int From_Equipment_Account = 0;
	
	private AccountTaskListAdapter adapter;
	private int curpage = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pd_task_list_act);
		
		from = getIntent().getIntExtra("from",FROM_QUERY );
		
		headerLeft = (TextView)findViewById(R.id.header_left);
		headerTitle = (TextView)findViewById(R.id.header_title);
		headerLeft.setText("盘点");
		headerTitle.setText("盘点列表");
		
		noResult= (TextView)findViewById(R.id.noResult);
		listView = (ListView)findViewById(R.id.listView);
		
		listView.setOnScrollListener(this);
		mListFooterView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
						.inflate(R.layout.loadmore_bar, listView,false);
		mListFooterProgress = (ProgressBar) mListFooterView.findViewById(R.id.progress);
		mLoadingText = (TextView) mListFooterView.findViewById(R.id.loading_text);
		listView.addFooterView(mListFooterView, null, false);
		
		headerLeft.setOnClickListener(this);
		adapter=new AccountTaskListAdapter(this,from);
		listView.setAdapter(adapter);
		loadData();
		
		listView.setOnItemClickListener(this);
		IntentFilter filter=new IntentFilter();
		filter.addAction(REFRASH_ACTION);
		registerReceiver(receiver,filter);
		listView.setOnItemClickListener(this);
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
	

	private void loadData() {
		new AsyncTask<String,Void,List<AccountTask>>(){
			@Override
			protected void onPreExecute(){
				noResult.setVisibility(View.GONE);
				if(curpage ==1){
					setFooterViewVisibility(View.GONE);
				}
			}
			@SuppressWarnings("null")
			@Override
			protected List<AccountTask> doInBackground(String... params) {
				List<AccountTask> taskList=null;
				//获取任务列表
				AccountTaskDao dao=new AccountTaskDao(PdTaskListActivity.this);
				List<AccountTask> list=dao.queryAccountTaskList(PAGE_SIZE, curpage, 1);
				dao.closeDB();
//				if(list !=null && list.size() > 0){
//					for(int i=0;i<list.size();i++){
//						AccountTask item=new AccountTask();
//						item.id=list.get(i).id;
//						item.name=list.get(i).name;
//						//item.index=i+1;
//						item.total=list.get(i).total;
//						taskList.add(item);
//					}
//				}
				return list;
			}
			@Override
			protected void onPostExecute(List<AccountTask> result){
				if(result !=null && result.size() >0){
					adapter.addData(result);
				}else{
					if(curpage ==1){
						noResult.setVisibility(View.VISIBLE);
					}
				}
				
				if(result == null || result.size()<PAGE_SIZE){
					setFooterViewVisibility(View.GONE);
				}else{
					if(curpage ==1 && result.size() > 10){
						setFooterViewVisibility(View.VISIBLE);
					}
				}
			}
		}.execute();		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id){
		try{
			AccountTask tempTask=(AccountTask)parent.getAdapter().getItem(position);
			if(tempTask !=null){
				Bundle bundle=new Bundle();
				bundle.putLong("taskId", tempTask.id);
				bundle.putString("taskName",tempTask.name);
				if(from == 1){
					IntentUtil.startActivityFromMain(PdTaskListActivity.this,PdEquipmentItemActivity.class,bundle);
				}else{
					
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
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
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		int last = view.getLastVisiblePosition();
		
		if(null != adapter){
			int adapterCnt = adapter.getCount()-1;
			if(last >= adapterCnt && mListFooterView.getVisibility()==View.VISIBLE){
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
	protected void onDestroy(){
		super.onDestroy();
		unregisterReceiver(receiver);
	}
}
 