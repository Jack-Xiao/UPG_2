package com.juchao.upg.android.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.juchao.upg.android.entity.InspectionTask;
import com.juchao.upg.android.entity.InspectionTaskEquipment;
import com.juchao.upg.android.entity.InspectionTaskEquipmentItem;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;

/**
 * 
 * 
 */
public final class InspectionTaskDao {

	private static final String TAG = InspectionTaskDao.class.getSimpleName();

	private DBHelper mDbHelper;
	private SQLiteDatabase db = null;

	public InspectionTaskDao(Context context) {
		mDbHelper = new DBHelper(context);
		db = mDbHelper.getWritableDatabase();
	}

	/**
	 * type： 0：定常点检 1 ：自定义点检 2：特种设备点检
	 * 
	 * @param entity
	 * @return
	 */
	public synchronized long insertInspectionTask(InspectionTask entity) {
		long result = 0;
		long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);
		try {
			boolean isExist = queryInspectionTaskIsExist(entity.id, userId,
					entity.status);
			if (isExist) {
				return result;
			}
			ContentValues cv = new ContentValues();
			cv.put("id", entity.id + "");
			cv.put("taskName", entity.taskName);
			cv.put("startTime", entity.startTime);
			cv.put("endTime", entity.endTime);
			cv.put("isDownloaded", "false");
			cv.put("state", 0);
			cv.put("userId", userId + "");
			cv.put("style", entity.status);
			cv.put("type", entity.intervalType);
			result = db.insert(DBHelper.Inspection_Task_Table_Name, null, cv);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public synchronized boolean updateInspectionTask(InspectionTask entity) {
		long result = 0;
		try {
			long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);
			String args[] = { entity.id + "", userId + "",entity.status+""};
			ContentValues cv = new ContentValues();
			cv.put("id", entity.id + "");
			cv.put("taskName", entity.taskName);
			cv.put("startTime", entity.startTime);
			cv.put("endTime", entity.endTime);
			cv.put("isDownloaded", entity.isDownloaded + "");
			cv.put("state", entity.state);
			result = db.update(DBHelper.Inspection_Task_Table_Name, cv,
					"id=? and userId=? and style=?", args);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result > 0 ? true : false;
	}

	public synchronized boolean queryInspectionTaskIsExist(long id,
			long userId, int style) {
		Cursor cs = null;
		try {
			String args[] = { id + "", userId + "", style + "" };
			cs = db.rawQuery("SELECT * FROM "
					+ DBHelper.Inspection_Task_Table_Name
					+ " WHERE id = ? and userId=? and style=?", args);
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

	public InspectionTask queryInspectionTaskById(long id,int style) {
		InspectionTask entity = null;
		long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);
		Cursor cs = null;
		try {
			String args[] = { id + "",style+"" };
			cs = db.rawQuery("SELECT * FROM "
					+ DBHelper.Inspection_Task_Table_Name
					+ " WHERE id = ? and userId=" + userId
					+ " and style=?", args);

			if (cs.getCount() > 0) {
				cs.moveToFirst();
				entity = new InspectionTask();
				entity.id = Long
						.parseLong(cs.getString(cs.getColumnIndex("id")));
				entity.taskName = cs.getString(cs.getColumnIndex("taskName"));
				entity.startTime = cs.getString(cs.getColumnIndex("startTime"));
				entity.endTime = cs.getString(cs.getColumnIndex("endTime"));
				entity.isDownloaded = Boolean.parseBoolean(cs.getString(cs
						.getColumnIndex("isDownloaded")));
				entity.state = cs.getInt(cs.getColumnIndex("state"));
				entity.status=style;
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
	 * 
	 * @param PageSize
	 *            每页大小
	 * @param pageNum
	 *            页码 1...
	 * @param state
	 *            0 :全部 ， 1 ：未完成 & 已下载 ， 2:已完成
	 * @return state
	 */
	public synchronized List<InspectionTask> queryInspectionTaskList(
			int PageSize, int pageNum, int state) {

		List<InspectionTask> list = null;
		InspectionTask entity = null;
		Cursor cs = null;
		long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);

		try {

			if (state == 0) {
				String args[] = { userId + "" };

				cs = db.rawQuery("SELECT * FROM "
						+ DBHelper.Inspection_Task_Table_Name
						+ " WHERE userId = ? ORDER BY _id DESC", args);
			} else if (state == 1) {
				String args[] = { userId + "", "true", "0" };
				cs = db.rawQuery(
						"SELECT * FROM "
								+ DBHelper.Inspection_Task_Table_Name
								+ " WHERE userId = ? AND isDownloaded = ? AND state = ? ORDER BY _id DESC",
						args);
				/*
				 * String sql="SELECT * FROM "
				 * +DBHelper.Inspection_Task_Table_Name + " t" + " join " +
				 * DBHelper.Inspection_Item_Table_Name +
				 * "i on t.id=i.inspectionTaskId and t.userId=i.userId where i.userId="
				 * + userId + " and i.state=2";
				 */
				// cs=db.rawQuery(sql, null);

			} else if (state == 2) {
				String args[] = { userId + "", "false" };
				cs = db.rawQuery(
						"SELECT * FROM "
								+ DBHelper.Inspection_Task_Table_Name
								+ " WHERE userId = ? AND isDownloaded = ? ORDER BY _id DESC",
						args);
			}

			int recordNum = cs.getCount();
			int start = (pageNum - 1) * PageSize;
			int index = start;
			int end = PageSize * pageNum - 1;

			cs.moveToPosition(start - 1);
			if (recordNum < start) {
				return null;
			}

			list = new ArrayList<InspectionTask>();
			while (cs.moveToNext()) {
				if (index >= start && index <= end) {
					entity = new InspectionTask();
					entity.id = Long.parseLong(cs.getString(cs
							.getColumnIndex("id")));
					entity.taskName = cs.getString(cs
							.getColumnIndex("taskName"));
					entity.startTime = cs.getString(cs
							.getColumnIndex("startTime"));
					entity.endTime = cs.getString(cs.getColumnIndex("endTime"));
					entity.isDownloaded = Boolean.parseBoolean(cs.getString(cs
							.getColumnIndex("isDownloaded")));
					entity.state = cs.getInt(cs.getColumnIndex("state"));
					entity.status = cs.getInt(cs.getColumnIndex("style"));
					entity.intervalType=cs.getInt(cs.getColumnIndex("type"));
					list.add(entity);
				} else {
					break;
				}
				index++;
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
	 * 根据任务ID ，查询对应的点检项目数
	 * 
	 * @param taskId
	 * @return
	 */
	public int queryInspectionItemNum(long taskId, int style) {

		int inspectionItemNum = 0;
		// List<Long> inspectionEquipmentIdList = new ArrayList<Long>();
		long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);

		Cursor cs = null;
		try {
			String args[] = { taskId + "", 2 + "", userId + "", style + "" };

			/*
			 * cs = db.rawQuery("SELECT * FROM " +
			 * DBHelper.Inspection_Equipment_Table_Name +
			 * " WHERE inspectionTaskId = ? AND inspectionState = ?", args); if
			 * (cs.getCount() > 0) { while (cs.moveToNext()) { long id =
			 * Long.parseLong(cs.getString(cs .getColumnIndex("id")));
			 * inspectionEquipmentIdList.add(id); } }
			 */

			/*
			 * for (int i = 0; i < inspectionEquipmentIdList.size(); i++) {
			 * String args2[] = { inspectionEquipmentIdList.get(i) + "", "2" };
			 * cs = db.rawQuery("SELECT * FROM " +
			 * DBHelper.Inspection_Item_Table_Name +
			 * " WHERE inspectionTaskEquipmentId = ? AND state = ?", args2);
			 * 
			 * inspectionItemNum += cs.getCount(); }
			 */
			String sql = "SELECT * FROM "
					+ DBHelper.Inspection_Item_Table_Name
					+ " WHERE "
					+ " inspectionTaskId=? and state=? and userId=? and style=?";
			cs = db.rawQuery(sql, args);
			inspectionItemNum += cs.getCount();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cs != null && !cs.isClosed()) {
				cs.close();
			}
		}
		return inspectionItemNum;
	}

	/**
	 * 根据任务ID ，查询对应的点检项目列表
	 * 
	 * @param taskId
	 * @return
	 */
	public List<InspectionTaskEquipmentItem> queryInspectionItemList(
			long taskId, int style) {

		List<InspectionTaskEquipmentItem> inspectionItemList = null;
		List<Long> inspectionEquipmentIdList = new ArrayList<Long>();
		Cursor cs = null;
		long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);
		try {
			//String args[] = { taskId + "", 0 + "", userId + "", style + "" };

//			cs = db.rawQuery(
//					"SELECT * FROM "
//							+ DBHelper.Inspection_Equipment_Table_Name
//							+ " WHERE inspectionTaskId = ? AND inspectionState = ? and userId=? and style=?",
//					args);
//			if (cs.getCount() > 0) {
//				while (cs.moveToNext()) {
//					long id = Long.parseLong(cs.getString(cs
//							.getColumnIndex("id")));
//					inspectionEquipmentIdList.add(id);
//				}
//			}
			
//			for (int i = 0; i < inspectionEquipmentIdList.size(); i++) {
//				String args2[] = { inspectionEquipmentIdList.get(i) + "", "2",
//						userId + "", style + "" };
//				cs = db.rawQuery(
//						"SELECT * FROM "
//								+ DBHelper.Inspection_Item_Table_Name
//								+ " WHERE inspectionTaskEquipmentId = ? AND state = ? and userId=? and style=?",
//						args2);
			    String args[] = { taskId + "", 2 + "", userId + "", style + "" };	
			
				String sql = "SELECT * FROM "
						+ DBHelper.Inspection_Item_Table_Name
						+ " WHERE "
						+ " inspectionTaskId=? and state=? and userId=? and style=?";
				cs=db.rawQuery(sql, args);

				if (cs.getCount() > 0) {
					inspectionItemList = new ArrayList<InspectionTaskEquipmentItem>();
					while (cs.moveToNext()) {
						InspectionTaskEquipmentItem entity = new InspectionTaskEquipmentItem();
						entity.id = Long.parseLong(cs.getString(cs
								.getColumnIndex("id")));
						entity.inspectionTaskEquipmentId = Long
								.parseLong(cs.getString(cs
										.getColumnIndex("inspectionTaskEquipmentId")));
						entity.equipmentSpotcheckId = Long
								.parseLong(cs.getString(cs
										.getColumnIndex("equipmentSpotcheckId")));
						entity.equipmentSpotcheckName = cs.getString(cs
								.getColumnIndex("equipmentSpotcheckName"));
						entity.result = cs.getString(cs
								.getColumnIndex("result"));
						entity.faultDescribe = cs.getString(cs
								.getColumnIndex("faultDescribe"));
						entity.state = cs.getInt(cs.getColumnIndex("state"));
						entity.costTime = cs.getInt(cs
								.getColumnIndex("costTime"));
						entity.startTime = cs.getString(cs
								.getColumnIndex("startTime"));
						entity.endTime = cs.getString(cs
								.getColumnIndex("endTime"));
						entity.imageNames = cs.getString(cs
								.getColumnIndex("imageNames"));

						entity.style = cs.getInt(cs.getColumnIndex("style"));
						inspectionItemList.add(entity);
					}
				}
			//}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cs != null && !cs.isClosed()) {
				cs.close();
			}
		}

		return inspectionItemList;
	}

	/**
	 * 更新点检项目的状态 (完成)
	 * 
	 * @param inspectionTaskEquipmentId
	 * @return
	 */
	public boolean updateInspectionItem(long inspectionTaskItemId,int style) {
		try {
			long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);
			String sql = "update " + DBHelper.Inspection_Item_Table_Name
					+ " set state=3 where id=" + inspectionTaskItemId
					+ " and userId=" + userId
					+ " and style=" + style;
			db.execSQL(sql); 

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 退出系统时删除点检数据
	 * 
	 * @return
	 */
	public synchronized boolean deleteInspectionData() {
		try {
			String del1 = "DELETE  FROM " + DBHelper.Inspection_Task_Table_Name;
			db.execSQL(del1);

			String del2 = "DELETE  FROM "
					+ DBHelper.Inspection_Equipment_Table_Name;
			db.execSQL(del2);

			String del3 = "DELETE  FROM " + DBHelper.Inspection_Item_Table_Name;
			db.execSQL(del3);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public synchronized void closeDB() {
		if (db != null && db.isOpen()) {
			db.close();
		}
	}

	/**
	 * 删除本地数据库中 后台已取消的任务。
	 * 
	 * @param taskIds
	 */
	public void removeCancelInspectionTask(List<String> taskIds) {
		long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);
		String[] args = { userId + "" };
		String sql = "SELECT * FROM " + DBHelper.Inspection_Task_Table_Name
				+ " WHERE userId=?";
		Cursor cs = null;
		try {
			cs = db.rawQuery(sql, args);
			if (cs.getCount() > 0) {
				while (cs.moveToNext()) {
					long id = Long.parseLong(cs.getString(cs
							.getColumnIndex("id")));
					int style = Integer.parseInt(cs.getString(cs
							.getColumnIndex("style")));
					if (!taskIds.contains(id + "_" + style)) {
						deleteInspectionTask(id, style);
					}
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

	public void deleteInspectionTask(Long id, int style) {
		try {
			long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);
			String sql = "DELETE FROM " + DBHelper.Inspection_Task_Table_Name
					+ " WHERE id=" + id + " and userId=" + userId
					+ " AND style = " + style;
			db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int queryAllHasUploadEquipmentItemNum(long taskId, int style) {
		long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);
		int result = 0;

		String sql = "SELECT * FROM " + DBHelper.Inspection_Item_Table_Name
				+ " WHERE inspectionTaskId= " + taskId
				+ " and state in(0,1,2) and userId=" + userId 
				+ " AND style="+ style;
		Cursor cs = null;
		try {
			cs = db.rawQuery(sql, null);
			result = cs.getCount();
		} catch (Exception e) {
			result = -1;
		} finally {
			if (cs != null && cs.isClosed()) {
				cs.close();
			}
		}
		return result;
	}

	public boolean restoreInspectionItem(Long id, int style, Long inspectionTaskEquipmentId, long taskId) {
		try {
			long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);
			
			String sql="update "+ DBHelper.Inspection_Task_Table_Name
					   +" set state=0 where id=" +taskId 
					   +" and userId= "+ userId
					   +" and style=" + style;
			db.execSQL(sql);
			
			sql="update " + DBHelper.Inspection_Equipment_Table_Name
			   +" set inspectionState=0 where inspectionTaskId=" +taskId
			   +" and id=" + inspectionTaskEquipmentId 
			   +" and userId=" +userId
			   +" and style="+style;
			db.execSQL(sql);
			
			sql = "update " + DBHelper.Inspection_Item_Table_Name
					+ " set state=1 where id=" + id
					+ " and userId=" + userId
					+ " and style=" + style;
			db.execSQL(sql); 

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
