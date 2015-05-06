package com.cjcornell.cyrano;

public class Constants 
{
    public static final String FB_USER_ID = "userid";
    public static final String CYRANO_USER_ID = "cyrano_userid";
    public static final String FIRSTNAME = "firstname";
    public static final String LASTNAME = "lastname";
    public static final String EMAIL = "email";
    public static final String MAC_ADDRESS_WI_FI = "macaddress";
    public static final String MAC_ADDRESS_BLUETOOTH = "bluetooth_macaddress";
    public static final String ACCESS_TOKEN = "accesstoken";
    public static final String LOGGEDIN = "loggedin";
    
    public static final String DEBUG_TAG = "cyrano";
    public static final boolean DEBUG = true;
    
    public static final int CONNECTION_TIMEOUT = 60;
    public static final int SOCKET_TIMEOUT = 30;
    
    public static final String SERVER_ROOT = "http://cyrano.cjcornell.com/REST/index.php";
    
    public static final String SERVICE_DEFAULT_SETTINGS = "defaultsettings";
    public static final String SERVICE_USER_SCRIPT = "UserScripts";
    public static final String SERVICE_ALL_SCRIPT = "AllScripts";
    public static final String SERVICE_COMMAND_SCRIPTS = "CommandScripts";
    public static final String SERVICE_UPDATE_BLUETOOTH_EARPIECE = "BtEarpieceUpdate";
    public static final String SERVICE_USER_LIST_MATCHED_BT = "UserListMatchedBT";
    public static final String SERVICE_USER_INFO = "UserInfoBtList";
    public static final String SERVICE_BT_USER = "CompareBtIDList";
    public static final String SERVICE_SEARCH_FOR_FRIENDS = "FriendListMatchedBT";
    public static final String SERVICE_SEARCH_FOR_DEVICES = "DeviceListMatchedBT";
    public static final String SERVICE_SEARCH_FOR_TRIGGERS = "TriggerListMatchedBT";
    public static final String SERVICE_AUTH = "auth";
    public static final String SERVICE_USER_REGISTRATION = "userRegistration";
    public static final String SERVICE_LOGIN = "login";
    
    
    public static final String SEPERATOR = "/";
    
    public final static String DISPLAY_FRIENDS = "com.cjcornell.cyrano.DISPLAY_FRIENDS";
    public final static String DISPLAY_BT_FRIENDS = "com.cjcornell.cyrano.DISPLAY_BT_FRIENDS";
    
    public static final String RESTART_GPS = "com.cjcornell.cyrano.RESTART_GPS";
    public static final String SHUTDOWN_FFS = "com.cjcornell.cyrano.SHUTDOWN_FFS";
    
    // All Script keys
    public static final String KEY_SCRIPT_ID = "script_id";
    public static final String KEY_SCRIPT_TYPE = "script_type";
    public static final String KEY_SCRIPT_NAME = "script_name";
    public static final String KEY_SCRIPT_DESCRIPTION = "script_desc";
    public static final String KEY_SCRIPT_PRIVACY_LEVEL = "privacy_level";
    public static final String KEY_SCRIPT_SHARING_TYPES = "sharing_types";
    public static final String KEY_SCRIPT_IMAGE = "script_image";
    public static final String KEY_SCRIPT_CREATE_DATE = "create_date";
    public static final String KEY_SCRIPT_UPDATE_DATE = "update_date";
    public static final String KEY_SCRIPT_PRELOAD_FLAG = "preload_flag";
    
    // Get command keys
    public static final String KEY_COMMAND_ID = "command_id";
    public static final String KEY_COMMAND_SCRIPT_ID = "script_id";
    public static final String KEY_COMMAND_COMMAND_ID = "command_id";
    public static final String KEY_COMMAND_NUMBER = "command_num";
    public static final String KEY_COMMAND_NAME = "command_name";
    public static final String KEY_COMMAND_DESCRIPTION = "command_desc";
    public static final String KEY_COMMAND_TYPE = "command_type";
    public static final String KEY_COMMAND_IMAGE = "command_image";
    public static final String KEY_COMMAND_FILENAME = "command_filename";
    public static final String KEY_COMMAND_URL = "command_url";
    public static final String KEY_COMMAND_DELAY = "command_delay";
    public static final String KEY_COMMAND_TRIGGER_FLAG = "command_trigger_flag";
    public static final String KEY_COMMAND_STOP_ENABLED = "command_stop_enabled";
    public static final String KEY_COMMAND_PAUSE_ENABLED = "command_pause_enabled";
    public static final String KEY_COMMAND_NEXT = "cfNext";
    public static final String KEY_COMMAND_PREVIOUS = "cfPrevious";
    public static final String KEY_COMMAND_BRANCH_TO_REC_1 = "command_branchto_rec_1"; 
    public static final String KEY_COMMAND_BRANCH_TO_LABEL_1 = "command_branchto_label_1";
    public static final String KEY_COMMAND_BRANCH_TO_REC_2 = "command_branchto_rec_2"; 
    public static final String KEY_COMMAND_BRANCH_TO_LABEL_2 = "command_branchto_label_2";
    public static final String KEY_COMMAND_BRANCH_TO_REC_3 = "command_branchto_rec_3"; 
    public static final String KEY_COMMAND_BRANCH_TO_LABEL_3 = "command_branchto_label_3";
    public static final String KEY_COMMAND_BRANCH_TO_REC_4 = "command_branchto_rec_4"; 
    public static final String KEY_COMMAND_BRANCH_TO_LABEL_4 = "command_branchto_label_4";
    public static final String KEY_COMMAND_LABEL_ORIENTATION = "command_label_orientation";
    public static final String KEY_COMMAND_OTHER = "other";
    public static final String KEY_COMMAND_CANCEL = "cancel_id";
    
    
    public static final String SOUND_SCRIPT = "content://media/internal/audio/media/39";
    public static final String SOUND_FRIEND = "content://media/internal/audio/media/46";
    public static final String SOUND_TRIGGER = "content://media/internal/audio/media/62";
    public static final String SOUND_DEVICE = "content://media/internal/audio/media/87";
    
}
