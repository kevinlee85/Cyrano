/*this class search for friends*/
package com.cjcornell.cyrano.task;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONObject;

import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.ContactsContract.RawContacts.Data;
import android.text.TextUtils;
import android.util.Log;

import com.cjcornell.cyrano.ActivityCyrano;
import com.cjcornell.cyrano.data.DataStore;

public class TaskSendSimulationFile extends AsyncTask<List<BluetoothDevice>, Void, JSONObject> {
    private final static String TAG = "TaskSendFile";
    private ActivityCyrano activity;
    File f, f1;
    int count = 0, value = 1;
    Calendar c = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss");
    boolean simulationvalue, command, block, executeblock;
    List<BluetoothDevice> devices;
    HashMap<String, Integer> giveidstobtids = new HashMap<String, Integer>();
    HashMap<String, String> IDSofBTIDS = new HashMap<String, String>();
    List<String> after_compaire = new ArrayList<String>();
    String[] keywords = { "START", "OFF", "NEVER", "IGNORE" };// not add BTIDs
                                                              // until the END
                                                              // keyword

    /********* work only for Dedicated IP ***********/
    static final String FTP_HOST = "97.74.215.53";

    /********* FTP USERNAME ***********/
    static final String FTP_USER = "cyranotech";

    /********* FTP PASSWORD ***********/
    static final String FTP_PASS = "Elance2014!";

    public FTPClient client;

    /**********
     * Pick file from sdcard
     * 
     * @param blutoothdevicename
     *******/

    public TaskSendSimulationFile(ActivityCyrano activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();

    }

    @Override
    protected JSONObject doInBackground(List<BluetoothDevice>... arg0) {
        devices = arg0[0];
        String msg = "On" + "\n";
        for (int i = 0; i < devices.size(); i++) {
            msg += "\n" + devices.get(i).getName() + "   " + devices.get(i).getAddress() + "\n";
        }

        // generateNoteOnSD("simulation.txt", msg);
        f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes/simulation.txt");
        if (!f.exists()) {
            generateNoteOnSD("simulation.txt", "");
        }
        // Log.v(FTP_USER, f.toString());
        uploadFile(f);
        return null;

    }

    @Override
    protected void onPostExecute(JSONObject result) {

    }

    public class MyTransferListener implements FTPDataTransferListener {

        public void started() {

            // Transfer started

            Log.v("............", " Upload Started ...");

        }

        public void transferred(int length) {

            // Yet other length bytes has been transferred since the last time
            // this
            // method was called

            Log.v("............", " transferred ...");

        }

        public void completed() {

            // Transfer completed

            Log.v("............", " completed ....");

        }

        public void aborted() {

            // Transfer aborted

            Log.v("............", " transfer aborted , please try again...");

        }

        public void failed() {

            // Transfer failed

            Log.v("............", "failed ...");
        }

    }

    public void uploadFile(File fileName) {

        client = new FTPClient();

        try {

            client.connect(FTP_HOST, 21);
            client.login(FTP_USER, FTP_PASS);
            client.setType(FTPClient.TYPE_BINARY);
            client.changeDirectory("/");

            client.download("simulation.txt", f, new MyTransferListener());
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(f));
                String line;
                count = 0;
                String val = DataStore.getInstance().inc + "".trim();
                Log.e(TAG, val + " inc");
                while ((line = br.readLine()) != null) {
                    count++;
                    if (count == 1 && line.equalsIgnoreCase("no") || line.equalsIgnoreCase("off")
                            || line.equalsIgnoreCase("n") || line.equalsIgnoreCase("false")) {
                        Log.e(TAG, "Simulation fails");
                        simulationvalue = false;
                    } else if (count == 1 && line.equalsIgnoreCase("on") || line.equalsIgnoreCase("true")
                            || line.equalsIgnoreCase("start") || line.equalsIgnoreCase("y"))
                        simulationvalue = true;

                    if (simulationvalue) {
                        if (line.startsWith("//"))
                            Log.e(TAG, "skip");
                        else {

                            if (line.startsWith("SCAN") && line.contains("START"))
                                command = true;
                            if (line.startsWith("SCAN") && line.contains("END"))
                                command = false;
                            if (line.startsWith("SCAN") && line.contains("off") || line.contains("IGNORE")
                                    || line.contains("OFF") || line.contains("NEVER") || line.contains("START")) {
                                Log.e(TAG, "block start");
                                executeblock = true;
                            }
                            if (line.startsWith("SCAN") && line.contains("END")) {
                                executeblock = false;
                                Log.e(TAG, "block end");
                            }

                            if (command) {
                                Log.e(TAG, TextUtils.isEmpty(line.substring(5, 7).trim()) + "happy");
                                if ((line.substring(5, 7)).trim().equals(val)
                                        || TextUtils.isEmpty(line.substring(5, 7).trim())) {
                                    block = true;
                                }
                                if (block) {
                                    if (line.startsWith("SCAN") && command == true)
                                        Log.v(TAG, "SKIP");
                                    else {
                                        Log.v(TAG, line);
                                        if (line.contains("//")) {
                                            int value = line.toString().indexOf("//");
                                            String pure = line.substring(0, value).trim();

                                            text.append(pure.trim());
                                            text.append('\n');

                                        } else {

                                            text.append(line.trim());
                                            text.append('\n');

                                        }
                                    }
                                    Log.e(TAG, "BHUPINDER");
                                }

                            } else {
                                if (line.startsWith("SCAN") || command == true || line.contains("off")
                                        || line.contains("IGNORE") || line.contains("NEVER"))
                                    Log.v(TAG, "SKIP JI");
                                else {
                                    if (line.contains("//")) {
                                        int value = line.toString().indexOf("//");
                                        String pure = line.substring(0, value).trim();
                                        if (executeblock)
                                            Log.v(TAG, "executeblock" + executeblock);
                                        else {
                                            text.append(pure.trim());
                                            text.append('\n');
                                        }

                                    } else {
                                        if (executeblock)
                                            Log.v(TAG, "executeblock" + executeblock);
                                        else {
                                            text.append(line.trim());
                                            text.append('\n');
                                        }
                                    }
                                }

                            }

                        }
                    }

                }
                String[] lines = text.toString().split(System.getProperty("line.separator"));
                Log.e(TAG, "value of count" + count);
                for (int i = 0; i < lines.length; i++) {
                    String output = lines[i].replaceAll(" ", "");
                    DataStore.getInstance().getbluletoothmac().add(output);
                }
                br.close();
                if (DataStore.getInstance().getpriorandcomlist().isEmpty()) {
                    DataStore.getInstance().setpriorandcomlist(DataStore.getInstance().getbluletoothmac());
                    for (int i = 0; i < DataStore.getInstance().getbluletoothmac().size(); i++) {
                        giveidstobtids.put(DataStore.getInstance().getbluletoothmac().get(i), value);
                    }
                    DataStore.getInstance().setIDSofBTIDS(giveidstobtids);
                } else {
                    for (String temp : DataStore.getInstance().getbluletoothmac()) {
                        for (String temp1 : DataStore.getInstance().getpriorandcomlist()) {
                            if (temp.compareTo(temp1) == 0) {
                                Log.v(TAG, temp + " " + "correct");
                                after_compaire.add(temp.trim());
                                IDSofBTIDS.put(temp.trim(), df.format(c.getTime()) + "");

                                if (DataStore.getInstance().getIDSofBTIDS().get(temp) == null) {
                                    Log.e("tag", "exception");
                                } else {
                                    int valofbtid = DataStore.getInstance().getIDSofBTIDS().get(temp);
                                    DataStore.getInstance().getIDSofBTIDS().put(temp, valofbtid + value);
                                }

                            }

                        }
                    }
                    // DataStore.getInstance().getbluletoothmac().clear();
                    // DataStore.getInstance().setblutoothdevicemac(after_compaire);
                    DataStore.getInstance().setTimestampofmatchedBT(IDSofBTIDS);
                    Log.e(TAG, DataStore.getInstance().getIDSofBTIDS().toString());

                }
            } catch (IOException e) {
                // You'll need to add proper error handling here
                Log.d(TAG, e.toString());
            }
            // client.upload(fileName, new MyTransferListener());
            // Log.v(TAG,
            // text.toString()+DataStore.getInstance().getbluletoothmac().toString());
            // Log.i(TAG, DataStore.getInstance().getbluletoothmac().get(0));

        } catch (Exception e) {
            e.printStackTrace();
            try {
                client.disconnect(true);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

    }

    public void generateNoteOnSD(String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            // Toast.makeText(activity, "Saved", Toast.LENGTH_SHORT).show();
            Log.v(TAG, "saved........");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
