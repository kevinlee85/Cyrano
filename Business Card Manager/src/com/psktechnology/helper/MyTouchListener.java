package com.psktechnology.helper;

import android.content.ClipData;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnTouchListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MyTouchListener implements OnTouchListener {
	
	SeekBar sbtextize;

	public MyTouchListener(SeekBar sbtextize) {
		this.sbtextize = sbtextize;
	}

	@Override
	public boolean onTouch(final View v, MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			ClipData data = ClipData.newPlainText("", "");
			DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
			v.startDrag(data, shadowBuilder, v, 0);
			v.setVisibility(View.INVISIBLE);
			
			if(v instanceof TextView) {
				
				sbtextize.setVisibility(View.VISIBLE);
				sbtextize.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					
					int p = 0;
					
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						if(p < 18) {
							p = 18;
							sbtextize.setProgress(p);
						}
					}
					
					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {	}
					
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
						p = progress;
						((TextView) v).setTextSize(p);
							
					}
				});
				
			} else
				sbtextize.setVisibility(View.GONE);
			
			return true;
		} else {
			return false;
		}
	}

}