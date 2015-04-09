//this class use to fetch script description from database and play audio whentrigger is matched.
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

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import android.util.Log;

import com.cjcornell.cyrano.ActivityCyrano;
import com.cjcornell.cyrano.Constants;
import com.cjcornell.cyrano.ScriptItem;
import com.cjcornell.cyrano.SharedpreferenceUtility;
import com.cjcornell.cyrano.TextToSpeachService;
import com.cjcornell.cyrano.data.DataStore;

public class TaskTriggerScript extends AsyncTask<Object, Void, JSONObject> {

    private final String TAG = "BHUPINDERTask";
    private ActivityCyrano cyranoContext;
    private int firstCommand;
    private ProgressDialog dialog;
    String des[];
    private ScriptItem script;
    String id="";
    JSONArray commands=null;
    private List<String> lDES = new ArrayList<String>();
    public TaskTriggerScript(ActivityCyrano activity, String id2) {
        // TODO Auto-generated constructor stub
        this.id=id2;
        this.cyranoContext = activity;
    }

    private void showDialog() {
        dialog = new ProgressDialog(cyranoContext);
        dialog.setCancelable(false);
        dialog.setMessage("Loading..");
       // dialog.show();
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
       // showDialog();
    }
    
    /**
     * This is the task to execute in the background. In this case, it will grab the troubleshooting group.
     */
    @Override
    protected JSONObject doInBackground(Object... params) {
       
        String scriptId = id;
        
        /* Send a GET request */
        HttpClient httpClient = new DefaultHttpClient();
        
        String accessToken =  SharedpreferenceUtility.getInstance(cyranoContext).getString(Constants.ACCESS_TOKEN);
        
        String userId = SharedpreferenceUtility.getInstance(cyranoContext).getString(Constants.CYRANO_USER_ID);
        
        String requestUrl = Constants.SERVER_ROOT 
                + Constants.SEPERATOR +"GetScripts"
                + Constants.SEPERATOR + accessToken 
                + Constants.SEPERATOR + userId
                + Constants.SEPERATOR + scriptId;
        
        //String requestUrl = COMMAND_URL + "/" + DataStore.getInstance().getBaseParameterString();
        Log.e(TAG, "Sending GET request at URL " + requestUrl);

        try {
            HttpGet httpGet = new HttpGet(requestUrl);
            JSONObject response = new JSONObject(httpClient.execute(httpGet, new BasicResponseHandler()));
            Log.v(TAG, response.toString());
            
            Log.d(TAG, "Successfully obtained instruction set for group " + scriptId);
            return response;
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
        String d="";
       
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
       Log.v(TAG, parsedResponse.toString());
      
            try {
                commands = parsedResponse.getJSONArray("body");
                Log.e(TAG, commands.length()+"");
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                Log.v(TAG, e1.toString());
            }
           
           // DataStore.getInstance().getTriggerDES().clear();
           // for(int i=0;i<commands.length();i++)
            //{
              // JSONObject jsonChildNode = commands.getJSONObject(i);
              // d= jsonChildNode.optString("command_desc").toString();
              // lDES.add(d);
           // }
           
           //  Log.v(TAG, lDES.size()+"");
            // DataStore.getInstance().setTriggerDES(lDES);
            // cyranoContext.triggerDES();
          //
        //} catch (JSONException e) {
          //  Log.e(TAG, "Error parsing JSON response: " + e.toString());
        //}
           
            List<ScriptItem> parsedScripts = new ArrayList<ScriptItem>();
            for (int i = 0; i < commands.length(); i++) {
               
                    try {
                        parsedScripts.add(new ScriptItem(commands.getJSONObject(i)));
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        Log.v(TAG, e.toString()+"BHUPINDER");
                    }
                Log.v(TAG, parsedScripts.size()+"");
            }
            // Set the troubleshooting items and remove the splash screen
           
            Log.d(TAG, commands.toString()+"\n"+"BHUPINDER");
            cyranoContext.startTroubleshooting(parsedScripts.get(0));
           
    }
}