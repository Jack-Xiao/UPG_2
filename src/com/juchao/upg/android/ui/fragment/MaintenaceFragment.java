package com.juchao.upg.android.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.juchao.upg.android.R;
import com.juchao.upg.android.ui.DjTaskListActivity;
import com.juchao.upg.android.ui.WxTaskListActivity;
import com.juchao.upg.android.ui.WxTaskEffectConfirmActivity;
import com.juchao.upg.android.ui.WxTaskEquipmentListActivity;
import com.juchao.upg.android.ui.WxTaskUploadActivity;
import com.juchao.upg.android.util.IntentUtil;
import com.juchao.upg.android.view.ImageTextView;
/**
 * @Date 2014-10-24
 * @author xiao-jiang
 *
 */
public class MaintenaceFragment extends BaseFragment implements OnClickListener{
	
	private static final String TAG=MaintenaceFragment.class.getSimpleName();
	private Context mContext;
	
	private ImageTextView taskDownload,resultUpload,maintenace,taskQuery,effectConfirm;
	 
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		mContext=getActivity();
		View contentView=inflater.inflate(R.layout.maintenace_fragment, null);
		initView(contentView);
		return contentView;
	}
	private void initView(View contentView) {
		taskDownload=(ImageTextView)contentView.findViewById(R.id.maintenace_task_download);
		resultUpload=(ImageTextView)contentView.findViewById(R.id.maintenace_result_upload);
		maintenace=(ImageTextView)contentView.findViewById(R.id.maintenace);
		taskQuery=(ImageTextView)contentView.findViewById(R.id.maintenace_task_query);
		effectConfirm=(ImageTextView)contentView.findViewById(R.id.maintenace_effectconfirm);
		
		taskDownload.setText("任务下载");
		taskDownload.setIcon(R.drawable.weixiu_icon_download);
		
		resultUpload.setText("结果上传");
		resultUpload.setIcon(R.drawable.weixiu_icon_upload);
		
		maintenace.setText("设备维修");
		maintenace.setIcon(R.drawable.weixiu_icon_maintenace);
		//maintenace.setIcon(R.drawable.weixiu_icon_maintenace);
		
		taskQuery.setText("任务查询");
		taskQuery.setIcon(R.drawable.weixiu_icon_query);
		
		effectConfirm.setText("效果确认");
		effectConfirm.setIcon(R.drawable.weixiu_icon_effectconfirm); 
		
		taskDownload.setOnClickListener(this);
		resultUpload.setOnClickListener(this);
		maintenace.setOnClickListener(this);
		taskQuery.setOnClickListener(this); 
		effectConfirm.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.maintenace_task_download:			
			IntentUtil.startActivity(getActivity(),WxTaskListActivity.class);
			break;
		case R.id.maintenace_result_upload:
			IntentUtil.startActivity(getActivity(), WxTaskUploadActivity.class);
			break;
		case R.id.maintenace:
			Bundle bundle = new Bundle();
			//bundle.putInt("from", DjTaskListActivity.From_Equipment_Inspection);
			bundle.putInt("from", WxTaskEquipmentListActivity.From_Equipment_Maintenace);
			IntentUtil.startActivityFromMain(getActivity(), WxTaskEquipmentListActivity.class , bundle);
			break;
		case R.id.maintenace_task_query:
			Bundle bundleQuery=new Bundle();
			bundleQuery.putInt("from", WxTaskEquipmentListActivity.From_Task_Query);
			IntentUtil.startActivity(getActivity(), WxTaskEquipmentListActivity.class, bundleQuery);
			break;
		case R.id.maintenace_effectconfirm:
			IntentUtil.startActivity(getActivity(), WxTaskEffectConfirmActivity.class);
			break;
		}
	}
}
