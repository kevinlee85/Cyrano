package com.psktechnology.helper;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

public class MyTextWatcher implements TextWatcher {
	
	TextView tv;
	String msg;
	
	public MyTextWatcher(TextView tv, String msg) {
		this.tv = tv;
		this.msg = msg;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		
		String word = s.toString().trim();
		int wordLength = word.length();

		if (wordLength > 0) {
			tv.setText(word);
		} else
			tv.setText(msg);
	}

	@Override
	public void afterTextChanged(Editable s) {	}

}