package com.psktechnology.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.psktechnology.adapter.CardlistAdapter;
import com.psktechnology.businesscardmanager.R;
import com.psktechnology.constant.AppGlobal;
import com.psktechnology.dialog.SelectCategoryDialog;
import com.psktechnology.fragmentactivity.DrawerActivity;
import com.psktechnology.interfaces.SelectCategoryDialogInterface;

public class SelectCard extends Fragment implements OnClickListener, SelectCategoryDialogInterface {
	
	View rootView;
	Activity activity;
	
	TextView tvtitle;
	Button btndrawer, btncard;
	
	ListView lvcards;
	
	int categoryType = 0;
	
	ArrayList<Integer> cardList;
	CardlistAdapter cardListAdpt;
	
	public SelectCard() {	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		rootView = inflater.inflate(R.layout.fragment_selectcard, container, false);

		init();
		return rootView;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.btndrawer:
			((DrawerActivity) activity).showMenu();
			break;
			
		case R.id.btnclose:
			new SelectCategoryDialog(activity, this).show();
			break;

		default:
			break;
		}
	}
	
	private void init() {
		activity = getActivity();
		
		btndrawer = (Button) rootView.findViewById(R.id.btndrawer);
		btndrawer.setTypeface(AppGlobal.setFontAwesomeFonts(activity));
		btndrawer.setOnClickListener(this);
		
		btncard = (Button) rootView.findViewById(R.id.btnclose);
		btncard.setTypeface(AppGlobal.setFontAwesomeFonts(activity));
		btncard.setText(getResources().getString(R.string.icon_card));
		btncard.setOnClickListener(this);
		
		tvtitle = (TextView) rootView.findViewById(R.id.tvtitle);
		tvtitle.setSelected(true);
		tvtitle.setText("Select Card");
		
		lvcards = (ListView) rootView.findViewById(R.id.lvcards);
		new SelectCategoryDialog(activity, this).show();
	}
	
	@Override
	public void setCategoryId(Integer categoryId, String catName) {
		this.categoryType = categoryId;
		
		if(categoryType != 0) {
			getSelectedCards(catName);
		} else {
			getAllCards();
		}
	}
	
	private void getAllCards() {
		this.tvtitle.setText("Select Card");
	}

	private void getSelectedCards(String catName) {
		this.tvtitle.setText("Select Card(" + catName + ")");
		
		switch (categoryType) {
		case 11:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.card_one);
			cardList.add(R.drawable.card_two);
			break;
			
		case 12:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.card_three);
			cardList.add(R.drawable.card_four);
			break;

		case 13:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.card_five);
			cardList.add(R.drawable.card_six);
			break;

		case 14:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.card_seven);
			cardList.add(R.drawable.card_eight);
			break;
			
		case 15:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.card_nine);
			cardList.add(R.drawable.card_ten);
			break;
			
		case 16:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_one);
			cardList.add(R.drawable.legal_two);
			break;
			
		case 17:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_three);
			cardList.add(R.drawable.legal_four);
			break;
			
		case 18:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.computer_one);
			cardList.add(R.drawable.computer_two);
			break;
			
		case 19:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.computer_three);
			cardList.add(R.drawable.computer_four);
			break;
			
		case 21:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;
			
		case 22:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;
			
		case 23:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;
			
		case 24:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;
			
		case 25:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;
			
		case 31:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;
			
		case 32:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;
			
		case 41:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;
			
		case 42:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;
			
		case 51:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;
			
		case 52:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;
			
		case 61:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;
			
		case 62:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;
			
		case 71:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;
			
		case 72:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;
			
		case 81:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;
			
		case 82:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;
			
		case 91:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;
			
		case 92:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;
			
		case 101:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;
			
		case 102:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;
			
		case 111:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;
			
		case 112:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;
			
		case 121:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;
			
		case 122:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;
			
		case 131:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;
			
		case 132:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;
			
		case 141:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;
			
		case 142:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;
			
		case 151:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;
			
		case 152:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;
			
		case 161:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;
			
		case 162:
			cardList = new ArrayList<Integer>();
			cardList.add(R.drawable.legal_five);
			cardList.add(R.drawable.computer_five);
			break;

		default:
			cardList = new ArrayList<Integer>();
			break;
			
		}
		
		cardListAdpt = new CardlistAdapter(activity, cardList, categoryType);
		lvcards.setAdapter(cardListAdpt);
		cardListAdpt.notifyDataSetChanged();
	}

}