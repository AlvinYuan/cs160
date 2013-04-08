package edu.berkeley.cs160.gsale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GarageSaleAdapter extends ArrayAdapter<GarageSale> {
	public LayoutInflater inflater;
	public GarageSaleAdapter(Context context, int textViewResourceId,
			GarageSale[] objects) {
		super(context, textViewResourceId, objects);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// TODO Auto-generated constructor stub
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (convertView == null) {
			v = inflater.inflate(R.layout.garage_sale_row, null);
		}
		
		GarageSale item = (GarageSale) super.getItem(position);
		((TextView) v.findViewById(R.id.title)).setText(item.title);
		if (item.dateTime(true, true) != null) {
			((TextView) v.findViewById(R.id.date)).setText(item.dateString(true));
		}
		return v;
	}
	

}
