/**
 * CLASS: CommandGroupTask
 *   This class is an asynchronous task used for retrieving the command groups for the scripts page
 */

package com.cjcornell.cyrano.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.cjcornell.cyrano.ActivityFacebook;

public class TaskRegisterUser extends AsyncTask<String, Void, HttpResponse> {
    private final static String TAG = "TaskRegisterUser";
    private ActivityFacebook activity;
    private ProgressDialog dialog;
    /**
     * Constructor - simply used to set the activity attribute
     * @param activity: The CyranoActivity activity
     */
    public TaskRegisterUser(ActivityFacebook activity) {
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
        activity.onErrorRegistration();
    }
    
    @Override
    protected void onCancelled(HttpResponse result) {
        super.onCancelled(result);
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        activity.onErrorRegistration();
    }
    /**
     * This is the task to execute in the background. In this case, it will grab the specified command group.
     */
    @Override
    protected HttpResponse doInBackground(String... arg0) {
        HttpClient httpClient = new DefaultHttpClient();
        
        String requestUrl = arg0[0];
        
        //http://cyrano.cjcornell.com/REST/index.php/userRegistration/accessToken/firstname/lastname/email/password/btMacAdd
        
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
            activity.onErrorRegistration();
            return;
        }
         
        try {
            
            JSONObject parsedResponse = getJSONObject(response);
            
            if(parsedResponse.getJSONObject("success").getInt("code") == 200) {
                JSONObject body = parsedResponse.getJSONObject("body");
                String userId = body.getInt("user_id") + "";
                String token= ""; 
                
                if(body.has("token")) {
                    token = body.getString("token");
                } 
                String firstName = body.getString("firstname");
                String lastName = body.getString("lastname");
                String email = body.getString("email");
                String macAddress = body.getString("phone_mac_addr");
                                
                activity.onCompleteRegistration(userId, token, firstName, lastName, email, macAddress);
            } else {
                activity.onErrorRegistration(parsedResponse.getJSONObject("success").getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            activity.onErrorRegistration();
        } catch (UnsupportedEncodingException e) {
            activity.onErrorRegistration();
            e.printStackTrace();
        } catch (IllegalStateException e) {
            activity.onErrorRegistration();
            e.printStackTrace();
        } catch (IOException e) {
            activity.onErrorRegistration();
            e.printStackTrace();
        }
        
    }
    
    private JSONObject getJSONObject(HttpResponse response) throws UnsupportedEncodingException, IllegalStateException, IOException, JSONException {
        
        StringBuilder builder = new StringBuilder(); 
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            
            for (String line = null; (line = reader.readLine()) != null;) {
                builder.append(line).append("\n");
            }
            
            return new JSONObject(builder.toString());
    }
}
