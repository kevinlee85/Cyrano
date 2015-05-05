package com.cjcornell.cyrano.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.util.Log;

import com.cjcornell.cyrano.BluetoothFriend;
import com.cjcornell.cyrano.data.DataStore;

public class ResetTimestampSnooze {

	
	public static void timeStamp_clearatcomplete_time() {
		Log.i("CLEAN PROCESS", "SNOOZE RESET PROCESS");
		SimpleDateFormat BTdate = new SimpleDateFormat("dd");
		SimpleDateFormat BThour = new SimpleDateFormat("hh");
		Calendar c = Calendar.getInstance();
		List<String> BTids = new ArrayList<String>();
		for (int i = 0; i < DataStore.getInstance().getbluletoothmac().size(); i++) {
			String BTwithTime = "";
			Log.i("", "0");
			String BTaddress = DataStore.getInstance().getbluletoothmac()
					.get(i);

			if (DataStore.getInstance().getTimestamps().get(BTaddress) != null) {

				BTwithTime = DataStore.getInstance().getTimestamps()
						.get(BTaddress);
				String BTdateStamp = BTwithTime.substring(
						BTwithTime.indexOf("/") + 1,
						BTwithTime.lastIndexOf("/"));
				String BThourStamp = BTwithTime.substring(
						BTwithTime.indexOf("-") + 1, BTwithTime.indexOf(":"));
				Log.i("", BTdate.format(c.getTime()) + " " + BTdateStamp + " "
						+ BThour.format(c.getTime()) + " " + BThourStamp);
				if (!BTdateStamp.equalsIgnoreCase(BTdate.format(c.getTime()))
						&& BThourStamp.equalsIgnoreCase(BThour.format(c
								.getTime()))) {
					Log.i("", "3");
					BTids.add(BTaddress);
				}

			}

		}
		// clear

		for (int i = 0; i < BTids.size(); i++) {

			Log.i("", "5");
			String pure="";
			if (DataStore.getInstance().getTimestamps().get(BTids.get(i)) != null) {

				DataStore.getInstance().getTimestamps().remove(BTids.get(i));

			}
			if (BTids.get(i).contains("GLOBAL")) {
				int value1 = BTids.get(i).toString().indexOf("GLOBAL");
				 pure = BTids.get(i).substring(0, value1).trim();
			}
			else
				pure=BTids.get(i);
			if (DataStore.getInstance().getIDSofBTIDS().get(pure) != null) {
				DataStore.getInstance().getIDSofBTIDS().remove(pure);
			}
			Log.i("", "6");

		}
	}
	
}
