package com.juchao.upg.android.view;

import com.juchao.upg.android.R;
import com.juchao.upg.android.entity.InspectionTaskEquipmentItem;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 使用Activity 实现对话框 
 * @author xiao-jiang
 *
 */
public class ActivityInspectionItemDetailUtil extends com.juchao.upg.android.ui.BaseActivity{
	
	private static final String TAG = ActivityInspectionItemDetailUtil.class.getSimpleName();
	private Context context;

	private LinearLayout codeLayout, planLayout, equipmentNum, equipmentName,
	itemIndex, itemName, way, expendTime, standard;
	private TextView code_title, plan_title, equipmentNum_title,equipmentName_title, itemIndex_title, itemName_title, way_title,
			expendTime_title, standard_title;
	private TextView code_content, plan_content, equipmentNum_content,equipmentName_content, itemIndex_content, itemName_content,
			way_content, expendTime_content, standard_content;
	
	private String strScanCode,strPlan,strManagementOrgNum,strEquipmentName,strProjectSeq,strProject,strMethod,strJudge;
	private Button btnPrevious,btnNext;
	
	public void onCreated(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dj_task_item_detail_act);
		context=this;
		InspectionTaskEquipmentItem item=(InspectionTaskEquipmentItem) getIntent().getSerializableExtra("inspectionItem");
		
		btnPrevious=(Button) findViewById(R.id.btnPrevious);
		btnNext=(Button) findViewById(R.id.btnNext);
		
		initView();
		
		
		
		
	}
	
	private void initView() {
		codeLayout = (LinearLayout) findViewById(R.id.codeLayout);
		planLayout = (LinearLayout) findViewById(R.id.planLayout);
		equipmentNum = (LinearLayout) findViewById(R.id.equipmentNum);
		equipmentName = (LinearLayout) findViewById(R.id.equipmentName);
		itemIndex = (LinearLayout) findViewById(R.id.itemIndex);
		itemName = (LinearLayout) findViewById(R.id.itemName);
		way = (LinearLayout) findViewById(R.id.way);
		expendTime = (LinearLayout) findViewById(R.id.expendTime);
		standard = (LinearLayout) findViewById(R.id.standard);

		code_title = (TextView) codeLayout.findViewById(R.id.title);
		plan_title = (TextView) planLayout.findViewById(R.id.title);
		equipmentNum_title = (TextView) equipmentNum.findViewById(R.id.title);
		equipmentName_title = (TextView) equipmentName.findViewById(R.id.title);
		itemIndex_title = (TextView) itemIndex.findViewById(R.id.title);
		itemName_title = (TextView) itemName.findViewById(R.id.title);
		way_title = (TextView) way.findViewById(R.id.title);
		expendTime_title = (TextView) expendTime.findViewById(R.id.title);
		standard_title = (TextView) standard.findViewById(R.id.title);

		code_title.setText("条码：");
		plan_title.setText("计划：");
		equipmentNum_title.setText("设备编号：");
		equipmentName_title.setText("设备名称：");
		itemIndex_title.setText("项目序号：");
		itemName_title.setText("项目：");
		way_title.setText("方法：");
		expendTime_title.setText("点检耗时：");
		standard_title.setText("判断基准：");

		code_content = (TextView) codeLayout.findViewById(R.id.content);
		code_content.setText("请扫描点检部位条码");
		plan_content = (TextView) planLayout.findViewById(R.id.content);
		equipmentNum_content = (TextView) equipmentNum.findViewById(R.id.content);
		equipmentName_content = (TextView) equipmentName.findViewById(R.id.content);
		itemIndex_content = (TextView) itemIndex.findViewById(R.id.content);
		itemName_content = (TextView) itemName.findViewById(R.id.content);
		way_content = (TextView) way.findViewById(R.id.content);
		expendTime_content = (TextView) expendTime.findViewById(R.id.content);
		standard_content = (TextView) standard.findViewById(R.id.content);
		
		
	}
}