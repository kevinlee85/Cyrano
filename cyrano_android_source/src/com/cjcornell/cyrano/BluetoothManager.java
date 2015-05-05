/**
 * CLASS: BluetoothManager
 *   This class contains functionality for managing Bluetooth channels. This is specifically
 *   for enabling audio to be passed through Bluetooth headests.
 */

package com.cjcornell.cyrano;

import java.util.List;

import com.cjcornell.cyrano.data.DataStore;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

public class BluetoothManager {
    // For Bluetooth connectvity
    private static String TAG = "BluetoothManager";
    private static BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static AudioManager aM;
    
    /**
     * Set the audio manager of the device.
     * @param c: The context this method is called from
     */
    public static void setAudioManager(Context c) {
        aM = (android.media.AudioManager)c.getSystemService(Context.AUDIO_SERVICE);
    }
    
    /**
     * Get bluetooth MAC adress
     */
    public static String getBluetoothMacAddress() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // if device does not support Bluetooth
        if(mBluetoothAdapter==null){
            Log.d(TAG,"device does not support bluetooth");
            return null;
        }
        return mBluetoothAdapter.getAddress();
    }
    /**
     * Check if a Bluetooth headset is connected. If so, route audio to Bluetooth SCO.
     */
    private static void initializeAudioMode(Context context) {
        BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                if (profile == BluetoothProfile.HEADSET) {
                    BluetoothHeadset bh = (BluetoothHeadset) proxy;
                    List<BluetoothDevice> devices = bh.getConnectedDevices();
                    if (devices.size() > 0) {
                        
                        /*for (int i = 0; i < devices.size(); i++) {
                            Log.d(TAG, "****Mac: " + devices.get(i).getAddress() + "\n****Name: " + devices.get(i).getName());
                            
                            ParcelUuid[] uuids = devices.get(i).ge getUuids();                            for (ParcelUuid uuid: uuids) {
                                Log.d(TAG, "-> UUID: " + uuid.getUuid().toString());
                            }
                        }*/
                        
                        // Save earpiece mac
                      
                        DataStore.getInstance().setEarpieceMacAddress(devices.get(0).getAddress());
                        Log.e(TAG, "BLUETOOTH SERVICES"+devices.get(0).getAddress());
                        enableBluetoothSCO();
                    }
                }
                
                mBluetoothAdapter.closeProfileProxy(profile, proxy);
            }
            @Override
            public void onServiceDisconnected(int profile) {
                Log.d(TAG, "profilr"+profile);
            }
            
        };
        mBluetoothAdapter.getProfileProxy(context, mProfileListener, BluetoothProfile.HEADSET);
    }
    
    /**
     * Bluetooth Connectvity
     *   The following methods are associated with enabling/disabling Bluetooth.
     *   In the future we may want to disable other sources of audio.
     */
    private static void enableBluetoothSCO() {        
        aM.setMode(AudioManager.MODE_IN_COMMUNICATION);     
        Log.v(TAG, " new : "+AudioManager.MODE_NORMAL);
       Log.v(TAG, "new : "+AudioManager.MODE_IN_COMMUNICATION);
        aM.startBluetoothSco();
        aM.setBluetoothScoOn(true);
    }
    public  static void disableBluetoothSCO(Context c) {   
            setAudioManager(c);
            aM.setMode(AudioManager.MODE_NORMAL);
            aM.stopBluetoothSco();
            aM.setBluetoothScoOn(false);
    }

    /** Right now, this simply enables Bluetooth */
    @SuppressLint("NewApi")
    public static boolean enableBluetooth(Context c) {
        // If there is an adapter, enable it if not already enabled
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable(); 
            }
            setAudioManager(c);
            initializeAudioMode(c);
            Log.v(TAG, "Mode: " + aM.getMode());
            Log.v(TAG, "SCO: " + aM.isBluetoothScoOn());
            Log.v(TAG, "A2DP: " + aM.isSpeakerphoneOn());
            if(aM.isBluetoothScoOn()==false)
            {
                aM.setMode(AudioManager.MODE_NORMAL);
            }
            return true;
        } else {
            Log.v(TAG, "There is no bluetooth adapter");
            return false;
        }
    }

    /** Right now, this simply disables Bluetooth */
    public static void disableBluetooth() {
        // If there is an adapter, disabled it if not already disabled
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.disable(); 
            }
        } else {
            Log.v(TAG, "There is no bluetooth adapter");
        }
    }
}
