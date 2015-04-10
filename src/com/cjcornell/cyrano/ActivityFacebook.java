package com.cjcornell.cyrano;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings.Secure;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.cjcornell.cyrano.data.DataStore;
import com.cjcornell.cyrano.task.TaskRegisterUser;
import com.cjcornell.cyrano.task.TaskUserLogin;
import com.cjcornell.cyrano.utils.Utils;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

/**
 * Facebook Login
 */
public class ActivityFacebook extends Activity implements OnClickListener
{
	private UiLifecycleHelper uiHelper;
	private LoginButton loginBtn;
	private String fbId;
	private String userFirstName;
	private String userLastName;
	private String userEmail;
	private String userabout_text;
	private String userMacAddress;
	private String userAccessToken;
	private String userCyranoId;
	private Session fbSession;
		
	private boolean isFbRequestProgress;
	
	private Session.StatusCallback callback = new Session.StatusCallback() { 
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    
		super.onCreate(savedInstanceState);
		
		/*try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.cjcornell.cyrano", 
                    PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                }
        } catch (NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }*/
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weblogin);
		uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
	    loginBtn = ((LoginButton)findViewById(R.id.fbAuthButton));
	    loginBtn.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
	    loginBtn.setReadPermissions(Arrays.asList( "email"));
	    
	    final Button buttonLogin = (Button) findViewById(R.id.button_login);
        final Button buttonRegister = (Button) findViewById(R.id.button_register);
        buttonLogin.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);
	    
	    if(SharedpreferenceUtility.getInstance(ActivityFacebook.this).getBoolean(Constants.LOGGEDIN)) {
	        successfulLogin();
	    }
	}
	@Override
	public void onResume() {
	    
	    super.onResume();
	    uiHelper.onResume();
	    
	    if(BuildConfig.DEBUG)Log.v("Cyrano","FacebookActivity - onResume() ");
	    Session session = Session.getActiveSession();
	    if (session != null && (session.isOpened() || session.isClosed()) ) {
	        
		    if(BuildConfig.DEBUG)Log.v("Cyrano","FacebookActivity - onResume() - session not null");
	    	fbSession = session;
	        onSessionStateChange(session, session.getState(), null);
	    }
	    //loginBtn.setVisibility(View.VISIBLE);
	 
	}
	
//	 private void updateUI() {
//	        Session session = Session.getActiveSession();
//	        boolean enableButtons = (session != null && session.isOpened());
//
//	       
//
//	        if (enableButtons && user != null) {
//	            profilePictureView.setProfileId(user.getId());
//	            greeting.setText(getString(R.string.hello_user, user.getFirstName()));
//	        } else {
//	            profilePictureView.setProfileId(null);
//	            greeting.setText(null);
//	        }
//	    }
	
	@Override
	public void onBackPressed() {
	    
		super.onBackPressed();
		finish();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onPause() {
	    
	    super.onPause();
	    if(BuildConfig.DEBUG)Log.v("Cyrano","FacebookActivity - onPause() ");
	    uiHelper.onPause();
	}
	
	@Override
	public void onDestroy() {
	    
	    super.onDestroy();
	    if(BuildConfig.DEBUG)Log.v("Cyrano","FacebookActivity - onDestroy() ");
	    uiHelper.onDestroy();
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
	    
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}	
	
	private void processFbRequest() {
	    
		isFbRequestProgress = true;
		facebookUserDetails();
	}
		
	private void facebookUserDetails() {
	    
		// Request user data and show the results
        Request request = Request.newMeRequest(fbSession, new Request.GraphUserCallback() {
            // callback after Graph API response with user object
            @Override
            public void onCompleted(GraphUser user,Response response) {
                if (user != null) {
                    try {
                        
                        Utils.dLog("Facebook User: "+user.asMap().toString());
                        user.asMap().toString();
                        fbId = user.getId();
                        userFirstName = user.getFirstName();
                        userLastName = user.getLastName();
                       
                     
                        userEmail = user.asMap().get("email").toString();
                        saveFBUserData();
                        new AsyncHttpFbLogin().execute("");
                        response.getRequest().getSession().closeAndClearTokenInformation();
                     }
                     catch(Exception e) {
                         Utils.dLog("Can not retrieve Email from Facebook account. Please type your email ");
                     }

                     /*etName.setText(user.getName() + ","
                                    + user.getUsername() + ","
                                    + user.getId() + "," + user.getLink()
                                    + "," + user.getFirstName()+ user.asMap().get("email"));*/
                }
            }
        });
        Request.executeBatchAsync(request);		
	}
	
	private void successfulLogin() {
	    
        SharedpreferenceUtility.getInstance(ActivityFacebook.this).putBoolean(Constants.LOGGEDIN, true);
        Intent i = new Intent(this, ActivityCyrano.class);
        startActivity(i);
        finish(); // XXX: Should we finish here? Or not?
    }
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    
		if(BuildConfig.DEBUG)Log.v("Cyrano", "onSessionStateChange");
		
	    if (state.isOpened() && !isFbRequestProgress) {
	        
	    	loginBtn.setVisibility(View.INVISIBLE);
	    	//showProgressDialog("","Please wait ...");
	        if(BuildConfig.DEBUG)Log.v("Cyrano", "Session opened");
	        fbSession = session;
	        processFbRequest();
	    } else if (state.isClosed()) {
	    	if(BuildConfig.DEBUG)Log.v("Cyrano", "Session closed");
	    } else {
	    	if(BuildConfig.DEBUG)Log.v("Cyrano", "onSessionStateChange -- else -- State : "+state.toString());
	    }
	}
	
	private class AsyncHttpFbLogin extends AsyncTask<String, Void, Integer> {
	    
	    private ProgressDialog dialog;
	    
        @Override
        protected void onPreExecute() {
            if (dialog == null) {
                dialog = new ProgressDialog(ActivityFacebook.this);
                dialog.setCancelable(false);
                dialog.setMessage("Logging in...");
            }
            
            dialog.show();
        }

        @Override
        protected Integer doInBackground(String... loginInfo) {
            
            /*String accessToken = loginInfo[0];
            String userId = loginInfo[1];*/
            //String accessToken =  DataStore.getInstance().getBaseParameterString();
            String accessToken =  SharedpreferenceUtility.getInstance(ActivityFacebook.this).getString(Constants.ACCESS_TOKEN);
            String btMac =  SharedpreferenceUtility.getInstance(ActivityFacebook.this).getString(Constants.MAC_ADDRESS_BLUETOOTH);
            String userId = SharedpreferenceUtility.getInstance(ActivityFacebook.this).getString(Constants.FB_USER_ID);
            
            String requestUrl = Constants.SERVER_ROOT 
                    + Constants.SEPERATOR + Constants.SERVICE_AUTH
                    + Constants.SEPERATOR + accessToken 
                    + Constants.SEPERATOR + userId
                    + Constants.SEPERATOR + btMac;
            
            Utils.dLog("Sending request for auth: " + requestUrl);

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet request = new HttpGet(requestUrl);
                String response = httpClient.execute(request, new BasicResponseHandler());
                JSONObject parsedResponse = new JSONObject(response);
                JSONObject success=  parsedResponse.getJSONObject("success");
                JSONObject body=  parsedResponse.getJSONObject("body");
                
                return body.getInt("user_id");
            } catch (HttpResponseException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            if (result == 0) {
                
                Utils.showShortToast(ActivityFacebook.this, "Incorrect login information.");
                Utils.removeUserData(ActivityFacebook.this);
                finish();
                startActivity(getIntent());
            } else {
                //new UpdateBluetoothAddress().execute(BluetoothManager.getBluetoothMacAddress());
                SharedpreferenceUtility.getInstance(ActivityFacebook.this).putString(Constants.CYRANO_USER_ID, result+"");
                userCyranoId = result + "";
                Utils.dLog("User logged in successfully.");
                successfulLogin();
            }
        }
    }

	private void hideKeyboard(EditText editText) {
	    InputMethodManager imm = (InputMethodManager)getSystemService(
	            Context.INPUT_METHOD_SERVICE);
	      imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
	
	private boolean validateEmailAddress(String emailAddress){
	    String  expression="^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";  
	       CharSequence inputStr = emailAddress;  
	       Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);  
	       Matcher matcher = pattern.matcher(inputStr);  
	       return matcher.matches();
	}
	
	private TextWatcher getTextWatcher(final EditText editText) {
	    return new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(editText.getError() != null) {
                    editText.setError(null);
                }
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                
            }
        };
	}
	
    @Override
    public void onClick(View v) {
        
        switch (v.getId()) {
            case R.id.button_login:
                Utils.dLog("Login button clicked");
                final Dialog dialogLogin = new Dialog(ActivityFacebook.this);
                dialogLogin.setTitle("Login");
                dialogLogin.setContentView(R.layout.dialog_login);
                dialogLogin.show();
                
                final EditText editTextEmailLogin = (EditText) dialogLogin.findViewById(R.id.edittext_email);
                editTextEmailLogin.addTextChangedListener(getTextWatcher(editTextEmailLogin));
                final EditText editTextPasswordLogin = (EditText) dialogLogin.findViewById(R.id.edittext_password);
                
                //setDummyLogin(editTextEmailLogin, editTextPasswordLogin);
                editTextPasswordLogin.addTextChangedListener(getTextWatcher(editTextPasswordLogin));
                final Button buttonLogin = (Button) dialogLogin.findViewById(R.id.button_inner_login);
                buttonLogin.setOnClickListener(new OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        
                        String email = editTextEmailLogin.getText().toString().trim();
                        String password = editTextPasswordLogin.getText().toString().toString();
                        boolean isError = false;
                        
                        boolean isErrorLogin = false;
                        if(TextUtils.isEmpty(editTextEmailLogin.getText())) {
                            editTextEmailLogin.setError("Required field");
                            isErrorLogin = true;
                        } else {
                            if(!validateEmailAddress(editTextEmailLogin.getText().toString())) {
                                editTextEmailLogin.setError("Invalid Email");
                                isErrorLogin = true;
                            }
                        }
                        
                        
                        if(TextUtils.isEmpty(editTextPasswordLogin.getText())) {
                            editTextPasswordLogin.setError("Required field");
                            isErrorLogin = true;
                        }
                        
                        if (isErrorLogin) {
                            return;
                        }
                        
                        dialogLogin.setOnDismissListener(new OnDismissListener() {
                            
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                hideKeyboard(editTextEmailLogin);
                                hideKeyboard(editTextPasswordLogin);
                            }
                        });
                        
                        // Saving Android ID
                        String android_id = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
                        String btMac = BluetoothManager.getBluetoothMacAddress();
                        String url = Constants.SERVER_ROOT 
                                    + Constants.SEPERATOR + Constants.SERVICE_LOGIN
                                    + Constants.SEPERATOR + android_id.hashCode() 
                                    + Constants.SEPERATOR + editTextEmailLogin.getText()
                                    + Constants.SEPERATOR + editTextPasswordLogin.getText()
                                    + Constants.SEPERATOR + btMac;
                        
                        userEmail = editTextEmailLogin.getText().toString();
                        new TaskUserLogin(ActivityFacebook.this).execute(url);
                        
                    }
                });
                
                
                break;
            case R.id.button_register:

                Utils.dLog("Register button clicked");
                
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy); 
                
                final Dialog dialogRegister = new Dialog(ActivityFacebook.this);
                dialogRegister.setTitle("Login");
                dialogRegister.setContentView(R.layout.dialog_registration);
                dialogRegister.show();
                
                final EditText editTextFirstName = (EditText) dialogRegister.findViewById(R.id.edittext_firstname);
                editTextFirstName.addTextChangedListener(getTextWatcher(editTextFirstName));
                
                final EditText editTextLastName = (EditText) dialogRegister.findViewById(R.id.edittext_lastname);
                editTextLastName.addTextChangedListener(getTextWatcher(editTextLastName));
                final EditText editTextEmail = (EditText) dialogRegister.findViewById(R.id.edittext_email);
                editTextEmail.addTextChangedListener(getTextWatcher(editTextEmail));
                final EditText editTextPassword = (EditText) dialogRegister.findViewById(R.id.edittext_password);
                editTextPassword.addTextChangedListener(getTextWatcher(editTextPassword));
                final EditText editTextConfirmPassword = (EditText) dialogRegister.findViewById(R.id.edittext_confirmed_password);
                editTextConfirmPassword.addTextChangedListener(getTextWatcher(editTextConfirmPassword));
                final Button buttonRegister = (Button) dialogRegister.findViewById(R.id.button_inner_register);
                
               // setDummyResistration(editTextFirstName, editTextLastName, editTextEmail, editTextPassword, editTextConfirmPassword);
                buttonRegister.setOnClickListener(new OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        String firstName = editTextFirstName.getText().toString().trim();
                        String lastName = editTextLastName.getText().toString().trim();
                        String email = editTextEmail.getText().toString().trim();
                        String password = editTextPassword.getText().toString().toString();
                        String cPassword = editTextConfirmPassword.getText().toString();
                        boolean isError = false;
                        
                        if(TextUtils.isEmpty(editTextFirstName.getText())) {
                            editTextFirstName.setError("Required field");
                            isError = true;
                        }
                        
                        if(TextUtils.isEmpty(editTextLastName.getText())) {
                            editTextLastName.setError("Required field");
                            isError = true;
                        } 
                        
                        if(TextUtils.isEmpty(editTextEmail.getText())) {
                            editTextEmail.setError("Required field");
                            isError = true;
                        } else {
                            if(!validateEmailAddress(editTextEmail.getText().toString())) {
                                editTextEmail.setError("Invalid Email");
                                isError = true;
                            }
                        }
                        
                        if(TextUtils.isEmpty(editTextPassword.getText())) {
                            editTextPassword.setError("Required field");
                            isError = true;
                        }
                        
                        if(TextUtils.isEmpty(editTextConfirmPassword.getText())) {
                            editTextConfirmPassword.setError("Required field");
                            isError = true;
                        }
                        
                        if(!editTextConfirmPassword.getText().toString().equals(editTextPassword.getText().toString())) {
                            editTextConfirmPassword.setError("Not matcching");
                            isError = true;
                        }
                        
                        if(isError) {
                            return;
                        }
                        
                        
                        // Saving Android ID
                        String android_id = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
                        
                        String url = Constants.SERVER_ROOT
                                + Constants.SEPERATOR + Constants.SERVICE_USER_REGISTRATION
                                + Constants.SEPERATOR + android_id.hashCode() 
                                + Constants.SEPERATOR + editTextFirstName.getText().toString().trim()
                                + Constants.SEPERATOR + editTextLastName.getText().toString().trim()
                                + Constants.SEPERATOR + editTextEmail.getText().toString().trim()
                                + Constants.SEPERATOR + editTextPassword.getText().toString()
                                + Constants.SEPERATOR + BluetoothManager.getBluetoothMacAddress();
                        
                        userFirstName = editTextFirstName.getText().toString().trim();
                        userLastName = editTextLastName.getText().toString().trim();
                        userEmail = editTextEmail.getText().toString().trim();
                        
                        dialogRegister.setOnDismissListener(new OnDismissListener() {
                            
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                hideKeyboard(editTextFirstName);
                                hideKeyboard(editTextLastName);
                                hideKeyboard(editTextEmail);
                                hideKeyboard(editTextPassword);
                                hideKeyboard(editTextConfirmPassword);
                            }
                        });
                        new TaskRegisterUser(ActivityFacebook.this).execute(url);
                        //http://cyrano.cjcornell.com/REST/index.php/userRegistration/accessToken/firstname/lastname/email/password/btMacAdd
                    }
                });
            
                break;
            default:
                break;
        }
    }
    
    private void setDummyResistration(EditText editTextFirstName, EditText editTextLastName, EditText editTextEmail,
            EditText editTextPassword, EditText editTextConfirmPassword) {
        
        editTextFirstName.setText("Test");
        editTextLastName.setText("User");
        editTextEmail.setText("testuser@gmail.com");
        editTextPassword.setText("test");
        editTextConfirmPassword.setText("test");
    }
    private void setDummyLogin(EditText editTextEmailLogin, EditText editTextPasswordLogin) {
        
        editTextEmailLogin.setText("testuser@gmail.com");
        editTextPasswordLogin.setText("test");
        
    }
    public void onErrorRegistration() {
        Utils.eLog("onErrorRegistration");
        Utils.showShortToast(ActivityFacebook.this, "Error in registration");
    }
    
    public void onErrorRegistration(String message) {
        Utils.eLog("onErrorRegistration: " + message);
        Utils.showShortToast(ActivityFacebook.this, message);
    }
    
    /**
     * Execute after successful FB login
     */
    private void saveFBUserData() {
        
        Session session = Session.getActiveSession();
        userAccessToken = session.getAccessToken();
        DataStore.getInstance().setAccessToken(userAccessToken);
        
        SharedpreferenceUtility.getInstance(ActivityFacebook.this).putString(Constants.ACCESS_TOKEN, userAccessToken);
        SharedpreferenceUtility.getInstance(ActivityFacebook.this).putString(Constants.FB_USER_ID, fbId);
        SharedpreferenceUtility.getInstance(ActivityFacebook.this).putString(Constants.FIRSTNAME, userFirstName);
        SharedpreferenceUtility.getInstance(ActivityFacebook.this).putString(Constants.LASTNAME, userLastName);
        SharedpreferenceUtility.getInstance(ActivityFacebook.this).putString(Constants.EMAIL, userEmail);
        SharedpreferenceUtility.getInstance(ActivityFacebook.this).putBoolean(Constants.LOGGEDIN, true);
        
        WifiManager manager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String address = info.getMacAddress();

        SharedpreferenceUtility.getInstance(ActivityFacebook.this).putString(Constants.MAC_ADDRESS_WI_FI, address);
        
        BluetoothManager.enableBluetooth(ActivityFacebook.this);
        userMacAddress = BluetoothManager.getBluetoothMacAddress();
        
        
        DataStore.getInstance().setBtMacAddress(userMacAddress);
        SharedpreferenceUtility.getInstance(ActivityFacebook.this).putString(Constants.MAC_ADDRESS_BLUETOOTH, userMacAddress);
        
        Friend me = new Friend(fbId, userFirstName, userLastName, userEmail, userMacAddress,userabout_text);
        DataStore.getInstance().setMe(me);
        Utils.dLog(me.toString());
    }
    
    /**
     * Execute after successful Cyrano login
     */
    private void saveCyranoUserData() {
        
        Friend me = new Friend(userCyranoId, userFirstName, userLastName, userEmail, userMacAddress,userabout_text);
        
        DataStore.getInstance().setMe(me);
        DataStore.getInstance().setAccessToken(userAccessToken);
        
        SharedpreferenceUtility.getInstance(ActivityFacebook.this).putString(Constants.CYRANO_USER_ID, userCyranoId);
        SharedpreferenceUtility.getInstance(ActivityFacebook.this).putString(Constants.ACCESS_TOKEN, userAccessToken);
        SharedpreferenceUtility.getInstance(ActivityFacebook.this).putString(Constants.FIRSTNAME, userFirstName);
        SharedpreferenceUtility.getInstance(ActivityFacebook.this).putString(Constants.LASTNAME, userLastName);
        SharedpreferenceUtility.getInstance(ActivityFacebook.this).putString(Constants.EMAIL, userEmail);
        SharedpreferenceUtility.getInstance(ActivityFacebook.this).putString(Constants.MAC_ADDRESS_BLUETOOTH, userMacAddress);
        SharedpreferenceUtility.getInstance(ActivityFacebook.this).putBoolean(Constants.LOGGEDIN, true);
        
        Utils.dLog(me.toString());
        
        WifiManager manager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String address = info.getMacAddress();
        SharedpreferenceUtility.getInstance(ActivityFacebook.this).putString(Constants.MAC_ADDRESS_WI_FI, address);
        
        BluetoothManager.enableBluetooth(ActivityFacebook.this);
        DataStore.getInstance().setBtMacAddress(userMacAddress);
    }
    
    public void onCompleteRegistration(String userId, String token, String firstName, String lastName, String email, String macAddress) {
        
        userCyranoId = userId; 
        //Saving Android ID
        String android_id = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
        userAccessToken = token.equals("") ? android_id.hashCode()+"" : token;
        userFirstName = firstName;
        userLastName = lastName;
        userEmail = email;
        userMacAddress = macAddress;
        
        saveCyranoUserData();
        successfulLogin();
    }
    public void onErrorLogin() {
        Utils.eLog("onErrorLogin");
        Utils.showShortToast(ActivityFacebook.this, "Error in login");
    }
    
    public void onErrorLogin(String message) {
        Utils.eLog("onErrorLogin: " + message);
        Utils.showShortToast(ActivityFacebook.this, message);
    }
    
    public void onCompleteLogin(String userId, String token, String firstName, String lastName, String email, 
    		String macAddress,String about_text) {
        
        userCyranoId = userId; 
        userAccessToken = token;
        userFirstName = firstName;
        userLastName = lastName;
        userEmail = email;
        userMacAddress = macAddress;
        userabout_text = about_text;
        
        saveCyranoUserData();
        successfulLogin();
    }
}