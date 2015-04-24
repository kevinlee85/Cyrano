package com.psktechnology.dialog;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

import com.psktechnology.adapter.DrawerExpandableListAdapter;
import com.psktechnology.businesscardmanager.R;
import com.psktechnology.interfaces.SelectCategoryDialogInterface;

public class SelectCategoryDialog extends Dialog implements OnClickListener {
	
	Activity activity;
	
	TextView tvtitle;
	Button btndrawer, btnclose;
	
	ExpandableListView exlvcategory;
	Button btnclear;
	
	List<String> listGroup;
	List<String> listGroupIcons;
	
    HashMap<String, List<String>> listChild;
    HashMap<String, List<String>> listChildIcons;
    
    DrawerExpandableListAdapter DrawerExpandableListAdapter;
    SelectCategoryDialogInterface selectCatInterface;
	
	public SelectCategoryDialog(Activity activity, SelectCategoryDialogInterface selectCatInterface) {
		super(activity);
		this.activity = activity;
		this.selectCatInterface = selectCatInterface;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.dialog_select_category);
		
		btndrawer = (Button) findViewById(R.id.btndrawer);
		btndrawer.setVisibility(View.INVISIBLE);
		
		btnclose = (Button) findViewById(R.id.btnclose);
		btnclose.setVisibility(View.INVISIBLE);
		
		tvtitle = (TextView) findViewById(R.id.tvtitle);
		tvtitle.setSelected(true);
		tvtitle.setText("Select Category/Subcategory");
		
		exlvcategory = (ExpandableListView) findViewById(R.id.exlvcategory);
		btnclear = (Button) findViewById(R.id.btnclear);

		btnclear.setOnClickListener(this);
		
		listGroupIcons = Arrays.asList(activity.getResources().getStringArray(R.array.category_menu_icons));
		listGroup = Arrays.asList(activity.getResources().getStringArray(R.array.category_menu));
		
		listChildIcons = new HashMap<String, List<String>>();
		listChild = new HashMap<String, List<String>>();
		
		List<String> automotive_sub_menu = Arrays.asList(activity.getResources().getStringArray(R.array.automotive_sub_menu));
		List<String> business_sub_menu = Arrays.asList(activity.getResources().getStringArray(R.array.business_sub_menu));
		List<String> computers_sub_menu = Arrays.asList(activity.getResources().getStringArray(R.array.computers_sub_menu));
		List<String> construction_sub_menu = Arrays.asList(activity.getResources().getStringArray(R.array.construction_sub_menu));
		List<String> education_sub_menu = Arrays.asList(activity.getResources().getStringArray(R.array.education_sub_menu));
		List<String> entertainment_sub_menu = Arrays.asList(activity.getResources().getStringArray(R.array.entertainment_sub_menu));
		List<String> food_sub_menu = Arrays.asList(activity.getResources().getStringArray(R.array.food_sub_menu));
		List<String> health_sub_menu = Arrays.asList(activity.getResources().getStringArray(R.array.health_sub_menu));
		List<String> home_sub_menu = Arrays.asList(activity.getResources().getStringArray(R.array.home_sub_menu));
		List<String> legal_sub_menu = Arrays.asList(activity.getResources().getStringArray(R.array.legal_sub_menu));
		List<String> manufacturing_sub_menu = Arrays.asList(activity.getResources().getStringArray(R.array.manufacturing_sub_menu));
		List<String> merchants_sub_menu = Arrays.asList(activity.getResources().getStringArray(R.array.merchants_sub_menu));
		List<String> miscellaneous_sub_menu = Arrays.asList(activity.getResources().getStringArray(R.array.miscellaneous_sub_menu));
		List<String> personal_sub_menu = Arrays.asList(activity.getResources().getStringArray(R.array.personal_sub_menu));
		List<String> real_sub_menu = Arrays.asList(activity.getResources().getStringArray(R.array.real_sub_menu));
		List<String> travel_sub_menu = Arrays.asList(activity.getResources().getStringArray(R.array.travel_sub_menu));
		
		List<String> automotive_sub_menu_icons = Arrays.asList(activity.getResources().getStringArray(R.array.automotive_sub_menu_icons));
		List<String> business_sub_menu_icons = Arrays.asList(activity.getResources().getStringArray(R.array.business_sub_menu_icons));
		List<String> computers_sub_menu_icons = Arrays.asList(activity.getResources().getStringArray(R.array.computers_sub_menu_icons));
		List<String> construction_sub_menu_icons = Arrays.asList(activity.getResources().getStringArray(R.array.construction_sub_menu_icons));
		List<String> education_sub_menu_icons = Arrays.asList(activity.getResources().getStringArray(R.array.education_sub_menu_icons));
		List<String> entertainment_sub_menu_icons = Arrays.asList(activity.getResources().getStringArray(R.array.entertainment_sub_menu_icons));
		List<String> food_sub_menu_icons = Arrays.asList(activity.getResources().getStringArray(R.array.food_sub_menu_icons));
		List<String> health_sub_menu_icons = Arrays.asList(activity.getResources().getStringArray(R.array.health_sub_menu_icons));
		List<String> home_sub_menu_icons = Arrays.asList(activity.getResources().getStringArray(R.array.home_sub_menu_icons));
		List<String> legal_sub_menu_icons = Arrays.asList(activity.getResources().getStringArray(R.array.legal_sub_menu_icons));
		List<String> manufacturing_sub_menu_icons = Arrays.asList(activity.getResources().getStringArray(R.array.manufacturing_sub_menu_icons));
		List<String> merchants_sub_menu_icons = Arrays.asList(activity.getResources().getStringArray(R.array.merchants_sub_menu_icons));
		List<String> miscellaneous_sub_menu_icons = Arrays.asList(activity.getResources().getStringArray(R.array.miscellaneous_sub_menu_icons));
		List<String> personal_sub_menu_icons = Arrays.asList(activity.getResources().getStringArray(R.array.personal_sub_menu_icons));
		List<String> real_sub_menu_icons = Arrays.asList(activity.getResources().getStringArray(R.array.real_sub_menu_icons));
		List<String> travel_sub_menu_icons = Arrays.asList(activity.getResources().getStringArray(R.array.travel_sub_menu_icons));
		
		listChild.put(listGroup.get(0), automotive_sub_menu);
		listChild.put(listGroup.get(1), business_sub_menu);
		listChild.put(listGroup.get(2), computers_sub_menu);
		listChild.put(listGroup.get(3), construction_sub_menu);
		listChild.put(listGroup.get(4), education_sub_menu);
		listChild.put(listGroup.get(5), entertainment_sub_menu);
		listChild.put(listGroup.get(6), food_sub_menu);
		listChild.put(listGroup.get(7), health_sub_menu);
		listChild.put(listGroup.get(8), home_sub_menu);
		listChild.put(listGroup.get(9), legal_sub_menu);
		listChild.put(listGroup.get(10), manufacturing_sub_menu);
		listChild.put(listGroup.get(11), merchants_sub_menu);
		listChild.put(listGroup.get(12), miscellaneous_sub_menu);
		listChild.put(listGroup.get(13), personal_sub_menu);
		listChild.put(listGroup.get(14), real_sub_menu);
		listChild.put(listGroup.get(15), travel_sub_menu);
		
		listChildIcons.put(listGroup.get(0), automotive_sub_menu_icons);
		listChildIcons.put(listGroup.get(1), business_sub_menu_icons);
		listChildIcons.put(listGroup.get(2), computers_sub_menu_icons);
		listChildIcons.put(listGroup.get(3), construction_sub_menu_icons);
		listChildIcons.put(listGroup.get(4), education_sub_menu_icons);
		listChildIcons.put(listGroup.get(5), entertainment_sub_menu_icons);
		listChildIcons.put(listGroup.get(6), food_sub_menu_icons);
		listChildIcons.put(listGroup.get(7), health_sub_menu_icons);
		listChildIcons.put(listGroup.get(8), home_sub_menu_icons);
		listChildIcons.put(listGroup.get(9), legal_sub_menu_icons);
		listChildIcons.put(listGroup.get(10), manufacturing_sub_menu_icons);
		listChildIcons.put(listGroup.get(11), merchants_sub_menu_icons);
		listChildIcons.put(listGroup.get(12), miscellaneous_sub_menu_icons);
		listChildIcons.put(listGroup.get(13), personal_sub_menu_icons);
		listChildIcons.put(listGroup.get(14), real_sub_menu_icons);
		listChildIcons.put(listGroup.get(15), travel_sub_menu_icons);
		
		DrawerExpandableListAdapter = new  DrawerExpandableListAdapter(activity, listGroupIcons, listGroup, listChildIcons, listChild);
		exlvcategory.setAdapter(DrawerExpandableListAdapter);
		
		exlvcategory.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				
				switch (groupPosition) {

				default:
					break;
					
				}
				return false;
			}
		});

		// Listview on child click listener
		exlvcategory.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				
				switch (groupPosition) {
				case 0:
					switch (childPosition) {
					case 0:
						selectCatInterface.setCategoryId(11, "Auto Accessories");
						dismiss();
						break;
					case 1:
						selectCatInterface.setCategoryId(12, "Auto Dealers – New");
						dismiss();
						break;
					case 2:
						selectCatInterface.setCategoryId(13, "Auto Dealers – Used");
						dismiss();
						break;
					case 3:
						selectCatInterface.setCategoryId(14, "Detail & Carwash");
						dismiss();
						break;
					case 4:
						selectCatInterface.setCategoryId(15, "Gas Stations");
						dismiss();
						break;
					case 5:
						selectCatInterface.setCategoryId(16, "Motorcycle Sales & Repair");
						dismiss();
						break;
					case 6:
						selectCatInterface.setCategoryId(17, "Rental & Leasing");
						dismiss();
						break;
					case 7:
						selectCatInterface.setCategoryId(18, "Service, Repair & Parts");
						dismiss();
						break;
					case 8:
						selectCatInterface.setCategoryId(19, "Towing");
						dismiss();
						break;
					default:
						break;
					}
					break;
					
				case 1:
					switch (childPosition) {
					case 0:
						selectCatInterface.setCategoryId(21, "Consultants");
						dismiss();
						break;
					case 1:
						selectCatInterface.setCategoryId(22, "Employment Agency");
						dismiss();
						break;
					case 2:
						selectCatInterface.setCategoryId(23, "Marketing & Communications");
						dismiss();
						break;
					case 3:
						selectCatInterface.setCategoryId(24, "Office Supplies");
						dismiss();
						break;
					case 4:
						selectCatInterface.setCategoryId(25, "Printing & Publishing");
						dismiss();
						break;
					default:
						break;
					}
					break;
					
				case 2:
					switch (childPosition) {
					case 0:
						selectCatInterface.setCategoryId(31, "Computer Programming & Support");
						dismiss();
						break;
					case 1:
						selectCatInterface.setCategoryId(32, "Consumer Electronics & Accessories");
						dismiss();
						break;
					default:
						break;
					}
					break;
					
				case 3:
					switch (childPosition) {
					case 0:
						selectCatInterface.setCategoryId(41, "Blasting & Demolition");
						dismiss();
						break;
					case 1:
						selectCatInterface.setCategoryId(42, "Building Materials & Supplies");
						dismiss();
						break;
					default:
						break;
					}
					break;
					
				case 4:
					switch (childPosition) {
					case 0:
						selectCatInterface.setCategoryId(51, "Adult & Continuing Education");
						dismiss();
						break;
					case 1:
						selectCatInterface.setCategoryId(52, "Early Childhood Education");
						dismiss();
						break;
					default:
						break;
					}
					break;
					
				case 5:
					switch (childPosition) {
					case 0:
						selectCatInterface.setCategoryId(61, "Artists, Writers");
						dismiss();
						break;
					case 1:
						selectCatInterface.setCategoryId(62, "Event Planners & Supplies");
						dismiss();
						break;
					default:
						break;
					}
					break;
					
				case 6:
					switch (childPosition) {
					case 0:
						selectCatInterface.setCategoryId(71, "Desserts, Catering & Supplies");
						dismiss();
						break;
					case 1:
						selectCatInterface.setCategoryId(72, "Fast Food & Carry Out");
						dismiss();
						break;
					default:
						break;
					}
					break;
					
				case 7:
					switch (childPosition) {
					case 0:
						selectCatInterface.setCategoryId(81, "Acupuncture");
						dismiss();
						break;
					case 1:
						selectCatInterface.setCategoryId(82, "Assisted Living & Home Health Caret");
						dismiss();
						break;
					default:
						break;
					}
					break;
					
				case 8:
					switch (childPosition) {
					case 0:
						selectCatInterface.setCategoryId(91, "Antiques & Collectibles");
						dismiss();
						break;
					case 1:
						selectCatInterface.setCategoryId(92, "Cleaning");
						dismiss();
						break;
					default:
						break;
					}
					break;
					
				case 9:
					switch (childPosition) {
					case 0:
						selectCatInterface.setCategoryId(101, "Accountants");
						dismiss();
						break;
					case 1:
						selectCatInterface.setCategoryId(102, "Attorneys");
						dismiss();
						break;
					default:
						break;
					}
					break;
					
				case 10:
					switch (childPosition) {
					case 0:
						selectCatInterface.setCategoryId(111, "Distribution, Import/Export");
						dismiss();
						break;
					case 1:
						selectCatInterface.setCategoryId(112, "Manufacturing");
						dismiss();
						break;
					default:
						break;
					}
					break;
					
				case 11:
					switch (childPosition) {
					case 0:
						selectCatInterface.setCategoryId(121, "Cards & Gifts");
						dismiss();
						break;
					case 1:
						selectCatInterface.setCategoryId(122, "Clothing & Accessories");
						dismiss();
						break;
					default:
						break;
					}
					break;
					
				case 12:
					switch (childPosition) {
					case 0:
						selectCatInterface.setCategoryId(131, "Civic Groups");
						dismiss();
						break;
					case 1:
						selectCatInterface.setCategoryId(132, "Funeral Service Providers & Cemetaries");
						dismiss();
						break;
					default:
						break;
					}
					break;
					
				case 13:
					switch (childPosition) {
					case 0:
						selectCatInterface.setCategoryId(141, "Agencies & Brokerage");
						dismiss();
						break;
					case 1:
						selectCatInterface.setCategoryId(142, "Agents & Brokers");
						dismiss();
						break;
					default:
						break;
					}
					break;
					
				case 14:
					switch (childPosition) {
					case 0:
						selectCatInterface.setCategoryId(151, "Animal Care & Supplies");
						dismiss();
						break;
					case 1:
						selectCatInterface.setCategoryId(152, "Barber & Beauty Salons");
						dismiss();
						break;
					default:
						break;
					}
					break;
					
				case 15:
					switch (childPosition) {
					case 0:
						selectCatInterface.setCategoryId(161, "Hotel, Motel & Extended Stay");
						dismiss();
						break;
					case 1:
						selectCatInterface.setCategoryId(162, "Moving & Storage");
						dismiss();
						break;
					default:
						break;
					}
					break;
					
				default:
					break;
				}
				
				return false;
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.btnclear:
			selectCatInterface.setCategoryId(0, "");
			dismiss();
			break;
			
		default:
			break;
			
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		dismiss();
	}

}