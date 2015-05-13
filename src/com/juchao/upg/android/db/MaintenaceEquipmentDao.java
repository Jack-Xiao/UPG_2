package com.juchao.upg.android.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.juchao.upg.android.entity.MaintenaceTaskEquipment;
import com.juchao.upg.android.entity.MaintenaceTaskEquipmentItem;
import com.juchao.upg.android.entity.MaintenaceTaskEquipmentProblem;
import com.juchao.upg.android.util.ConstantUtil;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaintenaceEquipmentDao {

	private static final String TAG = MaintenaceEquipmentDao.class
			.getSimpleName();
	private static final int DownloadMaintenaceState = 0;
	private static final int HasDownloadMaintenaceState =1;
	private DBHelper mDbHelper;
	private SQLiteDatabase db = null;

	public MaintenaceEquipmentDao(Context context) {
		mDbHelper = new DBHelper(context);
		db = mDbHelper.getWritableDatabase();
	}
	
	/*public long insertEquipment(MaintenaceTaskEquipment entity) {
		long result = 0;
		try {
			ContentValues cv = new ContentValues();
			cv.put("id", entity.id);
			// cv.put("maintenaceTaskId", entity.maintenaceTaskId);
			// test: +++ @20141104
			cv.put("maintenaceTaskId", "1413894774588");
			cv.put("equipmentId", entity.equipmentId);
			cv.put("equipmentName", queryEquipmentName(entity.equipmentId));
			cv.put("type", entity.type);

			result = db.insert(DBHelper.Maintenace_Equipment_Table_Name, null,
					cv);

			if (entity.maintenaceTaskEquipmentProblems != null
					&& entity.maintenaceTaskEquipmentProblems.size() > 0) {
				for (MaintenaceTaskEquipmentProblem item : entity.maintenaceTaskEquipmentProblems) {
					ContentValues cvs = new ContentValues();
					cvs.put("id", item.id + "");
					// cvs.put("maintenaceTaskEquipmentId",
					// item.maintenaceTaskEquipmentId + "");
					cvs.put("mainteanceTaskEquipmentId", entity.equipmentId
							+ "");
					cvs.put("result", item.state + "");
					cvs.put("startTime", item.startTime + "");
					cvs.put("endTime", item.endTime + "");
					db.insert(DBHelper.Maintenace_Item_Table_Name, null, cvs);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}*/

	private String queryEquipmentName(long equipmentId) {
		String equipmentName = "";
		Cursor cs = null;
		try {

			String args[] = { equipmentId + "" };
			cs = db.rawQuery("SELECT * FROM "
					+ DBHelper.Base_Equipment_Table_Name + " WHERE id= ? ",
					args);
			if (cs.getCount() > 0) {
				cs.moveToFirst();
				equipmentName = cs
						.getString(cs.getColumnIndex("equipmentName"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cs != null && !cs.isClosed()) {
				cs.close();
			}
		}
		return equipmentName;
	}

	public void closeDB() {
		if (db != null && db.isOpen()) {
			db.close();
		}
	}

	public String queryEquipmentOperationNotice(long equipmentId) {
		String maintainNotice = "";
		Cursor cs = null;
		try {
			String args[] = { equipmentId + "" };
			cs = db.rawQuery("SELECT * FROM "
					+ DBHelper.Base_Equipment_Table_Name + " WHERE id = ? ",
					args);
			if (cs.getCount() > 0) {
				cs.moveToFirst();
				maintainNotice = cs.getString(cs
						.getColumnIndex("maintainNotice"));
			}

		} catch (Exception e) {

		} finally {
			if (cs != null && !cs.isClosed()) {
				cs.close();
			}
		}
		return maintainNotice;

	}

	public int queryItemSizeById(long id, int state) {
		int count = 0;
		Cursor cs = null;
		try {
			if (state == 0) {
				String args[] = { id + "" };
				cs = db.rawQuery("SELECT * FROM "
						+ DBHelper.Maintenace_Item_Table_Name
						+ " WHERE maintenaceTaskEquipmentId = ? ", args);

			} else if (state == 1) {
				String args[] = { id + "", "1" };
				cs = db.rawQuery(
						"SELECT * FROM "
								+ DBHelper.Maintenace_Item_Table_Name
								+ " WHERE maintenaceTaskEquipmentId = ? AND state = ? ",
						args);

			} else if (state == 2) {
				String args[] = { id + "", "2" };
				cs = db.rawQuery("SELECT * FROM "
						+ DBHelper.Maintenace_Item_Table_Name
						+ " WHERE maintenaceTaskEquipmentId = ? AND state = ?",
						args);
			} else if (state == 3) {
				String[] args = { id + "", "3" };
				cs = db.rawQuery("SELECT * FROM "
						+ DBHelper.Maintenace_Item_Table_Name
						+ " WHERE maintenaceTaskEquipmentId = ? AND state = ?",
						args);
			}
			count = cs.getCount();
			count = (count < 0) ? 0 : count;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cs != null && !cs.isClosed()) {
				cs.close();
			}
		}
		return count;
	}

	/**
	 * 根据维修设备Id, 查询该维修设备的项目列表
	 * 
	 * @param maintenaceTaskEquipmentId
	 * @param state
	 *            0:全部 1:未维修 2:已维修 未确认 3:已确认 4:完成
	 * @return
	 */

	public List<MaintenaceTaskEquipmentItem> queryMaintenaceItemList(
			long maintenaceTaskEquipmentId, int state) {
		List<MaintenaceTaskEquipmentItem> list = null;
		MaintenaceTaskEquipmentItem entity = null;
		Cursor cs = null;
		
		try {
			if (state == 0) {
				String args[] = { maintenaceTaskEquipmentId + "" };
				cs = db.rawQuery("SELECT * FROM "
						+ DBHelper.Maintenace_Item_Table_Name
						+ " WHERE maintenaceTaskEquipmentId = ?", args);
			} else if (state == 1) {
				String args[] = { maintenaceTaskEquipmentId + "", "1" };
				cs = db.rawQuery("SELECT * FROM "
						+ DBHelper.Maintenace_Item_Table_Name
						+ " WHERE maintenaceTaskEquipmentId = ? AND state=? ",
						args);
			} else if (state == 2) {
				String args[] = { maintenaceTaskEquipmentId + "", "2" };
				cs = db.rawQuery("SELECT * FROM "
						+ DBHelper.Maintenace_Item_Table_Name
						+ " WHERE maintenaceTaskEquipmentId = ? AND state =?",
						args);
			} else if (state == 3) {
				String args[] = { maintenaceTaskEquipmentId + "", "3" };
				cs = db.rawQuery(
						"SELECT * FROM "
								+ DBHelper.Maintenace_Item_Table_Name
								+ " WHERE maintenaceTaskEquipmentId = ? AND state = ? ",
						args);
			}
			if (cs.getCount() > 0) {
				list = new ArrayList<MaintenaceTaskEquipmentItem>();
				while (cs.moveToNext()) {
					entity = new MaintenaceTaskEquipmentItem();
					
					entity.id = Long.parseLong(cs.getString(cs
							.getColumnIndex("id")));
					entity.problemName=cs.getString(cs.getColumnIndex("problemName"));
					if(cs.getString(cs.getColumnIndex("problemDescribe")) !=null){
						entity.problemDescribe=cs.getString(cs.getColumnIndex("problemDescribe"));
					}
					else{
						entity.problemDescribe="";
					}
					
					//entity.problemDescribe=cs.getString(cs.getColumnIndex("problemDescribe"));
					
					/**entity.maintenaceTaskEquipmentId = Long
							.parseLong(cs.getString(cs
									.getColumnIndex("maintenaceTaskEquipmentId")));
					
					entity.equipmentSpotcheckId = Long.parseLong(cs
							.getString(cs
									.getColumnIndex("equipmentSpotcheckId")));
					entity.equipmentSpotcheckName = cs.getString(cs
							.getColumnIndex("equipmentSpotcheckName"));
					entity.result = cs.getString(cs.getColumnIndex("result"));
					entity.faultDescribe = cs.getString(cs
							.getColumnIndex("faultDescribe"));
					entity.state = cs.getInt(cs.getColumnIndex("state"));
					*/
					 entity.state=cs.getInt(cs.getColumnIndex("state"));
					 entity.uploadState=cs.getInt(cs.getColumnIndex("uploadState"));
					 list.add(entity);
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

	/**
	 * 根据任务Id, 查询维修设备列表(做上传时， 需增加状态判断)
	 * 
	 * @param id
	 * @return
	 */
	public List<MaintenaceTaskEquipment> queryEquipmentList(long id,int from) {
		List<MaintenaceTaskEquipment> list = null;
		MaintenaceTaskEquipment entity = null;
		Cursor cs = null;
		long userId=DefaultShared.getLong(Constants.KEY_USER_ID, 0l);
		try {
			String args[] = { id + "", 0 + ""};
			cs = db.rawQuery("SELECT * FROM "
					+ DBHelper.Maintenace_Equipment_Table_Name
					+ " WHERE maintenaceTaskId = ? AND maintenaceState = ? and userId=" + userId,
					args);
			if (cs.getCount() > 0) {
				list = new ArrayList<MaintenaceTaskEquipment>();
				while (cs.moveToNext()) {
					entity = new MaintenaceTaskEquipment();
					entity.id = Long.parseLong(cs.getString(cs
							.getColumnIndex("id")));
					int unComplementItemNum = queryItemSizeById(entity.id, 1);
					// test: @20141104 暂时关闭此项检测
					// 如果该纵设备下，没有未维修的项目， 则不返回该维修设备。
					/*
					 * if(unComplementItemNum == 0 ) { continue; }
					 */
					entity.maintenaceTaskId = Long.parseLong(cs.getString(cs
							.getColumnIndex("maintenaceTaskId")));
					entity.equipmentId = Long.parseLong(cs.getString(cs
							.getColumnIndex("equipmentId")));
					entity.type = cs.getString(cs.getColumnIndex("type"));
					entity.maintenaceState = cs.getInt(cs
							.getColumnIndex("maintenaceState"));
					entity.timeList = cs.getInt(cs.getColumnIndex("timeList"));
					entity.equipmentName = cs.getString(cs
							.getColumnIndex("equipmentName"));
					list.add(entity);
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

	/**
	 * 效果确认时  默认设备内没有该任务。。。。 此处需要更新。 task:@20141106
	 * @param entity
	 * @param taskId
	 * @param wxTaskState
	 * @return
	 */
	public long insertEquipment(MaintenaceTaskEquipment entity, long taskId,
			ConstantUtil.WxEffectConfirmTask wxTaskState) {
		long result = 0;
		int state = 0 ; 
		long userId=DefaultShared.getLong(Constants.KEY_USER_ID, 0l);
		
		try {
			if(!ExistMaintenaceTaskAndEquipment(taskId,entity.equipmentId)){
				ContentValues cv = new ContentValues();
				cv.put("id", entity.id);
				// cv.put("maintenaceTaskId", entity.maintenaceTaskId);
				// test: +++ @20141104
				cv.put("maintenaceTaskId", taskId);
				cv.put("equipmentId", entity.equipmentId);
				cv.put("equipmentName", queryEquipmentName(entity.equipmentId));
				cv.put("type", entity.type);
				cv.put("maintenaceState", DownloadMaintenaceState);
				cv.put("timeList", entity.timeList);
				cv.put("userId", userId);
				result = db.insert(DBHelper.Maintenace_Equipment_Table_Name, null,
						cv);
			}
			

			if (entity.maintenaceTaskEquipmentProblems != null
					&& entity.maintenaceTaskEquipmentProblems.size() > 0) {
				for (MaintenaceTaskEquipmentProblem item : entity.maintenaceTaskEquipmentProblems) {
					ContentValues cvs = new ContentValues();
					cvs.put("id", item.id + "");
					// test: @20141104
					// cvs.put("maintenaceTaskEquipmentId",
					// item.maintenaceTaskEquipmentId + "");
					cvs.put("maintenaceTaskId",taskId);
					cvs.put("maintenaceTaskEquipmentId", entity.equipmentId+ "");
					//cvs.put("problemDescribe", item.problemDesc);
					cvs.put("problemName", item.problemName);
					// cvs.put("equipmentSpotcheckId", item. + "");
					// cvs.put("equipmentSpotcheckName",
					// queryEquipmentItemName(item.maintenaceTaskEquipmentId));
					// cvs.put("result", item.state + "");
					cvs.put("startTime", item.startTime + "");
					cvs.put("endTime", item.endTime + "");
					cvs.put("userId", userId + "");
					/**if (TextUtils.isEmpty(item.state)) {
						cvs.put("state", 1);
					} else {
						cvs.put("state", 3);
					}*/
					//+++ @20141106 
					if(wxTaskState == ConstantUtil.WxEffectConfirmTask.WxEffectConfirmTaskState){
						state =4;
					}else{
						state= 3;
					}
					cvs.put("state", state);
					cvs.put("uploadState", Constants.TASK_NOT_UPLOAD);

					cvs.put("proDesc",item.problemDesc);
					cvs.put("proSuggestion", item.problemSuggestion);
					cvs.put("proMeasure", item.problemMeasure);
                    cvs.put("proPicId",item.problemPicId == null ? "":item.problemPicId);
                    cvs.put("faultSchemeType", item.faultSchemeType);
                    
					result = db.insert(DBHelper.Maintenace_Item_Table_Name,
							null, cvs);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	private boolean ExistMaintenaceTaskAndEquipment(long taskId,
			Long equipmentId) {
		long userId=DefaultShared.getLong(Constants.KEY_USER_ID, 0l);
		String args[]={taskId+"",equipmentId+""};
		String sql="SELECT * FROM " + DBHelper.Maintenace_Equipment_Table_Name + 
				" WHERE maintenaceTaskId=? and equipmentId=? and userId=" + userId;
		Cursor cs=null;
			try{
				cs=db.rawQuery(sql, args);
				if(cs.getCount() >0){
					return true;
				}
			}catch(Exception e){
					e.printStackTrace();
			}finally{
				if(cs !=null && !cs.isClosed()){
					cs.close();
				}
			}
		return false;
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

	/**
	 * 根据维修设备Id，查询的项目Id列表
	 * 
	 * @param maintenaceTaskEquipmentId
	 * @return
	 */
	public Map<Long, Long> queryMaintenaceItemIdList(
			long maintenaceTaskEquipmentId) {
		Map<Long, Long> itemIdToSid = new HashMap<Long, Long>();
		MaintenaceTaskEquipmentItem entity = null;
		Cursor cs = null;
		try {

			String args[] = { maintenaceTaskEquipmentId + "" };
			cs = db.rawQuery("SELECT * FROM "
					+ DBHelper.Maintenace_Item_Table_Name
					+ " WHERE maintenaceTaskEquipmentId = ? ", args);

			if (cs.getCount() > 0) {

				while (cs.moveToNext()) {
					long maintenaceItemId = Long.parseLong(cs.getString(cs
							.getColumnIndex("id")));
					long equipmentSpotcheckId = Long.parseLong(cs.getString(cs
							.getColumnIndex("equipmentSpotcheckId")));
					itemIdToSid.put(equipmentSpotcheckId, maintenaceItemId);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cs != null && !cs.isClosed()) {
				cs.close();
			}
		}
		return itemIdToSid;
	}

	/**
	 * 根据设备ID ，查询设备编号
	 * 
	 * @param equipmentId
	 * @return
	 */
	public String queryEquipmentOrgNum(long equipmentId) {
		String managementOrgNum = "";
		Cursor cs = null;
		try {
			String args[] = { equipmentId + "" };
			cs = db.rawQuery("SELECT * FROM "
					+ DBHelper.Base_Equipment_Table_Name + " WHERE id = ? ",
					args);

			if (cs.getCount() > 0) {
				cs.moveToFirst();
				managementOrgNum = cs.getString(cs
						.getColumnIndex("managementOrgNum"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cs != null && !cs.isClosed()) {
				cs.close();
			}
		}
		return managementOrgNum;
	}

	/**
	 * 更新维修项目的状态（待确认 ） state = 3 
	 * 已效果确认 state = 4;
	 * @param id
	 * 			uploadState 添加表示上传状态字段，0为未上传， 1 为已上传
	 */
	public boolean updateMaintenaceItem(Long id,int from) {  
		try {
			int state=3;
			int taskState=Constants.TASK_HAS_MAINTENACE;
			if(from == com.juchao.upg.android.ui.WxTaskEquipmentListActivity.From_EffectConfirm_Maintenace){
				state=4;
				taskState=Constants.TASK_HAS_EFFECT;
			}
			String sql = "update " + DBHelper.Maintenace_Item_Table_Name
					+ " set state= " + state + ",uploadState=2 where id=" + id;
			db.execSQL(sql);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public Integer commitCheckAfter(Long maintenaceTaskEquipmentId) {
		/**int result = queryItemSizeById(maintenaceTaskEquipmentId, 1);
		
		
		if (result == 0) {
			// 已维修未提交数为0 ， 则本 设备 维修已完成
			int itemState2 = queryItemSizeById(maintenaceTaskEquipmentId, 2);
			if (itemState2 == 0) {
				// 目前上传那块没用到设备的状态，而是根据任务下的所有项目状态来判断。 所以下面重置完成状态暂时没什么用处
				//updateMaintenaceEquipment(maintenaceTaskEquipmentId);
				
			}
		}*/
		// 只本方法只需要判断在问题表同一设备是否全部上传成功
		int result=queryEquipmentProblemNum(maintenaceTaskEquipmentId);
		
		return result;
	}

	private int queryEquipmentProblemNum(Long maintenaceTaskEquipmentId) {
		
		int result = -1;
		Cursor cs=null;
		try{
			String args[]={maintenaceTaskEquipmentId  + ""};
			String sql="SELECT * FROM " + DBHelper.Maintenace_Item_Table_Name + 
					   " WHERE maintenaceTaskEquipmentId =? ";
			cs=db.rawQuery(sql, args);
			result=cs.getCount();
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			if (cs != null && !cs.isClosed()) {
				cs.close();
			}
		}
		return result;
	}

	/**
	 * 更新维修设备的状态
	 * 
	 * @param maintenaceTaskEquipmentId
	 */
	private boolean updateMaintenaceEquipment(Long maintenaceTaskEquipmentId) {
		try {
			String sql = "update " + DBHelper.Maintenace_Equipment_Table_Name
					+ " set maintenaceState=1 where id= "
					+ maintenaceTaskEquipmentId;
			db.execSQL(sql);

		} catch (Exception e) {
			e.printStackTrace();
			return false; 
		}
		return true;
	}
	/**
	 * 更新维修项目  (已维修，未提交)
	 * @param maintenaceItem
	 * @return int[], 下标0： 保存是否成功， 1：未完成的点检项目数
	 */
	public Integer[] saveMaintenaceItem( 
			MaintenaceTaskEquipmentItem maintenaceItem,int from) {
		Integer [] result={0,0};
		try{
			String args[] = {maintenaceItem.id + ""};
			ContentValues cvs = new ContentValues();
			cvs.put("id", maintenaceItem.id + "");
			cvs.put("maintenaceTaskEquipmentId", maintenaceItem.equipmentId + "");
			//cvs.put("equipmentSpotcheckId", maintenaceItem.equipmentSpotcheckId + "");
			//cvs.put("equipmentSpotcheckName", maintenaceItem.equipmentSpotcheckName);
			cvs.put("result", maintenaceItem.result);
			cvs.put("problemDescribe", maintenaceItem.faultDescribe);
			//cvs.put("state", 2); //
			cvs.put("state",from);
			cvs.put("imageNames", maintenaceItem.imageNames);
			cvs.put("costTime", maintenaceItem.costTime);
			cvs.put("startTime", maintenaceItem.startTime);
			cvs.put("endTime", maintenaceItem.endTime);
			cvs.put("uploadState", "1");
			result[0] = db.update(DBHelper.Maintenace_Item_Table_Name, cvs, "id=?", args);
			
			result[1] = queryItemSizeById(maintenaceItem.equipmentId , 1);
			
//			if(result[1] == 0){
//				//已点检未提交数 为0 ，则本点检项目已完成。
//				int itemState2 = queryItemSizeById(inspectionItem.inspectionTaskEquipmentId , 2);
//				if(itemState2 == 0){
//					updateInspectionEquipment(inspectionItem.inspectionTaskEquipmentId);
//				}
//			}
			result[1] = commitCheckAfter(maintenaceItem.equipmentId);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}

	public MaintenaceTaskEquipmentItem getTheProblem(
			long maintenaceTaskEquipmentId,
			long currProblemcorresMaintenaceTaskId) {
		MaintenaceTaskEquipmentItem entity = null;
		Cursor cs = null;
		long userId=DefaultShared.getLong(Constants.KEY_USER_ID, 0L);
		
		try {
			String args[]={maintenaceTaskEquipmentId + "", currProblemcorresMaintenaceTaskId +"",userId+""};
			
			String sql="SELECT * FROM " + DBHelper.Maintenace_Item_Table_Name +
					" WHERE maintenaceTaskEquipmentId = ? " + 
					" and maintenaceTaskId = ? and userId=?";
			
			cs= db.rawQuery(sql, args);
			if (cs.getCount() > 0) {
				entity =new MaintenaceTaskEquipmentItem();
				while (cs.moveToNext()) {
					entity.id = Long.parseLong(cs.getString(cs
							.getColumnIndex("id")));
					entity.problemName=cs.getString(cs.getColumnIndex("problemName"));
					if(cs.getString(cs.getColumnIndex("problemDescribe")) !=null){
						entity.problemDescribe=cs.getString(cs.getColumnIndex("problemDescribe"));
					}
					else{
						entity.problemDescribe="";
					}
					entity.state=cs.getInt(cs.getColumnIndex("state"));
					entity.uploadState=cs.getInt(cs.getColumnIndex("uploadState"));
				}
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
	/**
	 * 获取设备的二维码， 即其设备编号。
	 * @param equipmentId
	 * @return
	 */
	public String queryEquipmentTDC(long equipmentId){
		Cursor cs=null;
		String args[]={equipmentId + ""};
		String TDC = null;
		try{
			String sql="SELECT * FROM " + DBHelper.Base_Equipment_Table_Name
					+ " WHERE id=?";
			cs=db.rawQuery(sql, args);
			if(cs.getCount() > 0){
				while (cs.moveToNext()) {
					TDC= cs.getString(cs.getColumnIndex("managementOrgNum"));
				}
			}
			Log.d(TAG, "get the equipment tdc : " + TDC);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(cs !=null && !cs.isClosed()){
				cs.close();
			}
		}
		return TDC;
	}

	public int queryProblemNum(long taskId, int from) {
		Cursor cs=null;
		int result = 0;
		String state="3";
		long userId=DefaultShared.getLong(Constants.KEY_USER_ID, 0L);
		
		if(from ==1){
			state="(4)";
		}else if(from ==2){
			state="(3,4)";
		}else{
			state="(3)";
		}
		String args[]={taskId + "", userId +"", 0+""};
		try{
			String sql="SELECT * FROM "+ DBHelper.Maintenace_Item_Table_Name + " WHERE " +
					" maintenaceTaskId=? and userId=? and uploadState = ? and state in " + state;
			cs=db.rawQuery(sql, args);
			result = cs.getCount();
					
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(cs !=null && !cs.isClosed()){
				cs.close();
			}
		}
		return result;
	}
	public String queryEquipmentNumber(long id) {
		String equNum = "";
		Cursor cs = null;
		try {
			String args[] = { id + "" };
			cs = db.rawQuery("SELECT * FROM "
					+ DBHelper.Base_Equipment_Table_Name + " WHERE id = ? ",
					args);

			if (cs.getCount() > 0) {
				cs.moveToFirst();
				equNum = cs.getString(cs
						.getColumnIndex("equNum"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cs != null && !cs.isClosed()) {
				cs.close();
			}
		}
		return equNum;
	}

	public MaintenaceTaskEquipmentProblem queryCurProblem(long problemId) {
		String sql="SELECT * FROM " + DBHelper.Maintenace_Item_Table_Name  
					+ " WHERE id=?";
		MaintenaceTaskEquipmentProblem entity= new MaintenaceTaskEquipmentProblem();
		Cursor cs=null;
		try{
			cs=db.rawQuery(sql, new String[]{problemId+""});
			while(cs.moveToNext()){
				entity.problemDesc = cs.getString(cs.getColumnIndex("proDesc"));
				entity.problemSuggestion=cs.getString(cs.getColumnIndex("proSuggestion"));
				entity.problemMeasure = cs.getString(cs.getColumnIndex("proMeasure"));
				entity.faultSchemeType = cs.getString(cs.getColumnIndex("faultSchemeType"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(cs !=null && cs.isClosed()){
				cs.close();
			}
		}
		return entity;
	}
}


