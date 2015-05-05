/**
 * CLASS: DefaultSettingsTask
 *   This class will load the default settings in the database.
 *   It is to be called at the start of CyranoActivity.
 */
package com.cjcornell.cyrano.task;

import java.io.IOException;
import java.util.ArrayList;
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
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.cjcornell.cyrano.ActivityCyrano;
import com.cjcornell.cyrano.BluetoothFriend;
import com.cjcornell.cyrano.Constants;
import com.cjcornell.cyrano.SharedpreferenceUtility;

public class TaskGetUserInfoBluetoothList extends AsyncTask<List<String>, Void, JSONArray> {
    private final static String TAG = "TaskGetUserInfoBluetoothList";
    
    private ActivityCyrano cyranoContext;
    private ProgressDialog dialog;
  
    public TaskGetUserInfoBluetoothList(ActivityCyrano activity) {
        this.cyranoContext = activity;
    }
    
    @Override
    protected void onCancelled() {
        super.onCancelled();
        cyranoContext.onErrorGetUserInfoBluetoothTask();
    }
    
    @Override
    protected void onCancelled(JSONArray result) {
        super.onCancelled(result);
        cyranoContext.onErrorGetUserInfoBluetoothTask();
    }
    
    private void showDialog() {
        //dialog = new ProgressDialog(cyranoContext);
        //dialog.setCancelable(false);
        //dialog.setMessage("Fetching friend(s) details...");
        //dialog.show();
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        showDialog();
    }
    
    /**
     * This is executed when the asynchronous task is started. It will grab a JSONObject of default settings.
     */
    @Override
    protected JSONArray doInBackground(List<String>... arg0) {
        
        HttpClient httpClient = new DefaultHttpClient();
        
        String accessToken =  SharedpreferenceUtility.getInstance(cyranoContext).getString(Constants.ACCESS_TOKEN);
        String userId = SharedpreferenceUtility.getInstance(cyranoContext).getString(Constants.CYRANO_USER_ID);
        
        StringBuilder addresses = new StringBuilder("");
        List<String> devices = arg0[0];
        for (int i = 0; i < devices.size(); i++) {
            addresses.append("'" + devices.get(i) + "'");
            if(i != devices.size() - 1) {
                addresses.append(",");
            }
        }
        
        String requestUrl = Constants.SERVER_ROOT 
                + Constants.SEPERATOR + Constants.SERVICE_USER_INFO 
                + Constants.SEPERATOR + accessToken 
                + Constants.SEPERATOR + userId
                + Constants.SEPERATOR + addresses.toString();
        // http://cyrano.cjcornell.com/REST/index.php/UserInfoBtList/{access_token} / {user_id} / { List of comma seperated single quoted BT mac address}
        
        Log.d(TAG, "Sending GET request at URL " + requestUrl);

        try {
            HttpGet httpGet = new HttpGet(requestUrl);
            JSONObject response = new JSONObject(httpClient.execute(httpGet, new BasicResponseHandler()));
            Log.v(TAG, requestUrl);    
            Log.d(TAG, "Successfully obtained UserListMatchedBT"+response.toString());
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
        
      //[{"last_name":"A","id":31,"first_name":"A","email":"","bt_mac_address":"C0:F8:DA:E9:B8:BE"}]
        try {
            List<BluetoothFriend> list = new ArrayList<BluetoothFriend>();
            for (int i = 0; i < arr.length(); i++) {
                // Set all the settings as appropriate
                JSONObject object = arr.getJSONObject(i);
                list.add(new BluetoothFriend(object.getString("id"), 
                        object.getString("first_name"), object.getString("last_name"), 
                        object.getString("email"), object.getString("phone_mac_addr")));
           
            }
            Log.v(TAG, "UserInfo loaded successfully.");
            cyranoContext.onCompleteGetUserInfoBluetoothTask(list);
            
        } catch (JSONException e) {
            e.printStackTrace();
            cyranoContext.onErrorGetUserInfoBluetoothTask();
        }
    }
}
