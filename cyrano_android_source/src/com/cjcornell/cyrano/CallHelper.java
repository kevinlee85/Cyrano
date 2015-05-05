package com.cjcornell.cyrano;

import android.content.Context;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallHelper {
    private final static String TAG = "CallHelper";
    int i = 0;
    int prev_state;
    public Context ctx;
    private TelephonyManager tm;
    private CallStateListener callStateListener;


    private class CallStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE: {
                    if ((prev_state == TelephonyManager.CALL_STATE_OFFHOOK)) {
                        prev_state = state;
                        // Answered Call which is ended
                        Log.v(TAG, "end");
                        new TextToSpeachService().getInstance().resumeMP();
                       
                    
                       
                    }
                    if ((prev_state == TelephonyManager.CALL_STATE_RINGING)) {
                        prev_state = state;
                        // Rejected or Missed call
                        Log.v(TAG, "Rejected");
                        new TextToSpeachService().getInstance().resumeMP();
                        
                    }

                   // if (i == 0) {
                     //   new Thread(new Runnable() {

                          //  @Override
                         //   public void run() {

                                // AudioManager aM = (AudioManager)
                                // ctx.getSystemService(Context.AUDIO_SERVICE);
                                //
                                // aM.setMode(AudioManager.MODE_IN_CALL);
                                // aM.startBluetoothSco();
                                // aM.setBluetoothScoOn(true);
                                // AudioMethods.textToSpeech(ctx,
                                // "Hello puran calling from 8226011160");

                          //  }
                        //}).start();
                    //}
                   // i++;
                    break;
                }
                case TelephonyManager.CALL_STATE_RINGING: {
                    // AudioMethods.textToSpeech(ctx,
                    // "Hello puran calling from 8226011160");
                   
              new TextToSpeachService().getInstance().pauseMP();
                    Log.d(TAG, "CALL_STATE_RINGING");
                    prev_state = state;

                    break;
                }
                case TelephonyManager.CALL_STATE_OFFHOOK: {
                    new TextToSpeachService().getInstance().pauseMP();
                    Log.d(TAG, "CALL_STATE_OFFHOOK");
                    prev_state = state;
                    break;
                }

            }
        }
    }

    // public class OutgoingReceiver extends BroadcastReceiver {
    // @Override
    // public void onReceive(Context context, Intent intent) {
    //
    // }
    // }
      
   
    public CallHelper(Context ctx) {
        this.ctx = ctx;
        callStateListener = new CallStateListener();
    }

    public void start() {
        tm = (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    public void stop() {
        tm.listen(callStateListener, PhoneStateListener.LISTEN_NONE);

    }
    
    
   

}
