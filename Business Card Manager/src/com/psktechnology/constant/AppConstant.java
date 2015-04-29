package com.psktechnology.constant;

public class AppConstant {
	
    // regular expressions

	public static final String emailExpress = "^[A-Za-z0-9]+([\\.\\-_]{1}[A-Za-z0-9]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+){0,1}(\\.[A-Za-z]{2,4})$";
    public static final String mobileExpress = "^[7-9][0-9]{9}$";
    
    
    // bundle constants
	
 	public static final String isFromViewCard = "isFromViewCard";


    // constants

    public static final String success = "success";
    public static final String fail = "fail";
    
    public static final String servicePending = "Service pending...";
	public static final String pleaseWait = "Please wait";
    public static final String underDevelopment = "This functinaloity is under development";
    
	public static final String noInternetConnection = "No Internet Connection";
	public static final String somethingWrong = "Something went wrong, Please try again.";
	public static final String enterValidInput = "Please enter valid input";

    public static final String enterEmail = "Please enter email id";
    public static final String enterValidEmail = "Please enter valid email id";
    public static final String enterPassword = "Please enter password";
    
    public static final String enterFName = "Please enter first name";
    public static final String enterLName = "Please enter last name";
    public static final String enterConfirmPassword = "Please enter confirm password";
    public static final String passwordDoNotMatch = "Password and Confirm Password do not match";
    

    // shared preferences key

    public static final String pref_UserId = "prefUserId";
    public static final String pref_RememberMe = "prefRememberMe";
    public static final String pref_InboxCounter = "prefInboxCounter";
	
	public static final String pref_CategoryId = "prefCategoryId";
	public static final String pref_CardId = "prefCardId";
	
	public static final String pref_CId = "prefCId";
	public static final String pref_Name = "prefName";
	public static final String pref_Title = "prefTitle";
	public static final String pref_Company = "prefCompany";
	public static final String pref_Phone = "prefPhone";
	public static final String pref_Email = "prefEmail";
	public static final String pref_Web = "prefWeb";
	public static final String pref_FbID = "prefFbID";
	public static final String pref_InID = "prefInID";
	public static final String pref_UserImage = "prefUserImage";
	public static final String pref_FontStyle = "prefFontStyle";
	public static final String pref_FontColor = "prefFontColor";
	public static final String pref_IsCardEdit = "prefIsCardEdit";
	public static final String pref_IsTemplate = "prefIsTemplate";

}