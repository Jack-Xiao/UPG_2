package com.juchao.upg.android.ui;

import java.util.ArrayList;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.juchao.upg.android.R;
import com.juchao.upg.android.entity.ConfigEntity;
import com.juchao.upg.android.net.upgrade.UpgradeUtils;
import com.juchao.upg.android.util.IntentUtil;

public class ConfigActivity extends BaseActivity implements OnClickListener , OnItemClickListener{

    
    private ListView listView;
    private TextView headerLeft , headerTitle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.config_act);
       
		headerLeft = (TextView)findViewById(R.id.header_left);
		headerTitle = (TextView)findViewById(R.id.header_title);
		headerLeft.setText("设备保全");
		headerTitle.setText("设置");
		listView = (ListView)findViewById(R.id.listView);
		listView.setAdapter(new ConfigAdapter(this));
		listView.setOnItemClickListener(this);
		headerLeft.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		case R.id.header_left:  //返回
			finishActivity();
			break;
		}
		
	}
	
	class ConfigAdapter extends BaseAdapter {
		private ArrayList<ConfigEntity> list;
		private LayoutInflater mLayoutInflater;

		public ConfigAdapter(Context context) {
			list = ConfigEntity.getConfigList();
			mLayoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public ConfigEntity getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mLayoutInflater.inflate(
						R.layout.config_item, null);
				holder.itemName = (TextView) convertView.findViewById(R.id.itemName);
				holder.itemRedPoint = (ImageView) convertView.findViewById(R.id.itemRedPoint);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.itemName.setText(getItem(position).text);
			if(getItem(position).showRed){
				holder.itemRedPoint.setVisibility(View.VISIBLE);
			}else{
				holder.itemRedPoint.setVisibility(View.GONE);
			}
			
			return convertView;
		}
		class ViewHolder {
			TextView itemName;
			ImageView itemRedPoint;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		try{
			if(position == 3){ //检查应用升级
				UpgradeUtils.checkNewVersion(ConfigActivity.this, true);
			}else {
				IntentUtil.startActivity(ConfigActivity.this, clsArray[position]);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private Class<?>[] clsArray = {ServiceAddressActivity.class,MsgRemindActivity.class, 
			UpdateDataActivity.class,null,AboutActivity.class};  

}
