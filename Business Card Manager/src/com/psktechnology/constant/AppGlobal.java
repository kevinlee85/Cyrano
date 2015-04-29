package com.psktechnology.constant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
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
	
	public static Typeface setFontStyle1(Activity activity) {
		Typeface typeface = Typeface.createFromAsset(activity.getAssets(), "fontstyle1.ttf");
		return typeface;
    }
    public static Typeface setFontStyle2(Activity activity) {
		Typeface typeface = Typeface.createFromAsset(activity.getAssets(), "fontstyle2.ttf");
		return typeface;
    }
    public static Typeface setFontStyle3(Activity activity) {
		Typeface typeface = Typeface.createFromAsset(activity.getAssets(), "fontstyle3.ttf");
		return typeface;
    }
    public static Typeface setFontStyle4(Activity activity) {
		Typeface typeface = Typeface.createFromAsset(activity.getAssets(), "fontstyle4.ttf");
		return typeface;
    }
    
    //	TODO capture required view as Business card image and go to target class
	public static void saveBusinessCard(Activity activity, View view, String cid) {
		
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Log.d("MyApp", "No SDCARD");
		} else {
			File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "BCard");
			if(!directory.exists())
				directory.mkdirs();
			
			File subdir = new File(Environment.getExternalStorageDirectory() + File.separator + "BCard" + File.separator + "Card");
			if (!subdir.exists())
				subdir.mkdirs();
			
			String mPath = Environment.getExternalStorageDirectory() + File.separator
							+ "BCard" + File.separator
							+ "Card" + File.separator;

			// create bitmap screen capture
			View v1 = view;
			v1.setDrawingCacheEnabled(true);

			Bitmap bitmap;
			bitmap = Bitmap.createBitmap(v1.getDrawingCache());

			v1.setDrawingCacheEnabled(false);

			OutputStream fout = null;
			File imageFile = new File(mPath, "bcard" + cid + ".png");

			try {
				fout = new FileOutputStream(imageFile);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fout);
				fout.flush();
				fout.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	//	TODO save user pic on device
	public static void saveUserPic(Activity activity, Bitmap bitmap, String cid) {
		
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Log.d("MyApp", "No SDCARD");
		} else {
			File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "BCard");
			if(!directory.exists())
				directory.mkdirs();
			
			File subdir = new File(Environment.getExternalStorageDirectory() + File.separator + "BCard" + File.separator + "User");
			if (!subdir.exists())
				subdir.mkdirs();
			
			String mPath = Environment.getExternalStorageDirectory() + File.separator
							+ "BCard" + File.separator
							+ "User" + File.separator;

			OutputStream fout = null;
			File imageFile = new File(mPath, "user" + cid + ".png");

			try {
				fout = new FileOutputStream(imageFile);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fout);
				fout.flush();
				fout.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	//	TODO get captured Business card image and return it as bitmap
	public static Bitmap getBusinessCard(Activity activity) {
		
		File localFile = new File(activity.getCacheDir(), "businesscard.png");
        localFile.setReadable(true, false);
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath(), options);
		
		return bitmap;
		
	}
	
	public static void shareText(Activity activity, String cId) {
		try
        {
			String path = Environment.getExternalStorageDirectory() + File.separator
							+ "BCard" + File.separator
							+ "Card" + File.separator;
			File localFile = new File(path, "bcard" + cId + ".png");
			localFile.setReadable(true, false);
			
			Intent sendIntent = new Intent(Intent.ACTION_SEND); 
			sendIntent.setClassName("com.android.mms", "com.android.mms.ui.ComposeMessageActivity");
			sendIntent.putExtra(Intent.EXTRA_STREAM, "file:/" + Uri.fromFile(localFile));
			sendIntent.setType("image/png");
			activity.startActivity(sendIntent);
			
           /* Intent i = new Intent(Intent.ACTION_SEND);
            i.putExtra(Intent.EXTRA_STREAM, "file:/" + Uri.fromFile(localFile));
            i.setType("image/png");
            activity.startActivity(i); */
        }
        catch (Exception localException)
        {
          for (;;)
          {
            Log.d("generateCustomChooserIntent", "Exception: File not found");
          }
        }
	}
	
	public static void shareEmail(Activity activity, String cId) {
		
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");

		try {
			String path = Environment.getExternalStorageDirectory() + File.separator
							+ "BCard" + File.separator
							+ "Card" + File.separator;
			File localFile = new File(path, "bcard" + cId + ".png");
			localFile.setReadable(true, false);
			
			intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(localFile));
			try {
				activity.startActivity(Intent.createChooser(intent, "Share with"));
			} catch (android.content.ActivityNotFoundException ex) {
				AppGlobal.showToast(activity, "Email have not been installed");
			}
		} catch (Exception localException) {
			for (;;) {
				Log.d("generateCustomChooserIntent",
						"Exception: File not found");
			}
		}

	}
	
	public static void shareFacebook(Activity activity, String cId) {
		
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setPackage("com.facebook.katana");
		intent.setType("image/png");

		try {
			String path = Environment.getExternalStorageDirectory() + File.separator
							+ "BCard" + File.separator
							+ "Card" + File.separator;
			File localFile = new File(path, "bcard" + cId + ".png");
			localFile.setReadable(true, false);
			
			intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(localFile));
			try {
				activity.startActivity(Intent.createChooser(intent, "Share with"));
			} catch (android.content.ActivityNotFoundException ex) {
				AppGlobal.showToast(activity, "Facebook have not been installed");
			}
		} catch (Exception localException) {
			for (;;) {
				Log.d("generateCustomChooserIntent", "Exception: File not found");
			}
		}
	}
	
	public static void shareLinkedin(Activity activity, String cId) {
		
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setPackage("com.linkedin.android");
		intent.setType("image/png");

		try {
			String path = Environment.getExternalStorageDirectory() + File.separator
						+ "BCard" + File.separator
						+ "Card" + File.separator;
			File localFile = new File(path, "bcard" + cId + ".png");
			localFile.setReadable(true, false);
			
			intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(localFile));
			try {
				activity.startActivity(Intent.createChooser(intent, "Share with"));
			} catch (android.content.ActivityNotFoundException ex) {
				AppGlobal.showToast(activity, "Linkedin have not been installed");
			}
		} catch (Exception localException) {
			for (;;) {
				Log.d("generateCustomChooserIntent", "Exception: File not found");
			}
		}
	}

}