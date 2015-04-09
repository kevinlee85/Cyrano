package com.cjcornell.cyrano.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.cjcornell.cyrano.ActivityCyrano;
import com.cjcornell.cyrano.Constants;
import com.cjcornell.cyrano.Friend;
import com.cjcornell.cyrano.SharedpreferenceUtility;
import com.cjcornell.cyrano.TextToSpeachService;
import com.cjcornell.cyrano.task.TaskSearchForDevices;
import com.cjcornell.cyrano.task.TaskSearchForFriends;
import com.cjcornell.cyrano.task.TaskSearchForTriggers;
import com.cjcornell.cyrano.task.TaskSendSimulationFile;

public class BluetoothDiscover {

    private static AudioManager aM;
    private static final String TAG = "BluetoothDiscover";
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter btAdapter;
    private ActivityCyrano activity;
    public int count=0;
    private List<BluetoothDevice> bluetoothDevices = null;
    public List<String> bluetoothmacaddress = null;

    public BluetoothDiscover(ActivityCyrano activity) {
        this.activity = activity;
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_UUID);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
     // filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        activity.registerReceiver(actionFoundReceiver, filter);
        btAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    public void start() {

        // allways discoverable to find BT users
        /*
         * if (btAdapter.getScanMode() !=
         * btAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) { Intent
         * discoverableIntent = new
         * Intent(btAdapter.ACTION_REQUEST_DISCOVERABLE);
         * discoverableIntent.putExtra(btAdapter.EXTRA_DISCOVERABLE_DURATION,0);
         * activity.startActivity(discoverableIntent);
         * 
         * }
         */
    	
        checkBTState();
    }

    // This routine is called when an activity completes.
    public void activityResult(int requestCode, int resultCode) {
        if (requestCode == REQUEST_ENABLE_BT) {
            checkBTState();
        }
    }

    public void stopAll() {
        if (btAdapter != null) {
            btAdapter.cancelDiscovery();
        }
        try {
            activity.unregisterReceiver(actionFoundReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void checkBTState() {
        if (btAdapter == null) {
            return;
        } else {
            if (btAdapter.isEnabled()) {
                btAdapter.startDiscovery();
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    private final BroadcastReceiver actionFoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            aM = (android.media.AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            if (aM.isBluetoothScoOn() == false) {
                Log.e(TAG, "SCO" + aM.isBluetoothScoOn());
                aM.setMode(AudioManager.MODE_NORMAL);
            }
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                /*
                 * Log.v(TAG, "Puran Device : " + device.getName() + " : " +
                 * device.getAddress());
                 */

                if (bluetoothDevices == null) {
                    bluetoothDevices = new ArrayList<BluetoothDevice>();
                    bluetoothmacaddress = new ArrayList<String>();

                } else {
                    Log.e(TAG, "add......." + device.getName());
                    bluetoothDevices.add(device);
                    bluetoothmacaddress.add(device.getAddress());

                }
                // new BTFriendFindAsynk(activity).execute(device.getAddress());
                // Toast.makeText(activity, device.getName() + ": " +
                // device.getAddress(), Toast.LENGTH_LONG).show();
                Log.d(TAG, device.getName() + ": " + device.getAddress());
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.v(TAG, "Puran Discovery Started...");
                if (bluetoothDevices == null) {
                    bluetoothDevices = new ArrayList<BluetoothDevice>();
                    bluetoothmacaddress = new ArrayList<String>();

                } else {
                    bluetoothDevices.clear();
                    bluetoothmacaddress.clear();

                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.v(TAG, "Puran Discovery Finish...");
                // Toast.makeText(activity, "Puran Discovery Finish...",
                // Toast.LENGTH_LONG).show();
                DataStore.getInstance().setBluetoothDeviceList(bluetoothDevices);
                DataStore.getInstance().getbluletoothmac().clear();
                DataStore.getInstance().setblutoothdevicemac(bluetoothmacaddress);
                // TaskSearchForTriggers(activity).execute(bluetoothDevices);
                count++;
               DataStore.getInstance().inc=count;
               new TaskSendSimulationFile(DataStore.getInstance().getActivity()).execute(bluetoothDevices);
               Log.e("heloo",DataStore.getInstance().setfriendsearchval+"");
               if(DataStore.getInstance().setfriendsearchval)
               {
               new TaskSearchForFriends(DataStore.getInstance().getActivity()).execute(DataStore.getInstance().getBluetoothDeviceList());
               DataStore.getInstance().setsearchval=true;
               }
              

            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                Log.v(TAG, "ACTION_STATE_CHANGED");
            }
        }
    };

   /* class BTFriendFindAsynk extends AsyncTask<String, Void, ArrayList<Friend>> {
        public String bluetoothURL = Constants.SERVER_ROOT + "/bluetooth";
        private final String TAG = "BTFriendFindAsynk";
        private ActivityCyrano activity;

        public BTFriendFindAsynk(ActivityCyrano activity) {
            this.activity = activity;
        }

        @Override
        protected ArrayList<Friend> doInBackground(String... params) {
            String btMac = params[0];

            String accessToken = SharedpreferenceUtility.getInstance(activity).getString(Constants.ACCESS_TOKEN);
            String userId = SharedpreferenceUtility.getInstance(activity).getString(Constants.CYRANO_USER_ID);
            String requestURL = bluetoothURL + "/" + accessToken + "/" + userId + "/" + btMac;

            HttpClient client = new DefaultHttpClient();
            Log.d(TAG, "Sending the Put Request");
            try {

                // Only ask the server for friends if the friend finder is on
                if (AppSettings.friendFinder) {
                    Log.d(TAG, "Asking server for nearby friends.");
                    HttpGet httpGet = new HttpGet(requestURL);

                    String response2 = client.execute(httpGet, new BasicResponseHandler());
                    Log.v(TAG, response2);
                    JSONObject parsedResponse = new JSONObject(response2);
                    if (parsedResponse.has("body")) {
                        ArrayList<Friend> friends = new ArrayList<Friend>(0);
                        JSONArray userIds = parsedResponse.getJSONArray("body");
                        for (int index = 0; index < userIds.length(); index++) {
                            JSONObject friendData = userIds.getJSONObject(index);
                            Friend f = new Friend(friendData.getString("id"), friendData.getString("first_name"),
                                    friendData.getString("last_name"), friendData.getString("email"),
                                    friendData.getString("email"), friendData.getDouble("distance"),
                                    friendData.getDouble("latitude"), friendData.getDouble("longitude"),
                                    friendData.getString("details1"), friendData.getString("details2"),
                                    friendData.getString("details3"));

                            // String id, String firstname, String lastname,
                            // String email,
                            // double distance, double latitude, double
                            // longitude,
                            // String details1, String details2, String details3

                            Log.d(TAG, "User " + f + " is nearby.");
                            friends.add(f);
                        }
                        // Log.v(TAG, ""+friends.size()+"\t"+response2);
                        return friends;
                    } else {
                        Log.e(TAG, "Error checking users near me.");
                    }
                } else {
                    Log.v(TAG, "Friend finder off - not asking server for nearby friends");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error contacting server.");
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Friend> friends) {
            if (AppSettings.friendFinder) {
                if (friends != null && friends.size() > 0) {
                    Intent i = new Intent(ActivityCyrano.DISPLAY_BT_FRIENDS);
                    i.putExtra("friend", friends.get(0));
                    LocalBroadcastManager.getInstance(activity).sendBroadcast(i);
                    Log.v(TAG, "Displayed found friends");
                }
            } else {
                Log.v(TAG, "Friend finder off - not displaying any friends");
            }
        }
    }*/
}