package com.psktechnology.webservice;

import java.io.File;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.psktechnology.asyncTask.AsyncGetService;
import com.psktechnology.asyncTask.AsyncPostService;
import com.psktechnology.constant.WSConstant;

public class WebServices {
	
	public void registerMember(Activity activity, String fname, String lname, String email, String password) {

        new AsyncGetService(activity, WSConstant.RT_REGISTER, true)
        					.execute(WSConstant.WS_REGISTER
        							+ "fname=" + fname + "&"
        							+ "lname=" + lname + "&"
        							+ "email=" + email + "&"
        							+ "password=" + password);
    }
	
    public void loginMember(Activity activity, String email, String password) {
        
        new AsyncGetService(activity, WSConstant.RT_LOGIN, true)
        					.execute(WSConstant.WS_LOGIN
        							+ "email=" + email + "&"
        							+ "password=" + password);
    }

    public void getSecurityQuestion(Activity activity, String responseType, String url) {
        new AsyncGetService(activity, responseType, false).execute(url);
    }
    
    public void getCourseDetail(Activity activity, String responseType, String url) {
        new AsyncGetService(activity, responseType, false).execute(url);
    }

    public void registerStep2(Activity activity, String userId, String name, String phone, String gender, String DOB,
                              String question1key, String answer1, String question2key, String answer2) {

        ArrayList<NameValuePair> namevalue = new ArrayList<NameValuePair>();

        namevalue.add(new BasicNameValuePair("txtUserId", userId));
        namevalue.add(new BasicNameValuePair("txtFullname", name));
        namevalue.add(new BasicNameValuePair("txtPhone", phone));
        namevalue.add(new BasicNameValuePair("txtSex", gender));
        namevalue.add(new BasicNameValuePair("txtDob", DOB));
        namevalue.add(new BasicNameValuePair("txtQuestionid1", question1key));
        namevalue.add(new BasicNameValuePair("txtAnswer1", answer1));
        namevalue.add(new BasicNameValuePair("txtQuestionid2", question2key));
        namevalue.add(new BasicNameValuePair("txtAnswer2", answer2));

        new AsyncPostService(activity, WSConstant.RT_REGISTER_STEP2, namevalue, true).execute(WSConstant.WS_REGISTER_STEP2);
    }
    
    public void getInbox(Activity activity, Fragment fragment, String userid) {

        ArrayList<NameValuePair> namevalue = new ArrayList<NameValuePair>();
        namevalue.add(new BasicNameValuePair("userid", userid));

        new AsyncPostService(activity, fragment, WSConstant.RT_INBOX, namevalue, true).execute(WSConstant.WS_INBOX);
    }
    
    public void getSentbox(Activity activity, Fragment fragment, String userid) {

        ArrayList<NameValuePair> namevalue = new ArrayList<NameValuePair>();
        namevalue.add(new BasicNameValuePair("userid", userid));

        new AsyncPostService(activity, fragment, WSConstant.RT_SENTBOX, namevalue, true).execute(WSConstant.WS_SENTBOX);
    }
    
    public void getInboxDetails(Activity activity, String queryid, String userid) {

        ArrayList<NameValuePair> namevalue = new ArrayList<NameValuePair>();
        namevalue.add(new BasicNameValuePair("queryid", queryid));
        namevalue.add(new BasicNameValuePair("userid", userid));

        new AsyncPostService(activity, WSConstant.RT_INBOX_DETAILS, namevalue, true).execute(WSConstant.WS_INBOX_DETAILS);
    }
    
    public void getSentboxDetails(Activity activity, String queryid, String userid) {

        ArrayList<NameValuePair> namevalue = new ArrayList<NameValuePair>();
        namevalue.add(new BasicNameValuePair("queryid", queryid));
        namevalue.add(new BasicNameValuePair("userid", userid));

        new AsyncPostService(activity, WSConstant.RT_SENTBOX_DETAILS, namevalue, true).execute(WSConstant.WS_SENTBOX_DETAILS);
    }
    
    public void SetCreateNewTask(Activity activity, String txtTitle, String userid, String courseid, String categoryid,
    		String txtusernotes, String txtestimatewords,
    		String txtchat, String txtreqdate, String txtreqtime, String txtsummary) {

        ArrayList<NameValuePair> namevalue = new ArrayList<NameValuePair>();
        namevalue.add(new BasicNameValuePair("txtTitle", txtTitle));
        namevalue.add(new BasicNameValuePair("userid", userid));
        namevalue.add(new BasicNameValuePair("courseid", courseid));
        namevalue.add(new BasicNameValuePair("categoryid", categoryid));
        namevalue.add(new BasicNameValuePair("txtusernotes", txtusernotes));
        namevalue.add(new BasicNameValuePair("txtestimatewords", txtestimatewords));
        namevalue.add(new BasicNameValuePair("txtchat", txtchat));
        namevalue.add(new BasicNameValuePair("txtreqdate", txtreqdate));
        namevalue.add(new BasicNameValuePair("txtreqtime", txtreqtime));
        namevalue.add(new BasicNameValuePair("txtsummary", txtsummary));

        new AsyncPostService(activity, WSConstant.RT_ADDITEM, namevalue, false).execute(WSConstant.WS_ADDITEM);
    }
    
    public void forgotPassword(Activity activity, String username) {

        ArrayList<NameValuePair> namevalue = new ArrayList<NameValuePair>();
        namevalue.add(new BasicNameValuePair("username", username));

        new AsyncPostService(activity, WSConstant.RT_FORGOT_PASSWORD, namevalue, true).execute(WSConstant.WS_FORGOT_PASSWORD);
    }
    
    public void uploadFile(Activity activity, String queryId, File file, String fileType) {

        ArrayList<NameValuePair> namevalue = new ArrayList<NameValuePair>();
        namevalue.add(new BasicNameValuePair("queryid", queryId));
        namevalue.add(new BasicNameValuePair("txtFilesQuery[]", String.valueOf(file)));
        namevalue.add(new BasicNameValuePair("selFileType[]", fileType));

        new AsyncPostService(activity, WSConstant.RT_ADDFILES, namevalue, false).execute(WSConstant.WS_ADDFILES);
    }
    
    public void getFileList(Activity activity, String queryid) {

        ArrayList<NameValuePair> namevalue = new ArrayList<NameValuePair>();
        namevalue.add(new BasicNameValuePair("queryid", queryid));

        new AsyncPostService(activity, WSConstant.RT_SENTFILES, namevalue, false).execute(WSConstant.WS_SENTFILES);
    }
    
    public void deleteFile(Activity activity, String fileId) {

        ArrayList<NameValuePair> namevalue = new ArrayList<NameValuePair>();
        namevalue.add(new BasicNameValuePair("fileids[]", fileId));

        new AsyncPostService(activity, WSConstant.RT_DELETEFILES, namevalue, false).execute(WSConstant.WS_DELETEFILES);
    }

}