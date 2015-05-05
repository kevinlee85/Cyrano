package com.cjcornell.cyrano;

import android.util.Log;

import com.cjcornell.cyrano.data.DataStore;

public class SnoozeMuteCheck {
	public String TAG="SnoozeMuteProcess";
	public boolean btIDcheckSnooze(String address) {
		// TODO Auto-generated method stub
		Log.i(TAG, address+"");
		if(DataStore.getInstance().getIDSofBTIDS().get(address)!=null){
		if(DataStore.getInstance().getIDSofBTIDS().get(address)>1)
		return true;
		else
		return false;
		}
		else
			return false;
	}
}
