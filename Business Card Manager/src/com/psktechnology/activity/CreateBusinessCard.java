package com.psktechnology.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.psktechnology.businesscardmanager.R;
import com.psktechnology.constant.AppConstant;
import com.psktechnology.constant.AppGlobal;
import com.psktechnology.constant.WSConstant;
import com.psktechnology.helper.MyTextWatcher;
import com.psktechnology.interfaces.WSResponseListener;
import com.psktechnology.model.ResponseObject;
import com.psktechnology.webservice.WebServices;

public class CreateBusinessCard extends Activity implements OnClickListener, WSResponseListener {
	
	Activity activity;
	
	TextView tvtitle;
	Button btnback, btncontacts;
	
	TextView tvname, tvtitlee, tvcompany, tvphone, tvemail, tvweb, tvfbid, tvinid;
	TextView tviconphone, tviconemail, tviconweb, tviconfb, tviconin;
	EditText etname, ettitle, etcompany, etphone, etemail, etweb, etfb, etin;
	ImageView ivuser;
	
	Button btnstyle, btncolor, btnsave, btncancel;
	int color = -1, font = -1;
	String encodedImage = "";
	
	int categoryId = -1;
	int cardId = -1;
	String cId;
	
	Boolean isCardEdit = false;
	Boolean isTemplate = false;
	
	private Uri uriContact;
    private String contactID;
    
    WebServices ws;
    String userId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_create_businesscard);
		
		init();
	}
	
	private void init() {
		activity = (Activity) CreateBusinessCard.this;
		ws = new WebServices();
		
		userId = AppGlobal.getStringPreference(activity, AppConstant.pref_UserId);
		categoryId = AppGlobal.getIntegerPreference(activity, AppConstant.pref_CategoryId);
		cardId = AppGlobal.getIntegerPreference(activity, AppConstant.pref_CardId);
		
		btnback = (Button) findViewById(R.id.btndrawer);
		btnback.setTypeface(AppGlobal.setFontAwesomeFonts(activity));
		btnback.setText(getResources().getString(R.string.icon_back));
		btnback.setOnClickListener(this);
		
		btncontacts = (Button) findViewById(R.id.btnclose);
		btncontacts.setTypeface(AppGlobal.setFontAwesomeFonts(activity));
		btncontacts.setText(getResources().getString(R.string.icon_users));
		btncontacts.setOnClickListener(this);
		
		tvtitle = (TextView) findViewById(R.id.tvtitle);
		tvtitle.setSelected(true);
		tvtitle.setText("Business Card Organizer");
		
		tvname = (TextView) findViewById(R.id.tvname);
		tvtitlee = (TextView) findViewById(R.id.tvtitlee);
		tvcompany = (TextView) findViewById(R.id.tvcompany);
		tvphone = (TextView) findViewById(R.id.tvphone);
		tvemail = (TextView) findViewById(R.id.tvemail);
		tvweb = (TextView) findViewById(R.id.tvweb);
		tvfbid = (TextView) findViewById(R.id.tvfbid);
		tvinid = (TextView) findViewById(R.id.tvinid);
		
		tviconphone = (TextView) findViewById(R.id.tviconphone);
		tviconemail = (TextView) findViewById(R.id.tviconemail);
		tviconweb = (TextView) findViewById(R.id.tviconweb);
		tviconfb = (TextView) findViewById(R.id.tviconfb);
		tviconin = (TextView) findViewById(R.id.tviconin);
		
		tviconphone.setTypeface(AppGlobal.setFontAwesomeFonts(activity));
		tviconemail.setTypeface(AppGlobal.setFontAwesomeFonts(activity));
		tviconweb.setTypeface(AppGlobal.setFontAwesomeFonts(activity));
		tviconfb.setTypeface(AppGlobal.setFontAwesomeFonts(activity));
		tviconin.setTypeface(AppGlobal.setFontAwesomeFonts(activity));
		
		etname = (EditText) findViewById(R.id.etname);
		ettitle = (EditText) findViewById(R.id.ettitle);
		etcompany = (EditText) findViewById(R.id.etcompany);
		etphone = (EditText) findViewById(R.id.etphone);
		etemail = (EditText) findViewById(R.id.etemail);
		etweb = (EditText) findViewById(R.id.etweb);
		etfb = (EditText) findViewById(R.id.etfb);
		etin = (EditText) findViewById(R.id.etin);
		ivuser = (ImageView) findViewById(R.id.ivuser);
		
		btnstyle = (Button) findViewById(R.id.btnstyle);
		btncolor = (Button) findViewById(R.id.btncolor);
		btnsave = (Button) findViewById(R.id.btnsave);
		btncancel = (Button) findViewById(R.id.btncancel);
		
		btnstyle.setOnClickListener(this);
		btncolor.setOnClickListener(this);
		btnsave.setOnClickListener(this);
		btncancel.setOnClickListener(this);
		ivuser.setOnClickListener(this);
		
		etname.addTextChangedListener(new MyTextWatcher(tvname, "(Your Name)"));
		ettitle.addTextChangedListener(new MyTextWatcher(tvtitlee, "(Your Title)"));
		etcompany.addTextChangedListener(new MyTextWatcher(tvcompany, "(Your Company)"));
		etphone.addTextChangedListener(new MyTextWatcher(tvphone, "(Your Phone Number)"));
		etemail.addTextChangedListener(new MyTextWatcher(tvemail, "(Your Email Address)"));
		etweb.addTextChangedListener(new MyTextWatcher(tvweb, "(Your Website)"));
		etfb.addTextChangedListener(new MyTextWatcher(tvfbid, "(Your Facebook ID)"));
		etin.addTextChangedListener(new MyTextWatcher(tvinid, "(Your LinkedIn ID)"));
		
		checkForEdit();
	}

	private void checkForEdit() {
		
		isCardEdit = AppGlobal.getBooleanPreference(activity, AppConstant.pref_IsCardEdit);
		if(isCardEdit) {
			
			categoryId = AppGlobal.getIntegerPreference(activity, AppConstant.pref_CategoryId);
			cardId = AppGlobal.getIntegerPreference(activity, AppConstant.pref_CardId);
			cId = AppGlobal.getStringPreference(activity, AppConstant.pref_CId);
			
			String name = AppGlobal.getStringPreference(activity, AppConstant.pref_Name);
			String title = AppGlobal.getStringPreference(activity, AppConstant.pref_Title);
			String company = AppGlobal.getStringPreference(activity, AppConstant.pref_Company);
			String phone = AppGlobal.getStringPreference(activity, AppConstant.pref_Phone);
			String email = AppGlobal.getStringPreference(activity, AppConstant.pref_Email);
			String web = AppGlobal.getStringPreference(activity, AppConstant.pref_Web);
			String fbId = AppGlobal.getStringPreference(activity, AppConstant.pref_FbID);
			String inId = AppGlobal.getStringPreference(activity, AppConstant.pref_InID);
			
			font = AppGlobal.getIntegerPreference(activity, AppConstant.pref_FontStyle);
			color = AppGlobal.getIntegerPreference(activity, AppConstant.pref_FontColor);
			
			isTemplate = AppGlobal.getBooleanPreference(activity, AppConstant.pref_IsTemplate);
			
			switch (font) {
			case 0:
				tvname.setTypeface(null, Typeface.NORMAL);
				tvtitlee.setTypeface(null, Typeface.NORMAL);
				tvcompany.setTypeface(null, Typeface.NORMAL);
				tvphone.setTypeface(null, Typeface.NORMAL);
				tvemail.setTypeface(null, Typeface.NORMAL);
				tvweb.setTypeface(null, Typeface.NORMAL);
				tvfbid.setTypeface(null, Typeface.NORMAL);
				tvinid.setTypeface(null, Typeface.NORMAL);
				break;
				
			case 1:
				setFontStyle(AppGlobal.setFontStyle1(activity));
				break;
				
			case 2:
				setFontStyle(AppGlobal.setFontStyle2(activity));
				break;
				
			case 3:
				setFontStyle(AppGlobal.setFontStyle3(activity));
				break;
				
			case 4:
				setFontStyle(AppGlobal.setFontStyle4(activity));
				break;

			default:
				break;
			}
			
			switch (color) {
			case 0:
				setFontColor(getResources().getColor(android.R.color.black));
				break;
				
			case 1:
				setFontColor(getResources().getColor(R.color.red));
				break;
				
			case 2:
				setFontColor(getResources().getColor(R.color.green));
				break;
				
			case 3:
				setFontColor(getResources().getColor(R.color.blue));
				break;
				
			case 4:
				setFontColor(getResources().getColor(android.R.color.white));
				break;

			default:
				break;
			}
			
			etname.setText(name);
			ettitle.setText(title);
			etcompany.setText(company);
			etphone.setText(phone);
			etemail.setText(email);
			etweb.setText(web);
			etfb.setText(fbId);
			etin.setText(inId);
			
			encodedImage = AppGlobal.getStringPreference(activity, AppConstant.pref_UserImage);
			if(!encodedImage.equalsIgnoreCase("")) {
			    byte[] b = Base64.decode(encodedImage, Base64.DEFAULT);
			    Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
			    if(bitmap != null) {
			    	ivuser.setImageBitmap(bitmap);
			    }
			}
		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.btndrawer:
			onBackPressed();
			break;
			
		case R.id.ivuser:
			selectUserPic();
			break;
			
		case R.id.btnstyle:
			PopupMenu popup = new PopupMenu(activity, btnstyle);
		    popup.getMenuInflater().inflate(R.menu.popup_style, popup.getMenu());
		    
		    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {

					switch (item.getItemId()) {
						
					case R.id.styleDefault:
						font = 0;
						
						tvname.setTypeface(null, Typeface.NORMAL);
						tvtitlee.setTypeface(null, Typeface.NORMAL);
						tvcompany.setTypeface(null, Typeface.NORMAL);
						tvphone.setTypeface(null, Typeface.NORMAL);
						tvemail.setTypeface(null, Typeface.NORMAL);
						tvweb.setTypeface(null, Typeface.NORMAL);
						tvfbid.setTypeface(null, Typeface.NORMAL);
						tvinid.setTypeface(null, Typeface.NORMAL);
						return true;
						
					case R.id.style1:
						font = 1;
						setFontStyle(AppGlobal.setFontStyle1(activity));
						return true;
						
					case R.id.style2:
						font = 2;
						setFontStyle(AppGlobal.setFontStyle2(activity));
						return true;
						
					case R.id.style3:
						font = 3;
						setFontStyle(AppGlobal.setFontStyle3(activity));
						return true;
						
					case R.id.style4:
						font = 4;
						setFontStyle(AppGlobal.setFontStyle4(activity));
						return true;

					default:
						return true;
					}
				}
			});
		 
		    popup.show();
			break;
			
		case R.id.btncolor:
			PopupMenu popup_color = new PopupMenu(activity, btncolor);
			popup_color.getMenuInflater().inflate(R.menu.popup_color, popup_color.getMenu());
		    
			popup_color.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {

					switch (item.getItemId()) {
						
					case R.id.colorDefault:
						color = 0;
						setFontColor(getResources().getColor(android.R.color.black));
						return true;
						
					case R.id.colorRed:
						color = 1;
						setFontColor(getResources().getColor(R.color.red));
						return true;
						
					case R.id.colorGreen:
						color = 2;
						setFontColor(getResources().getColor(R.color.green));
						return true;
						
					case R.id.colorBlue:
						color = 3;
						setFontColor(getResources().getColor(R.color.blue));
						return true;
						
					case R.id.colorWhite:
						color = 4;
						setFontColor(getResources().getColor(android.R.color.white));
						return true;

					default:
						return true;
					}
				}

				
			});
		 
			popup_color.show();
			break;
			
		case R.id.btnsave:
			
			if (isValidate()) {
				if (AppGlobal.isNetworkConnected(activity)) {
					
					String name = etname.getText().toString().trim();
					String title = ettitle.getText().toString().trim();
					String company = etcompany.getText().toString().trim();
					String phone = etphone.getText().toString().trim();
					String email = etemail.getText().toString().trim();
					String web = etweb.getText().toString().trim();
					String fb = etfb.getText().toString().trim();
					String in = etin.getText().toString().trim();
					
					if(!isCardEdit) {
						
						ws.saveCard(activity, categoryId, cardId, name, title, company, phone, email, web, fb, in, "", font, color,
								((isTemplate)? 1 : 0));
					}
					else {
						ws.updateCard(activity, categoryId, cardId, name, title, company, phone, email, web, fb, in, "", font, color,
								((isTemplate)? 1 : 0), cId);
						
						isCardEdit = false;
						AppGlobal.setBooleanPreference(activity, AppConstant.pref_IsCardEdit, isCardEdit);
					}
				} else
					AppGlobal.showToast(activity, AppConstant.noInternetConnection);
			}
			
		/*	try {
				mDbHelper = new DBHelper(activity);
				mDb = mDbHelper.getWritableDatabase();
			
				ContentValues values = new ContentValues();
				values.put( DBHelper.CATEGORY_ID, categoryId);
				values.put( DBHelper.CARD_ID, cardId);
				values.put( DBHelper.USER_NAME, etname.getText().toString().trim());
				values.put( DBHelper.USER_TITLE, ettitle.getText().toString().trim());
				values.put( DBHelper.USER_COMPANY, etcompany.getText().toString().trim());
				values.put( DBHelper.USER_PHONE, etphone.getText().toString().trim());
				values.put( DBHelper.USER_EMAIL, etemail.getText().toString().trim());
				values.put( DBHelper.USER_WEB, etweb.getText().toString().trim());
				values.put( DBHelper.USER_FB, etfb.getText().toString().trim());
				values.put( DBHelper.USER_IN, etin.getText().toString().trim());
				values.put( DBHelper.USER_IMAGE, encodedImage);
				values.put( DBHelper.USER_FONT, font);
				values.put( DBHelper.USER_COLOR, color);
				values.put( DBHelper.IS_TEMPLATE, String.valueOf(isTemplate));
				
				if(!isCardEdit) {
					cId = mDb.insert(DBHelper.tbl_Cards, null, values);
					if (cId != -1) {
						AppGlobal.showToast(activity, "Save success");
					} else {
						AppGlobal.showToast(activity, "Error in insertion");
					}
				}
				else {
					long row = mDb.update(DBHelper.tbl_Cards, values, DBHelper.ID +" = "+ cId, null);
					if (row != -1) {
						AppGlobal.showToast(activity, "Update success");
					} else {
						AppGlobal.showToast(activity, "Error in Updation");
					}
					
					isCardEdit = false;
					AppGlobal.setBooleanPreference(activity, AppConstant.pref_IsCardEdit, isCardEdit);
				}
			}
			catch (Exception e) {
				AppGlobal.showToast(activity, e.getMessage());
			}
			finally {
				mDb.close();
				mDbHelper.close();
				
				AppGlobal.setStringPreference(activity, AppConstant.pref_CId, String.valueOf(cId));
				AppGlobal.setStringPreference(activity, AppConstant.pref_Name, etname.getText().toString().trim());
				AppGlobal.setStringPreference(activity, AppConstant.pref_Title, ettitle.getText().toString().trim());
				AppGlobal.setStringPreference(activity, AppConstant.pref_Company, etcompany.getText().toString().trim());
				AppGlobal.setStringPreference(activity, AppConstant.pref_Phone, etphone.getText().toString().trim());
				AppGlobal.setStringPreference(activity, AppConstant.pref_Email, etemail.getText().toString().trim());
				AppGlobal.setStringPreference(activity, AppConstant.pref_Web, etweb.getText().toString().trim());
				AppGlobal.setStringPreference(activity, AppConstant.pref_FbID, etfb.getText().toString().trim());
				AppGlobal.setStringPreference(activity, AppConstant.pref_InID, etin.getText().toString().trim());
				AppGlobal.setStringPreference(activity, AppConstant.pref_UserImage, encodedImage);
				
				AppGlobal.setIntegerPreference(activity, AppConstant.pref_FontStyle, font);
				AppGlobal.setIntegerPreference(activity, AppConstant.pref_FontColor, color);
				
				AppGlobal.setBooleanPreference(activity, AppConstant.pref_IsTemplate, isTemplate);
				
				startActivity(new Intent(activity, FinalizeBusinessCard.class));
				finish();
			} */
			break;
			
		case R.id.btncancel:
			onBackPressed();
			break;
			
		case R.id.btnclose:
			openContactList();
			break;

		default:
			break;
		}
	}
	
	private boolean isValidate() {
		boolean result = true;

		return result;
	}

	// TODO open contact list to pick a contact details
	private void openContactList() {
		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(intent, 300);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		AppGlobal.setIntegerPreference(activity, AppConstant.pref_CategoryId, -1);
        AppGlobal.setIntegerPreference(activity, AppConstant.pref_CardId, -1);
		
		AppGlobal.setStringPreference(activity, AppConstant.pref_CId, "");
		AppGlobal.setStringPreference(activity, AppConstant.pref_Name, "");
		AppGlobal.setStringPreference(activity, AppConstant.pref_Title, "");
		AppGlobal.setStringPreference(activity, AppConstant.pref_Company, "");
		AppGlobal.setStringPreference(activity, AppConstant.pref_Phone, "");
		AppGlobal.setStringPreference(activity, AppConstant.pref_Email, "");
		AppGlobal.setStringPreference(activity, AppConstant.pref_Web, "");
		AppGlobal.setStringPreference(activity, AppConstant.pref_FbID, "");
		AppGlobal.setStringPreference(activity, AppConstant.pref_InID, "");
		AppGlobal.setStringPreference(activity, AppConstant.pref_UserImage, "");
		
		AppGlobal.setIntegerPreference(activity, AppConstant.pref_FontStyle, -1);
		AppGlobal.setIntegerPreference(activity, AppConstant.pref_FontColor, -1);
		
		AppGlobal.setBooleanPreference(activity, AppConstant.pref_IsTemplate, false);
		AppGlobal.setBooleanPreference(activity, AppConstant.pref_IsCardEdit, false);
	}

	// TODO open alert to display take image options
	private void selectUserPic() {
		
		final String[] items = {"Take From Gallery", "Take From Camera"};

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("Select");
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	String text = items[item];
		    	if(text.equalsIgnoreCase("Take From Gallery")) {
		    		
		    		Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);
		    		
		    	} else if(text.equalsIgnoreCase("Take From Camera")) {
		    		
		    		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		            startActivityForResult(cameraIntent, 200);
		            
		    	}
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
		
	}

	protected void setFontStyle(Typeface font) {
		tvname.setTypeface(font);
		tvtitlee.setTypeface(font);
		tvcompany.setTypeface(font);
		tvphone.setTypeface(font);
		tvemail.setTypeface(font);
		tvweb.setTypeface(font);
		tvfbid.setTypeface(font);
		tvinid.setTypeface(font);
	}

	private void setFontColor(int color) {
		tvname.setTextColor(color);
		tvtitlee.setTextColor(color);
		tvcompany.setTextColor(color);
		tvphone.setTextColor(color);
		tvemail.setTextColor(color);
		tvweb.setTextColor(color);
		tvfbid.setTextColor(color);
		tvinid.setTextColor(color);
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK) {
        	
        	if (requestCode == 100) {
        		
        		Uri selectedImageUri = data.getData();
                String filePath = getPath(selectedImageUri);
                
                Bitmap myBitmap = BitmapFactory.decodeFile(filePath);
                ivuser.setImageBitmap(myBitmap);
                
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] b = baos.toByteArray(); 

                encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                
            } else if (requestCode == 200) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ivuser.setImageBitmap(thumbnail);
                
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] b = baos.toByteArray();

                encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                
            } else if (requestCode == 300) {
            	
            	uriContact = data.getData();
            	 
                String name = getContactName();
                String phone = getContactNumber();
                String email = getEmail();
                Bitmap uImage = getContactPhoto();
                
                etname.setText(name);
                etphone.setText(phone);
                etemail.setText(email);
                
                if(uImage != null) {
                	ivuser.setImageBitmap(uImage);
                	
                	ByteArrayOutputStream baos = new ByteArrayOutputStream();
                	uImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] b = baos.toByteArray(); 

                    encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                }
            }
        }
          
    }
	
    private String getContactName() {
        String contactName = "";
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);
 
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }
 
        cursor.close();
        return contactName;
    }
    
    private String getContactNumber() {
    	 
        String contactNumber = null;
 
        // getting contacts ID
        Cursor cursorID = getContentResolver().query(uriContact, new String[]{ContactsContract.Contacts._ID}, null, null, null);
 
        if (cursorID.moveToFirst()) {
            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }
 
        cursorID.close();
 
        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        		new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                new String[] {contactID},
                null);
 
        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
 
        cursorPhone.close();
        return contactNumber;
    }
	
	private String getEmail() {
		
		Cursor emailCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[] { contactID }, null);
 
		String email = "";
        while (emailCursor.moveToNext())
        {
        	email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            int type = emailCursor.getInt(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
            String s = (String) ContactsContract.CommonDataKinds.Email.getTypeLabel(getResources(), type, "");
        }
 
        emailCursor.close();
        return email;
	}

	@SuppressWarnings("finally")
	private Bitmap getContactPhoto() {
		 
        Bitmap photo = null;
 
        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
            		ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactID)));
 
            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
            }
 
            assert inputStream != null;
            inputStream.close();
            
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        	return photo;
        }
 
    }
	
	public String getPath(Uri uri) {
		// just some safety built in
		
		if (uri == null) {
			return null;
		}
		
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		}
		
		// this is our fallback here
		return uri.getPath();
	}
	
	// TODO response from web service
	@Override
	public void onDelieverResponse(String serviceType, Object data, Exception error) {

		// MainResponseObject MainResponseObject = (MainResponseObject) data;
		ResponseObject responseObj = (ResponseObject) data;

		if (error == null) {
			if (responseObj != null) {

				if (serviceType.equalsIgnoreCase(WSConstant.RT_SAVE_CARD)) {

					if (responseObj.getStatus().equalsIgnoreCase(AppConstant.success)) {
						AppGlobal.showToast(activity, responseObj.getMsg());
						
						cId = responseObj.getId();
						ws.saveMessage(activity, userId, 0, "0", 0, 0, cId);

					} else if (responseObj.getStatus().equalsIgnoreCase(AppConstant.fail)) {
						AppGlobal.showToast(activity, responseObj.getMsg());
					}
				} else if (serviceType.equalsIgnoreCase(WSConstant.RT_SAVE_MESSAGE)) {

					if (responseObj.getStatus().equalsIgnoreCase(AppConstant.success)) {
						AppGlobal.showToast(activity, responseObj.getMsg());
						saveData();

					} else if (responseObj.getStatus().equalsIgnoreCase(AppConstant.fail)) {
						AppGlobal.showToast(activity, responseObj.getMsg());
					}
				} else if (serviceType.equalsIgnoreCase(WSConstant.RT_UPDATE_CARD)) {

					if (responseObj.getStatus().equalsIgnoreCase(AppConstant.success)) {
						AppGlobal.showToast(activity, responseObj.getMsg());
						saveData();

					} else if (responseObj.getStatus().equalsIgnoreCase(AppConstant.fail)) {
						AppGlobal.showToast(activity, responseObj.getMsg());
					}
				}
				

			}
		} else {
			AppGlobal.showToast(activity, error.getLocalizedMessage());
		}

	}

	private void saveData() {
		AppGlobal.setStringPreference(activity, AppConstant.pref_CId, String.valueOf(cId));
		AppGlobal.setStringPreference(activity, AppConstant.pref_Name, etname.getText().toString().trim());
		AppGlobal.setStringPreference(activity, AppConstant.pref_Title, ettitle.getText().toString().trim());
		AppGlobal.setStringPreference(activity, AppConstant.pref_Company, etcompany.getText().toString().trim());
		AppGlobal.setStringPreference(activity, AppConstant.pref_Phone, etphone.getText().toString().trim());
		AppGlobal.setStringPreference(activity, AppConstant.pref_Email, etemail.getText().toString().trim());
		AppGlobal.setStringPreference(activity, AppConstant.pref_Web, etweb.getText().toString().trim());
		AppGlobal.setStringPreference(activity, AppConstant.pref_FbID, etfb.getText().toString().trim());
		AppGlobal.setStringPreference(activity, AppConstant.pref_InID, etin.getText().toString().trim());
		AppGlobal.setStringPreference(activity, AppConstant.pref_UserImage, encodedImage);
		
		AppGlobal.setIntegerPreference(activity, AppConstant.pref_FontStyle, font);
		AppGlobal.setIntegerPreference(activity, AppConstant.pref_FontColor, color);
		
		AppGlobal.setBooleanPreference(activity, AppConstant.pref_IsTemplate, isTemplate);
		
		startActivity(new Intent(activity, FinalizeBusinessCard.class));
		finish();
	}

	@Override
	public void onDelieverResponse_Fragment(String serviceType, Object data,
			Exception error, Activity activity) {
	}

}