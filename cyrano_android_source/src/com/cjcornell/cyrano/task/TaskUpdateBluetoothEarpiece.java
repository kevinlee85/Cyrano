package com.cjcornell.cyrano.task;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

import com.cjcornell.cyrano.ActivityCyrano;
import com.cjcornell.cyrano.Constants;
import com.cjcornell.cyrano.SharedpreferenceUtility;

public class TaskUpdateBluetoothEarpiece extends AsyncTask<String, Void, HttpResponse> {
    private final static String TAG = "TaskUpdateBluetoothEarpiece";
    private ActivityCyrano activity;
    //private ProgressDialog dialog;
    /**
     * Constructor - simply used to set the activity attribute
     * @param activity: The CyranoActivity activity
     */
    public TaskUpdateBluetoothEarpiece(ActivityCyrano activity) {
        this.activity = activity;
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        /*dialog = new ProgressDialog(activity);
        dialog.setCancelable(false);
        dialog.setMessage("Loading..");
        dialog.show();*/
    }
    /**
     * This is the task to execute in the background. In this case, it will grab the specified command group.
     */
    @Override
    protected HttpResponse doInBackground(String... arg0) {
        HttpClient httpClient = new DefaultHttpClient();
        
        String accessToken =  SharedpreferenceUtility.getInstance(activity).getString(Constants.ACCESS_TOKEN);
        String userId = SharedpreferenceUtility.getInstance(activity).getString(Constants.CYRANO_USER_ID);
        
        String requestUrl = Constants.SERVER_ROOT 
                + Constants.SEPERATOR + Constants.SERVICE_UPDATE_BLUETOOTH_EARPIECE 
                + Constants.SEPERATOR + accessToken
                + Constants.SEPERATOR + userId
                + Constants.SEPERATOR + arg0[0]
                + Constants.SEPERATOR + arg0[1];
        
        //http://cyrano.cjcornell.com/REST/index.php/BtEarpieceUpdate/{ Access token} / {userId} / {earpiece_bt_id} / {earpiece_device_id}
        
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
        // If the JSONArray is null, there are no groups to parse
        if (response == null) return;
        
        /*if (dialog != null) {
            dialog.dismiss();
        }*/
        activity.onCompleteUpdateBluetoothEarpieceTask(response.getStatusLine().getStatusCode());
    }
}

