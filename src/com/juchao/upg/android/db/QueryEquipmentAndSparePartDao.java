package com.juchao.upg.android.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.juchao.upg.android.entity.BaseEquipmentAttachment;
import com.juchao.upg.android.entity.BaseSparePart;
import com.juchao.upg.android.entity.BaseEquipment;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;
import com.juchao.upg.android.util.Log;

public class QueryEquipmentAndSparePartDao {

	private static final String TAG = QueryEquipmentAndSparePartDao.class.getSimpleName();

	private DBHelper mDbHelper;
	private SQLiteDatabase db = null;

	public QueryEquipmentAndSparePartDao(Context context) {
		mDbHelper = new DBHelper(context);
		db = mDbHelper.getWritableDatabase();
	}

	public synchronized List<BaseEquipment> query(String key) {
		List<BaseEquipment> list = null;
		BaseEquipment entity = null;
		Cursor cs = null;
		String args[] = {  };
		String sql;
		try {
			sql="SELECT * FROM " + DBHelper.Base_Equipment_Table_Name + 
					" WHERE (managementOrgNum like '%"+ key+ "%' or equipmentName like '%" +key +"%') order by equipmentName";
			cs = db.rawQuery(sql, args);

			int recordNum = cs.getCount();
			/*int start = (pageNum - 1) * PageSize;
			int index = start;
			int end = PageSize * pageNum - 1;*/
			//cs.moveToPosition(start - 1);
			if (recordNum == 0) {
				return null;
			}

			list = new ArrayList<BaseEquipment>();
			while (cs.moveToNext()) {				 				
				//if (index >= start && index <= end) {
					/*entity = new BaseEquipment();
					entity.id = Long.parseLong(cs.getString(cs
							.getColumnIndex("id")));
					entity.k3NO = cs.getString(cs.getColumnIndex("k3NO"));
					entity.equipmentName = cs.getString(cs
							.getColumnIndex("equipmentName"));

					if(!cs.getString(cs.getColumnIndex("pEquipid")).equals("null")){
						System.out.print(cs.getString(cs.getColumnIndex("pEquipid")));
						Log.w("EquipmentDao ",cs.getString(cs.getColumnIndex("pEquipid")));
						entity.pEquipid = Long.parseLong(cs.getString(cs.getColumnIndex("pEquipid")));
					}else{
						entity.pEquipid=0L;
					}
					
					if(!cs.getString(cs.getColumnIndex("dutyOrgid")).equals("null"))
					{
						entity.dutyOrgid = Long.parseLong(cs.getString(cs.getColumnIndex("dutyOrgid"))); 
					}else{
						entity.dutyOrgid=0L;
					}
					
					entity.dutyUserid = Long.parseLong(cs.getString(cs
							.getColumnIndex("dutyUserid")));
					entity.modelNO = cs.getString(cs.getColumnIndex("modelNO"));
					
					entity.manufacturer = cs.getString(cs
							.getColumnIndex("manufacturer"));
					entity.mfgNO = cs.getString(cs.getColumnIndex("mfgNO"));
					
					entity.supplier = cs.getString(cs
							.getColumnIndex("supplier"));
					entity.purchaseDate = cs.getString(cs
							.getColumnIndex("purchaseDate"));
					entity.useStateid = Long.parseLong(cs.getString(cs
							.getColumnIndex("useStateid")));
					entity.equipmentTypeid = Long.parseLong(cs.getString(cs
							.getColumnIndex("equipmentTypeid")));
					entity.storage = cs.getString(cs.getColumnIndex("storage"));
					entity.remarks = cs.getString(cs.getColumnIndex("remarks"));
					entity.maintainNotice = cs.getString(cs
							.getColumnIndex("maintainNotice"));
					entity.managementOrgNum = cs.getString(cs
							.getColumnIndex("managementOrgNum"));
					entity.equipmentAmount = Integer.parseInt(cs.getString(cs
							.getColumnIndex("equipmentAmount")));
					entity.unitsid = Long.parseLong(cs.getString(cs
							.getColumnIndex("unitsid")));
					entity.delMark = Integer.parseInt(cs.getString(cs
							.getColumnIndex("delMark")));*/

					entity=GetEquipmentInfo(cs);
					list.add(entity);
				/*} else {
					break;
				}*/
				//index++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cs != null && !cs.isClosed()) {
				cs.close();
			}
		}
		return list;
	}

	public synchronized void closeDB() {
		if (db != null && db.isOpen()) {
			db.close();
		}
	}

	public BaseEquipment queryEquipment(long equipmentId) {
		BaseEquipment entity = null;
		String args[] = { equipmentId + "" };
		String sql;
		Cursor cs = null;
		sql = "SELECT * FROM " + DBHelper.Base_Equipment_Table_Name
				+ " WHERE id=?";
		try {

			cs = db.rawQuery(sql, args);
			while (cs.moveToNext()){
				entity=GetEquipmentInfo(cs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cs != null && !cs.isClosed()) {
				cs.close();
			}
		}
		return entity;
	}

	private BaseEquipment GetEquipmentNeedNameInfo(long id){
		BaseEquipment entity = null;
		String args[] = { id + "" };
		String sql;
		Cursor cs = null;
		sql = "SELECT * FROM " + DBHelper.Base_Equipment_Table_Name
				+ " WHERE id=?";
		try {

			cs = db.rawQuery(sql, args);
			while (cs.moveToNext()){
				entity.equipmentName = cs.getString(cs
						.getColumnIndex("equipmentName"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cs != null && !cs.isClosed()) {
				cs.close();
			}
		}
		return entity;
		
	}
	
	private BaseEquipment GetEquipmentInfo(Cursor cs){
		BaseEquipment entity = new BaseEquipment();
		entity.id = Long.parseLong(cs.getString(cs
				.getColumnIndex("id")));
		entity.k3NO = cs.getString(cs.getColumnIndex("k3NO"));
		entity.equipmentName = cs.getString(cs
				.getColumnIndex("equipmentName"));
		//从属设备
		if(!cs.getString(cs.getColumnIndex("pEquipid")).equals("null")){
			System.out.print(cs.getString(cs.getColumnIndex("pEquipid")));
			Log.w("EquipmentDao ",cs.getString(cs.getColumnIndex("pEquipid")));
			entity.pEquipid = Long.parseLong(cs.getString(cs.getColumnIndex("pEquipid")));
			
		}else{
			entity.pEquipid=0L;
			entity.pEquip="";
		}
		
		//责任部门
		if(!cs.getString(cs.getColumnIndex("dutyOrgid")).equals("null") )
		{
			entity.dutyOrgid = Long.parseLong(cs.getString(cs.getColumnIndex("dutyOrgid"))); 
		}else{
			entity.dutyOrgid=0L;
		}
		
		entity.dutyUserid = Long.parseLong(cs.getString(cs
				.getColumnIndex("dutyUserid")));
		entity.modelNO = cs.getString(cs.getColumnIndex("modelNO"));
		
		entity.manufacturer = cs.getString(cs
				.getColumnIndex("manufacturer"));
		entity.mfgNO = cs.getString(cs.getColumnIndex("mfgNO"));
		
		entity.supplier = cs.getString(cs
				.getColumnIndex("supplier"));
		entity.purchaseDate = cs.getString(cs
				.getColumnIndex("purchaseDate"));
		entity.useStateid = Long.parseLong(cs.getString(cs
				.getColumnIndex("useStateid")));
		entity.equipmentTypeid = Long.parseLong(cs.getString(cs
				.getColumnIndex("equipmentTypeid")));
		entity.storage = cs.getString(cs.getColumnIndex("storage"));
		entity.remarks = cs.getString(cs.getColumnIndex("remarks"));
		entity.maintainNotice = cs.getString(cs
				.getColumnIndex("maintainNotice"));
//		entity.managementOrgNum = cs.getString(cs
//				.getColumnIndex("managementOrgNum"));
		entity.managementOrgNum = cs.getString(cs.getColumnIndex("equNum"));
		entity.equipmentAmount = Integer.parseInt(cs.getString(cs
				.getColumnIndex("equipmentAmount")));
		entity.unitsid = Long.parseLong(cs.getString(cs
				.getColumnIndex("unitsid")));
		entity.delMark = Integer.parseInt(cs.getString(cs
				.getColumnIndex("delMark")));
		
		//名称
		if(entity.pEquipid == 0L){
			entity.pEquip="";
		}else{
			if(entity.pEquip != null){
				entity.pEquip=GetEquipmentNeedNameInfo(entity.pEquipid).equipmentName;
			}else{
				entity.pEquip="";
			}
		}
		//使用状况
		entity.units = GetCategoryInfo(entity.unitsid);
		entity.equipmentType=GetCategoryInfo(entity.equipmentTypeid);
		entity.useState=GetCategoryInfo(entity.useStateid);
		
		
		return entity;
	}

	private String GetCategoryInfo(Long useStateid) {
		String result="";
		String sql="SELECT * FROM " + DBHelper.Base_Category_Table_Name + " WHERE id = " + useStateid ;
		Cursor cs=null;
		try {

			cs = db.rawQuery(sql, null);
			while (cs.moveToNext()){
				result=cs.getString(cs.getColumnIndex("name"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cs != null && !cs.isClosed()) {
				cs.close();
			}
		}
		return result;
	}

	public List<BaseSparePart> querySparePart(String equipmentKey,
			String sparePartKey) {
		
		//String token=DefaultShared.getString(Constants.KEY_TOKEN, "");
/*		result = NetAccessor.querySparePartInfo(mContext,token,equipmentId,keyword);
		if(result !=null && result.code == 0 && result){
			
		}*/
		
		
		return null;
	}
	public long queryEquipmentId(String name){
		long id = 0L;
		String [] keyword= name.split("  ");
		String equipName=keyword[0].toString();
		String managementOrgNum=keyword[1].toString();
		Cursor cs=null;
			String sql="SELECT * FROM " + DBHelper.Base_Equipment_Table_Name + 
					" WHERE equipmentName='" + equipName +"' and managementOrgNum='" + managementOrgNum+"' order by equipmentName";
			try{
				cs = db.rawQuery(sql, null);
			while (cs.moveToNext()){
				id=Long.parseLong(cs.getString(cs.getColumnIndex("id")));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(cs !=null && cs.isClosed()){
				cs.close();
			}
		}
		return id;
	}

	public List<BaseEquipmentAttachment> queryEquipmentAttachment(long equipId) {
		Cursor cs=null;
		List<BaseEquipmentAttachment> list = null;
		try{
			list=new ArrayList<BaseEquipmentAttachment>();
			String sql="SELECT * FROM " + DBHelper.Base_Equip_Attachment +
					" WHERE equipId=?";
			cs=db.rawQuery(sql, new String[]{equipId+""});
			while(cs.moveToNext()){
				BaseEquipmentAttachment att=new BaseEquipmentAttachment();
				att.id =Long.parseLong(cs.getString(cs.getColumnIndex("id")));
				att.equipId=Long.parseLong(cs.getString(cs.getColumnIndex("equipId")));
				att.fileId = cs.getString(cs.getColumnIndex("fileId"));
				att.fileName=cs.getString(cs.getColumnIndex("fileName"));
				list.add(att);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(cs!=null && !cs.isClosed()){
				cs.close();
			}
		}
		return list;
	}
}