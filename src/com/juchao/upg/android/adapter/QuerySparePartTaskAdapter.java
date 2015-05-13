package com.juchao.upg.android.adapter;

import java.util.ArrayList;
import java.util.List;

import com.juchao.upg.android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juchao.upg.android.entity.BaseSparePart;
import com.juchao.upg.android.entity.QuerySparePart;
import com.juchao.upg.android.util.Log;

public class QuerySparePartTaskAdapter extends BaseAdapter{
	private List<QuerySparePart> list;
	private LayoutInflater mLayoutInflater;
	private Context context;
	private LinearLayout mLayout;
	private TextView name,modelNo,currentStore;
	
	public QuerySparePartTaskAdapter(Context context){
		this.context=context;
		mLayoutInflater=LayoutInflater.from(context);
		list=new ArrayList<QuerySparePart>();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView=mLayoutInflater.inflate(com.juchao.upg.android.R.layout.query_sparepart_item, null);
			name=(TextView)convertView.findViewById(R.id.name);
			modelNo=(TextView)convertView.findViewById(R.id.modelNo);
			currentStore=(TextView)convertView.findViewById(R.id.currentStore);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		name.setText(list.get(position).name);
		modelNo.setText(list.get(position).modelNo);
		currentStore.setText(list.get(position).currentStore + "");
		return convertView;
	}
	
	class ViewHolder{
		TextView name,modelNo,currentStore;
	}

	public void addData(List<QuerySparePart> list2) {
		if(this.list == null){
			this.list = new ArrayList<QuerySparePart>(); 
		}
		this.list.clear();
		this.list.addAll(list2);
	}
	public void setData(List<QuerySparePart> list3){
		if(this.list == null){
			this.list = new ArrayList<QuerySparePart>(); 
		}
		this.list=list3;
	}
	/*public void clearData(){
		if(this.list == null){
			this.list= new ArrayList<QuerySparePart>();
		}
		this.list.clear();
	}*/
}
