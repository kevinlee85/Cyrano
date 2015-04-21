package com.psktechnology.asyncTask;

import java.util.List;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import com.psktechnology.constant.AppConstant;
import com.psktechnology.constant.AppGlobal;
import com.psktechnology.fragmentactivity.DrawerActivity;
import com.psktechnology.interfaces.WSResponseListener;
import com.psktechnology.model.ResponseObject;
import com.psktechnology.webservice.WSRequest;

public class AsyncPostService extends AsyncTask<String, Void, Object> {

	Activity activity;
	String serviceType, url;

	// private final String LOG_TAG = "AsyncReg";
	Exception error = null;
	WSResponseListener wsResponseListener;
	List<NameValuePair> nameValuePair;

	boolean isLoaderEnable;
	Fragment fragment;

	public AsyncPostService(Activity activity, String serviceType, List<NameValuePair> nameValuePair, boolean isLoaderEnable) {
		this.activity = activity;
		this.serviceType = serviceType;
		this.nameValuePair = nameValuePair;
		this.isLoaderEnable = isLoaderEnable;
		wsResponseListener = (WSResponseListener) activity;
	}

	public AsyncPostService(Activity activity, Fragment Fragment, String serviceType, List<NameValuePair> nameValuePair, boolean isLoaderEnable) {
		this.activity = activity;
		this.serviceType = serviceType;
		this.nameValuePair = nameValuePair;
		this.isLoaderEnable = isLoaderEnable;
		this.fragment = Fragment;
		wsResponseListener = (WSResponseListener) Fragment;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		if (isLoaderEnable)
			AppGlobal.showProgressDialog(activity, AppConstant.pleaseWait);
	}

	@Override
	protected Object doInBackground(String... params) {

		try {
//			return new WSRequest().getPostRequest(params[0], MainResponseObject.class, null, nameValuePair);
			return new WSRequest().getPostRequest(params[0], ResponseObject.class, null, nameValuePair);
			// return null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);

        if (isLoaderEnable) {
            AppGlobal.dismissProgressDialog(activity);
        }
		
		if (activity instanceof DrawerActivity) {
			wsResponseListener.onDelieverResponse_Fragment(serviceType, result, error, activity);
		} else {
			wsResponseListener.onDelieverResponse(serviceType, result, error);
		}

	}

}