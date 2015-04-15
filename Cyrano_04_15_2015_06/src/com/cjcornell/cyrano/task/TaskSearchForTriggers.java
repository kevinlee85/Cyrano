/**
 * CLASS: TriggerFindTask
 *   This class will use to featch Trigger From database And play script
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
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import android.util.Log;

import com.cjcornell.cyrano.ActivityCyrano;
import com.cjcornell.cyrano.Constants;
import com.cjcornell.cyrano.R;
import com.cjcornell.cyrano.SharedpreferenceUtility;
import com.cjcornell.cyrano.TextToSpeachService;
import com.cjcornell.cyrano.data.DataStore;

public class TaskSearchForTriggers extends AsyncTask<List<BluetoothDevice>, Void, JSONObject> {
    private final static String TAG = "TaskSearchForTriggers";
    public List<String> Triggerscripts=new ArrayList<String>();
    
    private ActivityCyrano activity;
    private ProgressDialog dialog;
    String Id="";
  
    public TaskSearchForTriggers(ActivityCyrano activity) {
        this.activity = activity;
    }
    
    @Override
    protected void onCancelled() {
        super.onCancelled();
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        activity.onErrorSearchForTriggers();
    }
    
    @Override
    protected void onCancelled(JSONObject result) {
        super.onCancelled(result);
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        activity.onErrorSearchForTriggers();
    }
    
    private void showDialog() {
        dialog = new ProgressDialog(activity);
        dialog.setCancelable(false);
        dialog.setMessage("Searching for triggers...");
        dialog.show();
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //showDialog();
    }
    
    @Override
    protected JSONObject doInBackground(List<BluetoothDevice>... arg0) {
        
        HttpClient httpClient = new DefaultHttpClient();
        String accessToken =  SharedpreferenceUtility.getInstance(activity).getString(Constants.ACCESS_TOKEN);
        String userId = SharedpreferenceUtility.getInstance(activity).getString(Constants.CYRANO_USER_ID);
        
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
                + Constants.SEPERATOR + Constants.SERVICE_SEARCH_FOR_TRIGGERS 
                + Constants.SEPERATOR + accessToken 
                + Constants.SEPERATOR + userId
                + Constants.SEPERATOR + addresses.toString();

        //http://cyrano.cjcornell.com/REST/index.php/TriggerListMatchedBT/{access_token} / {user_id} / { List of comma seperated single quoted BT mac address}
        
       // Log.d(TAG, "Sending GET request at URL " + requestUrl);

        try {
            HttpGet httpGet = new HttpGet(requestUrl);
            JSONObject response = new JSONObject(httpClient.execute(httpGet, new BasicResponseHandler()));
                
            Log.d(TAG, "Successfully obtained TriggerListMatchedBT");
            if (response.has("body")) {
                return response.getJSONObject("body");
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
    protected void onPostExecute(JSONObject response) {
        
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        
        // If there was a problem, the JSONArray is null, and we should simply return
        if (response == null) {
            Log.v(TAG, "JSONObject is null.");
            activity.onErrorSearchForTriggers();
            return;
        }
        
        try {
            int size = response.getInt("Matched-Trigger-Count");
            JSONArray arr = response.getJSONArray("Triggers");
            activity.onCompleteSearchForTriggers(size, arr);
            for(int i=0;i<arr.length();i++)
            {
               JSONObject jsonChildNode = arr.getJSONObject(i);
               Id = jsonChildNode.optString("script_id").toString();
               Triggerscripts.add(Id);
            }
            Log.e(TAG, Triggerscripts.toString()+"\n"+"BHUPINDER");
            if(Id.length()>0)
            {
                DataStore.getInstance().setTriggerIDS(Triggerscripts);
                new TaskTriggerScript(activity,Id).execute(1,activity);
            }
            if(Id.length()>1)
            {
                new TextToSpeachService().getInstance().Triggerscript=true;
            }
            
           
            
        } catch (JSONException e) {
            e.printStackTrace();
            activity.onErrorSearchForDevices();
        }
    }

}
