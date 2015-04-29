package com.psktechnology.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.psktechnology.businesscardmanager.R;
import com.psktechnology.constant.AppConstant;
import com.psktechnology.constant.AppGlobal;
import com.psktechnology.constant.WSConstant;
import com.psktechnology.dialog.ForgotPassword_Dialog;
import com.psktechnology.fragmentactivity.DrawerActivity;
import com.psktechnology.interfaces.WSResponseListener;
import com.psktechnology.model.ResponseObject;
import com.psktechnology.webservice.WebServices;

public class Login extends Activity implements OnClickListener, WSResponseListener {
	
	Activity activity;
	
	TextView tvsignup, tvforgotpass;
	EditText etemail, etpassword;
	Button btnlogin;
	
	String email, password;
	WebServices ws;
	
	ForgotPassword_Dialog fp_dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_login);
		
		init();
		checkForSession();
	}
	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.tvsignup:
			signup();
			break;
			
		case R.id.tvforgotpass:
			forgotPassword();
			break;
			
		case R.id.btnlogin:
			login();
			break;

		default:
			break;
		}
	}
	
	// TODO check if user already saved his/her password
	private void checkForSession() {
		Boolean isLoggedIn = AppGlobal.getBooleanPreference(activity, AppConstant.pref_RememberMe);
		if(isLoggedIn) {
			gotoHomeScreen();
		}
	}

	private void forgotPassword() {
		fp_dialog = new ForgotPassword_Dialog(activity);
		fp_dialog.show();
	}

	private void signup() {
		startActivity(new Intent(activity, Signup.class));
		finish();
	}

	private void login() {
		if (isValidate()) {
			if (AppGlobal.isNetworkConnected(activity)) {
				ws.loginMember(activity, email, password);
			} else
				AppGlobal.showToast(activity, AppConstant.noInternetConnection);
		}
	}

	private boolean isValidate() {
		boolean result = true;
		
        email = etemail.getText().toString().trim();
        password = etpassword.getText().toString().trim();

        if(null != email && email.length() == 0) {
        	AppGlobal.showToast(activity, AppConstant.enterEmail);
            etemail.requestFocus();
            result = false;
        } else if(!email.matches(AppConstant.emailExpress)) {
			AppGlobal.showToast(activity, AppConstant.enterValidEmail);
			etemail.requestFocus();
			etemail.selectAll();
			result = false;
        } else if(null != password && password.length() == 0) {
            AppGlobal.showToast(activity, AppConstant.enterPassword);
            etpassword.requestFocus();
            result = false;
        }

        return result;
	}

	private void init() {
		activity = (Activity) Login.this;
		ws = new WebServices();
		
		tvsignup = (TextView)findViewById(R.id.tvsignup);
		tvforgotpass = (TextView)findViewById(R.id.tvforgotpass);
		etemail = (EditText)findViewById(R.id.etemail);
		etpassword = (EditText)findViewById(R.id.etpassword);
		btnlogin = (Button)findViewById(R.id.btnlogin);
		
		tvsignup.setOnClickListener(this);
		tvforgotpass.setOnClickListener(this);
		btnlogin.setOnClickListener(this);
	}
	
	// TODO response from web service
	@Override
	public void onDelieverResponse(String serviceType, Object data, Exception error) {

		ResponseObject responseObj = (ResponseObject) data;

		if (error == null) {
			if (responseObj != null) {
				
				if (serviceType.equalsIgnoreCase(WSConstant.RT_LOGIN)) {

					if (responseObj.getStatus().equalsIgnoreCase(AppConstant.success)) {
						AppGlobal.showToast(activity, responseObj.getMsg());

						saveData(responseObj);
						gotoHomeScreen();
						
					} else if (responseObj.getStatus().equalsIgnoreCase(AppConstant.fail)) {
						AppGlobal.showToast(activity, responseObj.getMsg());
					}
				} else if (serviceType.equalsIgnoreCase(WSConstant.RT_FORGOT_PASSWORD)) {
					
					if (responseObj.getStatus().equalsIgnoreCase(AppConstant.success)) {
						AppGlobal.showToast(activity, responseObj.getMsg());
						fp_dialog.dismiss();
					} else if (responseObj.getStatus().equalsIgnoreCase(AppConstant.fail)) {
						AppGlobal.showToast(activity, responseObj.getMsg());
					}

				}

			}
		} else {
			AppGlobal.showToast(activity, error.getLocalizedMessage());
		}

	}

	@Override
	public void onDelieverResponse_Fragment(String serviceType, Object data, Exception error, Activity activity) {	}
	
	// TODO save response data into prefrences
    private void saveData(ResponseObject responseObj) {
    	AppGlobal.setStringPreference(activity, AppConstant.pref_UserId, responseObj.getId());
    	AppGlobal.setBooleanPreference(activity, AppConstant.pref_RememberMe, true);
    }
	
	// TODO go to second step of registration
    private void gotoHomeScreen() {
		startActivity(new Intent(activity, DrawerActivity.class));
		finish();
	}

}