/**
 * CLASS: GPSAsyncTask
 *   This asynchronous task is used to update GPS coordinates
 */

package com.cjcornell.cyrano;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.cjcornell.cyrano.data.AppSettings;

public class GPSAsyncTask extends AsyncTask<Location, Void, ArrayList<Friend>> {
    public String locationURL = Constants.SERVER_ROOT + "/Location";

    private final String TAG = "GPSListener";
    private final long MAX_DISTANCE = 1000;

    //private FriendFinderService ffs;
    private Context ffs;

    /** Constructor - set the friend finder service attribute */
    public GPSAsyncTask(Context service) {
        ffs = service;
    }

    /**
     * This method is the one to be executed in the background - it will get the
     * user's GPS coordinates and update the database with them.
     */
    @Override
    protected ArrayList<Friend> doInBackground(Location... params) {
        Location loc = params[0];
        double latitude = 0
        ,longitude=0;
        if(loc!=null){
          latitude=  loc.getLatitude();
          longitude=  loc.getLongitude();
            Log.d(TAG, "Latitude: " + loc.getLatitude());
            Log.d(TAG, "Longitude: " + loc.getLongitude());  
        }
       
        String accessToken =  SharedpreferenceUtility.getInstance(ffs).getString(Constants.ACCESS_TOKEN);
        
        String userId = SharedpreferenceUtility.getInstance(ffs).getString(Constants.CYRANO_USER_ID);
        
        String requestURL = locationURL + "/" +  accessToken+ "/" + userId + "/"
                + latitude + "/" + longitude;
        HttpClient client = new DefaultHttpClient();
        Log.d(TAG, "Sending the Put Request"+requestURL+"**************************");

        try {
            HttpPut httpPut = new HttpPut(requestURL);
            HttpResponse response = client.execute(httpPut);
            if (response.getStatusLine().getStatusCode() == 200) {
                Log.d(TAG, "Successfully uploaded coordinates.");
            } else {
                Log.e(TAG, "Could not upload coordinates.");
            }

            /* Read the response fully before sending another request */
            response.getEntity().consumeContent();

            requestURL = requestURL + "/" + MAX_DISTANCE;
            Log.v(TAG, requestURL);

            // Only ask the server for friends if the friend finder is on
            if (AppSettings.friendFinder) {
                Log.d(TAG, "Asking server for nearby friends.");
                HttpGet httpGet = new HttpGet(requestURL);
                ArrayList<Friend> friends = new ArrayList<Friend>(0);
                Log.v(TAG, requestURL);
                // Code copied from FindFriendsTask begins here
                String response2 = client.execute(httpGet, new BasicResponseHandler());
                Log.d(TAG, "Response Got"+response2+"***********************");
                JSONObject parsedResponse = new JSONObject(response2);
                if (parsedResponse.has("body")) {
                    JSONArray userIds = parsedResponse.getJSONArray("body");
                    for (int index = 0; index < userIds.length(); index++) {
                        JSONObject friendData = userIds.getJSONObject(index);
                        Friend f = new Friend(friendData.getString("id"), friendData.getString("first_name"),
                                friendData.getString("last_name"), friendData.getString("email"), friendData.getString("bluetooth_address"),
                                 friendData.getDouble("distance"), friendData.getDouble("latitude"),
                                friendData.getDouble("longitude"), friendData.getString("details1"),
                                friendData.getString("details2"), friendData.getString("details3"));
                        Log.d(TAG, "User " + f + " is nearby.");
                        friends.add(f);
                    }
                    return friends;
                } else {
                    Log.e(TAG, "Error checking users near me.");
                    return friends;
                }
            } else {
                Log.v(TAG, "Friend finder off - not asking server for nearby friends");
            }
        }
        catch (Exception e) {
            Log.e(TAG, "Error contacting server.");
        }

        return null;
    }

    /**
     * This method is executed after the doInBackground method finishes. It will
     * display any found friends, if the settings permit it.
     */
    @Override
    protected void onPostExecute(ArrayList<Friend> friends) {
        // Do not display anything if the friend finder setting is off
        if (AppSettings.friendFinder) {
            if (friends != null && friends.size() > 0) {
                if (AppSettings.friendAudio && !ActivityCyrano.isActivityfront) {
                    Log.v(TAG, "Puran Bluetooth on");
                    /*if (!CyranoActivity.isActivityfront) {
                        BluetoothManager.enableBluetooth(ffs);
                    }*/
                    Intent ii = new Intent(ffs, TextToSpeachService.class);                
                    ii.putExtra("friends", friends);
                    //ii.putExtra("friends", friends.get(0).getFirstName());
                    ffs.startService(ii); 
                }
                FriendFinderService.gotFriends(friends,ffs);
            }else{
                FriendFinderService.friendList=friends;
            }
            Log.v(TAG, "Displayed found friends");
        } else {
            Log.v(TAG, "Friend finder off - not displaying any friends");
        }
    }

}