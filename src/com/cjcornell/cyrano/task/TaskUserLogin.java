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
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.cjcornell.cyrano.ActivityFacebook;

public class TaskUserLogin extends AsyncTask<String, Void, JSONObject> {
    private final static String TAG = "TaskUserLogin";
    
    private ActivityFacebook activity;

    private ProgressDialog dialog;
    
    public TaskUserLogin(ActivityFacebook activity) {
        this.activity = activity;
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
        activity.onErrorLogin();
    }
    
    @Override
    protected void onCancelled(JSONObject result) {
        super.onCancelled(result);
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        activity.onErrorLogin();
    }
    
    /**
     * This is executed when the asynchronous task is started. It will grab a JSONObject of default settings.
     */
    @Override
    protected JSONObject doInBackground(String... arg0) {
                
        HttpClient httpClient = new DefaultHttpClient();
        
        String requestUrl = arg0[0];
      
        Log.d(TAG, "Sending GET request at URL " + requestUrl);

        try {
            HttpGet httpGet = new HttpGet(requestUrl);
            JSONObject response = new JSONObject(httpClient.execute(httpGet, new BasicResponseHandler()));
                
            Log.d(TAG, "Successfully obtained user");
            return response;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This is executed after doInBackground method completes. It will set the default settings based
     * on the contents of the JSONObject returned by the doInBackground method.
     */
    @Override
    protected void onPostExecute(JSONObject result) {
        
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        
        try {
            
            // If there was a problem, the JSONArray is null, and we should simply return
            if (result == null) {
                Log.v(TAG, "JSONArray is null.");
                //activity.onErrorLogin();
                
                result = new JSONObject("{\"success\": {\"code\": 400,\"message\": \"Provided email or password couldn't be authenticated. \"}}");
                //return;
            }
            
            int code = result.getJSONObject("success").getInt("code");
            if(code == 200) {
                // Success login
                JSONObject body = result.getJSONObject("body");
                
                String accessToken = body.getString("token");
                String userId = body.getString("user_id");
                String firstName = body.getString("firstname");
                String lastName = body.getString("lastname");
                String email = body.getString("email");
                String about_text = body.getString("about_text");
//                String macAddress = body.getString("bt_mac_address");
                String macAddress = "";
                
                activity.onCompleteLogin(userId, accessToken, firstName, lastName, email, macAddress,about_text);
            } else {
                // Error login
                JSONObject success = result.getJSONObject("success");
                String message = success.getString("message");
                activity.onErrorLogin(message);
            }
            
        } catch (JSONException e) {

            activity.onErrorLogin();
            e.printStackTrace();
        }
        
    }
}
