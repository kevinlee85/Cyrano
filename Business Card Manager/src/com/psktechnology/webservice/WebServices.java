package com.psktechnology.webservice;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;

import com.psktechnology.asyncTask.AsyncPostService;
import com.psktechnology.constant.WSConstant;

public class WebServices {
	
	public void registerMember(Activity activity, String fname, String lname, String email, String password) {
		
		ArrayList<NameValuePair> namevalue = new ArrayList<NameValuePair>();

        namevalue.add(new BasicNameValuePair("fname", fname));
        namevalue.add(new BasicNameValuePair("lname", lname));
        namevalue.add(new BasicNameValuePair("email", email));
        namevalue.add(new BasicNameValuePair("password", password));

        new AsyncPostService(activity, WSConstant.RT_REGISTER, namevalue, true).execute(WSConstant.WS_REGISTER);
    }
	
    public void loginMember(Activity activity, String email, String password) {
    	
		ArrayList<NameValuePair> namevalue = new ArrayList<NameValuePair>();

        namevalue.add(new BasicNameValuePair("email", email));
        namevalue.add(new BasicNameValuePair("password", password));

        new AsyncPostService(activity, WSConstant.RT_LOGIN, namevalue, true).execute(WSConstant.WS_LOGIN);
    }
    
    public void forgotPassword(Activity activity, String email) {
    	
		ArrayList<NameValuePair> namevalue = new ArrayList<NameValuePair>();

        namevalue.add(new BasicNameValuePair("email", email));

        new AsyncPostService(activity, WSConstant.RT_FORGOT_PASSWORD, namevalue, true).execute(WSConstant.WS_FORGOT_PASSWORD);
    }

	public void saveCard(Activity activity, int categoryId, int cardId,
							String name, String title, String company, String phone,
							String email, String web, String fb, String in, String imagePath,
							int font, int color, int isTemplate) {
		
        ArrayList<NameValuePair> namevalue = new ArrayList<NameValuePair>();
        namevalue.add(new BasicNameValuePair("categoryId", String.valueOf(categoryId)));
        namevalue.add(new BasicNameValuePair("cardId", String.valueOf(cardId)));
        namevalue.add(new BasicNameValuePair("name", name));
        namevalue.add(new BasicNameValuePair("title", title));
        namevalue.add(new BasicNameValuePair("company", company));
        namevalue.add(new BasicNameValuePair("phone", phone));
        namevalue.add(new BasicNameValuePair("email", email));
        namevalue.add(new BasicNameValuePair("web", web));
        namevalue.add(new BasicNameValuePair("fb", fb));
        namevalue.add(new BasicNameValuePair("in", in));
        namevalue.add(new BasicNameValuePair("imagePath", imagePath));
        namevalue.add(new BasicNameValuePair("font", String.valueOf(font)));
        namevalue.add(new BasicNameValuePair("color", String.valueOf(color)));
        namevalue.add(new BasicNameValuePair("isTemplate", String.valueOf(isTemplate)));

        new AsyncPostService(activity, WSConstant.RT_SAVE_CARD, namevalue, true).execute(WSConstant.WS_SAVE_CARD);
		
	}

	public void saveMessage(Activity activity, String userId, int to, String mto, int type, int isTrash, String cardId) {
		
        ArrayList<NameValuePair> namevalue = new ArrayList<NameValuePair>();
        namevalue.add(new BasicNameValuePair("from", userId));
        namevalue.add(new BasicNameValuePair("to", String.valueOf(to)));
        namevalue.add(new BasicNameValuePair("mto", mto));
        namevalue.add(new BasicNameValuePair("type", String.valueOf(type)));
        namevalue.add(new BasicNameValuePair("isTrash", String.valueOf(isTrash)));
        namevalue.add(new BasicNameValuePair("cid", cardId));

        new AsyncPostService(activity, WSConstant.RT_SAVE_MESSAGE, namevalue, true).execute(WSConstant.WS_SAVE_MESSAGE);
		
	}

}