package com.juchao.upg.android.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.juchao.upg.android.entity.BaseCategory;
import com.juchao.upg.android.entity.BaseCommonProblem;
import com.juchao.upg.android.entity.BaseEquipment;
import com.juchao.upg.android.entity.BaseEquipmentAttachment;
import com.juchao.upg.android.entity.BaseEquipmentSpotcheck;
import com.juchao.upg.android.entity.BaseImage;
import com.juchao.upg.android.entity.BaseOperationNotice;
import com.juchao.upg.android.entity.BaseSparePart;
import com.juchao.upg.android.entity.EquipCheckPicture;
import com.juchao.upg.android.entity.EquipmentSparePart;
import com.juchao.upg.android.entity.InspectionTaskEquipmentItem;
import com.juchao.upg.android.entity.MaintenaceTaskEquipmentItem;
import com.juchao.upg.android.entity.ResEquipmentSparePart1;

import java.util.ArrayList;
import java.util.List;

public class BaseDataDao {

	private static final String TAG = InspectionEquipmentDao.class
			.getSimpleName();

	private DBHelper mDbHelper;
	private SQLiteDatabase db = null;

	public BaseDataDao(Context context) {
		mDbHelper = new DBHelper(context);
		db = mDbHelper.getWritableDatabase();
	}

	/**
	 * 插入基础设备数据
	 * 
	 * @return
	 */
	public long insertBaseEquipment(BaseEquipment entity) {
		long result = 0;
		try {
			ContentValues cv = new ContentValues();
			cv.put("id", entity.id + "");
			cv.put("k3NO", entity.k3NO);
			cv.put("equipmentName", entity.equipmentName);
			cv.put("pEquipid", entity.pEquipid + "");
			cv.put("dutyOrgid", entity.dutyOrgid + "");
			cv.put("dutyUserid", entity.dutyUserid + "");
			cv.put("modelNO", entity.modelNO);
			cv.put("manufacturer", entity.manufacturer);
			cv.put("mfgNO", entity.mfgNO);
			cv.put("supplier", entity.supplier);
			cv.put("purchaseDate", entity.purchaseDate);
			cv.put("useStateid", entity.useStateid + "");
			cv.put("equipmentTypeid", entity.equipmentTypeid);
			cv.put("storage", entity.storage);
			cv.put("remarks", entity.remarks);
			cv.put("maintainNotice", entity.maintainNotice);
			cv.put("managementOrgNum", entity.managementOrgNum);
			cv.put("equipmentAmount", entity.equipmentAmount);
			cv.put("unitsid", entity.unitsid + "");
			cv.put("updateTime", entity.updateTime);
			cv.put("delMark", entity.delMark);
			if(String.valueOf(entity.equNum) == "null"){
				cv.put("equNum", "");
			}else{
				cv.put("equNum", entity.equNum);
			}
			
			result = db.insert(DBHelper.Base_Equipment_Table_Name, null, cv);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 删除基础设备数据
	 * 
	 * @return
	 */
	public boolean deleteBaseEquipment() {
		try {
			String deleteInfoSqlString = "DELETE  FROM "
					+ DBHelper.Base_Equipment_Table_Name;
			db.execSQL(deleteInfoSqlString);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 插入基础备件数据
	 * 
	 * @return
	 */
	public long insertBaseSparePart(BaseSparePart entity) {
		long result = 0;
		try {
			ContentValues cv = new ContentValues();
			cv.put("id", entity.id + "");
			cv.put("name", entity.name);
			cv.put("modelNo", entity.modelNo);
			cv.put("replaceModelNo", entity.replaceModelNo);
			cv.put("makeCompany", entity.makeCompany);
			cv.put("provider", entity.provider);
			cv.put("maxStore", entity.maxStore);
			cv.put("minStore", entity.minStore);
			cv.put("currentStore", entity.currentStore);
			cv.put("goodsDate", entity.goodsDate);
			cv.put("storeAddr", entity.storeAddr);
			cv.put("updateTime", entity.updateTime);
			cv.put("delMark", entity.delMark);
			cv.put("description", entity.description);

			result = db.insert(DBHelper.Base_SparePart_Table_Name, null, cv);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 删除基础设备数据
	 * 
	 * @return
	 */
	public boolean deleteBaseSparePart() {
		try {
			String deleteInfoSqlString = "DELETE  FROM "
					+ DBHelper.Base_SparePart_Table_Name;
			db.execSQL(deleteInfoSqlString);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 插入基础点检项目数据
	 * 
	 * @return
	 */
	public long insertBaseEquipmentSpotcheck(BaseEquipmentSpotcheck entity) {
		long result = 0;
		try {
			ContentValues cv = new ContentValues();
			cv.put("id", entity.id + "");
			cv.put("name", entity.name);
			cv.put("checkGroupmark", entity.checkGroupmark);
			cv.put("projectTypeid", entity.projectTypeid + "");
			cv.put("projectNO", entity.projectNO);
			cv.put("checkType", entity.checkType);
			cv.put("iluLv", entity.iluLv);
			cv.put("checkMethod", entity.checkMethod);
			cv.put("judgementStandard", entity.judgementStandard);
			cv.put("checkedEquipId", entity.checkedEquipId + "");
			cv.put("updateTime", entity.updateTime);
			cv.put("delMark", entity.delMark);
			cv.put("costPlanTime", entity.costPlanTime);

			result = db.insert(DBHelper.Base_Equipment_Spotcheck_Table_Name,
					null, cv);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 根据扫描条码，查询项目详情信息。
	 * 
	 * @param scanCode
	 * @return
	 */
	public InspectionTaskEquipmentItem queryBaseEquipmentItem(String scanCode) {

		InspectionTaskEquipmentItem itemDetail = null;
		Cursor cs = null;
		try {
			String args[] = { scanCode };
			cs = db.rawQuery("SELECT * FROM "
					+ DBHelper.Base_Equipment_Spotcheck_Table_Name
					+ " WHERE projectNO = ? ", args);
			if (cs.getCount() > 0) {
				cs.moveToFirst();
				itemDetail = new InspectionTaskEquipmentItem();
				itemDetail.equipmentSpotcheckId = Long.parseLong(cs
						.getString(cs.getColumnIndex("id")));
				itemDetail.equipmentSpotcheckName = cs.getString(cs
						.getColumnIndex("name"));
				itemDetail.equipmentId = Long.parseLong(cs.getString(cs
						.getColumnIndex("checkedEquipId")));
				itemDetail.checkMethod = cs.getString(cs
						.getColumnIndex("checkMethod"));
				itemDetail.judgementStandard = cs.getString(cs
						.getColumnIndex("judgementStandard"));
				itemDetail.costPlanTime =cs.getLong(cs.getColumnIndex("costPlanTime"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cs != null && !cs.isClosed()) {
				cs.close();
			}
		}
		return itemDetail;
	}
	
	public InspectionTaskEquipmentItem queryBaseEquipmentItem(long id) {

		InspectionTaskEquipmentItem itemDetail = null;
		Cursor cs = null;
		try {
			String args[] = { id+"" };
			cs = db.rawQuery("SELECT * FROM "
					+ DBHelper.Base_Equipment_Spotcheck_Table_Name
					+ " WHERE id = ? ", args);
			if (cs.getCount() > 0) {
				cs.moveToFirst();
				itemDetail = new InspectionTaskEquipmentItem();
//				itemDetail.equipmentSpotcheckId = Long.parseLong(cs
//						.getString(cs.getColumnIndex("id")));
				itemDetail.equipmentSpotcheckId=id;
				itemDetail.equipmentSpotcheckName = cs.getString(cs
						.getColumnIndex("name"));
				itemDetail.equipmentId = Long.parseLong(cs.getString(cs
						.getColumnIndex("checkedEquipId")));
				itemDetail.checkMethod = cs.getString(cs
						.getColumnIndex("checkMethod"));
				itemDetail.judgementStandard = cs.getString(cs
						.getColumnIndex("judgementStandard"));
				itemDetail.costPlanTime =cs.getLong(cs.getColumnIndex("costPlanTime"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cs != null && !cs.isClosed()) {
				cs.close();
			}
		}
		return itemDetail;
	}

	/**
	 * 删除基础点检项目数据
	 * 
	 * @return
	 */
	public boolean deleteBaseEquipmentSpotcheck() {
		try {
			String deleteInfoSqlString = "DELETE  FROM "
					+ DBHelper.Base_Equipment_Spotcheck_Table_Name;
			db.execSQL(deleteInfoSqlString);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 插入常见问题的数据
	 * 
	 * @return
	 */
	public long insertBaseCommonProblem(BaseCommonProblem entity) {
		long result = 0;
		try {
			ContentValues cv = new ContentValues();
			cv.put("id", entity.id + "");
			cv.put("equipmentId", entity.equipmentId + "");
			cv.put("equipmentSpotcheckId", entity.equipmentSpotcheckId + "");
			cv.put("faultDescribe", entity.faultDescribe);
			cv.put("createTime", entity.createTime);
			cv.put("updateTime", entity.updateTime);

			result = db.insert(DBHelper.Base_Common_Problem_Table_Name, null,
					cv);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 删除常见问题的数据
	 * 
	 * @return
	 */
	public boolean deleteBaseCommonProblem() {
		try {
			String deleteInfoSqlString = "DELETE  FROM "
					+ DBHelper.Base_Common_Problem_Table_Name;
			db.execSQL(deleteInfoSqlString);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 获取20条常见问题
	 * 
	 * @return state
	 */
	public synchronized List<BaseCommonProblem> queryBaseCommonProblemList() {

		List<BaseCommonProblem> list = null;
		BaseCommonProblem entity = null;
		Cursor cs = null;
		try {
			cs = db.rawQuery("SELECT * FROM "
					+ DBHelper.Base_Common_Problem_Table_Name, null);

			if (cs.getCount() > 0) {
				int recordNum = 0;
				list = new ArrayList<BaseCommonProblem>();
				while (cs.moveToNext()) {
					if (recordNum < 20) {
						entity = new BaseCommonProblem();
						entity.id = Long.parseLong(cs.getString(cs
								.getColumnIndex("id")));
						entity.equipmentId = Long.parseLong(cs.getString(cs
								.getColumnIndex("equipmentId")));
						entity.equipmentSpotcheckId = Long
								.parseLong(cs.getString(cs
										.getColumnIndex("equipmentSpotcheckId")));
						entity.faultDescribe = cs.getString(cs
								.getColumnIndex("faultDescribe"));
						entity.createTime = cs.getString(cs
								.getColumnIndex("createTime"));
						entity.updateTime = cs.getString(cs
								.getColumnIndex("updateTime"));
						list.add(entity);
					} else {
						break;
					}
					recordNum++;
				}
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

	//处理异步线程关闭数据库。
	public synchronized void closeDB() {
		if (db != null && db.isOpen()) {
			db.close();
		}
	}

	/**
	 * 维修 根据扫描条码，查询项目详情信息。
	 * 
	 * @param scanCode
	 * @return
	 */
	public MaintenaceTaskEquipmentItem queryMaintenaceBaseEquipmentItem(
			String scanCode) {

		MaintenaceTaskEquipmentItem itemDetail = null;
		Cursor cs = null;
		/*
		 * try { String args[] = { scanCode }; cs = db.rawQuery("SELECT * FROM "
		 * + DBHelper.Base_Equipment_Spotcheck_Table_Name +
		 * " WHERE projectNO = ? ", args); if (cs.getCount() > 0) {
		 * cs.moveToFirst(); itemDetail = new MaintenaceTaskEquipmentItem();
		 * itemDetail.equipmentSpotcheckId =
		 * Long.parseLong(cs.getString(cs.getColumnIndex("id")));
		 * itemDetail.equipmentSpotcheckName =
		 * cs.getString(cs.getColumnIndex("name")); itemDetail.equipmentId =
		 * Long.parseLong(cs.getString(cs.getColumnIndex("checkedEquipId")));
		 * itemDetail.checkMethod =
		 * cs.getString(cs.getColumnIndex("checkMethod"));
		 * itemDetail.judgementStandard =
		 * cs.getString(cs.getColumnIndex("judgementStandard")); } } catch
		 * (Exception e) { e.printStackTrace(); } finally { if(cs != null &&
		 * !cs.isClosed()){ cs.close(); } }
		 */
		try {
			String args[] = { scanCode };
			cs = db.rawQuery("SELECT * FROM "
					+ DBHelper.Base_Equipment_Table_Name
					+ " WHERE managementOrgNum=? ", args);
			if (cs.getCount() > 0) {
				cs.moveToFirst();
				itemDetail = new MaintenaceTaskEquipmentItem();
				itemDetail.equipmentTDC = cs.getString(cs
						.getColumnIndex("managementOrgNum"));
				itemDetail.equipmentId = Long.parseLong(cs.getString(cs
						.getColumnIndex("id")));
				itemDetail.managementOrgNum = cs.getString(cs
						.getColumnIndex("managementOrgNum"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cs != null && !cs.isClosed()) {
				cs.close();
			}
		}
		return itemDetail;
	}

	public void checkEmptyMaintenaceEquipmentInfo() {
		String sql = "SELECT * FROM "
				+ DBHelper.Maintenace_Equipment_Table_Name
				+ " WHERE equipmentName=''";
		Cursor cs = null;
		String args[] = null;

		try {
			cs = db.rawQuery(sql, args);
			if (cs.getCount() > 0) {
				while (cs.moveToNext()) {
					args = null;
					long id = Long.parseLong(cs.getString(cs
							.getColumnIndex("equipmentId")));
					String equipmentName = queryEquipmentName(id);
					String args1[] = { id + "" };
					sql = "update  " + DBHelper.Maintenace_Equipment_Table_Name
							+ " set equipmentName='" + equipmentName
							+ "' WHERE equipmentId=?";

					db.execSQL(sql, args1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cs != null && !cs.isClosed()) {
				cs.close();
			}
		}
	}

	private String queryEquipmentName(long id) {
		String args[] = { id + "" };
		String sql = "SELECT * FROM " + DBHelper.Base_Equipment_Table_Name
				+ " Where id=?";
		Cursor cs = null;
		String name = "";
		try {
			cs = db.rawQuery(sql, args);
			if (cs.getCount() > 0) {
				while (cs.moveToNext()) {
					name = cs.getString(cs.getColumnIndex("equipmentName"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cs != null && !cs.isClosed()) {
				cs.close();
			}
		}
		return name;
	}

	public void checkEmptyInspectionEquipmenInfo() {
		String sql = "SELECT * FROM "
				+ DBHelper.Inspection_Equipment_Table_Name
				+ " WHERE equipmentName=''";
		Cursor cs = null;
		String args[] = null;

		try {
			cs = db.rawQuery(sql, args);
			if (cs.getCount() > 0) {
				while (cs.moveToNext()) {
					args = null;
					long id = Long.parseLong(cs.getString(cs
							.getColumnIndex("equipmentId")));
					String equipmentName = queryEquipmentName(id);
					sql = "update  " + DBHelper.Inspection_Equipment_Table_Name
							+ " set equipmentName='" + equipmentName
							+ "' WHERE equipmentId=?";
					String args1[] = { id + "" };
					db.execSQL(sql, args1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cs != null && !cs.isClosed()) {
				cs.close();
			}
		}
	}

	public String queryEquipmentItemName(long id) {
		String itemName = "";
		Cursor cs = null;
		try {
			String args[] = { id + "" };
			cs = db.rawQuery("SELECT * FROM "
					+ DBHelper.Base_Equipment_Spotcheck_Table_Name
					+ " WHERE id = ? ", args);

			if (cs.getCount() > 0) {
				cs.moveToFirst();
				itemName = cs.getString(cs.getColumnIndex("name"));

				Log.d(TAG, "queryEquipmentItem > itemName : " + itemName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cs != null && !cs.isClosed()) {
				cs.close();
			}
		}
		return itemName;
	}

	public void checkEmptyInspectionProjectInfo() {
		String sql = "SELECT * FROM " + DBHelper.Inspection_Item_Table_Name
				+ " WHERE equipmentSpotcheckName=''";
		Cursor cs = null;
		String args[] = null;

		try {
			cs = db.rawQuery(sql, args);
			if (cs.getCount() > 0) {
				while (cs.moveToNext()) {
					args = null;
					long id = Long.parseLong(cs.getString(cs
							.getColumnIndex("equipmentSpotcheckId")));
					String equipmentSpotcheckName = queryEquipmentItemName(id);
					sql = "update  " + DBHelper.Inspection_Item_Table_Name
							+ " set equipmentSpotcheckName='" + equipmentSpotcheckName + "'"
							+ " WHERE equipmentSpotcheckId=?";
					String args1[] = { id + "" };
					db.execSQL(sql, args1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cs != null && !cs.isClosed()) {
				cs.close();
			}
		}
	}

	public boolean deleteEquipmentSparePartRelation() {
		try {
			String deleteInfoSqlString = "DELETE  FROM "
					+ DBHelper.Base_Equipment_SparePart_Table_Name;
			db.execSQL(deleteInfoSqlString);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public boolean deleteBaseCategory() {
		try {
			String deleteInfoSqlString = "DELETE  FROM "
					+ DBHelper.Base_Category_Table_Name;
			db.execSQL(deleteInfoSqlString);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public long insertEquipmentSparePartRelation(EquipmentSparePart entity) {
		long result = 0;
		try {
			ContentValues cv = new ContentValues();
			cv.put("id", entity.id + "");
			cv.put("equid", entity.equid + "");
			cv.put("spapartid", entity.spapartid + "");
			result = db.insert(DBHelper.Base_Equipment_SparePart_Table_Name, null,
					cv);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public long insertBaseCategory(BaseCategory entity){
		long result = 0;
		try{
			ContentValues cv=new ContentValues();
			cv.put("id", entity.id);
			cv.put("name", entity.name);
			cv.put("parentid", entity.parentid);
			result=db.insert(DBHelper.Base_Category_Table_Name, null, cv);
		}catch(SQLException e){
			e.printStackTrace();
		}
		return result;
		
	}

	public boolean deleteBaseImage() {
		try{
			String sql="DELETE FROM "
					+DBHelper.Base_Image_Table_Name;
			db.execSQL(sql);
		}catch(Exception e){
			return false;
		}
		return true;
	}

	public long insertBaseImage(BaseImage image) {
		long result = 0;
		try{
			String [] args=image.id.split(",");
			for(int i =0;i<args.length;i++){
				
				ContentValues cv=new ContentValues();
				cv.put("id", args[i].toString());
				//cv.put("url", args[i].toString()); 
				result=db.insert(DBHelper.Base_Image_Table_Name, null, cv);
			}
			
		}catch(SQLException e){
			e.printStackTrace();
		}
		return result;
	}

	public boolean deleteBaseOperationNotice() {
		try{
			String sql = "DELETE FROM "
					+DBHelper.Base_Operation_Notice;
			db.execSQL(sql);
		}catch(Exception e){
			return false;
		}
		return true;
	}

	public long insertBaseOperationNotice(BaseOperationNotice notice) {
		long result = 0;
		try{
			ContentValues cv=new ContentValues();
			cv.put("id", notice.id);
			cv.put("equipmentId", notice.equipmentId);
			cv.put("type", notice.type);
			cv.put("notice", notice.notice);
			result=db.insert(DBHelper.Base_Operation_Notice, null,cv);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}

	public boolean deleteEquipCheckPicture() {
		try{
			String sql="DELETE FROM "
					+DBHelper.Base_Equip_Check_Picture;
			db.execSQL(sql);
		}catch(Exception e){
			return false;
		}
	
		return true;
	}

	public long insertBaseEquipCheckPicture(EquipCheckPicture entity) {
		long result = 0;
		try{
			ContentValues cv=new ContentValues();
			cv.put("id", entity.id);
			cv.put("pictureId", entity.pictureId);
			cv.put("checksubjectId", entity.checksubjectId);
			result=db.insert(DBHelper.Base_Equip_Check_Picture, null,cv);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}

	public List<BaseImage> queryBaseImage() {
		List<BaseImage> list=new ArrayList<BaseImage>();
		Cursor cs=null;
		try{
			String sql="SELECT * FROM "+DBHelper.Base_Image_Table_Name;
			cs=db.rawQuery(sql, null);
			if(cs.getCount() > 0){
				while(cs.moveToNext()){
					BaseImage base=new BaseImage();
					base.id=cs.getString(cs.getColumnIndex("id"));
					//base.url=cs.getString(cs.getColumnIndex("url"));
					list.add(base);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
			if(cs !=null && !cs.isClosed()){
				cs.close();
			}
		}
		return list;
	}

	public void insertBaseImageId(EquipCheckPicture entity) {
		
		try{
			ContentValues cv=new ContentValues();
			cv.put("id", entity.pictureId);
			db.insert(DBHelper.Base_Image_Table_Name, null, cv);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void deleteBaseEquipmentAttachment() {
		try{
			db.execSQL("DELETE FROM "
					+ DBHelper.Base_Equip_Attachment);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void insertBaseEquipmentAttachment(BaseEquipmentAttachment attachment) {
		try{
			ContentValues cv=new ContentValues();
			cv.put("id",attachment.id);
			cv.put("equipId", attachment.equipId);
			cv.put("fileId", attachment.fileId);
			cv.put("fileName",attachment.fileName);
			db.insert(DBHelper.Base_Equip_Attachment, null, cv);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	public List<BaseEquipmentAttachment> queryBaseEquipmentAttachment() {
		List <BaseEquipmentAttachment> list = new ArrayList<BaseEquipmentAttachment>();
		Cursor cr=null;
		try{
			String sql="SELECT * FROM " + DBHelper.Base_Equip_Attachment;
			cr=db.rawQuery(sql, null);
			while(cr.moveToNext()){
				BaseEquipmentAttachment atth = new BaseEquipmentAttachment();
				atth.fileId = cr.getString(cr.getColumnIndex("fileId"));
				atth.fileName = cr.getString(cr.getColumnIndex("fileName"));
				list.add(atth);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(cr !=null && !cr.isClosed()){
				cr.close();
			}
		}
		return list;
	}
	public List<ResEquipmentSparePart1> queryBaseEquipmentSparPart(long equId){
		List<ResEquipmentSparePart1> list = new ArrayList<ResEquipmentSparePart1>();
		Cursor cs=null;
		try{
			String sql="SELECT * FROM "+ DBHelper.Base_Equipment_SparePart_Table_Name 
						+" a join "+DBHelper.Base_SparePart_Table_Name + " b on a.spapartid = b.id WHERE equid=?";
			cs=db.rawQuery(sql, new String[]{equId+""});
			while(cs.moveToNext()){
				ResEquipmentSparePart1 entity = new ResEquipmentSparePart1();
				entity.spapartid = Long.parseLong(cs.getString(cs.getColumnIndex("spapartid")));
				entity.name = cs.getString(cs.getColumnIndex("name"));
				list.add(entity);
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

	public String querySparePartName(long id) {
		String name="";
		String sql = "SELECT * FROM " + DBHelper.Base_SparePart_Table_Name 
						+" WHERE id=?";
		Cursor cs=null;
		try{
			cs=db.rawQuery(sql,new String[]{id +""});
			while(cs.moveToNext()){
				name=cs.getString(cs.getColumnIndex("name"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(cs !=null && !cs.isClosed()){
				cs.close();
			}
		}
		return name;
	}

	public String updateSparepartcontent(long equipmentId,
			String sparepartContent) {
		String sql="SELECT * FROM "+ DBHelper.Base_Equipment_SparePart_Table_Name 
				+" a join "+DBHelper.Base_SparePart_Table_Name + " b on a.spapartid = b.id WHERE equid=?";
		Cursor cs=null;
		try{
			cs=db.rawQuery(sql, new String[]{equipmentId+""});
			while(cs.moveToNext()){
				sparepartContent=sparepartContent.replace(cs.getString(cs.getColumnIndex("spapartid")), cs.getString(cs.getColumnIndex("name")));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(cs!=null && cs.isClosed()){
				cs.close();
			}
		}
		return sparepartContent;
	}
}