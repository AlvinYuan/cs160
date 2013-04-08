package edu.berkeley.cs160.gsale;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class GarageSale implements java.io.Serializable{
	public static String SALE_ID_KEY = "SALE_ID_KEY";
	public static HashMap<Integer, GarageSale> mapIdToSale = null;
	
	public Calendar startDate = null;
	public Calendar endDate = null;
	public Calendar startTime = null;
	public Calendar endTime = null;
	public String title = null;
	public String description = null;
	public String location = null;
	public User planner = null;
	public ArrayList<Photo> photos = null;
	public Photo mainPhoto = null;
	public int id; //Unique identifier
	
	public GarageSale() {
		photos = new ArrayList<Photo>();
		
	}
	
	public void loadDetailsIntoView(View detailsView) {
		/* Title */
		if (title != null) {
			TextView detailsTitleTextView = (TextView) detailsView.findViewById(R.id.DetailsTitleTextView);
			detailsTitleTextView.setText(title);
		}
		/* Time */
		if (startTime != null && endTime != null) {
			TextView detailsTimeTextView = (TextView) detailsView.findViewById(R.id.DetailsTimeTextView);
			detailsTimeTextView.setText(timeString(true) + " - " + timeString(false));
		}
		/* Date */
		if (startDate != null && endDate != null) {
			TextView detailsDateTextView = (TextView) detailsView.findViewById(R.id.DetailsDateTextView);
			detailsDateTextView.setText(dateString(true) + " - " + dateString(false));
		}
		/* Location */
		if (location != null) {	
			TextView detailsLocationTextView = (TextView) detailsView.findViewById(R.id.DetailsLocationTextView);
			detailsLocationTextView.setText(location);
		}
		/* Description */
		if (description != null) {
			TextView detailsDescriptionTextView = (TextView) detailsView.findViewById(R.id.DetailsDescriptionTextView);
			detailsDescriptionTextView.setText(description);
		}
		/* Main Photo */
		ImageView detailsMainPhotoImageView = (ImageView) detailsView.findViewById(R.id.DetailsMainPhotoImageView);
		if (mainPhoto != null) {
			detailsMainPhotoImageView.setImageBitmap(mainPhoto.bitmap);
		} else {
			detailsMainPhotoImageView.setImageResource(R.drawable.ic_launcher);
		}
	}
	
	public String dateString(boolean isStartDate) {
		Calendar date;
		if (isStartDate) {
			date = startDate;
		} else {
			date = endDate;
		}
		String monthString = date.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
		String weekdayString = date.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US);
		int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);
		int year = date.get(Calendar.YEAR);
		String dateString = weekdayString + ", " + monthString + " " + dayOfMonth + ", " + year;
		return dateString;
	}
	
	public String timeString(boolean isStartTime) {
		Calendar time;
		if (isStartTime) {
			time = startTime;
		} else {
			time = endTime;
		}
		int hour = time.get(Calendar.HOUR);
		if (hour == 0) {
			hour = 12;
		}
		int minute = time.get(Calendar.MINUTE);
		String minuteString;
		if (minute < 10) {
			minuteString = "0" + minute;
		} else {
			minuteString = "" + minute;
		}
		String ampmString = time.getDisplayName(Calendar.AM_PM, Calendar.SHORT, Locale.US);
		String timeString = hour + ":" + minuteString + " " + ampmString;
		return timeString;
	}
	// Testing
	public static GarageSale[] generateSales() {
		GarageSale sales[] = {new GarageSale(), new GarageSale()};
		sales[0].title = "Sale this saturday!";
		sales[1].title = "Moving Sale - Sunday!";
		return sales;
	}
	
}
