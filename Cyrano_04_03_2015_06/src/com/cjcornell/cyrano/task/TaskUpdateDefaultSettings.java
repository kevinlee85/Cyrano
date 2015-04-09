/**
 * CLASS: CommandGroupTask
 *   This class is an asynchronous task used for retrieving the command groups for the scripts page
 */

package com.cjcornell.cyrano.task;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.cjcornell.cyrano.ActivitySettings;
import com.cjcornell.cyrano.Constants;
import com.cjcornell.cyrano.SharedpreferenceUtility;
import com.cjcornell.cyrano.data.AppSettings;

public class TaskUpdateDefaultSettings extends AsyncTask<Void, Void, HttpResponse> {
    private final static String TAG = "UpdateDefaultSettingTask";
    private ActivitySettings activity;
    private ProgressDialog dialog;
    private long timeStamp;
    /**
     * Constructor - simply used to set the activity attribute
     * @param activity: The CyranoActivity activity
     */
    public TaskUpdateDefaultSettings(ActivitySettings activity, long timeStamp) {
        this.activity = activity;
        this.timeStamp = timeStamp;
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(activity);
        dialog.setCancelable(false);
        dialog.setMessage("Loading..");
        dialog.show();
    }
    
    @Override
    protected void onCancelled() {
        super.onCancelled();
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        activity.onErrorUpdateSettings();
    }
    
    @Override
    protected void onCancelled(HttpResponse result) {
        super.onCancelled(result);
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        activity.onErrorUpdateSettings();
    }
    /**
     * This is the task to execute in the background. In this case, it will grab the specified command group.
     */
    @Override
    protected HttpResponse doInBackground(Void... arg0) {
        HttpClient httpClient = new DefaultHttpClient();
        
        String accessToken =  SharedpreferenceUtility.getInstance(activity).getString(Constants.ACCESS_TOKEN);
        String userId = SharedpreferenceUtility.getInstance(activity).getString(Constants.CYRANO_USER_ID);
        
        String requestUrl = Constants.SERVER_ROOT 
                + Constants.SEPERATOR + Constants.SERVICE_DEFAULT_SETTINGS 
                + Constants.SEPERATOR + accessToken 
                + Constants.SEPERATOR + userId
                + Constants.SEPERATOR + (AppSettings.terseMode ? 1 : 0)
                + Constants.SEPERATOR + (AppSettings.tsAudio ? 1 : 0)
                + Constants.SEPERATOR + (AppSettings.graphicalMode ? 1 : 0)
                + Constants.SEPERATOR + (AppSettings.friendFinder ? 1 : 0)
                + Constants.SEPERATOR + (AppSettings.friendAudio ? 1 : 0)
                + Constants.SEPERATOR + AppSettings.textSize
                + Constants.SEPERATOR + AppSettings.gpsTimeDelay
                + Constants.SEPERATOR + AppSettings.maxFriends
                + Constants.SEPERATOR + (AppSettings.autoDisplayFriends ? 1 : 0)
                + Constants.SEPERATOR + timeStamp;
        
        /* http://cyrano.cjcornell.com/REST/index.php/defaultsettings/
         * accessToken/userId/terseMode/tsAudio/graphicalMode/friendFinder/friendAudio/textSize/gpsTimeDelay/maxFriends/autoDisplayFriends/nearbyTimeout    
         */
        
        Log.d(TAG, "Sending PUT request at URL " + requestUrl);

        try {
            HttpPut httpPut = new HttpPut(requestUrl);
            return httpClient.execute(httpPut);
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
    protected void onPostExecute(HttpResponse response) {
        
        if (dialog != null) {
            dialog.dismiss();
        }
        
        // If the JSONArray is null, there are no groups to parse
        if (response == null) {
            activity.onErrorUpdateSettings();
            return;
        }
        
        activity.onCompleteUpdateSettingTask(response.getStatusLine().getStatusCode());
    }
}
