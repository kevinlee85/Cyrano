/**
 * CLASS: DefaultSettingsTask
 *   This class will load the default settings in the database.
 *   It is to be called at the start of CyranoActivity.
 */
package com.cjcornell.cyrano.task;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import android.util.Log;

import com.cjcornell.cyrano.ActivityCyrano;
import com.cjcornell.cyrano.Constants;
import com.cjcornell.cyrano.SharedpreferenceUtility;
import com.cjcornell.cyrano.data.DataStore;

public class TaskGetBluetoothMatchingDevice extends AsyncTask<List<BluetoothDevice>, Void, JSONArray> {
    private final static String TAG = "TaskGetBluetoothMatchingDevice";
    
    private ActivityCyrano cyranoContext;
    private ProgressDialog dialog;
  
    public TaskGetBluetoothMatchingDevice(ActivityCyrano activity) {
        this.cyranoContext = activity;
    }
    
    @Override
    protected void onCancelled() {
        super.onCancelled();
        cyranoContext.onErrorBluetoothMatchingDeviceTask();
    }
    
    @Override
    protected void onCancelled(JSONArray result) {
        super.onCancelled(result);
        cyranoContext.onErrorBluetoothMatchingDeviceTask();
    }
    
    private void showDialog() {
       // dialog = new ProgressDialog(cyranoContext);
        //dialog.setCancelable(false);
       // dialog.setMessage("Searching for nearby friends...");
       // dialog.show();
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //showDialog();
    }
    
    /**
     * This is executed when the asynchronous task is started. It will grab a JSONObject of default settings.
     */
    @Override
    protected JSONArray doInBackground(List<BluetoothDevice>... arg0) {
        
        HttpClient httpClient = new DefaultHttpClient();
        
        String accessToken =  SharedpreferenceUtility.getInstance(cyranoContext).getString(Constants.ACCESS_TOKEN);
        String userId = SharedpreferenceUtility.getInstance(cyranoContext).getString(Constants.CYRANO_USER_ID);
        
        StringBuilder addresses = new StringBuilder("");
        /*  for (int i = 0; i < devices.size(); i++) {
        addresses.append("'" + devices.get(i).getAddress() + "'");
        if(i != devices.size() - 1) {
            addresses.append(",");
        }*/
    
    //use this loop to add simulation file contents to search
    for (int i = 0; i < DataStore.getInstance().getbluletoothmac().size(); i++) {
        addresses.append("'" +DataStore.getInstance().getbluletoothmac().get(i)+ "'");
        if(i != DataStore.getInstance().getbluletoothmac().size() - 1) {
            addresses.append(",");
        }
        }
        
        String requestUrl = Constants.SERVER_ROOT 
                + Constants.SEPERATOR + Constants.SERVICE_USER_LIST_MATCHED_BT 
                + Constants.SEPERATOR + accessToken 
                + Constants.SEPERATOR + userId
                + Constants.SEPERATOR + addresses.toString();
        // http://cyrano.cjcornell.com/REST/index.php/UserListMatchedBT/{access_token} / {user_id} / { List of comma seperated single quoted BT mac address}
        
        Log.d(TAG, "Sending GET request at URL " + requestUrl);

        try {
            HttpGet httpGet = new HttpGet(requestUrl);
            JSONObject response = new JSONObject(httpClient.execute(httpGet, new BasicResponseHandler()));
                
            Log.d(TAG, "Successfully obtained UserListMatchedBT");
            if (response.has("body")) {
                return response.getJSONArray("body");
            } else {
                return null;
            }
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        } catch (ClientProtocolException e) {
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    /**
     * This is executed after doInBackground method completes. It will set the default settings based
     * on the contents of the JSONObject returned by the doInBackground method.
     */
    @Override
    protected void onPostExecute(JSONArray arr) {
        
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        
        // If there was a problem, the JSONArray is null, and we should simply return
        if (arr == null) {
            Log.v(TAG, "JSONArray is null.");
            return;
        }
        
        cyranoContext.onCompleteGetBluetoothMatchingDeviceTask(arr);
        
        /*try {
            // Set all the settings as appropriate
            JSONObject settings = arr.getJSONObject(0);
            AppSettings.setDefaults(
                settings.getString("terseMode"),
                settings.getString("tsAudio"),
                settings.getString("graphicalMode"),
                settings.getString("friendFinder"),
                settings.getString("friendAudio"),
                settings.getString("textSize"),
                settings.getString("gpsTimeDelay"),
                settings.getString("maxFriends"),
                settings.getString("autoDisplayFriends"));
            Log.v(TAG, "Default settings loaded successfully.");
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

}
