package com.juchao.upg.android.ui;

import java.io.File;
import java.util.List;

import android.R.color;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juchao.upg.android.R;
import com.juchao.upg.android.db.QueryEquipmentAndSparePartDao;
import com.juchao.upg.android.entity.BaseEquipment;
import com.juchao.upg.android.entity.BaseEquipmentAttachment;
import com.juchao.upg.android.util.FileUtil;
import com.juchao.upg.android.view.PopupQueryEquipmentDetailUtil;

public class QueryEquipmentItemActivity extends BaseActivity implements
		OnClickListener, OnScrollListener {
	private static final String TAG = QueryEquipmentItemActivity.class
			.getSimpleName();
	public static final int UPDATE = 0;
	public static final int INSERT =1;
	private long equipmentId;
	private TextView headerLeft, headerTitle, noResult;
	private LinearLayout equipmentInfo, pEquip, useStatus, storage,
			manufacturer, mfgNO, modelNO, base,equipAttachment;
	private TextView equipmentName, equipmentNum;
	private TextView dependenceEquipment_title, useStatus_title,
			curLocation_title, manufacturer_title, mfgNO_title, modelNO_title;
	private TextView dependenceEquipment_content, useStatus_content,
			curLocation_content, manufacturer_content, mfgNO_content,
			modelNO_content;

	private Button btnEquipmentMore;
	private Context context;
	private BaseEquipment currEquipment;
	private int type;
	private List<BaseEquipmentAttachment> list; 
	//private OnClickListener attClickListener;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.query_equipment_item_act);

		headerLeft = (TextView) findViewById(R.id.header_left);
		headerTitle = (TextView) findViewById(R.id.header_title);

		headerLeft.setText("设备列表");
		headerTitle.setText("设备详细");

		context = this;
		equipmentId = getIntent().getLongExtra("equipmentId", 0L);
		
		headerLeft.setOnClickListener(this);
		
		equipAttachment=(LinearLayout) findViewById(R.id.equipAttachment);
		// noResult= (TextView)findViewById(R.id.noResult);

		// explain
		

		

		btnEquipmentMore = (Button) findViewById(R.id.btnEquipmentInfoMore);
		

		btnEquipmentMore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				PopupQueryEquipmentDetailUtil.getInstance().showActionWindow(
						QueryEquipmentItemActivity.this, equipmentId);
			}
		});
		
		initView();
		loadData();
		InitEquipmentAttachment();
	}

	private void initView() {
		equipmentInfo = (LinearLayout) findViewById(R.id.equipmentInfo);
		pEquip = (LinearLayout) findViewById(R.id.pEquip);
		useStatus = (LinearLayout) findViewById(R.id.useStatus);
		storage = (LinearLayout) findViewById(R.id.storage);
		manufacturer = (LinearLayout) findViewById(R.id.manufacturer);
		mfgNO = (LinearLayout) findViewById(R.id.mfgNO);
		modelNO = (LinearLayout) findViewById(R.id.modelNO);

		// equipment

		equipmentName = (TextView) equipmentInfo.findViewById(R.id.title);
		TextPaint tp = equipmentName.getPaint();
		tp.setFakeBoldText(true);

		equipmentNum = (TextView) equipmentInfo.findViewById(R.id.content);

		// title
		dependenceEquipment_title = (TextView) pEquip.findViewById(R.id.title);
		useStatus_title = (TextView) useStatus.findViewById(R.id.title);
		curLocation_title = (TextView) storage.findViewById(R.id.title);
		manufacturer_title = (TextView) manufacturer.findViewById(R.id.title);
		mfgNO_title = (TextView) mfgNO.findViewById(R.id.title);
		modelNO_title = (TextView) modelNO.findViewById(R.id.title);

		dependenceEquipment_title.setText("从属设备：");
		useStatus_title.setText("使用状况：");
		curLocation_title.setText("当前位置：");
		manufacturer_title.setText("生产厂家：");
		mfgNO_title.setText("出厂编号：");
		modelNO_title.setText("型    号：");

		// content
		dependenceEquipment_content = (TextView) pEquip
				.findViewById(R.id.content);
		useStatus_content = (TextView) useStatus.findViewById(R.id.content);
		curLocation_content = (TextView) storage.findViewById(R.id.content);
		manufacturer_content = (TextView) manufacturer
				.findViewById(R.id.content);
		mfgNO_content = (TextView) mfgNO.findViewById(R.id.content);
		modelNO_content = (TextView) modelNO.findViewById(R.id.content);

	}

	private void loadData() {
		new AsyncTask<String, Void, BaseEquipment>() {
			protected void onPreExecute() {
				// noResult.setVisibility(View.GONE);
			}

			@Override
			protected BaseEquipment doInBackground(String... params) {
				QueryEquipmentAndSparePartDao dao = new QueryEquipmentAndSparePartDao(
						QueryEquipmentItemActivity.this);
				BaseEquipment entity = dao.queryEquipment(equipmentId);
				dao.closeDB();
				return entity;
			}

			protected void onPostExecute(BaseEquipment entity) {
				// equipment information.
				currEquipment = entity;
				mHandler.sendEmptyMessage(UPDATE);
			}
		}.execute();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.header_left: // 返回
			
			finishActivity();

			break;
		//case R.id.wxFlowExplain:
		//	type=1;
			
//			System.out.print("点击了   维修");
//			break;
//		//case null:
//			type=2;
//			if(!FileUtil.existExplainFile(equipmentId,type)){
//				
//			}else{
//				
//			}
//			
//			Intent intent=new Intent();
//			Bundle bundle=new Bundle();
//			
//			bundle.putString("OpenMode", "ReadMode");
//			
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			intent.setAction(android.content.Intent.ACTION_VIEW);
//			//intent.setClassName("cn.wps.moffice_eng", "cn.wps.moffice.documentmanager.PreStartActivity2");
//			
//			String path=FileUtil.getRootDir() + "/upg/explain/2.pdf";
//				//Uri uri=Uri.parse(path);
//				Uri uri=Uri.fromFile(new File(path));
//			
////			bundle.putBoolean("SEND_CLOSE_BROAD", true);
//				//bundle.putString("ThirdPackage", "com.juchao.upg.android");
////			bundle.putBoolean("CLEAR_TRACE", true);
////			bundle.putBoolean("CLEAR_BUFFER", true);
////			File file=new File(path);
////			if(file == null || !file.exists()){
////				Log.d("Error ", "this file does not exist");
////			}
//			
//			intent.setDataAndType(uri,"application/pdf");
//			//intent.setData(uri);
//			
//			intent.putExtras(bundle);
//			try{
//				startActivity(intent);
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//			
//			System.out.print("点击了   点检");
//			break;
//		//case R.id.operationExplain:
//			
//			type=3;
//			
//			System.out.print("点击了   操作");
//			break;
		}
	}

	@Override
	public void onResume(){
		super.onResume();
		
	}
	
	@Override
	public void onPause(){
		super.onPause();
		//equipAttachment.removeAllViews();
	}
	private void InitEquipmentAttachment() {
		new AsyncTask<Long,Void,List<BaseEquipmentAttachment>>(){

			@SuppressWarnings("rawtypes")
			@Override
			protected List doInBackground(Long... params) {
				long equipId=params[0];
				QueryEquipmentAndSparePartDao dao = new QueryEquipmentAndSparePartDao(
						QueryEquipmentItemActivity.this);
				List<BaseEquipmentAttachment> atts = dao.queryEquipmentAttachment(equipId);
				
				return atts;
			}
			
			@Override
			protected void onPostExecute(List <BaseEquipmentAttachment> atts) {
				// equipment information.
				//currEquipment = entity;
				//mHandler.sendEmptyMessage(UPDATE);
				list = atts;
				mHandler.sendEmptyMessage(INSERT);
			} 
		}.execute(equipmentId);
	}
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unused")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE:
				equipmentNum.setText(currEquipment.managementOrgNum);
				equipmentName.setText(currEquipment.equipmentName);
				// another information.
				dependenceEquipment_content.setText(currEquipment.pEquip + "");
				useStatus_content.setText(currEquipment.useState + "");
				curLocation_content.setText(currEquipment.storage);
				manufacturer_content.setText(currEquipment.manufacturer);
				mfgNO_content.setText(currEquipment.mfgNO);
				modelNO_content.setText(currEquipment.modelNO);
				break;
			case INSERT:
				
				for(BaseEquipmentAttachment att: list){

					insertTextView(att);
				}
				break;
			}
		}
	};
	
	private OnClickListener attClickListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			if(v !=null){
				PackageManager packageManager =getPackageManager();
				Intent intent = new Intent();
				Bundle bundle=new Bundle();
				bundle.putString("OpenMode", "ReadMode");
				intent.setAction(android.content.Intent.ACTION_VIEW);
				
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				String fileName=v.getTag().toString();
				//Uri uri=Uri.parse(fileName);
				Uri uri=Uri.fromFile(new File(fileName));
				String suffix= fileName.substring(fileName.lastIndexOf("."),fileName.length()).toLowerCase().toString();
				
				if(suffix.equals(".doc") | suffix.equals(".docx" )){
					intent.setDataAndType(uri, "application/msword");
				}else if(suffix.equals(".pdf")) {
					  intent.setDataAndType(uri, "application/pdf");   
				}else if(suffix.equals(".xlsx")){
					intent.setDataAndType(uri, "application/vnd.ms-excel");
				}else if(suffix.equals(".txt")){
					intent.setDataAndType(uri, "text/plain");   
				}else if(suffix.equals(".gif") || suffix.equals(".jpeg") || suffix.equals(".jpg") || suffix.equals(".png") || suffix.equals(".bmp")){
					intent.setDataAndType(uri, "image/*");
				}else if(suffix.equals(".mpa") || suffix.equals(".mpeg") || suffix.equals(".mpg") || suffix.equals(".mpv2") || suffix.equals(".mov") || suffix.equals(".flv") || suffix.equals(".mp4")
						|| suffix.equals(".mp2")|| suffix.equals(".mpe") || suffix.equals(".lsf") || suffix.equals(".asf") || suffix.equals(".asx") || suffix.equals(".avi") ||suffix.equals(".movie")){
					intent.setDataAndType(uri, "video/*");
				}else{
					return;
				}
				
				List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
				if(activities.size()>0)
				startActivity(intent);
			}
		}
	};
	
	private void insertTextView(BaseEquipmentAttachment att){
		TextView mTextView=new TextView(context);
		mTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		mTextView.setText(att.fileName);
		mTextView.setPadding(20, 10, 0, 10);
		mTextView.setTextSize(16);
		mTextView.setTextColor(R.color.black);
		mTextView.setOnClickListener(attClickListener);
		//mTextView.setBackgroundDrawable(R.drawable.textview_selector);
		//mTextView.setBackgroundColor(R.drawable.textview_selector);
		mTextView.setBackgroundResource(R.drawable.textview_selector);
		
		mTextView.setTag(FileUtil.getBaseEquipmentAttachmentDir() +"/"+att.fileName);
		
		int index = equipAttachment.getChildCount();
		if(index > 1){
			equipAttachment.addView(mTextView,index -1);
		}else{
			equipAttachment.addView(mTextView, 0);
		}
	}
	
	

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}

}
