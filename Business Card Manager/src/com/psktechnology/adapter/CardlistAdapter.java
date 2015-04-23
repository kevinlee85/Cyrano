package com.psktechnology.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.psktechnology.businesscardmanager.R;
import com.psktechnology.constant.AppConstant;
import com.psktechnology.constant.AppGlobal;

public class CardlistAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private Activity activity;

	ArrayList<Integer> cardList;
	int categoryType;

	public CardlistAdapter(Activity activity, ArrayList<Integer> cardList, int categoryType) {
		this.activity = activity;
		mInflater = LayoutInflater.from(activity);
		this.cardList = cardList;
		this.categoryType = categoryType;
	}

	@Override
	public int getCount() {
		return cardList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	public class Holder {
		ImageView ivcard;
	}

	public void intialize(View ConvertView, Holder holder) {
		holder.ivcard = (ImageView) ConvertView.findViewById(R.id.ivcard);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		final Holder holder;
		
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_cardlist, null);
			holder = new Holder();
			intialize(convertView, holder);

			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		holder.ivcard.setImageResource(cardList.get(position));
		
		OnClickListener onclick = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				
				case R.id.ivcard:
//					AppGlobal.setIntegerPreference(activity, AppConstant.pref_CategoryId, categoryType);
//					AppGlobal.setIntegerPreference(activity, AppConstant.pref_CardId, (position+1));
//					activity.startActivity(new Intent(activity, CreateBusinessCard.class));
					break;
					
				default:
					break;
				}
			}
		};
		
		holder.ivcard.setOnClickListener(onclick);

		return convertView;
	}

}