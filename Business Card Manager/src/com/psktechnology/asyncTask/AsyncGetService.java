package com.psktechnology.asyncTask;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.psktechnology.constant.AppConstant;
import com.psktechnology.constant.AppGlobal;
import com.psktechnology.interfaces.WSResponseListener;
import com.psktechnology.model.ResponseObject;
import com.psktechnology.webservice.WSRequest;

public class AsyncGetService extends AsyncTask<String, Void, Object> {

    Activity activity;
    String serviceType;
    boolean isLoaderEnable;

    WSResponseListener wsResponseListener;

    Exception error = null;
    private final String LOG_TAG = "AsyncGetService";

    public AsyncGetService(Activity activity, String serviceType, boolean isLoaderEnable) {
        this.activity = activity;
        this.serviceType = serviceType;
        this.isLoaderEnable = isLoaderEnable;
        wsResponseListener = (WSResponseListener) activity;
    }

    public AsyncGetService(Activity activity, String serviceType, boolean isLoaderEnable, Fragment fragment) {
        this.activity = activity;
        this.serviceType = serviceType;
        this.isLoaderEnable = isLoaderEnable;
        wsResponseListener = (WSResponseListener) fragment;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (isLoaderEnable)
            AppGlobal.showProgressDialog(activity, AppConstant.pleaseWait);
    }

    @Override
    protected Object doInBackground(String... params) {

        try {
//			return new WSRequest().getGetRequest(params[0], MainResponseObject.class);
            return new WSRequest().getGetRequest(params[0], ResponseObject.class);

        } catch (Exception e) {
            this.error = e;
            Log.e(LOG_TAG, e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);

        if (isLoaderEnable) {
            AppGlobal.dismissProgressDialog(activity);
        }
        wsResponseListener.onDelieverResponse(serviceType, result, error);
    }

}