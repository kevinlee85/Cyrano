/**
 * CLASS: DefaultSettingsTask
 *   This class will load the default settings in the database.
 *   It is to be called at the start of CyranoActivity.
 */
package com.cjcornell.cyrano.task;

import java.io.IOException;

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

import com.cjcornell.cyrano.ActivityCyrano;
import com.cjcornell.cyrano.utils.Utils;

public class TaskAdvanceSearch extends AsyncTask<String, Void, JSONObject> {
    private final static String TAG = "TaskAdvanceSearch";
    
    private ActivityCyrano activity;
    private ProgressDialog dialog;
    private boolean countOnly;
    private boolean detailedRecord;
    private String serviceName;
    
    public TaskAdvanceSearch(ActivityCyrano activityCyrano, String service, boolean countOnly, boolean detailedRecord) {
        this.activity = activityCyrano;
        this.serviceName = service.toLowerCase();
        this.countOnly = countOnly;
        this.detailedRecord = detailedRecord;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        activity.onErrorAdvanceSearch();
    }
    
    @Override
    protected void onCancelled(JSONObject result) {
        super.onCancelled(result);
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        activity.onErrorAdvanceSearch();
    }
    
    private void showDialog() {
        dialog = new ProgressDialog(activity);
        dialog.setCancelable(false);
        
        if(serviceName.contains(Utils.KEYWORD_FRIEND)) {
            dialog.setMessage("Searching for friends...");
        } else if(serviceName.contains(Utils.KEYWORD_DEVICE)) {
            dialog.setMessage("Searching for devices...");
        } else {
            dialog.setMessage("Searching for triggers...");
        }
        
        dialog.show();
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        showDialog();
    }
    
    @Override
    protected JSONObject doInBackground(String... arg0) {
        
        HttpClient httpClient = new DefaultHttpClient();
        
        // http://cyrano.cjcornell.com/REST/index.php/
        // DeviceListMatchedBTCmn/{access_token} /{user_id} / { List of comma seperated single quoted BT mac address} / {C / R} / {L / F}/ {limit count}
        // FriendListMatchedBTCmn/{access_token} / {user_id} / { List of comma seperated single quoted BT mac address} / {C / R} / {L / F}/ {limit count}
        // TriggerListMatchedBTCmn/{access_token} / {user_id} / { List of comma seperated single quoted BT mac address} / {C / R} / {L / F}/ {limit count}
        
        Utils.dLog(TAG + ": Sending GET request at URL " + arg0[0]);

        try {
            HttpGet httpGet = new HttpGet(arg0[0]);
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
            activity.onErrorAdvanceSearch();
            return;
        }
        
        try {
            if(countOnly) {
                int size = 0;
                if(serviceName.contains(Utils.KEYWORD_FRIEND)) {
                    size = response.getInt("Matched-Friend-Count");
                    activity.onCompleteAdvanceSearchCount(size, serviceName);
                } else if(serviceName.contains(Utils.KEYWORD_DEVICE)) {
                    size = response.getInt("Matched-Device-Count");
                    activity.onCompleteAdvanceSearchCount(size, serviceName);
                } else {
                    size = response.getInt("Matched-Trigger-Count");
                    activity.onCompleteAdvanceSearchCount(size, serviceName);
                }
            } else {
                JSONArray arr = null;
                if(serviceName.contains(Utils.KEYWORD_FRIEND)) {
                    arr = response.getJSONArray("Matched-Friends");
                    activity.onCompleteAdvanceSearchResult(arr, serviceName);
                } else if(serviceName.contains(Utils.KEYWORD_DEVICE)) {
                    arr = response.getJSONArray("Matched-Devices");
                    activity.onCompleteAdvanceSearchResult(arr, serviceName);
                } else {
                    arr = response.getJSONArray("Triggers");
                    activity.onCompleteAdvanceSearchResult(arr, serviceName);
                }
            }
            
        } catch (JSONException e) {
            e.printStackTrace();
            activity.onErrorAdvanceSearch();
        }
    }

}
