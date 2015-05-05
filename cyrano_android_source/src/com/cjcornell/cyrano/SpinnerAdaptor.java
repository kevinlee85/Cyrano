package com.cjcornell.cyrano;

import java.util.zip.Inflater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SpinnerAdaptor extends  BaseAdapter{
	
	String SNoozevalues[];
	Context context;
	public SpinnerAdaptor(ActivityCyrano activityCyrano, String[] snoozevalues) {
		// TODO Auto-generated constructor stub
		 SNoozevalues=snoozevalues;
		 context=activityCyrano;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return SNoozevalues.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		 LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         convertView = inflater.inflate(R.layout.snooze_adaptor, parent, false);
         TextView tv=(TextView)convertView.findViewById(R.id.snoozevaluesadaptor);
         tv.setText(SNoozevalues[position]+"");
		return convertView;
	}

}
