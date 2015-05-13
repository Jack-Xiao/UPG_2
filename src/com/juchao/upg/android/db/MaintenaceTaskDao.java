package com.juchao.upg.android.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.juchao.upg.android.entity.MaintenacePlan;
import com.juchao.upg.android.entity.MaintenaceTask;
import com.juchao.upg.android.entity.MaintenaceTaskEquipmentItem;
import com.juchao.upg.android.util.ConstantUtil;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;

public class MaintenaceTaskDao {

	private static final String TAG = MaintenaceTaskDao.class.getSimpleName();

	private DBHelper mDbHelper;
	private SQLiteDatabase db = null;

	public MaintenaceTaskDao(Context context) {
		mDbHelper = new DBHelper(context);
		db = mDbHelper.getWritableDatabase();
	}

	public synchronized long insertMaintenaceTask(MaintenaceTask entity,
			ConstantUtil.WxEffectConfirmTask state) {
		long result = 0;
		long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);

		try {
			// boolean isExist = queryMaintenaceTaskIsExist(entity.id);
			boolean isExist = queryMaintenaceTaskIsExist(entity.id, userId);
			if (isExist) {
				if (state == ConstantUtil.WxEffectConfirmTask.WxEffectConfirmTaskState) {
					String args[] = { entity.id + "", userId + "" };
					ContentValues cv = new ContentValues();
					cv.put("state", 4);
					cv.put("taskState", 1);
					result = db.update(DBHelper.Maintenace_Task_Table_Name, cv,
							"id=? AND userId=?", args);
				}
				return result;

			} else {
				int taskState = 0;
				int curState = 0;
				if (state == ConstantUtil.WxEffectConfirmTask.WxEffectConfirmTaskState) {
					taskState = 4;
					curState = 1;
				}
				ContentValues cv = new ContentValues();
				cv.put("id", entity.id + "");
				cv.put("taskName", entity.taskName); // 维修任务名称
				// cv.put("startTime", entity.startTime);
				// cv.put("endTime", entity.endTime);
				cv.put("isDownloaded", "false");
				cv.put("taskState", curState);
				cv.put("state", taskState);
				cv.put("userId", userId + "");
				cv.put("planName", entity.plan.planName);
				cv.put("planId", entity.plan.id);
				result = db.insert(DBHelper.Maintenace_Task_Table_Name, null,
						cv);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			result = -1;
		}
		return result;
	}

	private boolean queryMaintenaceTaskIsExist(long id, long userId) {
		Cursor cs = null;
		try {
			String args[] = { id + "", userId + "" };

			cs = db.rawQuery("SELECT * FROM  "
					+ DBHelper.Maintenace_Task_Table_Name
					+ " WHERE id= ? AND userId = ?", args);
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

	public MaintenaceTask queryMaintenaceTaskByid(long taskId) {
		MaintenaceTask entity = null;
		Cursor cs = null;
		long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);
		try {
			String args[] = { taskId + "", userId + "" };
			cs = db.rawQuery("select * from "
					+ DBHelper.Maintenace_Task_Table_Name
					+ " WHERE id = ? AND userId=?", args);
			if (cs.getCount() > 0) {
				cs.moveToFirst();
				entity = new MaintenaceTask();
				entity.id = Long
						.parseLong(cs.getString(cs.getColumnIndex("id")));
				entity.taskName = cs.getString(cs.getColumnIndex("taskName"));
				entity.startTime = cs.getString(cs.getColumnIndex("startTime"));
				entity.endTime = cs.getString(cs.getColumnIndex("endTime"));
				entity.isDownloaded = Boolean.parseBoolean(cs.getString(cs
						.getColumnIndex("isDownloaded")));
				entity.state = cs.getInt(cs.getColumnIndex("state"));
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

	public synchronized boolean updateMaintenaceTask(MaintenaceTask entity) {
		long result = 0;
		try {
			long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);
			String args[] = { entity.id + "",userId +"" };
			ContentValues cv = new ContentValues();
			//cv.put("id", entity.id + "");
			cv.put("taskName", entity.taskName);
			cv.put("startTime", entity.startTime);
			cv.put("endTime", entity.endTime);
			cv.put("isDownloaded", entity.isDownloaded + "");
			cv.put("taskState", entity.state);
			
			//cv.put("userId", userId + "");
			result = db.update(DBHelper.Maintenace_Task_Table_Name, cv, "id=? and userId=?",
					args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result > 0 ? true : false;
	}

	public synchronized void closeDB() {
		if (db != null && db.isOpen()) {
			db.close();
		}

	}

	/**
	 * 
	 * @param PageSize
	 *            每页大小
	 * @param pageNum
	 *            页码1...
	 * @param state
	 *            1:全部(任务下载界面) 2:未完成 & 已下载,未维修 （维修界面） 3： 已维修， 未效果确认（待确认界面） 4:
	 *            所有已下载的任务（查询界面）
	 *            
	 *            0：刚进界面时的全部维修任务 ，1：已下载的维修任务。5.街上传的任务，6 已下载的确认任务。
	 * @return
	 */

	public synchronized List<MaintenaceTask> queryMaintenaceTaskList(
			int PageSize, int pageNum, int state,
			ConstantUtil.WxEffectConfirmTask taskState) {
		List<MaintenaceTask> list = null;
		MaintenaceTask entity = null;
		Cursor cs = null;
		String sql = "";
		if (taskState == ConstantUtil.WxEffectConfirmTask.WxEffectConfirmTaskState) {
			// state = ConstantUtil.WXEFFECTCONFIRMSTATE;
		}
		long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);
		try {
			if (state == 0) {
				String args[] = { userId + "" };
				sql = "SELECT * FROM  "
						+ DBHelper.Maintenace_Task_Table_Name
						+ " WHERE userId = ? and taskState=0 order by id DESC ";
				cs = db.rawQuery(sql, args);

			} else if (state == 1) {
				sql = "SELECT * FROM "
						+ DBHelper.Maintenace_Task_Table_Name
						+ " WHERE userId=? AND isDownloaded= ? AND state=? and taskState=0"
						+ " ORDER BY id DESC ";
				String args[] = { userId + "", "true", "0" };
				cs = db.rawQuery(sql, args);
			}
			// +++ @20141105 修改待确认状态
			else if (state == 2) {
				String args[] = { userId + "" };
				sql = "SELECT * FROM  " + DBHelper.Maintenace_Task_Table_Name
						+ " WHERE userId=? and taskState=1 ORDER BY id DESC";
				cs = db.rawQuery(sql, args);
			}

			else if (state == 3) {
				String args[] = { userId + "", "false" };
				sql = "SELECT * FROM  "
						+ DBHelper.Maintenace_Task_Table_Name
						+ " WHERE userId=? AND isDownloaded=? ORDER BY id DESC";
				cs = db.rawQuery(sql, args);
			} else if (state == 4) { // 任务查询
				String args[] = { userId + "", "true" };
				sql = "SELECT * FROM "
						+ DBHelper.Maintenace_Task_Table_Name
						+ " WHERE userId= ? AND isDownloaded=? ORDER BY id DESC";
				cs = db.rawQuery(sql, args);
			} else if (state == 5) {
				String args[] = { userId + "" };
				sql = "select *,b.startTime ST,B.endTime ET, a.id problemId,count(a.maintenaceTaskEquipmentId) EquipmentNum,count(a.id) ProblemNum "
						+ "from "
						+ DBHelper.Maintenace_Item_Table_Name
						+ " a join "
						+ DBHelper.Maintenace_Task_Table_Name
						+ " b on b.id=a.maintenaceTaskId where a.state "
						+ " in(3,4) and a.uploadState = 1 and b.userId=? group by a.maintenaceTaskId";
				cs = db.rawQuery(sql, args);
				
			} else if (state == 6) {
				String[] args = { "true", "4", userId + "" };
				sql = "SELECT * FROM "
						+ DBHelper.Maintenace_Task_Table_Name
						+ " WHERE isDownloaded=? and state=? and userId=? ORDER BY id DESC";
				cs = db.rawQuery(sql, args);
			}
			int recordNum = cs.getCount();
			int start = (pageNum - 1) * PageSize;
			int index = start;
			int end = PageSize * pageNum - 1;
			cs.moveToPosition(start - 1);
			if (recordNum < start) {
				return null;
			}
			list = new ArrayList<MaintenaceTask>();
			if (cs.getCount() > 0) {
				while (cs.moveToNext()) {
					if (index >= start && index <= end) {
						entity = new MaintenaceTask();

						entity.state = cs.getInt(cs.getColumnIndex("state"));
						entity.taskName = cs.getString(cs
								.getColumnIndex("taskName"));

						entity.startTime = cs.getString(cs
								.getColumnIndex("startTime"));
						entity.endTime = cs.getString(cs
								.getColumnIndex("endTime"));

						entity.isDownloaded = Boolean.parseBoolean(cs
								.getString(cs.getColumnIndex("isDownloaded")));

						if (state == 5) {
							entity.problemId = cs.getLong(cs
									.getColumnIndex("problemId"));
							entity.id = cs.getLong(cs
									.getColumnIndex("maintenaceTaskId"));
							entity.equipmentNum = cs.getInt(cs
									.getColumnIndex("EquipmentNum"));
							entity.problemNum = cs.getInt(cs
									.getColumnIndex("ProblemNum"));
							//entity.isDownloaded = true;
						} else {
							entity.id = Long.parseLong(cs.getString(cs
									.getColumnIndex("id")));
						}
						list.add(entity);
					} else {
						break;
					}
					index++;
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
	 * 根据任务ID，查询对应的维修项目数
	 * 
	 * @param taskId
	 * @return
	 */
	public int queryMaintenaceItemNum(long taskId) {

		int maintenaceItemNum = 0;
		List<Long> maintenaceEquipmentIdList = new ArrayList<Long>();
		Cursor cs = null;
		long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);
		try {
			String args[] = { taskId + "", 0 + "", userId + "" };
			cs = db.rawQuery("SELECT * FROM "
					+ DBHelper.Maintenace_Equipment_Table_Name
					+ " WHERE maintenaceTaskId =? AND "
					+ " maintenaceState = ? AND userId=? ", args);
			if (cs.getCount() > 0) {
				while (cs.moveToNext()) {
					long id = Long.parseLong(cs.getString(cs
							.getColumnIndex("id")));
					maintenaceEquipmentIdList.add(id);
				}
			}
			for (int i = 0; i < maintenaceEquipmentIdList.size(); i++) {
				String args2[] = { maintenaceEquipmentIdList.get(i) + "", "2" };
				cs = db.rawQuery("SELECT * FROM "
						+ DBHelper.Maintenace_Item_Table_Name
						+ " WHERE maintenaceTaskEquipmentId = ? AND state = ?",
						args);
				maintenaceItemNum += cs.getCount();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cs != null && !cs.isClosed()) {
				cs.close();
			}
		}
		return maintenaceItemNum;
	}

	/**
	 * 根据任务ID，查询对应的维修项目列表
	 * 
	 * @param taskId
	 * @return
	 */
	public List<MaintenaceTaskEquipmentItem> queryMaintenaceItemList(long taskId) {
		List<MaintenaceTaskEquipmentItem> maintenaceItemList = null;
		List<Long> maintenaceEquipmentIdList = new ArrayList<Long>();

		Cursor cs = null;
		try {
			String args[] = { taskId + "", 0 + "" };
			cs = db.rawQuery("SELECT * FROM "
					+ DBHelper.Maintenace_Equipment_Table_Name
					+ " WHERE maintenaceTaskId = ?" + " AND maintenactState=?",
					args);
			if (cs.getCount() > 0) {
				while (cs.moveToNext()) {
					long id = Long.parseLong(cs.getString(cs
							.getColumnIndex("id")));
					maintenaceEquipmentIdList.add(id);
				}
			}
			for (int i = 0; i < maintenaceEquipmentIdList.size(); i++) {
				String args2[] = { maintenaceEquipmentIdList.get(i) + "", "2" };
				cs = db.rawQuery("SELECT * FROM "
						+ DBHelper.Maintenace_Item_Table_Name
						+ " WHERE maintenaceTaskEquipmentId = ? AND state = ?",
						args2);
				if (cs.getCount() > 0) {
					maintenaceItemList = new ArrayList<MaintenaceTaskEquipmentItem>();
					while (cs.moveToNext()) {
						MaintenaceTaskEquipmentItem entity = new MaintenaceTaskEquipmentItem();
						entity.id = Long.parseLong(cs.getString(cs
								.getColumnIndex("id")));
						entity.equipmentId = Long.parseLong(cs.getString(cs
								.getColumnIndex("maintenaceTaskEquipmentId")));
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
						maintenaceItemList.add(entity);
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
		return maintenaceItemList;
	}

	public boolean updateMaintenaceItem(long maintenaceTaskItemId) {
		try {
			String sql = "update " + DBHelper.Maintenace_Item_Table_Name
					+ "s et state = 3 where id=" + maintenaceTaskItemId;
			db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public synchronized void removeCancelTask(ArrayList<Long> list,int taskState) {

		/**
		 * boolean isExist = queryMaintenaceTaskIsExist(entity.id); try{
		 * if(!isExist){ String [] args={entity.id + ""}; String
		 * sql="delete from " + DBHelper.Maintenace_Task_Table_Name
		 * +" WHERE id = ?"; db.execSQL(sql, args); } }catch(Exception e){
		 * e.printStackTrace(); return false; } return true;
		 */
		Cursor cs = null;
		try {
			long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);
			String[] args = {taskState+"",userId+""};
			String sql = "SELECT * FROM  "
					+ DBHelper.Maintenace_Task_Table_Name +" WHERE taskState=? and userId=?";
			cs = db.rawQuery(sql, args);
			if (cs.getCount() > 0) {
				while (cs.moveToNext()) {
					long id = Long.parseLong(cs.getString(cs
							.getColumnIndex("id")));
					if (!list.contains(id)) {
						sql = "DELETE FROM "
								+ DBHelper.Maintenace_Task_Table_Name
								+ " WHERE id=" + id +" and userId=" +userId;
						db.execSQL(sql);
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

	public List<MaintenaceTaskEquipmentItem> queryMaintenaceItemProblemName(
			long id) {
		List<MaintenaceTaskEquipmentItem> maintenaceItemList = null;
		String sql = "SELECT * FROM " + DBHelper.Maintenace_Item_Table_Name
				+ " WHERE id= ?";
		String[] args = { id + "" };
		Cursor cs = null;
		try {
			cs = db.rawQuery(sql, args);
			if (cs.getCount() > 0) {
				maintenaceItemList = new ArrayList<MaintenaceTaskEquipmentItem>();
				while (cs.moveToNext()) {
					MaintenaceTaskEquipmentItem entity = new MaintenaceTaskEquipmentItem();
					entity.problemName = cs.getString(cs
							.getColumnIndex("problemName"));
					maintenaceItemList.add(entity);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cs != null && !cs.isClosed()) {
				cs.close();
			}
		}
		return maintenaceItemList;
	}

	// 使用联级查询即可 @20141111
	public List<MaintenaceTaskEquipmentItem> queryMaintenaceItemList(long id,
			Object object) {
		List<MaintenaceTaskEquipmentItem> maintenaceItemList = null;
		List<Long> maintenaceEquipmentIdList = new ArrayList<Long>();

		Cursor cs = null;
		try {
			long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);
			String args[] = { userId + "" };
			String sql = "select a.startTime ,a.endTime,a.problemDescribe,a.imageNames,a.sparePartIds,a.id,a.state,count(a.maintenaceTaskEquipmentId) EquipmentNum,count(a.id) ProblemNum "
					+ "from "
					+ DBHelper.Maintenace_Item_Table_Name
					+ " a join "
					+ DBHelper.Maintenace_Task_Table_Name
					+ " b on b.id=a.maintenaceTaskId where a.state "
					+ " in(3,4) and a.uploadState = 1 and b.userId =? group by a.maintenaceTaskId";
			cs = db.rawQuery(sql, args);
			if (cs.getCount() > 0) {
				maintenaceItemList = new ArrayList<MaintenaceTaskEquipmentItem>();
				while (cs.moveToNext()) {
					MaintenaceTaskEquipmentItem entity = new MaintenaceTaskEquipmentItem();
					entity.id = cs.getLong(cs.getColumnIndex("id"));
					entity.startTime = cs.getString(cs
							.getColumnIndex("startTime"));
					entity.endTime = cs.getString(cs.getColumnIndex("endTime"));
					//entity.problemDescribe = cs.getString(cs
					//.getColumnIndex("problemDescribe"));
					entity.faultDescribe= cs.getString(cs.getColumnIndex("problemDescribe"));
					
					entity.imageIds = cs.getString(cs
							.getColumnIndex("imageNames"));
					entity.sparePartIds = cs.getString(cs
							.getColumnIndex("sparePartIds"));
					entity.state = cs.getInt((cs.getColumnIndex("state")));
					maintenaceItemList.add(entity);
				}
			}

			/**
			 * String args[] = { taskId + "", 0 + "" }; cs =
			 * db.rawQuery("SELECT * FROM " +
			 * DBHelper.Maintenace_Equipment_Table_Name +
			 * " WHERE maintenaceTaskId = ?" + " AND maintenactState=?", args);
			 * if (cs.getCount() > 0) { while (cs.moveToNext()) { long id =
			 * Long.parseLong(cs.getString(cs .getColumnIndex("id")));
			 * maintenaceEquipmentIdList.add(id); } } for (int i = 0; i <
			 * maintenaceEquipmentIdList.size(); i++) { String args2[] = {
			 * maintenaceEquipmentIdList.get(i) + "", "2" }; cs =
			 * db.rawQuery("SELECT * FROM " +
			 * DBHelper.Maintenace_Item_Table_Name +
			 * " WHERE maintenaceTaskEquipmentId = ? AND state = ?", args2); if
			 * (cs.getCount() > 0) { maintenaceItemList = new
			 * ArrayList<MaintenaceTaskEquipmentItem>(); while (cs.moveToNext())
			 * { MaintenaceTaskEquipmentItem entity = new
			 * MaintenaceTaskEquipmentItem(); entity.id =
			 * Long.parseLong(cs.getString(cs .getColumnIndex("id")));
			 * entity.equipmentId = Long.parseLong(cs.getString(cs
			 * .getColumnIndex("maintenaceTaskEquipmentId")));
			 * entity.equipmentSpotcheckId = Long .parseLong(cs.getString(cs
			 * .getColumnIndex("equipmentSpotcheckId")));
			 * entity.equipmentSpotcheckName = cs.getString(cs
			 * .getColumnIndex("equipmentSpotcheckName")); entity.result =
			 * cs.getString(cs .getColumnIndex("result")); entity.faultDescribe
			 * = cs.getString(cs .getColumnIndex("faultDescribe")); entity.state
			 * = cs.getInt(cs.getColumnIndex("state")); entity.costTime =
			 * cs.getInt(cs .getColumnIndex("costTime")); entity.startTime =
			 * cs.getString(cs .getColumnIndex("startTime")); entity.endTime =
			 * cs.getString(cs .getColumnIndex("endTime")); entity.imageNames =
			 * cs.getString(cs .getColumnIndex("imageNames"));
			 * maintenaceItemList.add(entity); } } }
			 */
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cs != null && !cs.isClosed()) {
				cs.close();
			}
		}
		return maintenaceItemList;
	}

	public boolean updateMaintenaceItem(Long maintenaceTaskItemId, Object object) {
		try {
			String sql = "update " + DBHelper.Maintenace_Item_Table_Name
					+ " set uploadState= 2 where id=" + maintenaceTaskItemId;
			db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	public String queryPlanName(Long taskId) {

		String sql;
		Cursor cs = null;
		String[] args = { taskId + "" };
		MaintenacePlan plan = new MaintenacePlan();
		try {
			sql = "SELECT * FROM  " + DBHelper.Maintenace_Task_Table_Name
					+ " WHERE id=?";
			cs = db.rawQuery(sql, args);
			if (cs.getCount() > 0) {
				while (cs.moveToNext()) {
					plan.planName = cs.getString(cs.getColumnIndex("planName"));
					plan.id = Long.parseLong(cs.getString(cs
							.getColumnIndex("planId")));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cs != null && !cs.isClosed()) {
				cs.close();
			}
		}
		return plan.planName;
	}

}
