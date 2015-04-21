package com.psktechnology.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.psktechnology.businesscardmanager.R;
import com.psktechnology.constant.AppConstant;
import com.psktechnology.constant.AppGlobal;
import com.psktechnology.webservice.WebServices;

public class ForgotPassword_Dialog extends Dialog implements OnClickListener {
	
	Activity activity;
	EditText etemail;
	Button btnsend;
	
	TextView tvtitle;
	Button btndrawer, btnclose;
	
	String email;
	WebServices ws;

	public ForgotPassword_Dialog(Activity activity) {
		super(activity);
		this.activity = activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.dialog_forgot_password);
		
		init();
	}

	private void init() {
		ws = new WebServices();
		
		btndrawer = (Button) findViewById(R.id.btndrawer);
		btndrawer.setVisibility(View.INVISIBLE);
		
		btnclose = (Button) findViewById(R.id.btnclose);
		btnclose.setTypeface(AppGlobal.setFontAwesomeFonts(activity));
		btnclose.setText(activity.getResources().getString(R.string.icon_close));
		btnclose.setOnClickListener(this);
		
		tvtitle = (TextView) findViewById(R.id.tvtitle);
		tvtitle.setSelected(true);
		tvtitle.setText("Forgot Password");
		
		etemail = (EditText) findViewById(R.id.etemail);
		btnsend = (Button) findViewById(R.id.btnsend);
		
		btnsend.setOnClickListener(this);
		btnclose.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.btnclose:
			dismiss();
			break;
			
		case R.id.btnsend:
			send();
			break;

		default:
			break;
		}
	}

	private void send() {
		if (isValidate()) {
			if (AppGlobal.isNetworkConnected(activity)) {
				ws.forgotPassword(activity, email);
			} else {
				AppGlobal.showToast(activity, AppConstant.noInternetConnection);
			}
		}
	}

	private boolean isValidate() {
		boolean result = true;
		
		email = etemail.getText().toString().trim();
		
		if(null != email && email.length() == 0) {
			AppGlobal.showToast(activity, AppConstant.enterEmail);
			etemail.requestFocus();
			result = false;
		} else if(!email.matches(AppConstant.emailExpress)) {
			AppGlobal.showToast(activity, AppConstant.enterValidEmail);
			etemail.requestFocus();
			etemail.selectAll();
			result = false;
		}
		
		return result;
	}

}