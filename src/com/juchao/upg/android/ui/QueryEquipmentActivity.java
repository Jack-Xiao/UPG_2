package com.juchao.upg.android.ui;


import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.juchao.upg.android.R;
import com.juchao.upg.android.adapter.QueryEquipmentTaskAdapter;
import com.juchao.upg.android.db.QueryEquipmentAndSparePartDao;
import com.juchao.upg.android.entity.BaseEquipment;
import com.juchao.upg.android.util.ToastUtil;
import com.juchao.upg.android.util.IntentUtil;

public class QueryEquipmentActivity extends BaseActivity implements OnClickListener,OnScrollListener,OnItemClickListener {
	private static final String TAG=QueryEquipmentActivity.class.getSimpleName();
    private TextView headerLeft , headerTitle, noResult,queryNoResult ;
    private Button btnQuery;
    private EditText etKerywork;
    private ListView listView;
    
	public ProgressDialog progressDialog;
	public QueryEquipmentTaskAdapter adapter;
	private Context context;
	private int curpage=1;
	private int PAGE_SIZE=3;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.query_equipment_act);
       
		headerLeft = (TextView)findViewById(R.id.header_left);
		headerTitle = (TextView)findViewById(R.id.header_title);
		
		headerLeft.setText("备件查询");
		headerTitle.setText("设备查询");
		
		noResult= (TextView)findViewById(R.id.query_noResult);
		btnQuery = (Button)findViewById(R.id.btnQuery);
		etKerywork = (EditText)findViewById(R.id.etKerywork);
		listView = (ListView)findViewById(R.id.listView);
		
		
		btnQuery.setOnClickListener(this);
		listView.setOnScrollListener(this);
		headerLeft.setOnClickListener(this);
		
		mListFooterView =  ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.loadmore_bar,listView,false);
		mListFooterProgress=  (ProgressBar) mListFooterView.findViewById(R.id.progress);
		mLoadingText=(TextView)mListFooterView.findViewById(R.id.loading_text);
		//listView.addFooterView(mListFooterView, null, false);
		
		adapter = new QueryEquipmentTaskAdapter(this);
		listView.setOnItemClickListener(this);
		listView.setAdapter(adapter);
		
	}

	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
			case R.id.header_left:  //返回
				finishActivity();
				break;
			case R.id.btnQuery:  //确认
				
				String keywork = etKerywork.getText().toString().trim();
				if(TextUtils.isEmpty(keywork)){
					ToastUtil.showShortToast(QueryEquipmentActivity.this, "请输入设备名称、编号查询");
					return ;
				}
				
				doQuery(keywork);
				
				//ToastUtil.showShortToast(QueryEquipmentActivity.this, "设置成功");
				break;
		}
	}

	private void doQuery(String keywork) {

		new AsyncTask<String,Void,List<BaseEquipment>>(){
			protected void onPreExecute(){
				mListFooterProgress.setVisibility(View.VISIBLE);
				mLoadingText.setVisibility(View.VISIBLE);
				noResult.setVisibility(View.GONE);
				/*noResult.setVisibility(View.GONE);
				if(curpage==1){
					setFooterViewVisibility(View.GONE);
				}*/
			
//					adapter.clearData();
//					adapter.notifyDataSetChanged();
			};
			
			@Override
			protected List<BaseEquipment> doInBackground(String... params) {
				List<BaseEquipment> equipmentlist=null;
				String test=params[0];
				//获取设备信息列表
				QueryEquipmentAndSparePartDao dao=new QueryEquipmentAndSparePartDao(QueryEquipmentActivity.this);
				
				equipmentlist=dao.query(test);
				dao.closeDB();
				return equipmentlist;
			}
			protected void onPostExecute(List<BaseEquipment> result){
				//mListFooterProgress.setVisibility(View.GONE);
				/*if(result.size() == 0){
					noResult.setVisibility(View.VISIBLE);
				}else{
					noResult.setVisibility(View.GONE);
				}*/
				if(result !=null && result.size() > 0){
					adapter.setData(result);
					//adapter.addData(result);
				}else{
					adapter.setData(new ArrayList<BaseEquipment>());
					noResult.setVisibility(View.VISIBLE);
				}
				/*if(result == null || result.size() < PAGE_SIZE){
					setFooterViewVisibility(View.GONE);
				}else{
					if(curpage == 1 && result.size() > PAGE_SIZE ){
						setFooterViewVisibility(View.VISIBLE);
					}
				}*/
			}
		}.execute(keywork);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		try{
			BaseEquipment entity=(BaseEquipment)parent.getAdapter().getItem(position);
			if(entity !=null){
				Bundle bundle=new Bundle();
				bundle.putLong("equipmentId", entity.id);
				IntentUtil.startActivityFromMain(QueryEquipmentActivity.this, QueryEquipmentItemActivity.class, bundle);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		int last=view.getLastVisiblePosition();
		if(null !=adapter){
			int adapterCnt=adapter.getCount()-1;
			if(last >=adapterCnt && mListFooterView.getVisibility() == View.VISIBLE){
				Log.d(TAG, "curpage: " + curpage );
				++curpage;
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
	}		
}
