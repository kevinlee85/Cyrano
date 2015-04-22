package com.psktechnology.adapter;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.psktechnology.businesscardmanager.R;
import com.psktechnology.constant.AppGlobal;

public class DrawerExpandableListAdapter extends BaseExpandableListAdapter {

	Activity activity;
	
	List<String> listGroup;
	List<String> listGroupIcons;
	
	HashMap<String, List<String>> listChild;
	HashMap<String, List<String>> listChildIcons;
	
	private static final int[] EMPTY_STATE_SET = {};
	private static final int[] GROUP_EXPANDED_STATE_SET = { };

	private static final int[][] GROUP_STATE_SETS = {
		    EMPTY_STATE_SET, // 0
			GROUP_EXPANDED_STATE_SET // 1
	};

	public DrawerExpandableListAdapter(Activity activity, List<String> listGroupIcons, List<String> listGroup,
			HashMap<String, List<String>> listChildIcons, HashMap<String, List<String>> listChild) {
		this.activity = activity;
		this.listGroupIcons = listGroupIcons;
		this.listGroup = listGroup;
		this.listChildIcons = listChildIcons;
		this.listChild = listChild;
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		return this.listChild.get(this.listGroup.get(groupPosition)).get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.listchild, null);
		}
   		
   		final String childText = (String) getChild(groupPosition, childPosition);
   		final String childTextIcon = (String) listChildIcons.get(listGroup.get(groupPosition)).get(childPosition);
   		
   		TextView img_selection = (TextView) convertView.findViewById(R.id.imgchild);
   		img_selection.setTypeface(AppGlobal.setFontAwesomeFonts(activity));
		img_selection.setText(childTextIcon);
		
		TextView txtListChild = (TextView) convertView.findViewById(R.id.tvchild);
		txtListChild.setText(childText);
//		txtListChild.setTypeface(Constant.setFontGlobally((Activity)mcontext));
 		
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this.listChild.get(this.listGroup.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this.listGroup.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this.listGroup.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.listgroup, null);
		}
		
		String headerTitle = (String) getGroup(groupPosition);
		
		TextView img_selection = (TextView) convertView.findViewById(R.id.imggroup);
		img_selection.setTypeface(AppGlobal.setFontAwesomeFonts(activity));
		img_selection.setText(listGroupIcons.get(groupPosition));
		
		TextView lblListHeader = (TextView) convertView.findViewById(R.id.tvgroup);
 		lblListHeader.setText(headerTitle);
 		
 		TextView tvgcounter = (TextView) convertView.findViewById(R.id.tvgcounter);
 		tvgcounter.setVisibility(View.GONE);
 		
 		switch (groupPosition) {

		default:
			break;
		}
 		
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}