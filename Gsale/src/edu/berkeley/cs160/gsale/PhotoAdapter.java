package edu.berkeley.cs160.gsale;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoAdapter extends ArrayAdapter<Photo> {
	public LayoutInflater inflater;
	public PhotoAdapter(Context context, int textViewResourceId,
			List<Photo> objects) {
		super(context, textViewResourceId, objects);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (convertView == null) {
			v = inflater.inflate(R.layout.photo_row, null);
		}
		
		Photo item = (Photo) super.getItem(position);
		((ImageView) v.findViewById(R.id.list_image)).setImageBitmap(item.bitmap);
		((TextView) v.findViewById(R.id.photoDescription)).setText(item.description);
		return v;
	}

}
