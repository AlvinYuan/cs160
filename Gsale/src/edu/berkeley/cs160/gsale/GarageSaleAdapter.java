package edu.berkeley.cs160.gsale;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GarageSaleAdapter extends ArrayAdapter<GarageSale> {
	public GarageSaleAdapter(Context context, int textViewResourceId,
			GarageSale[] objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView t;
		if (convertView != null && convertView instanceof TextView) {
			t = (TextView) convertView;
		} else {
			t = new TextView(super.getContext());
		}
		t.setText("hello");
		return t;
	}

}
