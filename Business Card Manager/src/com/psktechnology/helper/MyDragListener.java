package com.psktechnology.helper;

import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.RelativeLayout;

public class MyDragListener implements OnDragListener {

	int x;
	int y;
	RelativeLayout layout;

	public MyDragListener(RelativeLayout l) {
		layout = l;
	}

	@Override
	public boolean onDrag(View v, DragEvent event) {
		int action = event.getAction();
		View view = (View) event.getLocalState();
		RelativeLayout l = (RelativeLayout) view.getParent();
		if (!l.equals(layout))
			return false;
		if (action == DragEvent.ACTION_DRAG_LOCATION) {
			x = (int) event.getX();
			y = (int) event.getY();
			Log.d("DRAG", x + "," + y);
		}
		if (action == DragEvent.ACTION_DRAG_ENDED) {
			Log.d("DRAG", "Dropped at " + x + "," + y);
			view.setX(x - (view.getWidth() / 2));
			view.setY(y - (view.getHeight() / 2));
			view.setVisibility(View.VISIBLE);
		}
		return true;
	}

}