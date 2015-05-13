package com.juchao.upg.android.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Picture;
import android.text.TextUtils;
import android.util.Log;

import com.juchao.upg.android.entity.InspectionTaskEquipment;
import com.juchao.upg.android.entity.InspectionTaskEquipmentItem;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;

/**
 * 
 * 
 */
public final class InspectionEquipmentDao {

	private static final String TAG = InspectionEquipmentDao.class
			.getSimpleName();

	private DBHelper mDbHelper;
	private SQLiteDatabase db = null;

	public InspectionEquipmentDao(Context context) {
		mDbHelper = new DBHelper(context);
		
		db = mDbHelper.getWritableDatabase();
	}

	public long insertEquipment(InspectionTaskEquipment entity, long taskId) {
		long result = 0;
		int style = 0;

		try {
			style = entity.style;
			long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);
			if (!ExistInspectionTaskEquipment(entity.id, taskId, userId, style)) {
				ContentValues cv = new ContentValues();
				cv.put("id", entity.id + "");
				cv.put("inspectionTaskId", taskId + "");
				cv.put("equipmentId", entity.equipmentId + "");
				cv.put("equipmentName", queryEquipmentName(entity.equipmentId));
				cv.put("type", entity.type);
				cv.put("inspectionState", entity.inspectionState);
				cv.put("timeList", entity.timeList);
				cv.put("userId", userId + "");
				cv.put("style", style);
				result = db.insert(DBHelper.Inspection_Equipment_Table_Name,
						null, cv);

				if (entity.inspectionTaskEquipmentItems != null
						&& entity.inspectionTaskEquipmentItems.size() > 0) {
					for (InspectionTaskEquipmentItem item : entity.inspectionTaskEquipmentItems) {
						ContentValues cvs = new ContentValues();
						cvs.put("id", item.id + "");
						cvs.put("inspectionTaskEquipmentId",
								item.inspectionTaskEquipmentId + "");
						cvs.put("equipmentSpotcheckId",
								item.equipmentSpotcheckId + "");
						cvs.put("equipmentSpotcheckName",
								queryEquipmentItemName(item.equipmentSpotcheckId));
						cvs.put("result", item.result);
						cvs.put("faultDescribe", item.faultDescribe);
						cvs.put("userId", userId);
						cvs.put("inspectionTaskId", taskId);
						cvs.put("style", style);
						if (TextUtils.isEmpty(item.result)) {
							cvs.put("state", 1);
						} else {
							cvs.put("state", 3);
						}
						db.insert(DBHelper.Inspection_Item_Table_Name, null,
								cvs);
					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	private boolean ExistInspectionTaskEquipment(long equipmentId, long taskId,
			long userId, int style) {
		String args[] = { taskId + "", equipmentId + "", userId + "",
				style + "" };
		String sql = "SELECT * FROM "
				+ DBHelper.Inspection_Equipment_Table_Name
				+ " WHERE inspectionTaskId=? and equipmentId=? and userId=? and style=?";
		Cursor cs = null;
		try {
			cs = db.rawQuery(sql, args);
			if (cs.getCount() > 0) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cs != null && !cs.isClosed()) {
				cs.close();
			}
		}
		return false;
	}

	/** -------------------------- 根据ID，查询名称 -------------------------- */
	public String queryEquipmentName(long id) {
		String equipmentName = "";
		Cursor cs = null;
		try {
			String args[] = { id + "" };
			cs = db.rawQuery("SELECT * FROM "
					+ DBHelper.Base_Equipment_Table_Name + " WHERE id = ? ",
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

	/**
	 * 根据设备ID ，查询设备编号
	 * 
	 * @param id
	 * @return
	 */
	public String queryEquipmentOrgNum(long id) {
		String managementOrgNum = "";
		Cursor cs = null;
		try {
			String args[] = { id + "" };
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
	 * 查询操作须知 (基础表)
	 * 
	 * @param id
	 * @return
	 */
	public String queryEquipmentOperationNotice(long id) {

		String maintainNotice = "";
		Cursor cs = null;
		try {
			String args[] = { id + "" };
			cs = db.rawQuery("SELECT * FROM "
					+ DBHelper.Base_Equipment_Table_Name + " WHERE id = ? ",
					args);

			if (cs.getCount() > 0) {
				cs.moveToFirst();
				maintainNotice = cs.getString(cs
						.getColumnIndex("maintainNotice"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cs != null && !cs.isClosed()) {
				cs.close();
			}
		}
		return maintainNotice;
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

	/** -------------------------- 根据ID，查询名称 - end------------------------- */

	/**
	 * 根据点检设备Id，查询点检设备实体
	 * 
	 * @param inspectionEquipmentId
	 * @return
	 */
	public InspectionTaskEquipment queryInspectionEquipmentEntity(
			long inspectionEquipmentId) {

		InspectionTaskEquipment entity = null;
		Cursor cs = null;
		Cursor cs2 = null;
		try {
			String args[] = { inspectionEquipmentId + "" };
			cs = db.rawQuery("SELECT * FROM "
					+ DBHelper.Inspection_Equipment_Table_Name
					+ " WHERE id = ? ", args);
			if (cs.getCount() > 0) {
				cs.moveToFirst();
				entity = new InspectionTaskEquipment();
				entity.id = Long
						.parseLong(cs.getString(cs.getColumnIndex("id")));
				entity.inspectionTaskId = Long.parseLong(cs.getString(cs
						.getColumnIndex("inspectionTaskId")));
				entity.equipmentId = Long.parseLong(cs.getString(cs
						.getColumnIndex("equipmentId")));
				entity.type = cs.getString(cs.getColumnIndex("type"));
				entity.inspectionState = cs.getInt(cs
						.getColumnIndex("inspectionState"));
				entity.timeList = cs.getInt(cs.getColumnIndex("timeList"));

				String args2[] = { entity.id + "" };
				cs2 = db.rawQuery("SELECT * FROM "
						+ DBHelper.Inspection_Item_Table_Name
						+ " WHERE inspectionTaskEquipmentId = ? ", args2);
				if (cs2.getCount() > 0) {
					entity.inspectionTaskEquipmentItems = new ArrayList<InspectionTaskEquipmentItem>();
					while (cs2.moveToNext()) {
						InspectionTaskEquipmentItem item = new InspectionTaskEquipmentItem();
						item.id = Long.parseLong(cs.getString(cs
								.getColumnIndex("id")));
						item.inspectionTaskEquipmentId = Long
								.parseLong(cs.getString(cs
										.getColumnIndex("inspectionTaskEquipmentId")));
						item.equipmentSpotcheckId = Long
								.parseLong(cs.getString(cs
										.getColumnIndex("equipmentSpotcheckId")));
						item.result = cs.getString(cs.getColumnIndex("result"));
						item.faultDescribe = cs.getString(cs
								.getColumnIndex("faultDescribe"));
						item.state = cs.getInt(cs.getColumnIndex("state"));
						item.style = cs.getInt(cs.getColumnIndex("style"));
						entity.inspectionTaskEquipmentItems.add(item);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cs != null && !cs.isClosed()) {
				cs.close();
			}

			if (cs2 != null && !cs2.isClosed()) {
				cs2.close();
			}
		}
		return entity;
	}

	/**
	 * 根据任务Id，查询点检设备列表 （做上传时，需增加状态判断）
	 * 
	 * 查询关连 任务Id ，用户名，状态 的设备
	 * 
	 * @param taskId
	 * @return
	 */
	public List<InspectionTaskEquipment> queryEquipmentList(long taskId,
			int style) {
		List<InspectionTaskEquipment> list = null;
		InspectionTaskEquipment entity = null;
		Cursor cs = null;
		Cursor cs1 = null;
		long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);
		try {
			String args[] = { taskId + "", 0 + "", userId + "", style + "" };

			cs = db.rawQuery(
					"SELECT * FROM "
							+ DBHelper.Inspection_Equipment_Table_Name
							+ " WHERE inspectionTaskId = ? AND inspectionState = ? and userId=?"
							+ " AND style=?", args);
			if (cs.getCount() > 0) {
				list = new ArrayList<InspectionTaskEquipment>();
				while (cs.moveToNext()) {
					entity = new InspectionTaskEquipment();
					entity.id = Long.parseLong(cs.getString(cs
							.getColumnIndex("id")));
					int unComplementItemNum = queryItemSizeById(taskId,
							entity.id, 1, style);
					if (unComplementItemNum == 0) { // 如果该点检设备下，没有未点检的项目，则不返回该点检设备。
						continue;
					}
					entity.inspectionTaskId = Long.parseLong(cs.getString(cs
							.getColumnIndex("inspectionTaskId")));
					entity.equipmentId = Long.parseLong(cs.getString(cs
							.getColumnIndex("equipmentId")));
					entity.type = cs.getString(cs.getColumnIndex("type"));
					entity.inspectionState = cs.getInt(cs
							.getColumnIndex("inspectionState"));
					entity.timeList = cs.getInt(cs.getColumnIndex("timeList"));
					entity.equipmentName = cs.getString(cs
							.getColumnIndex("equipmentName"));
					entity.style = cs.getInt(cs.getColumnIndex("style"));
					list.add(entity);
				}
			}
			/*
			 * String args[]={taskId+"", 1+"",userId+""}; String
			 * sql="SLECT distinct(inspectionTaskEquipmentId) FROM "+
			 * DBHelper.Inspection_Item_Table_Name +
			 * " WHERE inspectionTaskId=? and state=? and userId=?";
			 * 
			 * cs=db.rawQuery(sql, args); if(cs.getCount()>0){ cs.moveToFirst();
			 * while(cs.moveToNext()){ //HashSet <Long> ids=new HashSet<Long>();
			 * //ids.add(Long.parseLong(cs.getString(cs.getColumnIndex(
			 * "inspectionTaskEquipmentId")))); long
			 * currId=Long.parseLong(cs.getString
			 * (cs.getColumnIndex("inspectionTaskEquipmentId"))); String[]
			 * args1={currId+"",taskId + ""}; sql="SELECT * FROM " +
			 * DBHelper.Inspection_Equipment_Table_Name +
			 * " WHERE id= ? and inspectionTaskId=?"; cs1=db.rawQuery(sql,
			 * args1); while(cs1.moveToNext()){ entity = new
			 * InspectionTaskEquipment(); entity.id =
			 * Long.parseLong(cs.getString(cs.getColumnIndex("id")));
			 * entity.inspectionTaskId =
			 * Long.parseLong(cs.getString(cs.getColumnIndex
			 * ("inspectionTaskId"))); entity.equipmentId =
			 * Long.parseLong(cs.getString(cs.getColumnIndex("equipmentId")));
			 * entity.type = cs.getString(cs.getColumnIndex("type"));
			 * entity.inspectionState =
			 * cs.getInt(cs.getColumnIndex("inspectionState")); entity.timeList
			 * = cs.getInt(cs.getColumnIndex("timeList")); entity.equipmentName
			 * = cs.getString(cs.getColumnIndex("equipmentName"));
			 * list.add(entity); } } }
			 */

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cs1 != null && !cs1.isClosed()) {
				cs1.close();
			}
			if (cs != null && !cs.isClosed()) {
				cs.close();
			}
		}
		return list;
	}

	/**
	 * 根据点检设备Id，查询该点检设备的项目列表
	 * 
	 * @param taskId
	 * @param state
	 *            0：全部 1：未点检 ， 2：已点检 , 3 ：已完成 ,  4: 已点检和已完成
	 * @return
	 */
	public List<InspectionTaskEquipmentItem> queryInspectionItemList(
			long taskId, long inspectionTaskEquipmentId, int state, int style) {
		List<InspectionTaskEquipmentItem> list = null;
		InspectionTaskEquipmentItem entity = null;
		Cursor cs = null;
		try {
			// String args[] = { inspectionTaskEquipmentId + "" };
			long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);

			if (state == 0) {
				String args[] = { inspectionTaskEquipmentId + "", taskId + "",
						style + "" };
				cs = db.rawQuery("SELECT * FROM "
						+ DBHelper.Inspection_Item_Table_Name
						+ " WHERE inspectionTaskEquipmentId = ? and userId="
						+ userId + " and inspectionTaskId=? and style=? order by equipmentSpotcheckId", args);
			} else if (state == 1) {
				String args[] = { inspectionTaskEquipmentId + "", "1",
						taskId + "", style + "" };
				cs = db.rawQuery(
						"SELECT * FROM "
								+ DBHelper.Inspection_Item_Table_Name
								+ " WHERE inspectionTaskEquipmentId = ? AND state = ? and userId="
								+ userId
								+ " and inspectionTaskId=? and style=?  order by equipmentSpotcheckId", args);
			} else if (state == 2) {
				String args[] = { inspectionTaskEquipmentId + "", "2",
						taskId + "", style + "" };
				cs = db.rawQuery(
						"SELECT * FROM "
								+ DBHelper.Inspection_Item_Table_Name
								+ " WHERE inspectionTaskEquipmentId = ? AND state = ? and userId="
								+ userId
								+ " and inspectionTaskId=? and style=?  order by equipmentSpotcheckId", args);
			} else if (state == 3) {
				String args[] = { inspectionTaskEquipmentId + "", "3",
						taskId + "", style + "" };
				cs = db.rawQuery(
						"SELECT * FROM "
								+ DBHelper.Inspection_Item_Table_Name
								+ " WHERE inspectionTaskEquipmentId = ? AND state = ? and userId="
								+ userId
								+ " and inspectionTaskId=? and style=?  order by equipmentSpotcheckId", args);
			}else if(state == 4){
				String args[] = { inspectionTaskEquipmentId + "", 
						taskId + "", style + "" };
				cs = db.rawQuery(
						"SELECT * FROM "
								+ DBHelper.Inspection_Item_Table_Name
								+ " WHERE inspectionTaskEquipmentId = ? AND state in(2,3)  and userId="
								+ userId
								+ " and inspectionTaskId=? and style=?  order by equipmentSpotcheckId", args);
			}

			if (cs.getCount() > 0) {
				list = new ArrayList<InspectionTaskEquipmentItem>();
				while (cs.moveToNext()) {
					entity = new InspectionTaskEquipmentItem();
					entity.id = Long.parseLong(cs.getString(cs
							.getColumnIndex("id")));
					entity.inspectionTaskEquipmentId = Long
							.parseLong(cs.getString(cs
									.getColumnIndex("inspectionTaskEquipmentId")));
					entity.equipmentSpotcheckId = Long.parseLong(cs
							.getString(cs
									.getColumnIndex("equipmentSpotcheckId")));
					entity.equipmentSpotcheckName = cs.getString(cs
							.getColumnIndex("equipmentSpotcheckName"));
					entity.result = cs.getString(cs.getColumnIndex("result"));
					entity.faultDescribe = cs.getString(cs
							.getColumnIndex("faultDescribe"));
					entity.state = cs.getInt(cs.getColumnIndex("state"));
					entity.style = cs.getInt(cs.getColumnIndex("style"));
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
	 * 根据点检设备Id，查询该点检设备下的点检项目数
	 * 
	 * @param taskId
	 * @param state
	 *            0：全部 1：未点检 ， 2：已点检 未提交 ， 3：完成
	 * @return
	 */
	public int queryItemSizeById(long taskId, long inspectionEquipmentId,
			int state, int style) {
		int count = 0;
		Cursor cs = null;
		try {
			long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);
			if (state == 0) {
				String args[] = { inspectionEquipmentId + "", userId + "",
						taskId + "", style + "" };
				cs = db.rawQuery("SELECT * FROM "
						+ DBHelper.Inspection_Item_Table_Name
						+ " WHERE inspectionTaskEquipmentId = ? and userId=?"
						+ " and inspectionTaskId=? and style=?", args);
			} else if (state == 1) {
				String args[] = { inspectionEquipmentId + "", "1", userId + "",
						taskId + "", style + "" };
				cs = db.rawQuery(
						"SELECT * FROM "
								+ DBHelper.Inspection_Item_Table_Name
								+ " WHERE inspectionTaskEquipmentId = ? AND state = ? and userId=?"
								+ " and inspectionTaskId=? and style=?", args);
			} else if (state == 2) {
				String args[] = { inspectionEquipmentId + "", "2", userId + "",
						taskId + "", style + "" };
				cs = db.rawQuery(
						"SELECT * FROM "
								+ DBHelper.Inspection_Item_Table_Name
								+ " WHERE inspectionTaskEquipmentId = ? AND state = ? and userId=?"
								+ " and inspectionTaskId=? and style=?", args);
			} else if (state == 3) {
				String args[] = { inspectionEquipmentId + "", "3", userId + "",
						taskId + "", style + "" };
				cs = db.rawQuery(
						"SELECT * FROM "
								+ DBHelper.Inspection_Item_Table_Name
								+ " WHERE inspectionTaskEquipmentId = ? AND state = ? and userId=?"
								+ " and inspectionTaskId=? and style=?", args);
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
	 * 更新点检项目 （已点检，未提交）
	 * 
	 * @param inspectionItem
	 * @return int[] , 下标0: 保存是否成功 ， 1： 未完成的点检项目数
	 */
	public Integer[] saveInspectionItem(long inspectionTaskId,
			InspectionTaskEquipmentItem inspectionItem) {

		Integer[] result = { 0, 0 };
		int style = inspectionItem.style;
		try {
			String args[] = { inspectionItem.id + "", style + "" };

			ContentValues cvs = new ContentValues();
			cvs.put("id", inspectionItem.id + "");
			cvs.put("inspectionTaskEquipmentId",
					inspectionItem.inspectionTaskEquipmentId + "");
			cvs.put("equipmentSpotcheckId", inspectionItem.equipmentSpotcheckId
					+ "");
			cvs.put("equipmentSpotcheckName",
					inspectionItem.equipmentSpotcheckName);
			cvs.put("result", inspectionItem.result);
			cvs.put("faultDescribe", inspectionItem.faultDescribe);
			cvs.put("state", 2);
			cvs.put("imageNames", inspectionItem.imageNames);
			cvs.put("costTime", inspectionItem.costTime);
			cvs.put("startTime", inspectionItem.startTime);
			cvs.put("endTime", inspectionItem.endTime);
			result[0] = db.update(DBHelper.Inspection_Item_Table_Name, cvs,
					"id=? and style=?", args);

			result[1] = queryItemSizeById(inspectionTaskId,
					inspectionItem.inspectionTaskEquipmentId, 1, style);

			// if(result[1] == 0){
			// //已点检未提交数 为0 ，则本点检项目已完成。
			// int itemState2 =
			// queryItemSizeById(inspectionItem.inspectionTaskEquipmentId , 2);
			// if(itemState2 == 0){
			// updateInspectionEquipment(inspectionItem.inspectionTaskEquipmentId);
			// }
			// }
			result[1] = commitCheckAfter(inspectionTaskId,
					inspectionItem.inspectionTaskEquipmentId, style);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 提交后检查
	 * 
	 * @param inspectionTaskEquipmentId
	 * @return
	 */
	public int commitCheckAfter(long inspectionTaskId,
			long inspectionTaskEquipmentId, int style) {
		int result = queryItemSizeById(inspectionTaskId,
				inspectionTaskEquipmentId, 1, style);
		if (result == 0) {
			// 已点检未提交数 为0 ，则本点检设备已完成。
			int itemState2 = queryItemSizeById(inspectionTaskId,
					inspectionTaskEquipmentId, 2, style);
			if (itemState2 == 0) {
				// 目前上传那块没用到设备的状态，而是根据任务下的所有项目状态来判断。 所以下面重置完成状态暂时没什么用处
				updateInspectionEquipment(inspectionTaskId,
						inspectionTaskEquipmentId, style);
			}
		}
		return result;
	}

	/**
	 * 更新点检设备的状态
	 * 
	 * @param inspectionTaskEquipmentId
	 * @return
	 */
	private boolean updateInspectionEquipment(long inspectionTaskId,
			long inspectionTaskEquipmentId, int style) {
		try {
			long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);
			String sql = "update " + DBHelper.Inspection_Equipment_Table_Name
					+ " set inspectionState=1 where id="
					+ inspectionTaskEquipmentId + " and inspectionTaskId="
					+ inspectionTaskId + " and userId=" + userId
					+ " AND style=" + style;
			db.execSQL(sql);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 更新点检项目的状态 (完成)
	 * 
	 * @param inspectionTaskEquipmentId
	 * @return
	 */
	public boolean updateInspectionItem(long inspectionTaskItemId, int style) {
		try {
			long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);

			String sql = "update " + DBHelper.Inspection_Item_Table_Name
					+ " set state=3 where id=" + inspectionTaskItemId
					+ " and userId=" + userId + " and style=" + style;
			db.execSQL(sql);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 根据点检设备Id，查询的项目Id列表
	 * 
	 * @param inspectionTaskEquipmentId
	 * @return
	 */
	public Map<Long, Long> queryInspectionItemIdList(
			long inspectionTaskEquipmentId, int style) {
		Map<Long, Long> itemIdToSid = new HashMap<Long, Long>();
		InspectionTaskEquipmentItem entity = null;
		Cursor cs = null;
		try {

			String args[] = { inspectionTaskEquipmentId + "", style + "" };
			cs = db.rawQuery("SELECT * FROM "
					+ DBHelper.Inspection_Item_Table_Name
					+ " WHERE inspectionTaskEquipmentId = ? " + " AND style=?",
					args);

			if (cs.getCount() > 0) {

				while (cs.moveToNext()) {
					long inspectionItemId = Long.parseLong(cs.getString(cs
							.getColumnIndex("id")));
					long equipmentSpotcheckId = Long.parseLong(cs.getString(cs
							.getColumnIndex("equipmentSpotcheckId")));
					itemIdToSid.put(equipmentSpotcheckId, inspectionItemId);
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
	 * 
	 */
	public synchronized void closeDB() {
		if (db != null && db.isOpen()) {
			db.close();
		}
	}

	public int queryEquipmentItemNum(long taskId, int style) {
		int result = 0;
		long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);
		String args[] = { taskId + "", "1", userId + "", style + "" };
		Cursor cs = null;
		try {
			String sql = "SELECT * FROM " + DBHelper.Inspection_Item_Table_Name
					+ " WHERE inspectionTaskId=? and state=?"
					+ " and userId=? and style=?";
			cs = db.rawQuery(sql, args);
			result = cs.getCount();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cs != null && !cs.isClosed()) {
				cs.close();
			}
		}
		result = (result < 0) ? 0 : result;
		return result;
	}

	public String GetBaseEquipmentSpotcheckProjectNo(long id) {
		String sql = "SELECT * FROM "
				+ DBHelper.Base_Equipment_Spotcheck_Table_Name + " WHERE id=?";
		Cursor cs = null;
		String result = "";
		try {
			cs = db.rawQuery(sql, new String[] { id + "" });
			if (cs.getCount() > 0) {
				cs.moveToFirst();
				result = cs.getString(cs.getColumnIndex("projectNO"));
			}
		} finally {
			cs.close();
		}
		return result;
	}

	public Map<String, Long> queryScanCodeIdsMap(
			List<InspectionTaskEquipmentItem> itemList1) {
		Map<String, Long> scanCodeIdMap = new HashMap<String, Long>();

		// for(InspectionTaskEquipmentItem item : itemList1){
		Cursor cs = null;
		try {
			for (int i = 0; i < itemList1.size(); i++) {
				
				InspectionTaskEquipmentItem item = itemList1.get(i);
				long curId = item.equipmentSpotcheckId;
				String sql = "SELECT * FROM "
						+ DBHelper.Base_Equipment_Spotcheck_Table_Name
						+ " WHERE id=?";
				cs = db.rawQuery(sql, new String[] { curId + "" });
				
				if (cs.getCount() > 0) {
					cs.moveToFirst();
					scanCodeIdMap
							.put(cs.getString(cs.getColumnIndex("projectNO")),
									curId);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			Log.d(TAG, e.getMessage());
		}
		finally {
			if(cs!=null && !cs.isClosed()){
				cs.close();
			}
		}
		return scanCodeIdMap;
	}

	public String queryEquipmentOperationNotice(long equipmentId, int type) {

		String notice = "";
		Cursor cs = null;
		try {
			String args[] = { equipmentId + "",type+""};
			cs = db.rawQuery("SELECT * FROM "
					+ DBHelper.Base_Operation_Notice + " WHERE equipmentId=? and type=?",
					args);

			if (cs.getCount() > 0) {
				cs.moveToFirst();
				notice = cs.getString(cs
						.getColumnIndex("notice"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cs != null && !cs.isClosed()) {
				cs.close();
			}
		}
		return notice;
	}

	public List<String> queryInspectionItemPiecture(long inspectionItemId) {
		List<String> result=new ArrayList<String>();
		Cursor cs=null;
		try{
			String sql="SELECT * FROM " + DBHelper.Base_Equip_Check_Picture
					+ " WHERE checksubjectId=?";
			cs=db.rawQuery(sql, new String []{inspectionItemId +""});
			
			if(cs.getCount()>0){
				while(cs.moveToNext()){
					result.add(cs.getString(cs.getColumnIndex("pictureId")));
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
		return result;
	}

	public String queryPictureInfo(String id) {
		
		String result="";
		Cursor cs=null;
		try{
			String sql="SELECT * FROM "  + DBHelper.Base_Image_Table_Name 
					  + " WHERE id=?";
			cs=db.rawQuery(sql, new String[]{id});
			//cs=db.rawQuery(sql,null);
			if(cs.getCount() > 0){
				cs.moveToFirst();
				result=cs.getString(cs.getColumnIndex("url"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}if(cs !=null && !cs.isClosed()){
			cs.close();
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

    public String queryMaintenacePic(long problemId) {
        String picIds = null;
        Cursor cs=null;
        try{
            String[] args={problemId +""};
            cs=db.rawQuery("SELECT * FROM "
                        +DBHelper.Maintenace_Item_Table_Name
                        + " WHERE id=?",args);
            	if(cs.getCount()>0){
                while (cs.moveToNext()){
                    picIds = cs.getString(cs.getColumnIndex("proPicId"));
                }
                }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if( cs !=null && !cs.isClosed()){
                cs.close();
            }
        }
        return picIds;
    }
}
