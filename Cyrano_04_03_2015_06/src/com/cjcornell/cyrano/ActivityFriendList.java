/**
 * CLASS: SettingsActivity
 *   This is the settings page for Cyrano.
 */


package com.cjcornell.cyrano;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cjcornell.cyrano.data.DataStore;

public class ActivityFriendList extends Activity {
    
    private final String TAG = "ActivityFriendList";
    private ListView listViewFriends;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_bluetooth_friend);
        listViewFriends = (ListView) findViewById(R.id.listview_friends);
            List<BluetoothFriend> list = DataStore.getInstance().getFrientList();
            String friends[] = new String[list.size()];
            
            for (int i = 0; i < list.size(); i++) {
                friends[i] = list.get(i).getName();
            }
            
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, friends);
            listViewFriends.setVisibility(View.VISIBLE);
            listViewFriends.setAdapter(adapter);
            Log.v(TAG, friends.toString());
            
           
        
    }        
        
}
