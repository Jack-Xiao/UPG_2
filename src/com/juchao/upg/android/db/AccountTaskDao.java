package com.juchao.upg.android.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.juchao.upg.android.entity.AccountEquipment;
import com.juchao.upg.android.entity.AccountTask;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;

public class AccountTaskDao {
	private Context mContext;
	private SQLiteDatabase db = null;
	private DBHelper mDbHelper;
	public static final int ACCOUNT_TASK_NOT_FINISH_STATUS = 0;
	public static final int ACCOUNT_TASK_HAS_FINISH_STATUS = 1;

	public AccountTaskDao(Context mContext) {
		this.mContext = mContext;
		mDbHelper = new DBHelper(mContext);
		db = mDbHelper.getWritableDatabase();
	}

	@SuppressLint("SimpleDateFormat")
	public synchronized long insertAccountTask(AccountTask entity) {
		long result = 0;
		long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);

//		Calendar calendar=Calendar.getInstance();
//		long unixTime=calendar.getTimeInMillis();
//		long unixTimeGMT=unixTime-TimeZone.getDefault().getRawOffset();
//		
//		Date date=new Date(unixTimeGMT);
//		
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		String date1=format.format(date) ;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("GMT+0"));

		try {
			if (ExistAccoutTask(entity.id, userId)) {
				return result;
			}
			ContentValues cv = new ContentValues();
			cv.put("id", entity.id + "");
			cv.put("name", entity.name);
			cv.put("status", ACCOUNT_TASK_NOT_FINISH_STATUS);
			cv.put("startTime", format.format(entity.startTime));
			cv.put("endTime",
					entity.endTime == null ? "" : format.format(entity.endTime));
			

			
			cv.put("userId", userId);
			cv.put("isDownloaded", false + "");
			cv.put("range", entity.range);
			cv.put("total", entity.total);
			cv.put("taskStatus", ACCOUNT_TASK_NOT_FINISH_STATUS);
			result = db.insert(DBHelper.Account_Task_Table_Name, null, cv);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	private boolean ExistAccoutTask(long id, long userId) {
		String sql = "";
		Cursor cs = null;

		String args[] = { id + "", userId + "" };
		sql = "SELECT * FROM " + DBHelper.Account_Task_Table_Name
				+ " WHERE id=? and userId=?";
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

	/**
	 * 删除已经完成的任务
	 * 
	 * @param taskIds
	 */
	public void removeFinishAccountTask(List<Long> taskIds) {
		long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);
		String[] args = { userId + "" };
		String sql = "SELECT * FROM " + DBHelper.Account_Task_Table_Name
				+ " WHERE userId=?";
		Cursor cs = null;
		try {
			cs = db.rawQuery(sql, args);
			if (cs.getCount() > 0) {
				while (cs.moveToNext()) {
					long id = Long.parseLong(cs.getString(cs
							.getColumnIndex("id")));
					if (!taskIds.contains(id)) {
						deleteInspectionTask(id, userId);
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

	private void deleteInspectionTask(long id, long userId) {
		try {
			String sql = "DELETE FROM " + DBHelper.Account_Task_Table_Name
					+ " WHERE id=" + id + " and userId=" + userId;
			db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * state: 0 全部， 1， 未完成&已下载 2.已完成
	 * 
	 * @param pageSize
	 * @param pageNum
	 * @param state
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public List<AccountTask> queryAccountTaskList(int pageSize, int pageNum,
			int state) {
		List<AccountTask> list = null;
		AccountTask entity = null;
		Cursor cs = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);

		try {
			if (state == 0) {
				String sql = "SELECT * FROM "
						+ DBHelper.Account_Task_Table_Name
						+ " WHERE userId=? and status =0 ORDER BY _id DESC";
				String[] args = { userId + "" };
				cs = db.rawQuery(sql, args);
			} else if (state == 1) {
				String[] args = { userId + "", true + "",
						ACCOUNT_TASK_NOT_FINISH_STATUS + "" };
				String sql = "SELECT * FROM "
						+ DBHelper.Account_Task_Table_Name
						+ " WHERE userId=? and isDownloaded=? and status=? order by _id DESC";
				cs = db.rawQuery(sql, args);
			}

			int recordNum = cs.getCount();
			int start = (pageNum - 1) * pageSize;
			int index = start;
			int end = pageSize * pageNum - 1;
			cs.moveToPosition(start - 1);
			if (recordNum < start) {
				return null;
			}
			list = new ArrayList<AccountTask>();
			while (cs.moveToNext()) {
				if (index >= start && index <= end) {
					entity = new AccountTask();
					entity.id = Long.parseLong(cs.getString(cs
							.getColumnIndex("id")));
					entity.name = cs.getString(cs.getColumnIndex("name"));
					entity.total = Integer.parseInt(cs.getString(cs
							.getColumnIndex("total")));
					entity.isDownloaded = Boolean.parseBoolean(cs.getString(
							cs.getColumnIndex("isDownloaded")).trim());
					entity.startTime = format.parse(cs.getString(cs
							.getColumnIndex("startTime")));
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

	public synchronized void closeDB() {
		if (db != null && db.isOpen()) {
			db.close();
		}
	}

	public synchronized long insertEquipment(AccountEquipment entity) {
		long result = 0;
		long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);
		long uploadStatus = 0;
		try {
			ContentValues cv = new ContentValues();
			cv.put("id", entity.id);
			cv.put("accountId", entity.accountId);
			cv.put("equipmentId", entity.equipmentId);
			cv.put("assetCode", queryAssetCode(entity.equipmentId));
			cv.put("equNum",queryAssetCode1(entity.equipmentId) );
			cv.put("status", entity.status);
			if(entity.status == 1){
				uploadStatus=1;
			}
			cv.put("uploadStatus", uploadStatus);
			
			cv.put("userId", userId);
			result = db.insert(DBHelper.Account_Equipment_Table_Name, null, cv);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private String queryAssetCode(long equipmentId) {
		String result = null;
		Cursor cs = null;
		try {
			String sql = "SELECT * FROM " + DBHelper.Base_Equipment_Table_Name
					+ " WHERE id=" + equipmentId;
			cs = db.rawQuery(sql, null);
			while (cs.moveToNext()) {
				result = cs.getString(cs.getColumnIndex("managementOrgNum"));
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
	
	private String queryAssetCode1(long equipmentId) {
		String result = null;
		Cursor cs = null;
		try {
			String sql = "SELECT * FROM " + DBHelper.Base_Equipment_Table_Name
					+ " WHERE id=" + equipmentId;
			cs = db.rawQuery(sql, null);
			while (cs.moveToNext()) {
				result = cs.getString(cs.getColumnIndex("equNum"));
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

	public void updateAccountTask(long taskId) {
		long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);
		String args[] = { true + "", userId + "", taskId + "" };
		String sql = "UPDATE " + DBHelper.Account_Task_Table_Name
				+ " set isDownloaded =?" + " WHERE userId=? and id=?";
		db.execSQL(sql, args);
	}

	/**
	 * 0 全部设备 1 已盘点设备 2 未盘点设备
	 * 
	 * @param taskId
	 * @param i
	 * @return
	 */
	@SuppressWarnings("null")
	public List<AccountEquipment> queryTaskEquipment(long taskId, int status) {
		long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);
		List<AccountEquipment> list = new ArrayList<AccountEquipment>();
		AccountEquipment entity;
		Cursor cs = null;

		try {
			if (status == 0) {
				String args[] = { userId + "", taskId + "" };
				String sql = "SELECT* from "
						+ DBHelper.Account_Equipment_Table_Name + " WHERE"
						+ " userId=? and accountId=? order by id";
				cs = db.rawQuery(sql, args);

			} else if (status == 1) {
				String args[] = { userId + "", taskId + "", "1" };
				String sql = "SELECT* from "
						+ DBHelper.Account_Equipment_Table_Name + " WHERE"
						+ " userId=? and accountId=? and status=?  order by id";
				cs = db.rawQuery(sql, args);

			} else if (status == 2) {
				String args[] = { userId + "", taskId + "", "0" };
				String sql = "SELECT* from "
						+ DBHelper.Account_Equipment_Table_Name + " WHERE"
						+ " userId=? and accountId=? and status=?  order by id";
				cs = db.rawQuery(sql, args);
			}

			while (cs.moveToNext()) {
				entity = new AccountEquipment();
				entity.accountId = Long.parseLong(cs.getString(cs
						.getColumnIndex("accountId")));
				entity.assetCode = cs.getString(cs.getColumnIndex("assetCode"));
				entity.equNum = cs.getString(cs.getColumnIndex("equNum"));
				entity.equipmentName = queryEquipmentName(Long.parseLong(cs
						.getString(cs.getColumnIndex("equipmentId"))));
				entity.status = Integer.parseInt(cs.getString(cs
						.getColumnIndex("status")));
				list.add(entity);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cs != null && cs.isClosed()) {
				cs.close();
			}
		}
		return list;
	}

	public String queryEquipmentName(long equipmentId) {
		String result = "";
		Cursor cs = null;
		String[] args = { equipmentId + "" };
		try {
			String sql = "SELECT * FROM " + DBHelper.Base_Equipment_Table_Name
					+ " WHERE " + " id=?";
			cs = db.rawQuery(sql, args);
			while (cs.moveToNext()) {
				result = cs.getString(cs.getColumnIndex("equipmentName"));
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (cs != null && cs.isClosed()) {
				cs.close();
			}
		}
		return result;
	}

	@SuppressLint("SimpleDateFormat")
	public List<AccountEquipment> handleScan(String scanName, long taskId) {
		List<AccountEquipment> list = new ArrayList<AccountEquipment>();
		AccountEquipment entity;

		long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);
		Cursor cs = null;
		long locId;
		
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		format.setTimeZone(TimeZone.getTimeZone("GMT+0"));
//		Date date=new Date(Calendar.getInstance().getTimeInMillis());
//		String date1=format.format(date);
		
		Calendar calendar=Calendar.getInstance();
		long unixTime=calendar.getTimeInMillis();
		
		try {
			String[] args = { scanName };
			String sql = "SELECT * FROM "
					+ DBHelper.Account_Equipment_Table_Name
					+ " WHERE status=0 and accountId=" + taskId
					+ " and userId=" + userId + " AND assetCode=?";
			cs = db.rawQuery(sql, args);
			if (cs.getCount() > 0) {
				cs.moveToFirst();
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");

				locId = Long.parseLong(cs.getString(cs.getColumnIndex("_id")));
				entity = new AccountEquipment();
				entity.accountId = Long.parseLong(cs.getString(cs
						.getColumnIndex("accountId")));
				entity.assetCode = cs.getString(cs.getColumnIndex("assetCode")); // 条码编号
				entity.equNum= cs.getString(cs.getColumnIndex("equNum")); // 设备编号
				entity.equipmentName = queryEquipmentName(Long.parseLong(cs
						.getString(cs.getColumnIndex("equipmentId"))));
				entity.status = 1;
				entity.equipmentId = Long.parseLong(cs.getString(cs
						.getColumnIndex("equipmentId")));

				entity.scanCodeTime = new Date(unixTime);
				list.add(entity);
				String currTime = format.format(entity.scanCodeTime);

				sql = "UPDATE " + DBHelper.Account_Equipment_Table_Name
						+ " SET " + "scanCodeTime ='" + currTime
						+ "',status=1 WHERE _id=" + locId;
				db.execSQL(sql);
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

	public void updateAccountTaskItemUploadStatus(AccountEquipment item) {
		try {
			long equipmentId = item.equipmentId;
			long taskId = item.accountId;

			long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);
			String sql = "update " + DBHelper.Account_Equipment_Table_Name
					+ " set uploadStatus=1 where accountId=" + taskId
					+ " and equipmentId=" + equipmentId + " and userId="
					+ userId;
			db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * status 0 未采集 / 1 已采集 uploadStatus 0 未上传 1 已上传
	 * 
	 * @param id
	 * @return
	 */
	public int queryAccountWaitUploadItemNum(long id) {
		long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);
		int result = 0;
		Cursor cs = null;
		String sql = "SELECT * FROM " + DBHelper.Account_Equipment_Table_Name
				+ " WHERE accountId=" + id + " AND userId=" + userId
				+ " AND status=1 AND uploadStatus=0";
		try {
			cs = db.rawQuery(sql, null);
			result = cs.getCount();

		} catch (Exception e) {
			e.printStackTrace();
			result = -1;
		}
		return result;
	}

	@SuppressLint("SimpleDateFormat")
	public List<AccountEquipment> queryAccountTaskNeedUploadItemList(long taskId) {
		List<AccountEquipment> list = new ArrayList<AccountEquipment>();
		Cursor cs = null;
		AccountEquipment entity;
		long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			
			String sql = "SELECT * FROM "
					+ DBHelper.Account_Equipment_Table_Name
					+ " WHERE accountId=" + taskId + " AND userId=" + userId
					+ " AND status=1 AND uploadStatus=0";
			cs = db.rawQuery(sql, null);
			if (cs.getCount() > 0) {
				while (cs.moveToNext()) {
					entity = new AccountEquipment();
					entity.accountId = Long.parseLong(cs.getString(cs
							.getColumnIndex("accountId")));
					entity.scanCodeTime = format.parse(cs.getString(cs
							.getColumnIndex("scanCodeTime")));
					entity.status = Integer.parseInt(cs.getString(cs
							.getColumnIndex("status")));
					entity.equipmentId = Long.parseLong(cs.getString(cs
							.getColumnIndex("equipmentId")));
					entity.equipmentName = queryEquipmentName(Long.parseLong(cs.getString(cs.getColumnIndex("equipmentId"))));
					list.add(entity);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public void updateAccountItem(long id) {
		long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);
		String sql = "UPDATE " + DBHelper.Account_Equipment_Table_Name
				+ " SET uploadStatus=1 WHERE id=" + id + " AND userId="
				+ userId;
		try {
			db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateAccountTask(AccountTask task) {
		long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);
		long accountId = task.id;
		String sql = "UPDATE " + DBHelper.Account_Task_Table_Name
				+ " SET status=1 WHERE userId=" + userId + " AND id=" + accountId;
		db.execSQL(sql);
	}

	public void closeAccountTask(long taskId) {
		long userId = DefaultShared.getLong(Constants.KEY_USER_ID, 0L);

		String sql = "UPDATE " + DBHelper.Account_Task_Table_Name
				+ " SET status=1 WHERE userId=" + userId + " AND id=" + taskId;
		db.execSQL(sql);

	}

	public boolean queryScan(String scan, long taskId) {
		Cursor cs = null;
		boolean result = false;
		try {
			String[] args = { scan};
			String sql = "SELECT * FROM "
					+ DBHelper.Account_Equipment_Table_Name
					+ " WHERE status=1 and accountId=" + taskId
					+ " AND assetCode=?";
			cs = db.rawQuery(sql, args);
			if (cs.getCount() > 0) {
				result = true;
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
}
