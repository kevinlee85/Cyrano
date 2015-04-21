package com.psktechnology.constant;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class AppGlobal {
	
	public static final String PREFS_NAME = "BusinessCardManagerApp";
	public static ProgressDialog progressDialog;
	
	public static void showToast(Activity activity, String msg) {
		Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }
	
	public static void showProgressDialog(Activity activity, String msg) {
		progressDialog = new ProgressDialog(activity);
		progressDialog.setMessage(msg);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
	}
	
	public static void dismissProgressDialog(Activity activity) {
		if (progressDialog != null)
			progressDialog.dismiss();
	}
	
	public static boolean isNetworkConnected(Activity activity) {
		ConnectivityManager connectivity = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}

		}
		return false;
	}
	
	public static boolean setStringPreference(Activity activity, String key, String value) {
		SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(key, value);
		return editor.commit();
	}
	public static String getStringPreference(Activity activity, String key) {
		SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		String value = settings.getString(key, "");
		return value;
	}
	
	public static boolean setIntegerPreference(Activity activity, String key, Integer value) {
		SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(key, value);
		return editor.commit();
	}
	public static int getIntegerPreference(Activity activity, String key) {
		SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		int value = settings.getInt(key, -1);
		return value;
	}
	
	public static boolean setBooleanPreference(Activity activity, String key, Boolean value) {
		SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(key, value);
		return editor.commit();
	}
	public static Boolean getBooleanPreference(Activity activity, String key) {
		SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Boolean value = settings.getBoolean(key, false);
		return value;
	}
	
	public static Typeface setFontAwesomeFonts(Activity activity) {
		Typeface typeface = Typeface.createFromAsset(activity.getAssets(), "fontawesome_webfont.ttf");
		return typeface;
    }

}