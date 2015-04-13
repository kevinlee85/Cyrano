package com.cjcornell.cyrano.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.R.string;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.cjcornell.cyrano.ActivityCyrano;
import com.cjcornell.cyrano.BluetoothFriend;
import com.cjcornell.cyrano.BluetoothUsers;
import com.cjcornell.cyrano.Friend;
import com.cjcornell.cyrano.ScriptItem;

/**
 * Stores data used throughout the application. Use this to save data that
 * everyone needs, instead of passing it from intent to intent. It will stay
 * valid even if you rotate the screen.
 */
public class DataStore {
    private static DataStore instance = new DataStore();

    private String token = null;
    private Friend me = null;
    public String TAG="DATASTORE";
    private String accessToken = null;
    private String macAddress = null;
    private String btMacAddress = null;
    private String earpieceMacAddress = null;
    private String phoneAndroidId = null;
    private List<BluetoothDevice> bluetoothDeviceList = new ArrayList<BluetoothDevice>();
    private List<BluetoothFriend> frientList = new ArrayList<BluetoothFriend>();
    public List<String> bluetoothmacaddress=new ArrayList<String>();
    private List<String> lDES = new ArrayList<String>();
    private boolean fetchSettingFlag = true;
    private BluetoothDiscover discover;
    private ActivityCyrano activity;
    public String[] DES;
    public List<String> Triggerscripts=new ArrayList<String>();
    public List<String> BlutoothFriendReminders=new ArrayList<String>();
    List<String> priorandcomplist=new ArrayList<String>();
    HashMap<String, Integer> giveidstobtids=new HashMap<String, Integer>();
    HashMap<String, String> IDSofBTIDS = new HashMap<String, String>();

    public boolean setsearchval;

    public boolean setfriendsearchval=true;

    public int inc;

    public int NumberofbuletoothFriends=0;
  
    protected DataStore() {}

    public static DataStore getInstance() {
        return instance;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String newToken) {
        token = newToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
    

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getBaseParameterString() {
        return this.accessToken + "/" + this.me.getId();
    }

    public Friend getMe() {
        return me;
    }

    public void setMe(Friend me) {
        this.me = me;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getBtMacAddress() {
        return btMacAddress;
    }

    public void setBtMacAddress(String btMacAddress) {
        this.btMacAddress = btMacAddress;
    }

    public String getEarpieceMacAddress() {
        return earpieceMacAddress;
    }

    public void setEarpieceMacAddress(String earpieceMacAddress) {
        this.earpieceMacAddress = earpieceMacAddress;
    }

    public String getPhoneAndroidId() {
        return phoneAndroidId;
    }

    public void setPhoneAndroidId(String phoneAndroidId) {
        this.phoneAndroidId = phoneAndroidId;
    }

    public List<BluetoothDevice> getBluetoothDeviceList() {
        return bluetoothDeviceList;
    }

    public void setBluetoothDeviceList(List<BluetoothDevice> bluetoothDeviceList) {
        if(bluetoothDeviceList == null) {
            this.bluetoothDeviceList = new ArrayList<BluetoothDevice>(bluetoothDeviceList);
            Log.v(TAG,"NULL");
        } else {
            this.bluetoothDeviceList = bluetoothDeviceList;
            Log.v(TAG,bluetoothDeviceList.toString());
         }
     }

    public List<BluetoothFriend> getFrientList() {
        return frientList;
    }

    public void setFrientList(List<BluetoothFriend> frientList) {
        this.frientList = frientList;
    }
    public boolean isFetchSettingFlag() {
        return fetchSettingFlag;
    }

    public void setFetchSettingFlag(boolean fetchSettingFlag) {
        this.fetchSettingFlag = fetchSettingFlag;
    }

    public BluetoothDiscover getDiscover() {
        return discover;
    }

    public void setDiscover(BluetoothDiscover discover) {
        this.discover = discover;
    }

    public ActivityCyrano getActivity() {
        return activity;
    }

    public void setActivity(ActivityCyrano activity) {
        this.activity = activity;
    }

    public void setTriggerDES(List<String> lDES2) {
        // TODO Auto-generated method stub
        lDES.addAll(lDES2);
    }

    public List<String> getTriggerDES() {
        // TODO Auto-generated method stub
        return lDES;
    }

    public void setblutoothdevicemac(List<String> bluetoothmacaddress) {
        this.bluetoothmacaddress.addAll(bluetoothmacaddress);
        // TODO Auto-generated method stub
        
    }

    public List<String> getbluletoothmac() {
        // TODO Auto-generated method stub
        return bluetoothmacaddress;
    }

    public void setTriggerIDS(List<String> triggerscripts2) {
        // TODO Auto-generated method stub
        Triggerscripts=triggerscripts2;
        
    }

    public List<String> getTriggerIDS() {
        // TODO Auto-generated method stub
        return Triggerscripts;
    }

    public void set_BlutoothFriendReminders(List<String> blutoothFriendReminders) {
        // TODO Auto-generated method stub
        BlutoothFriendReminders=blutoothFriendReminders;
        
    }

    public List<String> get_BluetoothFriendReminder() {
        // TODO Auto-generated method stub
        return BlutoothFriendReminders;
    }
    public void setpriorandcomlist(List<String> compairelist)
    {
        priorandcomplist=compairelist;
    }
    public List<String> getpriorandcomlist()
    {
        return priorandcomplist;
    }

    public void setIDSofBTIDS(HashMap<String, Integer> giveidstobtids2) {
        // TODO Auto-generated method stub
        giveidstobtids=giveidstobtids2;
        
    }

    public HashMap<String, Integer> getIDSofBTIDS() {
        // TODO Auto-generated method stub
        return giveidstobtids;
    }

    public void setTimestampofmatchedBT(HashMap<String, String> iDSofBTIDS2) {
        // TODO Auto-generated method stub
        IDSofBTIDS=iDSofBTIDS2;
        
    }
    

  
    
}
