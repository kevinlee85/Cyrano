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

import com.cjcornell.cyrano.ActivityCyrano;
import com.cjcornell.cyrano.Constants;
import com.cjcornell.cyrano.SharedpreferenceUtility;
import com.cjcornell.cyrano.data.DataStore;
import com.cjcornell.cyrano.utils.Utils;

public class TaskSearchForDevices extends AsyncTask<List<BluetoothDevice>, Void, JSONObject> {
    private final static String TAG = "TaskSearchForDevices";
    
    private ActivityCyrano activity;
    private ProgressDialog dialog;
  
    public TaskSearchForDevices(ActivityCyrano activity) {
        this.activity = activity;
    }
    
    @Override
    protected void onCancelled() {
        super.onCancelled();
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        activity.onErrorSearchForDevices();
    }
    
    @Override
    protected void onCancelled(JSONObject result) {
        super.onCancelled(result);
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        activity.onErrorSearchForDevices();
    }
    
    private void showDialog() {
        dialog = new ProgressDialog(activity);
        dialog.setCancelable(false);
        dialog.setMessage("Searching for devices...");
        //dialog.show();
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        showDialog();
    }
    
    @Override
    protected JSONObject doInBackground(List<BluetoothDevice>... arg0) {
        
        HttpClient httpClient = new DefaultHttpClient();
        String accessToken =  SharedpreferenceUtility.getInstance(activity).getString(Constants.ACCESS_TOKEN);
        String userId = SharedpreferenceUtility.getInstance(activity).getString(Constants.CYRANO_USER_ID);
        
        StringBuilder addresses = new StringBuilder("");
        List<BluetoothDevice> devices = arg0[0];
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
                + Constants.SEPERATOR + Constants.SERVICE_SEARCH_FOR_DEVICES 
                + Constants.SEPERATOR + accessToken 
                + Constants.SEPERATOR + userId
                + Constants.SEPERATOR + addresses.toString();
        //http://cyrano.cjcornell.com/REST/index.php/DeviceListMatchedBT/{access_token} / {user_id} / { List of comma seperated single quoted BT mac address}
        //String d="http://cyrano.cjcornell.com/REST/index.php/DeviceListMatchedBT/1852398296/196/'64:77:91:50:29:87'";
       // Utils.dLog(TAG + ": Sending GET request at URL " + requestUrl+"BHUPINDER");

        try {
            HttpGet httpGet = new HttpGet(requestUrl);
            JSONObject response = new JSONObject(httpClient.execute(httpGet, new BasicResponseHandler()));
                
            Utils.dLog(TAG + ": Successfully obtained DeviceListMatchedBT");
            if (response.has("body")) {
                return response.getJSONObject("body");
            } else {
                return null;
            }
        } catch (JSONException e) {
            Utils.eLog(TAG + ": " + e.toString());
        } catch (ClientProtocolException e) {
            Utils.eLog(TAG + ": " +  e.toString());
        } catch (IOException e) {
            Utils.eLog(TAG + ": " +  e.toString());
        }
        return null;
    }

    /**
     * This is executed after doInBackground method completes. It will set the default settings based
     * on the contents of the JSONObject returned by the doInBackground method.
     */
    @Override
    protected void onPostExecute(JSONObject response) {
        
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        
        // If there was a problem, the JSONArray is null, and we should simply return
        if (response == null) {
            Utils.eLog(TAG + ": JSONObject is null.");
            activity.onErrorSearchForDevices();
            return;
        }
        
        try {
            int size = response.getInt("Matched-Device-Count");
            JSONArray arr = response.getJSONArray("Devices");
            
            activity.onCompleteSearchForDevices(size, arr);
        } catch (JSONException e) {
            e.printStackTrace();
            activity.onErrorSearchForDevices();
        }
    }

}
