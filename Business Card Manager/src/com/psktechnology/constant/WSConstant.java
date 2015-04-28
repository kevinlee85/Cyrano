package com.psktechnology.constant;

public class WSConstant {
	
	/********** WS URL ************/
	
	public static final String WS_ROOT = "http://phptest.byethost9.com/BCard/services/";
	
	public static final String WS_REGISTER = WS_ROOT + "userInsert.php";
	public static final String WS_LOGIN = WS_ROOT + "userLogin.php";
	public static final String WS_FORGOT_PASSWORD = WS_ROOT + "forgotPassword.php";
	public static final String WS_SAVE_CARD = WS_ROOT + "cardInsert.php";
	public static final String WS_SAVE_MESSAGE = WS_ROOT + "messageInsert.php";

	
	/********** WS Parameters Request Types ************/	

	public static final String RT_REGISTER = "userInsert";
	public static final String RT_LOGIN = "userLogin";
	public static final String RT_FORGOT_PASSWORD = "forgotPassword";
	public static final String RT_SAVE_CARD = "cardInsert";
	public static final String RT_SAVE_MESSAGE = "messageInsert";

	
	/********** WS Parameters ************/
    
	public static final String PARAM_EMAIL = "&Email=";
	public static final String PARAM_PASSWORD = "&Password=";

}