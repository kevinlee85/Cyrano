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
import com.psktechnology.fragmentactivity.DrawerActivity;
import com.psktechnology.interfaces.WSResponseListener;
import com.psktechnology.model.ResponseObject;
import com.psktechnology.webservice.WebServices;

public class Signup extends Activity implements OnClickListener, WSResponseListener {
	
	Activity activity;
	
	TextView tvtitle;
	Button btnback, btnclose;
	
	EditText etfname, etlname, etemail, etpassword, etconfirmpassword;
	Button btnregister;
	
	String fname, lname, email, password, confirmpassword;
	
	WebServices ws;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_signup);
		
		init();
	}
	
	private void init() {
		activity = (Activity) Signup.this;
		ws = new WebServices();
		
		btnback = (Button) findViewById(R.id.btndrawer);
		btnback.setTypeface(AppGlobal.setFontAwesomeFonts(activity));
		btnback.setText(getResources().getString(R.string.icon_back));
		btnback.setOnClickListener(this);
		
		btnclose = (Button) findViewById(R.id.btnclose);
		btnclose.setVisibility(View.INVISIBLE);
		
		tvtitle = (TextView) findViewById(R.id.tvtitle);
		tvtitle.setSelected(true);
		tvtitle.setText("Create New Account");
		
		etfname = (EditText) findViewById(R.id.etfname);
		etlname = (EditText) findViewById(R.id.etlname);
		etemail = (EditText) findViewById(R.id.etemail);
		etpassword = (EditText) findViewById(R.id.etpassword);
		etconfirmpassword = (EditText) findViewById(R.id.etconfirmpassword);
		btnregister = (Button) findViewById(R.id.btnregister);
		
		btnregister.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.btndrawer:
			onBackPressed();
			break;
			
		case R.id.btnregister:
			register();
			break;

		default:
			break;
		}

	}
	
	private void register() {
		if (isValidate()) {
			if (AppGlobal.isNetworkConnected(activity)) {
				ws.registerMember(activity, fname, lname, email, password);
			} else
				AppGlobal.showToast(activity, AppConstant.noInternetConnection);
		}
	}

	private boolean isValidate() {
		
		boolean result = true;
		
		fname = etfname.getText().toString().trim();
		lname = etlname.getText().toString().trim();
		email = etemail.getText().toString().trim();
		password = etpassword.getText().toString().trim();
		confirmpassword = etconfirmpassword.getText().toString().trim();
		
		if(null != fname && fname.length() == 0) {
			AppGlobal.showToast(activity, AppConstant.enterFName);
			etfname.requestFocus();
			result = false;
		} else if(null != lname && lname.length() == 0) {
			AppGlobal.showToast(activity, AppConstant.enterLName);
			etlname.requestFocus();
			result = false;
		} else if(null != email && email.length() == 0) {
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
		} else if(null != confirmpassword && confirmpassword.length() == 0) {
			AppGlobal.showToast(activity, AppConstant.enterConfirmPassword);
			etconfirmpassword.requestFocus();
			result = false;
		} else if(!confirmpassword.equals(password)) {
			AppGlobal.showToast(activity, AppConstant.passwordDoNotMatch);
			result = false;
		}

		return result;
	}
	
	// TODO response from web service
	@Override
	public void onDelieverResponse(String serviceType, Object data, Exception error) {

		// MainResponseObject MainResponseObject = (MainResponseObject) data;
		ResponseObject responseObj = (ResponseObject) data;

		if (error == null) {
			if (responseObj != null) {
				
				if (serviceType.equalsIgnoreCase(WSConstant.RT_REGISTER)) {

					if (responseObj.getStatus().equalsIgnoreCase(AppConstant.success)) {
						AppGlobal.showToast(activity, responseObj.getMsg());

						saveData(responseObj);
						gotoHomeScreen();

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