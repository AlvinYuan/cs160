package edu.berkeley.cs160.gsale;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MessageAdapter extends ArrayAdapter<Message> {
	public LayoutInflater inflater;

	public MessageAdapter(Context context, int textViewResourceId,
			List<Message> objects, boolean isEditing) {
		super(context, textViewResourceId, objects);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (convertView == null) {
			v = inflater.inflate(R.layout.message_row, null);
		}
		
		Message message = (Message) super.getItem(position);
		((TextView) v.findViewById(R.id.SubjectTextView)).setText(message.subject);
		((TextView) v.findViewById(R.id.SaleInfoTextView)).setText("Sale: " + GarageSale.allSales.get(message.saleId).title);
		((TextView) v.findViewById(R.id.ContentTextView)).setText(message.content);

		return v;
	}

}
