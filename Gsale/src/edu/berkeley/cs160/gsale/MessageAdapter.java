package edu.berkeley.cs160.gsale;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

public class MessageAdapter extends ArrayAdapter<Message> {
	public LayoutInflater inflater;

	public MessageAdapter(Context context, int textViewResourceId,
			List<Message> objects, boolean isEditing) {
		super(context, textViewResourceId, objects);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
}
