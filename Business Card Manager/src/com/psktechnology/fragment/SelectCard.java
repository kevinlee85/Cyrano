package com.psktechnology.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.psktechnology.businesscardmanager.R;
import com.psktechnology.constant.AppGlobal;
import com.psktechnology.dialog.SelectCategoryDialog;
import com.psktechnology.fragmentactivity.DrawerActivity;
import com.psktechnology.interfaces.SelectCategoryDialogInterface;

public class SelectCard extends Fragment implements OnClickListener, SelectCategoryDialogInterface {
	
	View rootView;
	Activity activity;
	
	TextView tvtitle;
	Button btndrawer, btnclose;
	
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

		default:
			break;
		}
	}
	
	private void init() {
		activity = getActivity();
		
		btndrawer = (Button) rootView.findViewById(R.id.btndrawer);
		btndrawer.setTypeface(AppGlobal.setFontAwesomeFonts(activity));
		btndrawer.setOnClickListener(this);
		
		btnclose = (Button) rootView.findViewById(R.id.btnclose);
		btnclose.setVisibility(View.INVISIBLE);
		
		tvtitle = (TextView) rootView.findViewById(R.id.tvtitle);
		tvtitle.setSelected(true);
		tvtitle.setText("Select Card");
		
		new SelectCategoryDialog(activity, this).show();
	}
	
	@Override
	public void setCategoryId(Integer categoryId, String catName) {
//		llsearch.setVisibility(View.VISIBLE);
//		llbuttons.setVisibility(View.VISIBLE);
//		
//		this.categoryType = categoryId;
//		this.tvtitle.setText("Select Your Card(" + catName + ")");
//		getCards();
		
	}

}