package com.cjcornell.cyrano.utils;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.cjcornell.cyrano.BuildConfig;
import com.cjcornell.cyrano.Constants;
import com.cjcornell.cyrano.SharedpreferenceUtility;

public class Utils {
    
    public static final String TAG = "Cyrano";
    public static final String FIRST_PARAM_COUNT = "C";
    public static final String FIRST_PARAM_RECORD = "R";
    public static final String SECOND_PARAM_LIGHT = "L";
    public static final String SECOND_PARAM_FULL = "F";
    
    public final static String KEYWORD_FRIEND = "friend";
    public final static String KEYWORD_DEVICE = "device";
    public final static String KEYWORD_TRIGGER = "trigger";
    
    public static void showLongToast(Activity activity, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }
    
    public static void showShortToast(Activity activity, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }
    
    public static void dLog(String message) {
        if(BuildConfig.DEBUG) Log.d(TAG, message);
    }
    
    public static void eLog(String message) {
        Log.e(TAG, message);
    }

    public static void removeUserData(Activity activity) {
        
        SharedpreferenceUtility.getInstance(activity).remove(Constants.ACCESS_TOKEN);
        SharedpreferenceUtility.getInstance(activity).remove(Constants.FB_USER_ID);
        SharedpreferenceUtility.getInstance(activity).remove(Constants.FIRSTNAME);
        SharedpreferenceUtility.getInstance(activity).remove(Constants.LASTNAME);
        SharedpreferenceUtility.getInstance(activity).remove(Constants.EMAIL);
        SharedpreferenceUtility.getInstance(activity).remove(Constants.LOGGEDIN);
        SharedpreferenceUtility.getInstance(activity).remove(Constants.MAC_ADDRESS_WI_FI);
        SharedpreferenceUtility.getInstance(activity).remove(Constants.MAC_ADDRESS_BLUETOOTH);
    }
}
