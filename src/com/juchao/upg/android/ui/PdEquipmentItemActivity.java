package com.juchao.upg.android.ui;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.juchao.upg.android.R;
import com.juchao.upg.android.adapter.AccountEquipmentListAdapter;
import com.juchao.upg.android.db.AccountTaskDao;
import com.juchao.upg.android.entity.AccountEquipment;

public class PdEquipmentItemActivity extends BaseActivity implements OnClickListener{
	public static final String TAG=PdEquipmentItemActivity.class.getSimpleName();
	public long taskId=0L;
	public String taskName="";
	private TextView headerLeft,headerTitle,noResult;
	private ListView listView;
	private AccountEquipmentListAdapter adapter;
	private TextView assertName,assertCode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pd_task_list_1);
		taskId=getIntent().getLongExtra("taskId", 0L);
		taskName=getIntent().getStringExtra("taskName");
		
		headerLeft=(TextView)findViewById(R.id.header_left);
		headerTitle=(TextView)findViewById(R.id.header_title);
		
		headerLeft.setText("盘点查看");
		headerTitle.setText(taskName);
		assertName=(TextView)findViewById(R.id.assertName);
		assertCode=(TextView)findViewById(R.id.assertCode);
		
		//加粗
		assertName.setText("项目");
		TextPaint tp=assertName.getPaint();
		tp.setFakeBoldText(true);
		//加粗
		assertCode.setText("条码");
		TextPaint t=assertCode.getPaint();
		t.setFakeBoldText(true);
		
		//noResult= (TextView)findViewById(R.id.noResult);
		listView = (ListView)findViewById(R.id.listView);
		
		adapter=new AccountEquipmentListAdapter(this);
		listView.setAdapter(adapter);
		headerLeft.setOnClickListener(this);
		loadData();
	}

	private void loadData() {
		new AsyncTask<String,Void,List<AccountEquipment>>(){
			@Override
			protected void onPreExecute(){
			//	noResult.setVisibility(View.GONE);
			}
			 
			@Override
			protected List<AccountEquipment> doInBackground(String... params) {
				AccountTaskDao dao=new AccountTaskDao(PdEquipmentItemActivity.this);
				List <AccountEquipment> list =dao.queryTaskEquipment(taskId,0);
				dao.closeDB();
				return list;
			}
			@Override
			protected void onPostExecute (List<AccountEquipment> result){
				if(result !=null && result.size()>0){
					adapter.addData(result);
				}else{
					//noResult.setVisibility(View.VISIBLE);
				}
			}
		}.execute();
	}

	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		case R.id.header_left: //返回
			finishActivity();
			break;
		}
	}
}
