package com.cjcornell.cyrano;

import java.util.HashMap;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;

public class PuranSpeakTest extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        BluetoothManager.enableBluetooth(this);
    };

    @Override
    protected void onPause() {
        super.onPause();
        BluetoothManager.disableBluetoothSCO(this);
    };

    String TTS_TAG = "PuranTest";
    TextToSpeech speech;

    
    public void test(View view) {
       
//            Intent ii = new Intent(this, TextToSpeachService.class);
//            ii.putExtra("speach", "Hello my testing Speach to text");
//            startService(ii);
//       
        
        // MyRunnable runnable = new MyRunnable();
        // new Thread(runnable).start();

    }

    @SuppressLint("NewApi")
    class MyRunnable implements Runnable {

        @Override
        public void run() {
            speech = new TextToSpeech(PuranSpeakTest.this, new OnInitListener() {

                @SuppressLint("NewApi")
                @SuppressWarnings("deprecation")
                @Override
                public void onInit(int status) {
                    Log.v(TTS_TAG, "Init : " + status + TextToSpeech.SUCCESS);
                    if (status == TextToSpeech.SUCCESS) {

                        speech.setOnUtteranceCompletedListener(new OnUtteranceCompletedListener() {

                            @Override
                            public void onUtteranceCompleted(String utteranceId) {
                                Log.v(TTS_TAG, "1 Completed successfully");
                            }
                        });
                        speech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                            @Override
                            public void onDone(String utteranceId) {
                                Log.v(TTS_TAG, "2 Completed successfully");

                            }

                            @Override
                            public void onError(String utteranceId) {
                                Log.v(TTS_TAG, "Error id: " + utteranceId);

                            }

                            @Override
                            public void onStart(String utteranceId) {
                                Log.v(TTS_TAG, "TTS Started");
                            }
                        });
                        speech.setLanguage(Locale.US);
                        HashMap<String, String> params = new HashMap<String, String>();

                        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "stringId");
                        speech.speak("Completed successfully Puran patidar", TextToSpeech.QUEUE_FLUSH, params);
                    }
                }
            });
        }
    }
}
