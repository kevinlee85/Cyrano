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
import com.psktechnology.dialog.ForgotPassword_Dialog;
import com.psktechnology.webservice.WebServices;

public class Login extends Activity implements OnClickListener {
	
	Activity activity;
	
	TextView tvsignup, tvforgotpass;
	EditText etemail, etpassword;
	Button btnlogin;
	
	String email, password;
	WebServices ws;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_login);
		
		init();
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

	private void forgotPassword() {
		new ForgotPassword_Dialog(activity).show();
	}

	private void signup() {
		startActivity(new Intent(activity, Signup.class));
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

}