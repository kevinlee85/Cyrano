package com.psktechnology.interfaces;

import android.app.Activity;

public interface WSResponseListener {
	
	abstract void onDelieverResponse(String serviceType, Object data, Exception error);

	abstract void onDelieverResponse_Fragment(String serviceType, Object data, Exception error, Activity activity);

}