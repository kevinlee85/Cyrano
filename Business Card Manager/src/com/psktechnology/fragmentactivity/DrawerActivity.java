package com.psktechnology.fragmentactivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.LinearLayout;

import com.psktechnology.activity.Login;
import com.psktechnology.adapter.DrawerExpandableListAdapter;
import com.psktechnology.businesscardmanager.R;
import com.psktechnology.constant.AppConstant;
import com.psktechnology.constant.AppGlobal;
import com.psktechnology.fragment.MyCards;
import com.psktechnology.fragment.SelectCard;

public class DrawerActivity extends FragmentActivity {
	
	Activity activity;
	
	DrawerLayout mDrawerLayout;
	LinearLayout drawer_Linear_layout;
    ExpandableListView mDrawerList;
    
    List<String> listGroup;
	List<String> listGroupIcons;
	
    HashMap<String, List<String>> listChild;
    HashMap<String, List<String>> listChildIcons;
    
    DrawerExpandableListAdapter DrawerExpandableListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.fragment_activity_drawer_activity);
		activity = (Activity) DrawerActivity.this;
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer_Linear_layout = (LinearLayout) findViewById(R.id.drawer_Linear_layout);
		mDrawerList = (ExpandableListView) findViewById(R.id.left_drawer);
		
		listGroupIcons = Arrays.asList(getResources().getStringArray(R.array.drawer_menu_icons));
		listGroup = Arrays.asList(getResources().getStringArray(R.array.drawer_menu));
		
		listChildIcons = new HashMap<String, List<String>>();
		listChild = new HashMap<String, List<String>>();
		
		List<String> empty = new ArrayList<String>();
		
		listChild.put(listGroup.get(0), empty);
		listChild.put(listGroup.get(1), empty);
		listChild.put(listGroup.get(2), empty);
		listChild.put(listGroup.get(3), empty);
		listChild.put(listGroup.get(4), empty);
		
		DrawerExpandableListAdapter = new  DrawerExpandableListAdapter(activity, listGroupIcons, listGroup, listChildIcons, listChild);
		mDrawerList.setAdapter(DrawerExpandableListAdapter);
		
		int width = getResources().getDisplayMetrics().widthPixels - (getResources().getDisplayMetrics().widthPixels / 100) * 40;
		DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams) drawer_Linear_layout.getLayoutParams();
		params.width = width;
		drawer_Linear_layout.setLayoutParams(params);
		
		final Fragment fragment = new MyCards();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
		
		mDrawerList.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				
				FragmentManager fragmentManager  = getSupportFragmentManager();
				FragmentTransaction ft = fragmentManager.beginTransaction();
				
				switch (groupPosition) {
				
				case 0:
					MyCards mc = new MyCards();
					ft.replace(R.id.content_frame, mc);
					ft.commit();
					mDrawerLayout.closeDrawer(drawer_Linear_layout);
					break;
					
				case 1:
					SelectCard sc = new SelectCard();
					ft.replace(R.id.content_frame, sc);
					ft.commit();
					mDrawerLayout.closeDrawer(drawer_Linear_layout);
					break;
					
				case 2:
					AppGlobal.showToast(activity, AppConstant.underDevelopment);
					mDrawerLayout.closeDrawer(drawer_Linear_layout);
					break;
					
				case 3:
					AppGlobal.showToast(activity, AppConstant.underDevelopment);
					mDrawerLayout.closeDrawer(drawer_Linear_layout);
					break;
					
				case 4:
					AppGlobal.setBooleanPreference(activity, AppConstant.pref_RememberMe, false);
					mDrawerLayout.closeDrawer(drawer_Linear_layout);
					
					startActivity(new Intent(activity, Login.class));
					finish();
					break;

				default:
					break;
					
				}
				return false;
			}
		});

		// Listview on child click listener
		mDrawerList.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				
				FragmentManager fragmentManager  = getSupportFragmentManager();
				FragmentTransaction ft = fragmentManager.beginTransaction();
				
				switch (groupPosition) {
				default:
					break;
				}
				
				return false;
			}
		});

	}
	
	public void showMenu() {
 		mDrawerLayout.openDrawer(drawer_Linear_layout);
	}

}