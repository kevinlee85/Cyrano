/**
 * CLASS: CommandGroupTask
 *   This class is an asynchronous task used for retrieving the command groups for the scripts page
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

import com.cjcornell.cyrano.ActivityCyrano;
import com.cjcornell.cyrano.Constants;
import com.cjcornell.cyrano.ScriptItem;
import com.cjcornell.cyrano.SharedpreferenceUtility;
import com.cjcornell.cyrano.data.DataStore;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

public class TaskGetUserScripts extends AsyncTask<Void, Void, JSONArray> {
    private final static String TAG = "TaskGetUserScripts";
    private ActivityCyrano activity;
    private ProgressDialog dialog;
    /**
     * Constructor - simply used to set the activity attribute
     * @param activity: The CyranoActivity activity
     */
    public TaskGetUserScripts(ActivityCyrano activity) {
        this.activity = activity;
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(activity);
        dialog.setCancelable(false);
        dialog.setMessage("Loading...");
        dialog.show();
    }
    /**
     * This is the task to execute in the background. In this case, it will grab the specified command group.
     */
    @Override
    protected JSONArray doInBackground(Void... arg0) {
        HttpClient httpClient = new DefaultHttpClient();
        
        String accessToken =  SharedpreferenceUtility.getInstance(activity).getString(Constants.ACCESS_TOKEN);
        
        String userId = SharedpreferenceUtility.getInstance(activity).getString(Constants.CYRANO_USER_ID);
        
        String requestUrl = Constants.SERVER_ROOT 
                + Constants.SEPERATOR + Constants.SERVICE_USER_SCRIPT
                + Constants.SEPERATOR + accessToken 
                + Constants.SEPERATOR + userId;
        
        //String requestUrl = COMMAND_URL + "/" + DataStore.getInstance().getBaseParameterString();
        Log.e(TAG, "Sending GET request at URL " + requestUrl);

        try {
            HttpGet httpGet = new HttpGet(requestUrl);
            JSONObject response = new JSONObject(httpClient.execute(httpGet, new BasicResponseHandler()));
            Log.v(TAG, response.toString());
            Log.d(TAG, "Successfully obtained instruction set for group");
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
     * This will parse the results of the JSONObject retrieved from the doInBackground Method.
     */
    @Override
    protected void onPostExecute(JSONArray groups) {
        
        if (dialog != null) {
            dialog.dismiss();
        }
        
        // If the JSONArray is null, there are no groups to parse
        if (groups == null) return;
        
        // Go through each item in the JSONObject and add them to an ScriptItem ArrayList
      
        List<ScriptItem> parsedScripts = new ArrayList<ScriptItem>();
        for (int i = 0; i < groups.length(); i++) {
            try {
                parsedScripts.add(new ScriptItem(groups.getJSONObject(i)));
            } catch (JSONException e) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                Log.e(TAG, e.toString());
            }
        }
        // Set the troubleshooting items and remove the splash screen
       
        activity.onCompleteGetUserScriptsTask(parsedScripts);
        
        //activity.removeSplashScreen();
        if(DataStore.getInstance().getEarpieceMacAddress() != null) {
            
        }
    }
}
