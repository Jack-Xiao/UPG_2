package com.juchao.upg.android.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.juchao.upg.android.R;
import com.juchao.upg.android.ui.DjTaskDownloadActivity;
import com.juchao.upg.android.ui.DjTaskListActivity;
import com.juchao.upg.android.ui.DjTaskUploadActivity;
import com.juchao.upg.android.util.IntentUtil;
import com.juchao.upg.android.view.ImageTextView;
import com.juchao.upg.android.ui.PdTaskListActivity;
import com.juchao.upg.android.ui.PdTaskDownloadActivity;
import com.juchao.upg.android.ui.PdTaskUploadActivity;

public class AccountFragment extends BaseFragment implements OnClickListener{
	private Context mContext;
	private ImageTextView taskDownload,resultUpload,account,taskQuery;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View contentView=inflater.inflate(R.layout.account_fragment, null);
		
		initView(contentView);
		return contentView;
	}

	private void initView(View contentView) {
		taskDownload = (ImageTextView)contentView.findViewById(R.id.task_download);
		resultUpload = (ImageTextView)contentView.findViewById(R.id.result_upload);
		account = (ImageTextView)contentView.findViewById(R.id.account);
		taskQuery = (ImageTextView)contentView.findViewById(R.id.task_query);
		
		taskDownload.setText("任务下载");
		taskDownload.setIcon(R.drawable.dianjian_icon_download);
		resultUpload.setText("结果上传");
		resultUpload.setIcon(R.drawable.dianjian_icon_upload);
		account.setText("盘点扫描");
		account.setIcon(R.drawable.account_barcode);
		taskQuery.setText("盘点查看");
		taskQuery.setIcon(R.drawable.dianjian_icon_query);
		
		taskDownload.setOnClickListener(this);
		resultUpload.setOnClickListener(this);
		account.setOnClickListener(this);
		taskQuery.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.task_download: //任务下载
			IntentUtil.startActivityFromMain(getActivity(), com.juchao.upg.android.ui.PdTaskDownloadActivity.class);
			break;
		case R.id.result_upload: //结果上传
			IntentUtil.startActivityFromMain(getActivity(), PdTaskUploadActivity.class);
			break;
		case R.id.account: //设备点检
			Bundle bundle = new Bundle();
			bundle.putInt("from", PdTaskListActivity.FROM_ACCOUNT);
			IntentUtil.startActivityFromMain(getActivity(), PdTaskListActivity.class , bundle);
			break;
		case R.id.task_query:
			Bundle bundle1 = new Bundle();
			bundle1.putInt("from",PdTaskListActivity.FROM_QUERY );
			IntentUtil.startActivityFromMain(getActivity(), PdTaskListActivity.class,bundle1 );
			break;
		}
	}
}