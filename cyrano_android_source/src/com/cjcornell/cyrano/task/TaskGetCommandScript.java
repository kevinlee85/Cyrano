/**
 * CLASS: CoachingTask
 *   This class is an asynchronous task used for troubleshooting instructions.
 */
package com.cjcornell.cyrano.task;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cjcornell.cyrano.ActivityCyrano;
import com.cjcornell.cyrano.Command;
import com.cjcornell.cyrano.Constants;
import com.cjcornell.cyrano.ScriptItem;
import com.cjcornell.cyrano.SharedpreferenceUtility;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class TaskGetCommandScript extends AsyncTask<Object, Void, JSONObject> {

    private final String TAG = "CoachingTask";
    private ActivityCyrano cyranoContext;
    private int firstCommand;
    private ProgressDialog dialog;
    private ScriptItem script;
    
    public TaskGetCommandScript(ActivityCyrano activity) {
        this.cyranoContext = activity;
    }
    
    private void showDialog() {
        dialog = new ProgressDialog(cyranoContext);
        dialog.setCancelable(false);
        dialog.setMessage("Loading..");
        dialog.show();
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        showDialog();
    }
    
    /**
     * This is the task to execute in the background. In this case, it will grab the troubleshooting group.
     */
    @Override
    protected JSONObject doInBackground(Object... params) {
        script = (ScriptItem) params[0];
        String scriptId = script.getId() + "";
        
        cyranoContext = (ActivityCyrano) params[1];
        if (params.length >= 3) {
            firstCommand = (Integer)params[2];
        } else {
            firstCommand = 1;
        }
        
        /* Send a GET request */
        HttpClient httpClient = new DefaultHttpClient();
        
        String accessToken =  SharedpreferenceUtility.getInstance(cyranoContext).getString(Constants.ACCESS_TOKEN);
        
        String userId = SharedpreferenceUtility.getInstance(cyranoContext).getString(Constants.CYRANO_USER_ID);
        
        String requestUrl = Constants.SERVER_ROOT 
                + Constants.SEPERATOR + Constants.SERVICE_COMMAND_SCRIPTS 
                + Constants.SEPERATOR + accessToken 
                + Constants.SEPERATOR + userId 
                + Constants.SEPERATOR + scriptId;
        
        //String requestUrl = COMMAND_URL + "/" + DataStore.getInstance().getBaseParameterString() + "/" + groupID;
        Log.d(TAG, "Sending GET request at URL " + requestUrl);
        try {
            HttpGet httpGet = new HttpGet(requestUrl);
            String response = httpClient.execute(httpGet, new BasicResponseHandler());
            
            Log.d(TAG, "Successfully obtained instruction set for group " + scriptId);
            return new JSONObject(response);
        } catch (HttpResponseException e) {
            Log.e(TAG, "Could not obtain the instruction set for group " + scriptId);
        } catch (MalformedURLException e) {
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        } 
        return new JSONObject();
    }
    
    /**
     * This will parse the results of the JSONObject retrieved from the doInBackground Method.
     */
    @Override
    protected void onPostExecute(JSONObject parsedResponse) {
        
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        
        try {
         // Create the list of items and get the first command index, if there is one
            JSONArray commands = parsedResponse.getJSONArray("body");
            Log.e(TAG, commands.toString()+"BILLU");
            List<Command> items = new ArrayList<Command>();
            for (int i = 0; i < commands.length(); i++) {
                JSONObject command = commands.getJSONObject(i);
                items.add(new Command(command, (i == commands.length() - 1), script));
            }
            
            // If no items were found, there is nothing to troubleshoot
            if (items.size() == 0) {
                cyranoContext.finishTroubleshooting();
                Toast.makeText(cyranoContext, "This script has no commands", Toast.LENGTH_SHORT).show();
                
                
            // Otherwise, set the troubleshooting items and set the display to the first one
            } else {
                cyranoContext.setTroubleshootingItems(items);
                if (firstCommand > items.size()) {
                    firstCommand = 1;
                }
                cyranoContext.displayItemAt(firstCommand);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON response: " + e.toString());
        }
    }
}