package com.juchao.upg.android.ui.fragment;


import com.juchao.upg.android.R;
import com.juchao.upg.android.ui.DjTaskDownloadActivity;
import com.juchao.upg.android.ui.DjTaskListActivity;
import com.juchao.upg.android.ui.DjTaskUploadActivity;
import com.juchao.upg.android.util.IntentUtil;
import com.juchao.upg.android.view.ImageTextView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;


public class InspectionFragment extends BaseFragment implements OnClickListener{

	private static final String TAG = InspectionFragment.class.getSimpleName();
	private Context mContext;
	
	private ImageTextView taskDownload , resultUpload , inspection ,taskQuery;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		View contentView = inflater.inflate(R.layout.inspection_fragment, null);
		initView(contentView);
		return contentView;
	}


	private void initView(View contentView) {
		
		taskDownload = (ImageTextView)contentView.findViewById(R.id.task_download);
		resultUpload = (ImageTextView)contentView.findViewById(R.id.result_upload);
		inspection = (ImageTextView)contentView.findViewById(R.id.inspection);
		taskQuery = (ImageTextView)contentView.findViewById(R.id.task_query);
		
		taskDownload.setText("任务下载");
		taskDownload.setIcon(R.drawable.dianjian_icon_download);
		resultUpload.setText("结果上传");
		resultUpload.setIcon(R.drawable.dianjian_icon_upload);
		inspection.setText("设备点检");
		inspection.setIcon(R.drawable.dianjian_icon_inspection);
		taskQuery.setText("任务查询");
		taskQuery.setIcon(R.drawable.dianjian_icon_query);
		
		taskDownload.setOnClickListener(this);
		resultUpload.setOnClickListener(this);
		inspection.setOnClickListener(this);
		taskQuery.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.task_download: //任务下载
			IntentUtil.startActivityFromMain(getActivity(), DjTaskDownloadActivity.class);
			break;
		case R.id.result_upload: //结果上传
			IntentUtil.startActivityFromMain(getActivity(), DjTaskUploadActivity.class);
			
			break;
		case R.id.inspection: //设备点检
			Bundle bundle = new Bundle();
			bundle.putInt("from", DjTaskListActivity.From_Equipment_Inspection);
			IntentUtil.startActivityFromMain(getActivity(), DjTaskListActivity.class , bundle);
			break;
		case R.id.task_query:
			IntentUtil.startActivityFromMain(getActivity(), DjTaskListActivity.class );
			break;
		}
		
	}
	

}
