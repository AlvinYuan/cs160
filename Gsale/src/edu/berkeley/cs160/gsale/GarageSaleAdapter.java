package edu.berkeley.cs160.gsale;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GarageSaleAdapter extends ArrayAdapter<GarageSale> implements OnClickListener {
	public LayoutInflater inflater;
	public boolean isEditing;
	public HashMap<View, Integer> followedViewToPositionMap;
	
	public GarageSaleAdapter(Context context, int textViewResourceId,
			List<GarageSale> objects, boolean isEditing) {
		super(context, textViewResourceId, objects);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.isEditing = isEditing;
		followedViewToPositionMap = new HashMap<View, Integer>();
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (convertView == null) {
			if (isEditing) {
				v = inflater.inflate(R.layout.edit_garage_sale_row, null);
			} else {
				v = inflater.inflate(R.layout.view_garage_sale_row, null);
			}
		}
		
		GarageSale sale = (GarageSale) super.getItem(position);
		((TextView) v.findViewById(R.id.title)).setText(sale.title);
		sale.setDateString((TextView) v.findViewById(R.id.date));
		
		ImageView listImageView = (ImageView) v.findViewById(R.id.list_image);
		listImageView.setImageBitmap(sale.mainPhotoBitmap(getContext()));

		if (!isEditing) {
			Drawable followedDrawable;
			if (User.currentUser.followedSales.contains(sale)) {
				followedDrawable = getContext().getResources().getDrawable(R.drawable.followed);
			} else {
				followedDrawable = getContext().getResources().getDrawable(R.drawable.unfollowed);
			}
			ImageView followedImageView = (ImageView) v.findViewById(R.id.FollowedImageView);
			followedImageView.setImageDrawable(followedDrawable);
			followedViewToPositionMap.put(followedImageView, position);
			followedImageView.setOnClickListener(this);
		}
		
		if (!isEditing) {
			TextView locationTextView = (TextView) v.findViewById(R.id.location);
			if (!sale.location.equals(GarageSale.INVALID_STRING)) {
				locationTextView.setText(sale.location);
			} else {
				locationTextView.setText(R.string.NoLocation);
			}
		}
		return v;
	}
	
	/*
	 * For FollowedImageViewOnClick
	 */
	@Override
	public void onClick(View view) {
		int position = followedViewToPositionMap.get(view);
		GarageSale sale = getItem(position);
		sale.toggleFollowed(getContext());
		notifyDataSetChanged();
	}
}
