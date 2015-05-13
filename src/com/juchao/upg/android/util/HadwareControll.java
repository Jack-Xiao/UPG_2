package com.juchao.upg.android.util;

import android.os.Handler;
import android.os.Message;

import com.iData.Scan.BarcodeControll;

public class HadwareControll {
	public static BarcodeControll barcodeControll = new BarcodeControll();
	static public Handler m_handler = null;
	static public final int BARCODE_READ = 10;
	private static boolean m_stop = false;
	private static boolean start_Scan=false;
	
	public static final boolean IsIScan;
	
	static {
		if(barcodeControll !=null){
			IsIScan = true;
		}else{
			barcodeControll = new BarcodeControll();
			if(barcodeControll !=null){
				IsIScan = true;
			}else{
				IsIScan = false;
			}
		}
	}
	
	public static void Open() {

		barcodeControll.Barcode_open();
		m_stop = false;
		start_Scan=false;
		new BarcodeReadThread().start();
	}

	public static void Close() {
		m_stop = true;
		start_Scan=false;
		barcodeControll.Barcode_Close();
	}

	public static void scan_start() {
		barcodeControll.Barcode_StarScan();
		start_Scan=true;
	}

	public static void scan_stop() {

		barcodeControll.Barcode_StopScan();
	}

	static class BarcodeReadThread extends Thread {
		public void run() {
			// TODO Auto-generated method stub
			while (!m_stop) {
				try {
					Thread.sleep(50);
					ReadBarcodeID();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}

	public static void ReadBarcodeID() {
		String info = null;
		byte[] buffer = null;
		buffer = barcodeControll.Barcode_Read();
		try {
		info = new String(buffer, "GBK");
		}catch (java.io.UnsupportedEncodingException e) {
			e.printStackTrace();
		}
				
		if (info.length() > 0 && m_handler != null&&start_Scan) {
		
			Message msg = new Message();
			msg.what = BARCODE_READ;
			msg.obj = info;
			System.out.println("msg.obj=" + msg.obj);
			m_handler.sendMessage(msg);
		}
	}
}
