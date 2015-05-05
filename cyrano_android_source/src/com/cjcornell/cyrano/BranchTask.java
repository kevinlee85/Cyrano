/**
 * CLASS: BranchTask
 *   This class is an asynchronous task used for branching instructions.
 */

package com.cjcornell.cyrano;

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

import android.os.AsyncTask;
import android.util.Log;

public class BranchTask extends AsyncTask<Object, Void, JSONObject> {
    private final String TAG = "CoachingTask";
    private final String COMMAND_URL = ActivityWebLogin.SERVER_ROOT + "/branchcommand";
    private ActivityCyrano cyranoContext;
    private String instructionID;
    
    /**
     * This is the task to execute in the background. In this case, it will grab the new instruction set to branch to.
     */
    @Override
    protected JSONObject doInBackground(Object... params) {
        instructionID = params[0].toString();
        cyranoContext = (ActivityCyrano) params[1];
        
        /* Send a GET request */
        HttpClient httpClient = new DefaultHttpClient();
        
        String accessToken =  SharedpreferenceUtility.getInstance(cyranoContext).getString(Constants.ACCESS_TOKEN);
        
        String userId = SharedpreferenceUtility.getInstance(cyranoContext).getString(Constants.FB_USER_ID);
        
        String requestUrl = COMMAND_URL + "/" + accessToken + userId + "/" + instructionID;
        
        //String requestUrl = COMMAND_URL + "/" + DataStore.getInstance().getBaseParameterString() + "/" + instructionID;
        Log.d(TAG, "Sending GET request at URL " + requestUrl);
        try {
            HttpGet httpGet = new HttpGet(requestUrl);
            String response = httpClient.execute(httpGet, new BasicResponseHandler());
            
            Log.d(TAG, "Got commands for group containing " + instructionID);
            return new JSONObject(response);
        } catch (HttpResponseException e) {
            Log.e(TAG, "Could not obtain commands for group containing " + instructionID);
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
        try {
            // Create the list of items and get the first command index, if there is one
            int firstCommand = -1;
            JSONArray commands = parsedResponse.getJSONArray("body");
            List<Command> items = new ArrayList<Command>();
            if (commands.length() > 0) {
                ScriptItem parent = new ScriptItem(commands.getJSONObject(0));
                for (int i = 1; i < commands.length(); i++) {
                    JSONObject command = commands.getJSONObject(i);
                    items.add(new Command(command, (i == commands.length() - 1), parent));
                    if (command.getString("commandID").equals(instructionID)) {
                        firstCommand = i;
                    }
                }
            }
            
            // If no items were found, there is nothing to troubleshoot
            if (items.size() == 0) {
                cyranoContext.finishTroubleshooting();
                
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
