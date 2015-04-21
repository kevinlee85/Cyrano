package com.psktechnology.constant;

public class WSConstant {
	
	/********** WS URL ************/
	
	public static final String WS_ROOT = "http://programmingtutorial.in/devBusinessCard/services/";
	
	public static final String WS_REGISTER = WS_ROOT + "userInsert.php?";
	public static final String WS_LOGIN = WS_ROOT + "userLogin.php?";;
    public static final String WS_QUESTION1 = WS_ROOT + "securityquestion1";
    public static final String WS_QUESTION2 = WS_ROOT + "securityquestion2";
    public static final String WS_REGISTER_STEP2 = WS_ROOT + "step2";
    public static final String WS_REGISTER_STEP3 = WS_ROOT + "step3";
    public static final String WS_INBOX = WS_ROOT + "inbox";
    public static final String WS_SENTBOX = WS_ROOT + "sentitems";
    public static final String WS_INBOX_DETAILS = WS_ROOT + "inboxdetail";
    public static final String WS_SENTBOX_DETAILS = WS_ROOT + "sentdetails";
    public static final String WS_FORGOT_PASSWORD = WS_ROOT + "forgot";
    public static final String WS_GETCOURSE = WS_ROOT + "courses";
    public static final String WS_GETCATEGORIES = WS_ROOT + "categories";
    public static final String WS_ADDITEM = WS_ROOT + "additem";
    public static final String WS_ADDFILES = WS_ROOT + "addfiles";
    public static final String WS_SENTFILES = WS_ROOT + "sentfiles";
    public static final String WS_DELETEFILES = WS_ROOT + "deletefiles";

	
	/********** WS Parameters Request Types ************/	

	public static final String RT_REGISTER = "userInsert";
	public static final String RT_LOGIN = "login";
    public static final String RT_QUESTION1 = "securityquestion1";
    public static final String RT_QUESTION2 = "securityquestion2";
    public static final String RT_REGISTER_STEP2 = "step2";
    public static final String RT_REGISTER_STEP3 = "step3";
    public static final String RT_INBOX = "inbox";
    public static final String RT_SENTBOX = "sentitems";
    public static final String RT_INBOX_DETAILS = "inboxdetail";
    public static final String RT_SENTBOX_DETAILS = "sentdetails";
    public static final String RT_FORGOT_PASSWORD = "forgot";
    public static final String RT_GETCOURSE = "getcourse";
    public static final String RT_GETCATEGORIES = "getcategories";
    public static final String RT_ADDITEM = "additem";
    public static final String RT_ADDFILES = "addfiles";
    public static final String RT_SENTFILES = "sentfiles";
    public static final String RT_DELETEFILES = "deletefiles";

	
	/********** WS Parameters ************/
    
	public static final String PARAM_EMAIL = "&Email=";
	public static final String PARAM_PASSWORD = "&Password=";

}