package com.juchao.upg.android.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.juchao.upg.android.R;
import com.juchao.upg.android.adapter.QuerySparePartTaskAdapter;
import com.juchao.upg.android.db.DBHelper;
import com.juchao.upg.android.db.QueryEquipmentAndSparePartDao;
import com.juchao.upg.android.entity.BaseSparePart;
import com.juchao.upg.android.entity.QuerySparePart;
import com.juchao.upg.android.task.QuerySparePartTask;
import com.juchao.upg.android.ui.WxTaskUploadActivity.MaintenaceTaskAdapter;
import com.juchao.upg.android.util.NetUtils;
import com.juchao.upg.android.util.ToastUtil;

public class QuerySparePartActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener, OnScrollListener {
	private static final String TAG = QuerySparePartActivity.class
			.getSimpleName();
	private TextView headerLeft, headerTitle, noResult;
	private TextView equipment_title, sparePart_title;
	private AutoCompleteTextView equipment_content;
	private TextView sparePart_content;
	private ListView listView;
	private Button btnQuery;
	private QuerySparePartTaskAdapter adapter;
	private Context context;
	private QuerySparePartTask querySparePartTask;
	
	public ProgressBar progressBar;
	public ProgressDialog progressDialog;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.query_sparepart_act);

		context=this;
		headerLeft = (TextView) findViewById(R.id.header_left);
		headerTitle = (TextView) findViewById(R.id.header_title);
		headerLeft.setText("查询");
		headerTitle.setText("备件列表");

		equipment_content = (AutoCompleteTextView) findViewById(R.id.etEquipment);
		sparePart_content = (TextView) findViewById(R.id.etSparePart);
		listView = (ListView) findViewById(R.id.listView);
		noResult = (TextView) findViewById(R.id.query_noResult);

		listView.setOnScrollListener(this);
		
//		mListFooterView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
//				.inflate(R.layout.loadmore_bar, listView, false);
//		mListFooterProgress = (ProgressBar) mListFooterView
//				.findViewById(R.id.progress);
//		mLoadingText = (TextView) mListFooterView
//				.findViewById(R.id.loading_text);
//		listView.addFooterView(mListFooterView, null, false);
//		
//		mLoadingText.setVisibility(View.GONE);
//		mListFooterView.setVisibility(View.GONE);
		adapter = new QuerySparePartTaskAdapter(this);
		
		listView.setAdapter(adapter);
		
		headerLeft.setOnClickListener(this);
		btnQuery = (Button) findViewById(R.id.btnQuery);
		
		SparePartAdapter sparePartAdapter = new SparePartAdapter(this, null, 3);
		equipment_content.setAdapter(sparePartAdapter);
		equipment_content.setThreshold(1); // 设置输入一个字符就弹出提示列表(默认输入两个字符才弹出提示)
		
		btnQuery.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				String equipmentKey = equipment_content.getText()
						.toString().trim();
				final String keyword = sparePart_content.getText()
						.toString().trim();
				if (TextUtils.isEmpty(equipmentKey)) {
					ToastUtil.showShortToast(QuerySparePartActivity.this,
							"请输入设备名称、编号查询");
					return;
				}
				if(TextUtils.isEmpty(keyword)){
					ToastUtil.showShortToast(QuerySparePartActivity.this, "请输入编号关键信息");
					return;
				}
				QueryEquipmentAndSparePartDao dao=new QueryEquipmentAndSparePartDao(context);
				long equipmentId=dao.queryEquipmentId(equipmentKey);
				if(NetUtils.isNetworkConnected(context)){
					querySparePartTask=new QuerySparePartTask(context,new QuerySparePartTask.TaskCallBack() {
						
						@Override
						public void preExecute() {
							noResult.setVisibility(View.GONE);
							if(!isFinishing()){
								try{
									progressDialog = ProgressDialog.show(context, null, "查询中...",true,true);
									progressDialog.setCanceledOnTouchOutside(false);
									progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
										
										@Override
										public void onCancel(DialogInterface dialog) {
											if(querySparePartTask !=null){
												querySparePartTask.cancel(true);
											}
										}
									});
								}catch(Exception e){
									e.printStackTrace();
								}
							}
						}
						
						@Override
						public void callBackResult(List <QuerySparePart> taskList) {
							if(!isFinishing() && null !=progressDialog && progressDialog.isShowing()){
								try{
									progressDialog.dismiss();
									if(taskList !=null && taskList.size() > 0){
										adapter.setData(taskList);
										adapter.notifyDataSetChanged();
									}else{
										adapter.setData(new ArrayList<QuerySparePart>());
										adapter.notifyDataSetChanged();
										noResult.setVisibility(View.VISIBLE);
									}
									
									
								}catch(Exception e){
									e.printStackTrace();
								}
							}
						}
					});
					String [] args={equipmentId+"",keyword};
					querySparePartTask.execute(args);
				}
			}

			/*@Override
			public void onClick(View v) {
				final String equipmentKey = equipment_content.getText()
						.toString().trim();
				final String sparePartKey = sparePart_content.getText()
						.toString().trim();
				if (TextUtils.isEmpty(equipmentKey)) {
					ToastUtil.showShortToast(QuerySparePartActivity.this,
							"请输入设备名称、编号查询");
					return;
				}
				

				new AsyncTask<String, Void, List<BaseSparePart>>() {
					protected void onPreExecute() {
						noResult.setVisibility(View.GONE);
						setFooterViewVisibility(View.GONE);
					}

					@Override
					protected List<BaseSparePart> doInBackground(
							String... params) {
						List<BaseSparePart> list = null;
						// 获取备品备件
						QueryEquipmentAndSparePartDao dao = new QueryEquipmentAndSparePartDao(
								QuerySparePartActivity.this);
						list = dao.querySparePart(equipmentKey, sparePartKey);
						dao.closeDB();
						return list;
					}

					@Override
					protected void onPostExecute(List<BaseSparePart> list) {
						if (list != null && list.size() > 0) {
							adapter.addData(list);
						} else {
							// noResult.setVisibility(View.VISIBLE);
						}
					}
				};
			}*/
		});
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.header_left:
			finishActivity();
			break;
		}
	}

	@SuppressWarnings("unused")
	private class SparePartAdapter extends CursorAdapter {
		private int columnIndex;
		private DBHelper mDbHelper;
		private SQLiteDatabase db = null;
		//db = mDbHelper.getWritableDatabase();
		
		@SuppressLint("NewApi")
		public SparePartAdapter(Context context, Cursor c, int flags) {
			super(context, c, flags);
			this.columnIndex = flags;
			//DBHelper dbHelper=new DBHelper(context);
			mDbHelper = new DBHelper(context);
			db = mDbHelper.getWritableDatabase();
		}
		
		@Override
		public View newView(Context DBHelper, Cursor cursor, ViewGroup parent) {
			final LayoutInflater inflater = LayoutInflater.from(context);
			final TextView view = (TextView) inflater.inflate(
					R.layout.query_sparepart_equipment_line, parent, false);
					
			return view;
		}
		

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			((TextView) view).setText(cursor.getString(columnIndex) + "  " +cursor.getString(cursor.getColumnIndex("managementOrgNum")));
		}

		@Override
		public String convertToString(Cursor cursor) {
			return cursor.getString(columnIndex) + "  " +cursor.getString(cursor.getColumnIndex("managementOrgNum"));
			//return cursor.getString(columnIndex);
		}

		@Override 
		public Cursor runQueryOnBackgroundThread(CharSequence constraint){
			if(constraint !=null){
				Cursor cr=null;
				String sql="SELECT * FROM " + DBHelper.Base_Equipment_Table_Name +" WHERE equipmentName like '%" + 
				constraint + "%' or managementOrgNum like '%" + constraint +"%' order by equipmentName";
				cr= db.rawQuery(sql, null);
				return cr;
			}
			return null;
		}
	}
}