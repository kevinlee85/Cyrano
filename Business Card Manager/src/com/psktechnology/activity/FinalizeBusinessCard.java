package com.psktechnology.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.psktechnology.businesscardmanager.R;
import com.psktechnology.constant.AppConstant;
import com.psktechnology.constant.AppGlobal;
import com.psktechnology.helper.MyDragListener;
import com.psktechnology.helper.MyTouchListener;

public class FinalizeBusinessCard extends Activity implements OnClickListener {
	
	Activity activity;
//	SatelliteMenu satelliteMenu;
	
	TextView tvname, tvtitlee, tvcompany, tvphone, tvemail, tvweb;
	Button btndelete, btnshare, btnedit, btnlist;
	ImageView ivuser;
	RelativeLayout rlcard;
	SeekBar sbtextize;
	
	ImageView ivcard;
	
	int categoryId = -1;
	int cardId = -1;
	
	String cId;
	Boolean isFromViewCard = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		init();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.btndelete:
			deleteCard();
			break;
			
		case R.id.btnshare:
			openSharingOptions();
			break;
			
		case R.id.btnedit:
			editCard();
			break;
			
		case R.id.btnlist:
			gotoCardList();
			break;

		default:
			break;
		}
	}
	
	// TODO open popup to display sharing options
		private void openSharingOptions() {

			PopupMenu popup = new PopupMenu(activity, btnshare);
		    popup.getMenuInflater().inflate(R.menu.popup_sharing, popup.getMenu());
		    
		    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {

					switch (item.getItemId()) {
						
					case R.id.text:
						AppGlobal.shareText(activity, cId);
						return true;
						
					case R.id.email:
						AppGlobal.shareEmail(activity, cId);
						return true;
						
					case R.id.facebook:
						AppGlobal.shareFacebook(activity, cId);
						return true;
						
					case R.id.linkedin:
						AppGlobal.shareLinkedin(activity, cId);
						return true;

					default:
						return true;
					}
				}
			});
		 
		    popup.show();
		    
		}

	private void gotoCardList() {
	/*	startActivity(new Intent(activity, CardList.class).putExtra(AppConstant.listType, 2));
		onBackPressed(); */
	}

	private void editCard() {
		if(categoryId != 0 && cardId != 0) {
			AppGlobal.setBooleanPreference(activity, AppConstant.pref_IsCardEdit, true);
			startActivity(new Intent(activity, CreateBusinessCard.class));
			finish();
		} else
			AppGlobal.showToast(activity, "Edit not possible");
	}

	private void deleteCard() {
	/*	try {
			mDbHelper = new DBHelper(activity);
			mDb = mDbHelper.getWritableDatabase();

            long rows = mDb.delete(DBHelper.tbl_Cards, DBHelper.ID +" = "+ cId, null);
            if (rows > 0)
            {
            	onBackPressed();
            } else
            	Toast.makeText(activity, "Sorry, Could not Delete!",	Toast.LENGTH_SHORT).show();
            }
		catch (Exception ex) {
			Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_SHORT).show();
		}
		finally
		{
			mDb.close();
			mDbHelper.close();
		} */
	}

	@Override
	public void onBackPressed() {
		
		if(categoryId != 0 && cardId != 0) {
			if(sbtextize.getVisibility() == View.VISIBLE) {
				sbtextize.setVisibility(View.GONE);
			} else {
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
		} else {
			super.onBackPressed();
		}
		
	}

	private void init() {
		activity = (Activity) FinalizeBusinessCard.this;
		categoryId = AppGlobal.getIntegerPreference(activity, AppConstant.pref_CategoryId);
		cardId = AppGlobal.getIntegerPreference(activity, AppConstant.pref_CardId);
		cId = AppGlobal.getStringPreference(activity, AppConstant.pref_CId);
		
		if(categoryId != -1 && cardId != -1) {
			
			switch (categoryId) {
			case 11:
				switch (cardId) {
				case 1:
					setContentView(R.layout.custom_card_legal_one);
					break;
					
				case 2:
					setContentView(R.layout.custom_card_legal_two);
					break;

				default:
					setContentView(R.layout.custom_card_legal_one);
					break;
				}
				break;
				
			case 12:
				switch (cardId) {
				case 1:
					setContentView(R.layout.custom_card_computer_one);
					break;
					
				case 2:
					setContentView(R.layout.custom_card_computer_two);
					break;

				default:
					setContentView(R.layout.custom_card_computer_one);
					break;
				}
				break;
				
			case 13:
				switch (cardId) {
				case 1:
					setContentView(R.layout.custom_card_legal_one);
					break;
					
				case 2:
					setContentView(R.layout.custom_card_legal_one);
					break;

				default:
					setContentView(R.layout.custom_card_legal_one);
					break;
				}			
				break;
				
			case 14:
				switch (cardId) { 
				case 1:
					setContentView(R.layout.custom_card_legal_one);
					break;
					
				case 2:
					setContentView(R.layout.custom_card_legal_one);
					break;

				default:
					setContentView(R.layout.custom_card_legal_one);
					break;
				}			
				break;
				
			case 15:
				switch (cardId) { 
				case 1:
					setContentView(R.layout.custom_card_one);
					break;
					
				case 2:
					setContentView(R.layout.custom_card_two);
					break;

				default:
					setContentView(R.layout.custom_card_one);
					break;
				}			
				break;
				
			case 16:
				switch (cardId) { 
				case 1:
					setContentView(R.layout.custom_card_legal_one);
					break;
					
				case 2:
					setContentView(R.layout.custom_card_legal_one);
					break;

				default:
					setContentView(R.layout.custom_card_legal_one);
					break;
				}			
				break;
				
			case 17:
				switch (cardId) { 
				case 1:
					setContentView(R.layout.custom_card_legal_one);
					break;
					
				case 2:
					setContentView(R.layout.custom_card_legal_one);
					break;

				default:
					setContentView(R.layout.custom_card_legal_one);
					break;
				}			
				break;
				
			case 18:
				switch (cardId) { 
				case 1:
					setContentView(R.layout.custom_card_legal_one);
					break;
					
				case 2:
					setContentView(R.layout.custom_card_legal_one);
					break;

				default:
					setContentView(R.layout.custom_card_legal_one);
					break;
				}			
				break;
				
			case 19:
				switch (cardId) { 
				case 1:
					setContentView(R.layout.custom_card_legal_one);
					break;
					
				case 2:
					setContentView(R.layout.custom_card_legal_one);
					break;

				default:
					setContentView(R.layout.custom_card_legal_one);
					break;
				}			
				break;

			default:
				setContentView(R.layout.custom_card_legal_one);
				break;
			}
			
			tvname = (TextView) findViewById(R.id.tvname);
			tvtitlee = (TextView) findViewById(R.id.tvtitlee);
			tvcompany = (TextView) findViewById(R.id.tvcompany);
			tvphone = (TextView) findViewById(R.id.tvphone);
			tvemail = (TextView) findViewById(R.id.tvemail);
			tvweb = (TextView) findViewById(R.id.tvweb);
			
			btndelete = (Button) findViewById(R.id.btndelete);
			btnshare = (Button) findViewById(R.id.btnshare);
			btnedit = (Button) findViewById(R.id.btnedit);
			btnlist = (Button) findViewById(R.id.btnlist);
			
			ivuser = (ImageView) findViewById(R.id.ivuser);
			sbtextize = (SeekBar) findViewById(R.id.sbtextsize);
			
			ivcard = (ImageView) findViewById(R.id.ivcard);
			rlcard = (RelativeLayout) findViewById(R.id.rlcard);
			
			btndelete.setOnClickListener(this);
			btnshare.setOnClickListener(this);
			btnedit.setOnClickListener(this);
			btnlist.setOnClickListener(this);
			
			Bundle bundle = getIntent().getExtras();
			if (bundle != null) {
				isFromViewCard = bundle.getBoolean(AppConstant.isFromViewCard);
			}
			
			String name = AppGlobal.getStringPreference(activity, AppConstant.pref_Name);
			String title = AppGlobal.getStringPreference(activity, AppConstant.pref_Title);
			String company = AppGlobal.getStringPreference(activity, AppConstant.pref_Company);
			String phone = AppGlobal.getStringPreference(activity, AppConstant.pref_Phone);
			String email = AppGlobal.getStringPreference(activity, AppConstant.pref_Email);
			String web = AppGlobal.getStringPreference(activity, AppConstant.pref_Web);
			
			int font = AppGlobal.getIntegerPreference(activity, AppConstant.pref_FontStyle);
			int color = AppGlobal.getIntegerPreference(activity, AppConstant.pref_FontColor);
			
			switch (font) {
			case 0:
				tvname.setTypeface(null, Typeface.NORMAL);
				tvtitlee.setTypeface(null, Typeface.NORMAL);
				tvcompany.setTypeface(null, Typeface.NORMAL);
				tvphone.setTypeface(null, Typeface.NORMAL);
				tvemail.setTypeface(null, Typeface.NORMAL);
				tvweb.setTypeface(null, Typeface.NORMAL);
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
			
			if(!name.equalsIgnoreCase(""))
				tvname.setText(name);
			else
				tvname.setVisibility(View.INVISIBLE);
			
			if(!title.equalsIgnoreCase(""))
				tvtitlee.setText(title);
			else
				tvtitlee.setVisibility(View.INVISIBLE);
			
			if(!company.equalsIgnoreCase(""))
				tvcompany.setText(company);
			else
				tvcompany.setVisibility(View.INVISIBLE);
			
			if(!phone.equalsIgnoreCase(""))
				tvphone.setText(phone);
			else
				tvphone.setVisibility(View.INVISIBLE);
			
			if(!email.equalsIgnoreCase(""))
				tvemail.setText(email);
			else
				tvemail.setVisibility(View.INVISIBLE);
			
			if(!web.equalsIgnoreCase(""))
				tvweb.setText(web);
			else
				tvweb.setVisibility(View.INVISIBLE);
			
			String encodedImage = AppGlobal.getStringPreference(activity, AppConstant.pref_UserImage);
			if(!encodedImage.equalsIgnoreCase("")) {
			    byte[] b = Base64.decode(encodedImage, Base64.DEFAULT);
			    Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
			    if(bitmap != null) {
			    	ivuser.setImageBitmap(bitmap);
			    	if(!isFromViewCard)
			    		AppGlobal.saveUserPic(activity, bitmap, cId);
			    }
			} else
				ivuser.setVisibility(View.INVISIBLE);
			
			tvname.setOnTouchListener(new MyTouchListener(sbtextize));
			tvtitlee.setOnTouchListener(new MyTouchListener(sbtextize));
			tvcompany.setOnTouchListener(new MyTouchListener(sbtextize));
			tvphone.setOnTouchListener(new MyTouchListener(sbtextize));
			tvemail.setOnTouchListener(new MyTouchListener(sbtextize));
			tvweb.setOnTouchListener(new MyTouchListener(sbtextize));
			ivuser.setOnTouchListener(new MyTouchListener(sbtextize));
			
			rlcard.setOnDragListener(new MyDragListener(rlcard));
			
			if(!isFromViewCard) {
				new Handler().postDelayed(new Runnable() {
					public void run() {
						AppGlobal.saveBusinessCard(activity, rlcard, cId);
					}
				}, 2000);
			}
			
		} else {
			setContentView(R.layout.custom_card_legal_one);
			
			rlcard = (RelativeLayout) findViewById(R.id.rlcard);
			
			tvname = (TextView) findViewById(R.id.tvname);
			tvtitlee = (TextView) findViewById(R.id.tvtitlee);
			tvcompany = (TextView) findViewById(R.id.tvcompany);
			tvphone = (TextView) findViewById(R.id.tvphone);
			tvemail = (TextView) findViewById(R.id.tvemail);
			tvweb = (TextView) findViewById(R.id.tvweb);
			
			ivuser = (ImageView) findViewById(R.id.ivuser);
			sbtextize = (SeekBar) findViewById(R.id.sbtextsize);
			
			tvname.setVisibility(View.INVISIBLE);
			tvtitlee.setVisibility(View.INVISIBLE);
			tvcompany.setVisibility(View.INVISIBLE);
			tvphone.setVisibility(View.INVISIBLE);
			tvemail.setVisibility(View.INVISIBLE);
			tvweb.setVisibility(View.INVISIBLE);
			ivuser.setVisibility(View.INVISIBLE);
			sbtextize.setVisibility(View.INVISIBLE);
			
			btndelete = (Button) findViewById(R.id.btndelete);
			btnshare = (Button) findViewById(R.id.btnshare);
			btnedit = (Button) findViewById(R.id.btnedit);
			btnlist = (Button) findViewById(R.id.btnlist);
			
			btndelete.setOnClickListener(this);
			btnshare.setOnClickListener(this);
			btnedit.setOnClickListener(this);
			btnlist.setOnClickListener(this);
			
			String encodedImage = AppGlobal.getStringPreference(activity, AppConstant.pref_UserImage);
			if(!encodedImage.equalsIgnoreCase("")) {
			    byte[] b = Base64.decode(encodedImage, Base64.DEFAULT);
			    Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
			    if(bitmap != null) {
			    	rlcard.setBackgroundDrawable(new BitmapDrawable(bitmap));
			    }
			}
		}
		
	}

	protected void setFontStyle(Typeface font) {
		tvname.setTypeface(font);
		tvtitlee.setTypeface(font);
		tvcompany.setTypeface(font);
		tvphone.setTypeface(font);
		tvemail.setTypeface(font);
		tvweb.setTypeface(font);
	}
	
	private void setFontColor(int color) {
		tvname.setTextColor(color);
		tvtitlee.setTextColor(color);
		tvcompany.setTextColor(color);
		tvphone.setTextColor(color);
		tvemail.setTextColor(color);
		tvweb.setTextColor(color);
	}

}