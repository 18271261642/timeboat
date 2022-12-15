package com.imlaidian.utilslibrary.utils;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

import java.lang.reflect.Field;

import androidx.core.content.ContextCompat;

public class DisplayUtil {
	/**
	 * 将px值转换为dip或dp值，保证尺寸大小不变
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;

		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将dip或dp值转换为px值，保证尺寸大小不变
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;

		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;

		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;

		return (int) (spValue * fontScale + 0.5f);
	}


	/**
	 * 各种单位转换
	 * <p>该方法存在于TypedValue</p>
	 *
	 * @param unit    单位
	 * @param value   值
	 * @param metrics DisplayMetrics
	 * @return 转换结果
	 */
	public static float applyDimension(int unit, float value, DisplayMetrics metrics) {
		switch (unit) {
			case TypedValue.COMPLEX_UNIT_PX:
				return value;
			case TypedValue.COMPLEX_UNIT_DIP:
				return value * metrics.density;
			case TypedValue.COMPLEX_UNIT_SP:
				return value * metrics.scaledDensity;
			case TypedValue.COMPLEX_UNIT_PT:
				return value * metrics.xdpi * (1.0f / 72);
			case TypedValue.COMPLEX_UNIT_IN:
				return value * metrics.xdpi;
			case TypedValue.COMPLEX_UNIT_MM:
				return value * metrics.xdpi * (1.0f / 25.4f);
		}
		return 0;
	}

	private static  String[] permissionList = {
			Manifest.permission.WRITE_EXTERNAL_STORAGE ,
			Manifest.permission.READ_PHONE_STATE,
			Manifest.permission.SET_TIME_ZONE,
			Manifest.permission.INTERNET,
			//下面的权限获取不了
//			Manifest.permission.INSTALL_PACKAGES,
//			Manifest.permission.DELETE_PACKAGES,
//			Manifest.permission.INSTALL_LOCATION_PROVIDER,
//			Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS ,
//			Manifest.permission.READ_LOGS ,
//			Manifest.permission.SET_DEBUG_APP ,
//			Manifest.permission.WRITE_SETTINGS ,
//			Manifest.permission.SET_TIME ,
//			,Manifest.permission.REBOOT
			Manifest.permission.CHANGE_WIFI_MULTICAST_STATE ,
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.CHANGE_WIFI_STATE,
			Manifest.permission.ACCESS_NETWORK_STATE,
			Manifest.permission.ACCESS_WIFI_STATE ,
			Manifest.permission.CALL_PHONE ,
			Manifest.permission.ACCESS_FINE_LOCATION ,
			Manifest.permission.ACCESS_COARSE_LOCATION ,
			Manifest.permission.GET_ACCOUNTS ,
			Manifest.permission.RECEIVE_BOOT_COMPLETED ,
			Manifest.permission.DISABLE_KEYGUARD ,
			Manifest.permission.SET_TIME_ZONE ,
			Manifest.permission.WAKE_LOCK ,
			Manifest.permission.KILL_BACKGROUND_PROCESSES


	} ;



	public static void checkPermission(final Context context){
		for(String item : permissionList){
			LogUtil.d("DisplayUtil" , "checkPermission item= " + item);
			if (ContextCompat.checkSelfPermission(context, item)
					!= PackageManager.PERMISSION_GRANTED ){
				LogUtil.d("DisplayUtil" , "checkPermission not have granted " );
				showAlertDialogTip(context );
				break;
			}
		}


	}


	public static void showAlertDialogTip( Context context){
		Uri uri = Uri.parse("package:" + context.getPackageName());//包名
		Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", uri);
		context.startActivity(intent);
	}

	// 悬浮框 权限请求
	private void requestAlertWindowPermission( Context context) {
		if (Build.VERSION.SDK_INT >= 23) {
			Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
			intent.setData(Uri.parse("package:" + context.getPackageName()));
			context.startActivity(intent);
		}
	}


	// 获取屏幕的宽度
	public static int getScreenWidth(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	// 获取屏幕的宽度
	public static int getScreenWidth(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	// 获取屏幕的高度
	public static int getScreenHeight(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}

	//获取手机状态栏高度
	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;
	}

	//获取像素密度dpi
	public static int getDensityDpi(Context context){
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		return dm.densityDpi;
	}

	//获取像素密度dpi
	public static int getDensityDpi(Activity activity){
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.densityDpi;
	}

	//获取像素密度
	public static float getScaledDensity(Context context){
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		return dm.scaledDensity;
	}

	//获取像素密度
	public static float getScaledDensity(Activity activity){
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.scaledDensity;
	}
}
