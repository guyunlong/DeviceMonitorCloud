package com.Util;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

public class Utils {
	public static String PREFERENCES_NAME = "rtmpplayer";
	//public static String sIp = "dvr.dragra.com";
	public static String sIp = "www.mowa-cloud.com";
	//public static String sIp = "139.196.178.31";
	//public static String sIp = "ivs2.carvedge.com";
	public static  void showDialog(Context context,String msg){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(msg)
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
	public static  void showToast(Context context,String msg){
			Toast.makeText(context,msg, Toast.LENGTH_LONG).show();
	}
	public static  int convertDpToPixel(Context con,float dp) {
		DisplayMetrics metrics = con.getResources().getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return (int) px;
	}
	public static boolean hasConnection(Context context) {
		//
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				//
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					//
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			Log.v("error",e.toString());
		}
		return false;
	}

	/**
	 * 获取SD卡状态
	 * @return true - SD卡已安装，false - SD卡未安装
	 */
	public static boolean checkSD() {
		return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}
	/**
	 * 获取SD卡根目录路径
	 * @return 表示根目录路径字符串（末尾不带“/”，如"/sdcard"),若SD卡未安装，则返回null
	 */
	public static String getSDRootPath() {
		if(!checkSD()) {
			return null;
		} else {
			return Environment.getExternalStorageDirectory().getPath();
		}
	}
	/**
	 * 创建目录
	 * @param dirPath 目录路径（末尾不带“/”，如"/sdcard"）
	 * @return true - 创建成功，false - 创建失败
	 */
	public static boolean makeDir(String dirPath) {
		boolean isSuccessful = true;
		File dir = new File(dirPath);
		if(!dir.exists()) {
			isSuccessful = dir.mkdirs();
		}
		return isSuccessful;
	}
	public static void delFiles(String path) {
		File file = new File(path);
		if(!file.exists()) { return; }
		File[] files = file.listFiles();
		for(int i = 0; i < files.length; i++) {
			files[i].delete();
		}
	}



}
