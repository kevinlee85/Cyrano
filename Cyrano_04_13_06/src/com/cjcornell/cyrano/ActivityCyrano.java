/**
 * CLASS: CyranoActivity
 *   This activity is where the troubleshooting scripts and all the friend displays are shown.
 */

package com.cjcornell.cyrano;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cjcornell.cyrano.AudioMethods.AudioCompletionNotifiable;
import com.cjcornell.cyrano.ImageDownload.ImageLoader;
import com.cjcornell.cyrano.data.AppSettings;
import com.cjcornell.cyrano.data.BluetoothDiscover;
import com.cjcornell.cyrano.data.DataStore;
import com.cjcornell.cyrano.task.TaskAdvanceSearch;
import com.cjcornell.cyrano.task.TaskGetAllScripts;
import com.cjcornell.cyrano.task.TaskGetCommandScript;
import com.cjcornell.cyrano.task.TaskGetUserInfoBluetoothList;
import com.cjcornell.cyrano.task.TaskGetUserScripts;
import com.cjcornell.cyrano.task.TaskSearchForDevices;
import com.cjcornell.cyrano.task.TaskSearchForFriends;
import com.cjcornell.cyrano.task.TaskSearchForTriggers;
import com.cjcornell.cyrano.task.TaskUpdateBluetoothEarpiece;
import com.cjcornell.cyrano.utils.Utils;
import com.facebook.Session;

public class ActivityCyrano extends Activity implements AudioMethods.AudioCompletionNotifiable,
        com.cjcornell.cyrano.TextToSpeachService.AudioCompletionNotifiable {

    private final static String TAG = "Cyrano";
    private final static int MAX_BRANCHES = 4;
    public final static String DISPLAY_FRIENDS = "com.cjcornell.cyrano.DISPLAY_FRIENDS";
    public final static String DISPLAY_BT_FRIENDS = "com.cjcornell.cyrano.DISPLAY_BT_FRIENDS";
    // Milliseconds before automatically closing splash screen
    private final static int SPLASH_TIMEOUT = 10000;
    SharedPreferences sp;
    boolean isuipause = false;
    int triggerval;
    ImageLoader img = new ImageLoader(ActivityCyrano.this);
    /**
     * Layout attributes - it makes sense to put these here as they are accessed
     * by many methods
     */
    private RelativeLayout friendContent;
    private ImageView friendPicture;
    private TextView friendName,friendAboutText;
    private TextView friendCoordinates;

    private RelativeLayout friendsContent;
    private ListView friendsList;
    private RelativeLayout commandGroupContent;
    private ListView commandGroups;
    private CommandGroupAdapter commandGroupAdapter;

    private LinearLayout mainContent, playbackControls, branchControls;

    @SuppressWarnings("unused")
    private ImageView mainPicture, splashPicture;
    private TextView mainTitle, mainMessage;
    private Button pauseButton, stopButton, previousButton, nextButton;
    private Button[] branchButtons;
    private ScheduledThreadPoolExecutor runner;
    private ScheduledFuture<?> autoAdvance = null;
    private Handler uiHandler;
    List<Friend> btFriends;
    Customadaptor ca;

    private AudioManager am;
    private TaskSearchForFriends taskSearchForBTFriend = null;

    private TaskUpdateBluetoothEarpiece taskUpdateBluetoothEarpiece = null;
    private int statusTaskUpdateBluetoothEarpiece = 0;

    // TODO for test
    public static Activity context;
    public static boolean isActivityfront = false, activityispause = false;
    private BluetoothDiscover discover;
    // Used with friend notifications
    // private BroadcastReceiver gotFriends = new BroadcastReceiver() {
    // @Override
    // public void onReceive(Context context, Intent intent) {
    // Log.v(TAG, "Got message to display friends.");
    // List<Friend> friends =
    // Arrays.asList((Friend[])intent.getSerializableExtra("friends"));
    // displayFriends(friends);
    // }
    // };
    public BroadcastReceiver gotBTFriends = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Friend friend = (Friend)intent.getSerializableExtra("friend");
            Map<String, Friend> map = new HashMap<String, Friend>();
            btFriends.add(friend);
            for (Friend friend2 : btFriends) {
                map.put(friend2.getId(), friend2);
            }
            Set<String> ids = map.keySet();
            btFriends = new ArrayList<Friend>();
            for (String id : ids) {
                btFriends.add(map.get(id));
            }
            Log.v(TAG, "" + friend.getFirstName());
            // announceMultipleFriends(btFriends.toArray(new
            // Friend[btFriends.size()]),null);
            AudioMethods.textToSpeech(ActivityCyrano.this, getString(R.string.singleFriendMessage, friend.getName()));
            displayFriends(btFriends);

            // Get the list of friends to display and display them
        }
    };

    // A list of troubleshooting items
    private List<Command> tsItems = new ArrayList<Command>();
    private Command currentItem;

    // Application state variables
    private boolean currentlyTroubleshooting;
    private Runnable backButtonAction;

    // Dialog that displays the splash screen
    private Dialog mSplashDialog;
    private boolean speak;

    /**
     * Automatically advances to the next item. You can cancel the advance if
     * (e.g.) the user presses the next or previous button.
     * 
     * If there is nothing to advance to (we are the last item), we clear the UI
     * and finish the troubleshooting script.
     */
    private class Advancer implements Runnable {
        private Command whichItem;

        public Advancer(Command i) {
            whichItem = i;
        }

        @Override
        public void run() {
            Log.v(TAG, "Automatically advancing to next item...");
            autoAdvance = null;
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (whichItem.isLast()) {
                        finishTroubleshooting();
                    } else if (whichItem.canAdvance()) {
                        advanceItem(1);

                    }

                }

            });
        }
    }

    /**
     * Executed when the activity is first created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cyrano);
        context = this;
        // We must set these AFTER the content view is set
        setLayoutAttributes();
        new CallHelper(getApplicationContext());
        // showSplashScreen();

        // Load up the command groups
        new TaskGetUserScripts(this).execute();
        runner = new ScheduledThreadPoolExecutor(1);
        runner.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        runner.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);

        am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

        // Saving Android ID
        String android_id = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
        DataStore.getInstance().setPhoneAndroidId(android_id);

        // Give the settings activity access to this instance
        ActivitySettings.activity = ActivityCyrano.this;
        // Load default settings
        AppSettings.initSettings(this);

        initData();

        // Initialize for BT (if a headset is connected)
        // BluetoothManager.enableBluetooth(this);

        discover = new BluetoothDiscover(ActivityCyrano.this);
        DataStore.getInstance().setDiscover(discover);

        // Start up the FriendFinderService
        startService(new Intent(this, FriendFinderService.class));
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Friend me = DataStore.getInstance().getMe();
        if (me != null) {
            Editor e = sp.edit();
            e.putString("id", me.getId());
            e.putString("name", me.getName());
            e.putString("email", me.getEmail());
            e.putString("bmacaddress", DataStore.getInstance().getBtMacAddress());
            e.putString("about_text", me.getabout_text());
            e.commit();
        }

    }

    private void initData() {
        // KINJ
        // Pause audio and item advancing when sound from other app detected
        // and continue from current position when it stop

        int result = am.requestAudioFocus(new OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                // Toast.makeText(getApplicationContext(),
                // "onAudioFocusChange - Called", Toast.LENGTH_LONG).show();

                if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                    // Toast.makeText(getApplicationContext(),
                    // "onAudioFocusChange - Called - LOSS",
                    // Toast.LENGTH_LONG).show();

                    // currentItem.pause();
                    cancelAutoAdvance();

                    // uiHandler = null;
                } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                    // Toast.makeText(getApplicationContext(),
                    // "onAudioFocusChange - Called - GAIN",
                    // Toast.LENGTH_LONG).show();

                    // currentItem.play(CyranoActivity.this);

                    if (autoAdvance == null) {
                        // Toast.makeText(getApplicationContext(),
                        // "onAudioFocusChange - Called - GAIN - autoAdvance",
                        // Toast.LENGTH_LONG).show();

                        scheduleAutoAdvance(currentItem);
                    }
                }
            }
        }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

    }

    /**
     * Called when the options menu is created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cyrano, menu);
        return true;
    }

    /**
     * Called when an option is selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Get the option selected, and do the appropriate action
        int itemId = item.getItemId();
        Intent stopFFS;
        switch (itemId) {
            case R.id.action_nearby:
                // switchToNearbyFriends();
                // switchToNearbyFriendsBT();
                if (currentlyTroubleshooting) {
                    cancelAutoAdvance();
                    if (currentItem != null) {
                        currentItem.pause();
                    }
                }
                List<BluetoothFriend> list = DataStore.getInstance().getFrientList();
                if (list.size() > 0) {
                    displayMultipleFriends(list);
                    setupFriendsDisplay();
                    setUpFriendDisplayBackButton();
                } else {
                    Toast.makeText(this, R.string.noFriendsMessage, Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_scripts:
                if (currentlyTroubleshooting) {
                    finishTroubleshooting();
                } else {
                    resetUI();
                }
                return true;
            case R.id.action_settings:
                if (currentlyTroubleshooting) {
                    currentItem.pause();
                }
                Intent intent = new Intent(ActivityCyrano.this, ActivitySettings.class);
                startActivity(intent);
                return true;
            case R.id.action_account:
                showAccountInformation();
                return true;
            case R.id.action_about:
                showAbout();
                return true;
            case R.id.action_logout:
                // Location location= null;
                // new GPSAsyncTask(CyranoActivity.this).execute(location);
                stopFFS = new Intent(FriendFinderService.SHUTDOWN_FFS);
                LocalBroadcastManager.getInstance(this).sendBroadcast(stopFFS);
                Session session = Session.getActiveSession();
                session.closeAndClearTokenInformation();
                Log.v(TAG, "Logout detected");

                // Removing user data saved
                Utils.removeUserData(ActivityCyrano.this);

                // Make an intent to close all activities (and including) under
                // CyranoActivitiy
                Intent closeIntent = new Intent(getApplicationContext(), ActivityCyrano.class);
                closeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                closeIntent.putExtra("EXIT", true);

                // Make a new intent for the login page
                Intent loginIntent = new Intent(this, ActivityFacebook.class);

                // Start both activities
                // startActivity(closeIntent);
                startActivity(loginIntent);
                finish();
                return true;
            case R.id.action_exit:
                stopFFS = new Intent(FriendFinderService.SHUTDOWN_FFS);
                LocalBroadcastManager.getInstance(this).sendBroadcast(stopFFS);
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                startActivity(homeIntent);

            case R.id.action_search_for:
                final Dialog dialog = new Dialog(ActivityCyrano.this);
                dialog.setTitle(getResources().getString(R.string.action_search_for));
                dialog.setContentView(R.layout.dialog_search_for);
                dialog.show();

                final ListView listViewSearchFor = (ListView)dialog.findViewById(R.id.list_view_search_for);
                String[] options = getResources().getStringArray(R.array.array_search_for);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                        options);
                listViewSearchFor.setAdapter(adapter);
                listViewSearchFor.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        dialog.dismiss();
                        switch (position) {

                            case 0:
                                // Search for friend
                                discover.start();
                                new TaskSearchForFriends(ActivityCyrano.this).execute(DataStore.getInstance()
                                        .getBluetoothDeviceList());
                                break;

                            case 1:
                                // Search for devices
                                discover.start();
                                new TaskSearchForDevices(ActivityCyrano.this).execute(DataStore.getInstance()
                                        .getBluetoothDeviceList());
                                break;

                            case 2:
                                // Search for triggers
                                discover.start();
                                new TaskSearchForTriggers(ActivityCyrano.this).execute(DataStore.getInstance()
                                        .getBluetoothDeviceList());
                                break;

                            case 3:
                                // Advance search
                                discover.start();
                                dialog.dismiss();
                                advanceSearch();
                                break;
                            default:
                                break;
                        }
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Called when the pause play control is pressed
     */
    @Override
    protected void onPause() {

        // discover.stopAll();
        // uiHandler = null;
        AudioMethods.stopTextToSpeech();
        // cancelAutoAdvance();
        activityispause = true;
        /*
         * KINJ isActivityfront=false; cancelAutoAdvance();
         * 
         * 
         * if (currentItem != null) { currentItem.pause(); }
         */

        // OLD CODE

        // BluetoothManager.disableBluetoothSCO(this);

        // LocalBroadcastManager.getInstance(this).unregisterReceiver(gotFriends);
        discover.stopAll();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(gotBTFriends);
        super.onPause();
    }

    private void checkForCrashes() {

        CrashManager.register(this, "39a07ca7626e8c3846225d24d8f2871e");
    }

    private void checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this, "39a07ca7626e8c3846225d24d8f2871e");
    }

    /**
     * XXX: We stop the GPS friend detect stuff when Cyrano is closed. Perhaps
     * later we'll want to keep running, but do something besides switch to
     * Cyrano and read it out?
     */
    @Override
    protected void onResume() {

        super.onResume();
        
        isActivityfront = true;
        activityispause = false;
        checkForCrashes();
        checkForUpdates();
        // discover.start();
        BluetoothManager.enableBluetooth(this);

        uiHandler = new Handler();

        // IntentFilter iff = new IntentFilter(DISPLAY_FRIENDS);
        // LocalBroadcastManager.getInstance(this).registerReceiver(gotFriends,
        // iff);

        IntentFilter biff = new IntentFilter(DISPLAY_BT_FRIENDS);
        // IntentFilter biff = new IntentFilter(DISPLAY_FRIENDS);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_UUID);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        LocalBroadcastManager.getInstance(this).registerReceiver(gotBTFriends, biff);

        DataStore.getInstance().setActivity(ActivityCyrano.this);

        if (taskUpdateBluetoothEarpiece == null && statusTaskUpdateBluetoothEarpiece != 200
                && DataStore.getInstance().getEarpieceMacAddress() != null) {
            String[] params = { DataStore.getInstance().getEarpieceMacAddress(),
                    DataStore.getInstance().getPhoneAndroidId() };
            taskUpdateBluetoothEarpiece = new TaskUpdateBluetoothEarpiece(ActivityCyrano.this);
            taskUpdateBluetoothEarpiece.execute(params);
        }
    }

    /**
     * Called when the back button play control is pressed
     */
    @Override
    public void onBackPressed() {
        if (backButtonAction != null) {
            // copy the action in case r.run() sets a new action.
            Runnable r = backButtonAction;
            backButtonAction = null;
            r.run();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Shows the splash screen over the CyranoActivity
     */
    protected void showSplashScreen() {
        mSplashDialog = new Dialog(this, R.style.SplashScreen);
        mSplashDialog.setContentView(R.layout.splash_screen);
        // setSplashImage();
        mSplashDialog.setCancelable(false);
        mSplashDialog.show();

        // Set Runnable to remove splash screen just in case
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                removeSplashScreen();
            }
        }, SPLASH_TIMEOUT);
    }

    /**
     * Removes the Dialog that displays the splash screen
     */
    protected void removeSplashScreen() {
        if (mSplashDialog != null) {
            mSplashDialog.dismiss();
            mSplashDialog = null;
        }
    }

    /**
     * Show the nearby friends display
     */
    protected void switchToNearbyFriends() {
        // If we are in troubleshooting mode, do not auto advance and pause the
        // current item before switching displays
        if (currentlyTroubleshooting) {
            cancelAutoAdvance();
            if (currentItem != null) {
                currentItem.pause();
            }
        }
        // Get the list of friends to display and display them
        List<Friend> fl = FriendFinderService.friendList;
        if (fl != null && fl.size() > 0) {
            if (AppSettings.friendAudio) {
                // Intent ii = new Intent(this, TextToSpeachService.class);
                // ii.putExtra("friends", fl.toArray());
                // startService(ii);
                announceMultipleFriends(fl.toArray(new Friend[fl.size()]), null);
            }
            // displayMultipleFriends(fl);
            // setupFriendsDisplay();
            // setUpFriendDisplayBackButton();

            // If no friends are nearby, display a toast stating this
        } else {
            new TextToSpeachService().textToSpeech(ActivityCyrano.this,
                    this.getResources().getString(R.string.noFriendsMessage));
            Toast.makeText(this, R.string.noFriendsMessage, Toast.LENGTH_SHORT).show();
        }

    }

    private void announceMultipleFriends(Friend[] friends, AudioCompletionNotifiable n) {
        int numFriends = Math.min(AppSettings.maxFriends, friends.length);
        List<String> phrases = new ArrayList<String>();
        List<Integer> pauses = new ArrayList<Integer>();
        if (friends.length == 1) {
            phrases.add(this.getString(R.string.singleFriendsMessage));
        } else {
            phrases.add(this.getString(R.string.multipleFriendsMessage, friends.length));
        }
        pauses.add(AppSettings.pauseLength);
        for (int i = 0; i < numFriends; i++) {
            phrases.add(this.getString(R.string.singleFriendMessage, friends[i].getName()));
            pauses.add(1);
        }
        AudioMethods.playInstructions(this, phrases, pauses, n);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        discover.activityResult(requestCode, resultCode);
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Show the nearby friends display using bluetooth
     */
    protected void switchToNearbyFriendsBT() {

        if (taskSearchForBTFriend != null) {
            return;
        }

        // If we are in troubleshooting mode, do not auto advance and pause the
        // current item before switching displays
        if (currentlyTroubleshooting) {
            cancelAutoAdvance();
            if (currentItem != null) {
                currentItem.pause();
            }
        }

        speak = true;
        discover.start();
        taskSearchForBTFriend = new TaskSearchForFriends(ActivityCyrano.this);
        taskSearchForBTFriend.execute(DataStore.getInstance().getBluetoothDeviceList());
    }

    /**
     * Show the about display with Cyrano's version info
     */
    public void showAbout() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name);
            PackageInfo info;
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionStr = getString(R.string.versionText, info.versionName);
            builder.setMessage(versionStr);
            builder.create();
            builder.show();
        } catch (NameNotFoundException e) {
            Log.w(TAG, e);
        }
    }

    /**
     * Show the screen with the user's account information - this is the same as
     * the single friend display
     */
    private void showAccountInformation() {
        cancelAutoAdvance();
        setupFriendDisplay(true);
        setUpFriendDisplayBackButton();
        Log.v(TAG, "1");
        Friend me = DataStore.getInstance().getMe();
        Log.v(TAG, "2" + me);
        displaySingleFriend(me, true);
        Log.v(TAG, "3");
    }

    /** Set all the layout attributes */
    public void setLayoutAttributes() {
        friendContent = (RelativeLayout)findViewById(R.id.friendContent);
        friendPicture = (ImageView)findViewById(R.id.friendPicture);
        friendName = (TextView)findViewById(R.id.friendName);
        friendAboutText = (TextView)findViewById(R.id.friendAboutus);
        friendCoordinates = (TextView)findViewById(R.id.friendCoordinates);

        friendsContent = (RelativeLayout)findViewById(R.id.friendsContent);
        friendsList = (ListView)findViewById(R.id.friendsList);

        commandGroupContent = (RelativeLayout)findViewById(R.id.commandGroupContent);
        commandGroups = (ListView)findViewById(R.id.commandGroups);

        mainContent = (LinearLayout)findViewById(R.id.mainContent);
        mainTitle = (TextView)findViewById(R.id.mainTitle);
        mainPicture = (ImageView)findViewById(R.id.mainPicture);
        splashPicture = (ImageView)findViewById(R.id.splashPicture);
        mainMessage = (TextView)findViewById(R.id.mainMessage);

        playbackControls = (LinearLayout)findViewById(R.id.playbackControls);
        pauseButton = (Button)findViewById(R.id.pauseButton);
        stopButton = (Button)findViewById(R.id.stopButton);
        previousButton = (Button)findViewById(R.id.previousButton);
        nextButton = (Button)findViewById(R.id.nextButton);

        branchControls = (LinearLayout)findViewById(R.id.branchControls);
        branchButtons = new Button[MAX_BRANCHES];
        branchButtons[0] = (Button)findViewById(R.id.branch1);
        branchButtons[1] = (Button)findViewById(R.id.branch2);
        branchButtons[2] = (Button)findViewById(R.id.branch3);
        branchButtons[3] = (Button)findViewById(R.id.branch4);
    }

    /**
     * Set up the back button functionality for the friend display
     */
    private void setUpFriendDisplayBackButton() {
        if (currentlyTroubleshooting && currentItem != null) {
            backButtonAction = new Runnable() {
                @Override
                public void run() {
                    AudioMethods.stopTextToSpeech();
                    displayItem(currentItem);
                }
            };
        } else {
            backButtonAction = new Runnable() {
                @Override
                public void run() {
                    AudioMethods.stopTextToSpeech();
                    resetUI();
                }
            };
        }
    }

    /**
     * Sets up the UI objects for multiple friends. Call
     * {@link #setupFriendsDisplay()} to actually show them. To set up
     * everything for you, taking into account settings, you should be calling
     * {@link #displayFriends(List)}.
     * 
     * @param list
     *            The list of friends to display. Must not be null.
     */
    private void displayMultipleFriends(final List<BluetoothFriend> list) {

        // Set the adapter, using the layout corresponding to the current font
        // size setting
        ArrayAdapter<BluetoothFriend> adapter = null;
        if (AppSettings.textSize < 1) {
            adapter = new ArrayAdapter<BluetoothFriend>(this, R.layout.layout_list1, list);
        } else if (AppSettings.textSize == 1) {
            Log.v("bhupinder...", " " + list.toString());
            // adapter = new ArrayAdapter<Friend>(this, R.layout.layout_list2,
            // friends);

            ca = new Customadaptor(ActivityCyrano.this, list);
        } else {
            adapter = new ArrayAdapter<BluetoothFriend>(this, R.layout.layout_list3, list);
        }
        friendsList.setAdapter(ca);

        // Set up the action for clicking on a friend in the list
        friendsList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v(TAG, TAG + "bhupinder");
                setupFriendDisplay(false);
                Log.v(TAG, TAG + "bhupinder1");
                displaySingleFriend(list.get(position), false);
                Log.v(TAG, TAG + "bhupinder2");
                backButtonAction = new Runnable() {
                    @Override
                    public void run() {
                        setupFriendsDisplay();
                        displayMultipleFriends(list);
                    }
                };
            }

            private void displaySingleFriend(BluetoothFriend bluetoothFriend, boolean isMe) {
                // TODO Auto-generated method stub
                String url = null;
                Bitmap picture = null;
                // About to get very hacky...
                friendName.setText(bluetoothFriend.getName()
                        + (bluetoothFriend.getEmail().equals("") ? "" : " (" + bluetoothFriend.getEmail() + ")"));
                StringBuilder sb = new StringBuilder();
                sb.append("MAC Address" + bluetoothFriend.getAddress() + "\n")
                        .append("Email" + bluetoothFriend.getEmail() + "\n")
                        .append("ID" + bluetoothFriend.getId() + "\n");
                friendCoordinates.setText(sb.toString());

                // picture = new
                // FacebookProfileDownloader().execute(bluetoothFriend.getId()).get();
                url = "https://graph.facebook.com/" + bluetoothFriend.getId() + "/picture?type=large";
                Log.v(TAG, "friend.getid" + sp.getString("id", " "));

                // Only display the picture if we are in graphical mode
                if (AppSettings.graphicalMode) {
                    friendPicture.setVisibility(View.VISIBLE);
                    // friendPicture.setImageBitmap(picture);
                    img.DisplayImage(url, friendPicture);
                } else {
                    friendPicture.setVisibility(View.GONE);
                }

                // Only say anything if we drill down and friend audio is
                // enabled,
                // and we are not displaying the current Cyrano user.
                // Our service takes care of reading out friends usually.
                if (AppSettings.friendAudio && !isMe) {
                    // Intent ii = new Intent(this, TextToSpeachService.class);
                    // ii.putExtra("speach",friend.getName());
                    // startService(ii);
                    Log.v(TAG, TAG + "BHU");
                    AudioMethods.textToSpeech(ActivityCyrano.this, bluetoothFriend.getName());
                }

            }
        });
    }

    /**
     * Displays the UI objects to view multiple friends.
     */
    private void setupFriendsDisplay() {
        clearUI();
        setTitle(getString(R.string.app_name) + ": " + getString(R.string.multiFriendsHeading));
        friendsContent.setVisibility(View.VISIBLE);
    }

    /**
     * Sets up the UI objects for multiple friends. Call
     * {@link #setupFriendDisplay(boolean)} to actually show them. To set up
     * everything for you, taking into account settings, you should be calling
     * {@link #displayFriends(List)}.
     * 
     * @param friend
     *            The friend whose details to display. Must not be null.
     * @param isDrilldown
     *            If we're coming from the multiple friend list
     * @param isMe
     *            If we're displaying the Cyrano user
     */
    private void displaySingleFriend(Friend friend, boolean isMe) {
        // Log.v(TAG, friend.toString());
        Bitmap picture = null;
        String url = null;
        if (isMe) {
            friendName.setText(sp.getString("name", " ")
                    + (sp.getString("email", " ").equals("") ? "" : " (" + sp.getString("email", " ") + ")"));
            friendCoordinates.setText("Facebook ID: " + sp.getString("id", " ") + "\nMac Address: "
                    + sp.getString("bmacaddress", " "));
            friendAboutText.setText(sp.getString("about_text", " "));
            // picture = new
            // FacebookProfileDownloader().execute(sp.getString("id",
            // " ")).get();
            url = "https://graph.facebook.com/" + sp.getString("id", " ") + "/picture?type=large";
        } else {
            // About to get very hacky...
            friendName.setText(friend.getName() + (friend.getEmail().equals("") ? "" : " (" + friend.getEmail() + ")"));
            friendAboutText.setText(sp.getString("about_text", " "));

            StringBuilder sb = new StringBuilder();
            sb.append(AppSettings.formatter.format(friend.getLatitude())).append(", ")
                    .append(AppSettings.formatter.format(friend.getLongitude())).append(" (")
                    .append(friend.getDistanceString()).append(")\n").append("Details1: ").append(friend.getDetails1())
                    .append("\nDetails2: ").append(friend.getDetails2()).append("\nDetails3: ")
                    .append(friend.getDetails3());
            friendCoordinates.setText(sb.toString());

            // picture = new
            // FacebookProfileDownloader().execute(friend.getId()).get();
            url = "https://graph.facebook.com/" + friend.getId() + "/picture?type=large";
            Log.v(TAG, "friend.getid" + sp.getString("id", " "));
        }

        // Only display the picture if we are in graphical mode
        if (AppSettings.graphicalMode) {
            friendPicture.setVisibility(View.VISIBLE);
            // friendPicture.setImageBitmap(picture);
            img.DisplayImage(url, friendPicture);
        } else {
            friendPicture.setVisibility(View.GONE);
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
        @Override
              public void run(){
            new TextToSpeachService().getInstance().textToSpeech(ActivityCyrano.this,sp.getString("name", " ") + sp.getString("about_text", " "));

           }
        }, 20);
        
        // Only say anything if we drill down and friend audio is enabled,
        // and we are not displaying the current Cyrano user.
        // Our service takes care of reading out friends usually.
        if (AppSettings.friendAudio && !isMe) {
            // Intent ii = new Intent(this, TextToSpeachService.class);
            // ii.putExtra("speach",friend.getName());
            // startService(ii);
            AudioMethods.textToSpeech(this, friend.getName());
        }
    }

    /**
     * Displays the UI objects to show a single friend.
     * 
     * @param isMe
     *            If we're displaying the Cyrano user
     */
    private void setupFriendDisplay(boolean isMe) {
        clearUI();
        if (isMe) {
            setTitle(getString(R.string.app_name) + ": " + getString(R.string.myAccountHeading));
        } else {
            setTitle(getString(R.string.app_name) + ": " + getString(R.string.singleFriendHeading));
        }
        friendContent.setVisibility(View.VISIBLE);
    }

    /**
     * Display found friends, whether zero, one, or many. You should call this
     * function to display some friends.
     * 
     * @param friends
     *            The friends to display. May be null or empty.
     */
    public void displayFriends(final List<Friend> friends) {
        if (friends == null || friends.isEmpty()) {
            Log.v(TAG, "No friends found nearby");
            return;
        }

        cancelAutoAdvance();

        if (AppSettings.autoDisplayFriends) {
            setupFriendsDisplay();
            setUpFriendDisplayBackButton();
        }
        displayMultipleFriends(DataStore.getInstance().getFrientList());
    }

    /** Update the UI for the main display */
    public void displayItem(Command item) {
        clearUI();

        Log.v(TAG, "URL: " + item.getURL());

        // cancel the auto advance as we display something else
        cancelAutoAdvance();
        if (currentItem != null) {
            currentItem.stop();
        }

        currentItem = item;
        setTitle(getString(R.string.app_name) + ": " + item.getGroupName());

        // Show the step number if in verbose mode
        mainTitle.setText(item.getName());

        // Show the description only if we are NOT in terse mode
        if (!AppSettings.terseMode) {
            mainMessage.setText(item.getDescription());
            mainMessage.setVisibility(View.VISIBLE);
        }

        // We want the main content and playback controls to always be visible
        mainContent.setVisibility(View.VISIBLE);
        playbackControls.setVisibility(View.VISIBLE);
        playControlButtons(item);

        // Display the image associated with the item, if there is one and if
        // graphical mode is on
        if (AppSettings.graphicalMode && item.getURL() != null && !"".equals(item.getURL())) {
            mainPicture.setVisibility(View.VISIBLE);
            img.DisplayImage(item.getURL(), mainPicture);
        } else {
            mainPicture.setImageBitmap(null);
        }

        boolean shouldShowButtons = false;

        /*
         * for (int i = 0; i < item.getBranches().size(); ++i) { Command.Branch
         * br = item.getBranches().get(i); if (br != null) { shouldShowButtons =
         * true; branchButtons[i].setText(br.label);
         * branchButtons[i].setVisibility(View.VISIBLE); } else {
         * branchButtons[i].setVisibility(View.INVISIBLE); } }
         */
        if (shouldShowButtons) {
            branchControls.setVisibility(View.VISIBLE);
        }

        // play the item automatically
        item.play(this);
        scheduleAutoAdvance(item);
        Log.i(TAG, "display item");
    }

    /**
     * playControlButtons This method creates the play control buttons. Based on
     * the play control flags in the database, it will hide/display various play
     * controls.
     * 
     * The items know about the item 0 defaults and take them into account.
     * 
     * @param item
     *            : The item currently displayed
     */
    private void playControlButtons(Command item) {
        if (item.isStoppable())
            stopButton.setVisibility(View.VISIBLE);
        else
            stopButton.setVisibility(View.INVISIBLE);

        if (item.isPausable())
            pauseButton.setVisibility(View.VISIBLE);
        else
            pauseButton.setVisibility(View.INVISIBLE);

        nextButton.setVisibility(View.VISIBLE);
        // nextButton.setEnabled(item.canAdvance());

        previousButton.setVisibility(View.VISIBLE);
        previousButton.setEnabled(item.canGoBack());
    }

    /**
     * Display a single coaching item
     * 
     * @param itemIndex
     *            The instruction number of the item to show (NOT an index into
     *            our list of items).
     */
    public void displayItemAt(int itemIndex) {
        if (itemIndex > 0 && itemIndex <= tsItems.size()) {
            displayItem(tsItems.get(itemIndex - 1));
        }
    }

    /** Called when the pause button is clicked */
    public void pauseButtonClicked(View view) {
        Log.v(TAG, "Pause/play button clicked.");
        if (pauseButton.getText().equals(getString(R.string.pauseButtonText))) {
            currentItem.pause();
            cancelAutoAdvance();
            pauseButton.setText(getString(R.string.playButtonText));
            new TextToSpeachService().getInstance().mp.pause();
        } else {
            currentItem.play(this);
            if (autoAdvance == null) {
                scheduleAutoAdvance(currentItem);
                new TextToSpeachService().getInstance().mp.start();
            }
            pauseButton.setText(getString(R.string.pauseButtonText));
        }
    }

    /** Called when the stop button is clicked */
    public void stopButtonClicked(View view) {
        Log.v(TAG, "Stop button clicked.");
        // Stop aborts the whole troubleshooting script
        finishTroubleshooting();
    }

    /** Called when the previous button is clicked */
    public void previousButtonClicked(View view) {
        Log.v(TAG, "Previous button clicked.");
        // Above template will be over-written by non-nulls
        advanceItem(-1);
    }

    /** Called when the next button is clicked */
    public void nextButtonClicked(View view) {
        Log.v(TAG, "Next button clicked.");
        // Above template will be over-written by non-nulls
        advanceItem(1);
    }

    /** Get the branch button number */
    private int branchButtonNo(View view) {
        for (int i = 0; i < MAX_BRANCHES; ++i) {
            if (branchButtons[i] == view) {
                return i;
            }
        }
        return -1;
    }

    /**
     * branchClicked Executed when a branch is clicked. It will start a new
     * CoachingTask with the appropriate scripts to branch to.
     */
    public void branchClicked(View view) {
        /*
         * int branchNo = branchButtonNo(view);
         * 
         * // If the branch number is invalid, do nothing if (branchNo < 0 ||
         * branchNo >= currentItem.getBranches().size()) return;
         * 
         * // Branch to the appropriate troubleshooting group Command.Branch br
         * = currentItem.getBranches().get(branchNo); if (br.groupId >= 0) { new
         * CoachingTask(this).execute(br.groupId, this, br.instructionId); }
         * else if (br.instructionId > 0) { new
         * BranchTask().execute(br.instructionId, this); }
         */
    }

    /**
     * Start troubleshooting.
     */
    public void startTroubleshooting(ScriptItem script) {
        new TaskGetCommandScript(this).execute(script, this);
        backButtonAction = new Runnable() {
            @Override
            public void run() {
                finishTroubleshooting();
            }
        };
    }

    /**
     * Finish troubleshooting. This allows the friend detection to take over the
     * screen again.
     */
    public void finishTroubleshooting() {
        cancelAutoAdvance();
        if (currentItem != null) {
            currentItem.stop();
            Log.v(TAG, TAG);
        }
        Log.v(TAG, TAG + TAG);
        resetUI();
        currentlyTroubleshooting = false;
    }

    /**
     * Advance item or finish script. This is called when an audio item with
     * delay == 0 finishes.
     */
    @Override
    public void audioCompleted() {
        if (currentItem.isLast()) {
            finishTroubleshooting();
        } else {
            advanceItem(1);
        }
    }

    /**
     * Goes to the offset'th item after {@link currentItem} in {@link tsItems}.
     * 
     * @param offset
     *            The item offset number (negative numbers go to previous items)
     */
    public void advanceItem(int offset) {
        displayItemAt(currentItem.getNumber() + offset);
    }

    /**
     * Schedules an item to automatically advance. Does nothing if the item
     * should not advance automatically.
     * 
     * @param item
     *            The item which should advance
     */

    public void scheduleAutoAdvance(Command item) {
        cancelAutoAdvance();
        if (currentItem != null && currentItem.getDelay() > 0) {
            Log.v(TAG, "Advancing to next item in " + currentItem.getDelay() + " seconds");
            new TextToSpeachService().getInstance().scriptpaly(currentItem, ActivityCyrano.this);
            // autoAdvance = runner.schedule(new Advancer(currentItem),
            // (long)currentItem.getDelay(), TimeUnit.SECONDS);
            isuipause = true;
        }
    }

    /**
     * Cancels the current auto-advance.
     */
    public void cancelAutoAdvance() {
        if (autoAdvance != null) {
            autoAdvance.cancel(true);
            autoAdvance = null;
        }
    }

    /** Reset the UI to its default display */
    public void resetUI() {
        clearUI();
        backButtonAction = null;
        if (commandGroupAdapter != null) {
            commandGroupContent.setVisibility(View.VISIBLE);
            setTitle(getString(R.string.app_name) + ": " + getString(R.string.scriptsHeading));
        } else {
            new TaskGetAllScripts(this).execute();
        }
    }

    /** Clear the UI */
    public void clearUI() {

        // Set all content to be gone
        friendContent.setVisibility(View.GONE);
        friendsContent.setVisibility(View.GONE);
        commandGroupContent.setVisibility(View.GONE);
        mainContent.setVisibility(View.GONE);
        mainMessage.setVisibility(View.GONE);
        mainPicture.setVisibility(View.GONE);
        playbackControls.setVisibility(View.GONE);
        branchControls.setVisibility(View.GONE);
        setTitle(getString(R.string.app_name));
    }

    /** Set the font sizes based on the settings */
    public void setFontSizes() {

        mainMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, AppSettings.getTextSize());
        mainTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, AppSettings.getTitleSize());
    }

    // TODO: Probably get rid of this in favor of the single "currentItem"
    public void setTroubleshootingItems(List<Command> items) {

        this.tsItems = items;
    }

    /**
     * Set troubleshooting item groups - we really only need to call this once.
     */
    public void onCompleteGetUserScriptsTask(final List<ScriptItem> scripts) {

        Utils.dLog("onCompleteGetAllScriptsTask, Script length: " + scripts.size());
        commandGroupAdapter = new CommandGroupAdapter(this, scripts);
        commandGroups.setAdapter(commandGroupAdapter);
        commandGroups.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startTroubleshooting(scripts.get(position));
                new TextToSpeachService().getInstance().commandcallPush = true;

            }
        });

        resetUI();

        if (taskUpdateBluetoothEarpiece == null && statusTaskUpdateBluetoothEarpiece != 200
                && DataStore.getInstance().getEarpieceMacAddress() != null) {

            Log.d(TAG, "Inside onCompleteGetAllScriptsTask");
            taskUpdateBluetoothEarpiece = new TaskUpdateBluetoothEarpiece(ActivityCyrano.this);
            taskUpdateBluetoothEarpiece.execute(DataStore.getInstance().getEarpieceMacAddress(), DataStore
                    .getInstance().getPhoneAndroidId());
        }
    }

    public void onCompleteUpdateBluetoothEarpieceTask(int statusCode) {

        Utils.dLog("onCompleteUpdateBluetoothEarpieceTask, Status code: " + statusCode);
        if (statusCode == 200) {
            Toast.makeText(ActivityCyrano.this, "Earpiece is registered successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ActivityCyrano.this, "Earpiece is not registered", Toast.LENGTH_SHORT).show();
        }
        taskUpdateBluetoothEarpiece = null;
        statusTaskUpdateBluetoothEarpiece = statusCode;
    }

    public void onErrorUpdateBluetoothEarpieceTask() {

        Utils.eLog(TAG + ": onErrorUpdateBluetoothEarpieceTask");
        Utils.showShortToast(ActivityCyrano.this, "Getting error during updating earpiece");
        taskUpdateBluetoothEarpiece = null;
        statusTaskUpdateBluetoothEarpiece = 0;
    }

    public void onCompleteGetBluetoothMatchingDeviceTask(JSONArray arr) {

        Utils.dLog("onCompleteGetBluetoothMatchingDeviceTask, Friend found: " + arr.length());
        DataStore.getInstance().NumberofbuletoothFriends=arr.length();
        List<String> BlutoothFriendReminders=new ArrayList<String>();
        BlutoothFriendReminders.clear();
        taskSearchForBTFriend = null;
        if (arr.length() > 0) {
            Toast.makeText(ActivityCyrano.this, arr.length() + " friend(s) found", Toast.LENGTH_SHORT).show();
            List<String> devices = new ArrayList<String>();
            for (int i = 0; i < arr.length(); i++) {
                try {
                    devices.add(arr.getJSONObject(i).getString("bt_mac_address"));
                    BlutoothFriendReminders.add(arr.getJSONObject(i).getString("personal_reminder"));//this thing is use to store the personal reminder of friend
                    Log.v(TAG, devices.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            new TaskGetUserInfoBluetoothList(ActivityCyrano.this).execute(devices);
            DataStore.getInstance().set_BlutoothFriendReminders(BlutoothFriendReminders);
        } else {
            if (speak) {
                Toast.makeText(ActivityCyrano.this, this.getResources().getString(R.string.noFriendsMessage),
                        Toast.LENGTH_SHORT).show();
                // new TextToSpeachService().textToSpeech(ActivityCyrano.this,
                // this.getResources().getString(R.string.noFriendsMessage));
                speak = false;
            }
        }
    }

    public void onErrorBluetoothMatchingDeviceTask() {

        Utils.eLog(TAG + ": onErrorBluetoothMatchingDeviceTask");
        Utils.showShortToast(ActivityCyrano.this, "Getting error during matching bluetooth device");
        taskSearchForBTFriend = null;
    }

    public void onCompleteGetUserInfoBluetoothTask(List<BluetoothFriend> list) {

        Utils.dLog("onCompleteGetUserInfoBluetoothTask, Friend found : " + list.size());
        DataStore.getInstance().setFrientList(list);
    }

    public void onErrorGetUserInfoBluetoothTask() {

        Utils.eLog(TAG + ": onErrorGetUserInfoBluetoothTask");
        Utils.showShortToast(ActivityCyrano.this, "Getting error during fetching bluetooth device info");
    }

    public void onErrorSearchForFriend() {
        taskSearchForBTFriend = null;
        Utils.eLog(TAG + ": onErrorSearchForFriend"+"BHUPINDERE");
        Utils.showShortToast(ActivityCyrano.this, "Getting error during friend search");
        DataStore.getInstance().setsearchval = false;
        DataStore.getInstance().NumberofbuletoothFriends=0;
        if (DataStore.getInstance().setsearchval == false) {
        	  Utils.eLog(TAG + ": onErrorSearchForFriend"+"BHUPINDer1");
            new TaskSearchForDevices(DataStore.getInstance().getActivity()).execute(DataStore.getInstance()
                    .getBluetoothDeviceList());
            new TaskSearchForTriggers(DataStore.getInstance().getActivity()).execute(DataStore.getInstance()
                    .getBluetoothDeviceList());
            DataStore.getInstance().setfriendsearchval = false;
        }

    }

    public void onErrorSearchForFriend(String message) {
        taskSearchForBTFriend = null;
        Utils.eLog(TAG + ": onErrorSearchForFriend"+"BHUPINDer");
        Utils.showShortToast(ActivityCyrano.this, message);
       
    }

    public void onCompleteSearchForFriend(JSONArray arr) {

        taskSearchForBTFriend = null;
        Utils.dLog("onCompleteSearchForFriend: " + arr.toString());
        Utils.showShortToast(ActivityCyrano.this, arr.length() + " friend(s) found");
       

            if (arr.length() == 1) {
                new TextToSpeachService().getInstance().textToSpeech(ActivityCyrano.this,
                        this.getResources().getString(R.string.singleFriendsMessage));
            } else if (arr.length() > 1) {
                new TextToSpeachService().getInstance().textToSpeech(ActivityCyrano.this,
                        this.getResources().getString(R.string.multipleFriendsMessage, arr.length()));
            } else {
                DataStore.getInstance().NumberofbuletoothFriends=0;
                DataStore.getInstance().setsearchval = false;
                if (DataStore.getInstance().setsearchval = false) {
                    new TaskSearchForDevices(DataStore.getInstance().getActivity()).execute(DataStore.getInstance()
                            .getBluetoothDeviceList());
                    new TaskSearchForTriggers(DataStore.getInstance().getActivity()).execute(DataStore.getInstance()
                            .getBluetoothDeviceList());
                    DataStore.getInstance().setfriendsearchval = false;
                }
            }
            
        
    }

    public void onErrorSearchForDevices() {

        Utils.eLog(TAG + ": onErrorSearchForDevices");
        Utils.showShortToast(ActivityCyrano.this, "Getting error during device search");
    }

    public void onCompleteSearchForDevices(int size, JSONArray arr) {

        Utils.dLog("onCompleteSearchForDevices: " + arr.toString());
        Utils.showShortToast(ActivityCyrano.this, size + " device(s) found");   
    }

    public void onErrorSearchForTriggers() {

        Utils.eLog(TAG + ": onErrorSearchForTriggers");
        Utils.showShortToast(ActivityCyrano.this, "Getting error during trigger search");
        DataStore.getInstance().setfriendsearchval = true;

    }

    public void onCompleteSearchForTriggers(int size, JSONArray arr) {

        Utils.dLog("onCompleteSearchForTriggers: " + arr.toString());
        Utils.showShortToast(ActivityCyrano.this, size + " trigger(s) found");
        if (arr.length() == 0) {
            DataStore.getInstance().setfriendsearchval = true;
            ;
        }
    }

    public void onErrorAdvanceSearch() {
        Utils.eLog(TAG + ": onErrorAdvanceSearch");
        Utils.showShortToast(ActivityCyrano.this, "Getting error during advance search");
    }

    public void onCompleteAdvanceSearchCount(int size, String serviceName) {
        Utils.dLog("onCompleteAdvanceSearchCount: " + serviceName);

        Utils.showShortToast(
                ActivityCyrano.this,
                size
                        + (serviceName.contains(Utils.KEYWORD_FRIEND) ? " friend(s)" : serviceName
                                .contains(Utils.KEYWORD_DEVICE) ? " device(s)" : " trigger(s)") + " found");
    }

    public void onCompleteAdvanceSearchResult(JSONArray arr, String serviceName) {
        Utils.showShortToast(
                ActivityCyrano.this,
                arr.length()
                        + (serviceName.contains(Utils.KEYWORD_FRIEND) ? " friend(s) record" : serviceName
                                .contains(Utils.KEYWORD_DEVICE) ? " device(s) record" : " trigger(s) record")
                        + " found");
    }

    private void advanceSearch() {

        final Dialog dialog = new Dialog(ActivityCyrano.this);
        dialog.setTitle(getResources().getString(R.string.action_search_for));
        dialog.setContentView(R.layout.dialog_advance_search);
        dialog.show();

        final RadioGroup radioGroupSearchFor = (RadioGroup)dialog.findViewById(R.id.radio_group_search_options);
        final TextView textView = (TextView)dialog.findViewById(R.id.textview_select_record_count);
        final CheckBox checkBoxCountOnly = (CheckBox)dialog.findViewById(R.id.check_count_only);
        final NumberPicker numberPickerRecordCount = (NumberPicker)dialog
                .findViewById(R.id.number_picker_number_of_records_needed);
        numberPickerRecordCount.setMaxValue(100);
        numberPickerRecordCount.setMinValue(1);
        numberPickerRecordCount.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        final CheckBox checkBoxDetailRecord = (CheckBox)dialog.findViewById(R.id.check_detail_search);
        final Button buttonSearch = (Button)dialog.findViewById(R.id.button_search);

        checkBoxCountOnly.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                numberPickerRecordCount.setVisibility(isChecked ? View.INVISIBLE : View.VISIBLE);
                textView.setVisibility(isChecked ? View.INVISIBLE : View.VISIBLE);
                checkBoxDetailRecord.setVisibility(isChecked ? View.INVISIBLE : View.VISIBLE);
            }
        });
        checkBoxCountOnly.setChecked(true);

        buttonSearch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.dismiss();
                RadioButton selectedRadio = (RadioButton)dialog.findViewById(radioGroupSearchFor
                        .getCheckedRadioButtonId());
                Utils.dLog((String)selectedRadio.getTag());

                StringBuilder btAddresses = new StringBuilder("");
                List<BluetoothDevice> devices = DataStore.getInstance().getBluetoothDeviceList();
                for (int i = 0; i < devices.size(); i++) {
                    btAddresses.append("'" + devices.get(i).getAddress() + "'");
                    if (i != devices.size() - 1) {
                        btAddresses.append(",");
                    }
                }

                String accessToken = SharedpreferenceUtility.getInstance(ActivityCyrano.this).getString(
                        Constants.ACCESS_TOKEN);
                String userId = SharedpreferenceUtility.getInstance(ActivityCyrano.this).getString(
                        Constants.CYRANO_USER_ID);

                String service = (String)selectedRadio.getTag();
                boolean countOnly = checkBoxCountOnly.isChecked();
                boolean detailedRecord = checkBoxDetailRecord.isChecked();
                String requestUrl = Constants.SERVER_ROOT + Constants.SEPERATOR + service + Constants.SEPERATOR
                        + accessToken + Constants.SEPERATOR + userId + Constants.SEPERATOR + btAddresses.toString()
                        + Constants.SEPERATOR + (countOnly ? Utils.FIRST_PARAM_COUNT : Utils.FIRST_PARAM_RECORD)
                        + Constants.SEPERATOR + (detailedRecord ? Utils.SECOND_PARAM_FULL : Utils.SECOND_PARAM_LIGHT)
                        + Constants.SEPERATOR + numberPickerRecordCount.getValue();

                new TaskAdvanceSearch(ActivityCyrano.this, service, countOnly, detailedRecord).execute(requestUrl);

            }
        });
    }

    public void nextscript(Command whichItem) {

        if (whichItem.isLast()) {
            new TextToSpeachService().getInstance().commandcallPush = false;
            new TextToSpeachService().getInstance().commandcall = false;
            if (!new TextToSpeachService().getInstance().Triggerscript) {
                DataStore.getInstance().setfriendsearchval = true;
            }
            finishTroubleshooting();
        } else if (whichItem.canAdvance()) {
            advanceItem(1);

        }

    }

    /*
     * public void triggerDES() { // TODO Auto-generated method stub Log.v(TAG,
     * TAG + TAG); triggerval = 0; new
     * TextToSpeachService().getInstance().triggerscript(ActivityCyrano.this);
     * triggerSpeech(); }
     * 
     * public void triggerSpeech() { // TODO Auto-generated method stub
     * 
     * new TextToSpeachService().getInstance().textToSpeech(ActivityCyrano.this,
     * DataStore.getInstance().getTriggerDES().get(triggerval), (1 == 0) ?
     * ActivityCyrano.this : null); Log.v(TAG, "yahioo"); triggerval++;
     * 
     * }
     */

}
