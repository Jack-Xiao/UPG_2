package com.juchao.upg.android.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.juchao.upg.android.R;
import com.juchao.upg.android.ui.QueryEquipmentActivity;
import com.juchao.upg.android.ui.QuerySparePartActivity;
import com.juchao.upg.android.util.IntentUtil;
import com.juchao.upg.android.view.ImageTextView;


public class QueryFragment extends BaseFragment implements OnClickListener{

	private static final String TAG = QueryFragment.class.getSimpleName();

	private ImageTextView queryEquipment , querySpareparts;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View contentView = inflater.inflate(R.layout.query_fragment, null);
		initView(contentView);
		return contentView;
	}
	
	private void initView(View contentView) {
		
		queryEquipment = (ImageTextView)contentView.findViewById(R.id.query_equipment);
		querySpareparts = (ImageTextView)contentView.findViewById(R.id.query_spareparts);
		
		queryEquipment.setText("查询设备");
		queryEquipment.setIcon(R.drawable.query_equipment);
		querySpareparts.setText("查询备件");
		querySpareparts.setIcon(R.drawable.query_spareparts);
		
		queryEquipment.setOnClickListener(this);
		querySpareparts.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.query_equipment: //查询设备
			IntentUtil.startActivityFromMain(getActivity(), QueryEquipmentActivity.class);
			
			break; 
		case R.id.query_spareparts: //查询备件
			IntentUtil.startActivity(getActivity(), QuerySparePartActivity.class);
			
			break; 
		}
	}

}
