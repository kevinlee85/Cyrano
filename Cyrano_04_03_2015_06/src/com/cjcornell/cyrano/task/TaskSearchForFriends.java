/*this class search for friends*/
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

import android.bluetooth.BluetoothDevice;
import android.net.rtp.RtpStream;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.cjcornell.cyrano.ActivityCyrano;
import com.cjcornell.cyrano.BluetoothFriend;
import com.cjcornell.cyrano.Constants;
import com.cjcornell.cyrano.SharedpreferenceUtility;
import com.cjcornell.cyrano.data.DataStore;

public class TaskSearchForFriends extends AsyncTask<List<BluetoothDevice>, Void, JSONObject> {
    private final static String TAG = "TaskSearchForFriend";
    private ActivityCyrano activity;
    
  
    public TaskSearchForFriends(ActivityCyrano activity) {
        this.activity = activity;
    }
    
    @Override
    protected void onCancelled() {
        super.onCancelled();
        activity.onErrorSearchForFriend();
    }
    
    @Override
    protected void onCancelled(JSONObject result) {
        super.onCancelled(result);
        activity.onErrorSearchForFriend();
    }
    
    @Override
    protected JSONObject doInBackground(List<BluetoothDevice>... arg0) {
      
        HttpClient httpClient = new DefaultHttpClient();
        String accessToken =  SharedpreferenceUtility.getInstance(activity).getString(Constants.ACCESS_TOKEN);
        String userId = SharedpreferenceUtility.getInstance(activity).getString(Constants.CYRANO_USER_ID);
        
        StringBuilder addresses = new StringBuilder("");
        List<BluetoothDevice> devices = arg0[0];
  
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
                + Constants.SEPERATOR + Constants.SERVICE_SEARCH_FOR_FRIENDS 
                + Constants.SEPERATOR + accessToken 
                + Constants.SEPERATOR + userId
                + Constants.SEPERATOR + addresses.toString();
        //http://cyrano.cjcornell.com/REST/index.php/FriendListMatchedBT/{access_token} / {user_id}/ 
        // { List of comma seperated single quoted BT mac address}
       // String t="http://cyrano.cjcornell.com/REST/index.php/FriendListMatchedBT/1852398296/196/'64:77:91:50:29:87'";
        
       Log.d(TAG, "Sending GET request at URL " + requestUrl);

        try {
            HttpGet httpGet = new HttpGet(requestUrl);
            
            JSONObject response = new JSONObject(httpClient.execute(httpGet, new BasicResponseHandler()));
            
            
            Log.d(TAG, "Successfully obtained FriendListMatchedBT"+response.toString());
            if (response.has("success")) {
                return response;
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
    protected void onPostExecute(JSONObject result) {
        
        /*try {
            result = new JSONObject("{\"body\":[{\"user_id\":\"id1\",\"bt_mac_address\":\"mac1\"},{\"user_id\":\"id2\",\"bt_mac_address\":\"mac2\"},{\"user_id\":\"id3\",\"bt_mac_address\":\"mac3\"}],\"success\":{\"message\":\"success-message\",\"code\":\"200\"}}");
        } catch (JSONException e1) {
            e1.printStackTrace();
        }*/
        // If there was a problem, the JSONArray is null, and we should simply return
       
        if (result == null) {
            Log.v(TAG, "JSONArray is null.");
            activity.onErrorSearchForFriend();
            return;
        }
        
        try {
            if(result.has("success")) {
                JSONObject success = result.getJSONObject("success");
                int code = success.getInt("code");
                if(code == 200) {
                    JSONArray array = result.getJSONArray("body");
                    activity.onCompleteSearchForFriend(array);
                    activity.onCompleteGetBluetoothMatchingDeviceTask(array);
                    int lengthJsonArr = array.length();  
                } else {
                    activity.onErrorSearchForFriend(success.getString("message"));
                }
            } else {
                activity.onErrorSearchForFriend();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
