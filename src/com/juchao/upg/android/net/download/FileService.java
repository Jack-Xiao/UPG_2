package com.juchao.upg.android.net.download;

import java.util.Map;

import android.content.Context;


/**
 * 业务bean
 * 
 */
public class FileService {
//	private DBHelper mDBHelper;

	public FileService(Context context) {
//		mDBHelper = new DBHelper(context);
	}

	/**
	 * 获取每条线程已经下载的文件长度
	 * 
	 * @param path
	 * @return
	 */
	public Map<Integer, Integer> getData(String path) {
//		SQLiteDatabase db = mDBHelper.getReadableDatabase();
//		Cursor cursor = db
//				.rawQuery(
//						"select threadid, downlength from filedownlog where downpath=?",
//						new String[] { path });
//		Map<Integer, Integer> data = new HashMap<Integer, Integer>();
//		while (cursor.moveToNext()) {
//			data.put(cursor.getInt(0), cursor.getInt(1));
//		}
//		cursor.close();
//		db.close();
//		return data;
		return null;
	}

	/**
	 * 保存每条线程已经下载的文件长度
	 * 
	 * @param path
	 * @param map
	 */
	public void save(String path, Map<Integer, Integer> map) {// int threadid,
																// int position
//		SQLiteDatabase db = mDBHelper.getWritableDatabase();
//		db.beginTransaction();
//		try {
//			for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
//				db.execSQL(
//						"insert into filedownlog(downpath, threadid, downlength) values(?,?,?)",
//						new Object[] { path, entry.getKey(), entry.getValue() });
//			}
//			db.setTransactionSuccessful();
//		} finally {
//			db.endTransaction();
//		}
//		db.close();
	}

	/**
	 * 实时更新每条线程已经下载的文件长度
	 * 
	 * @param path
	 * @param map
	 */
	public void update(String path, int threadId, int pos) {
//		SQLiteDatabase db = mDBHelper.getWritableDatabase();
//		db.execSQL(
//				"update filedownlog set downlength=? where downpath=? and threadid=?",
//				new Object[] { pos, path, threadId });
//		db.close();
	}

	/**
	 * 当文件下载完成后，删除对应的下载记录
	 * 
	 * @param path
	 */
	public void delete(String path) {
//		SQLiteDatabase db = mDBHelper.getWritableDatabase();
//		db.execSQL("delete from filedownlog where downpath=?",
//				new Object[] { path });
//		db.close();
	}

}
