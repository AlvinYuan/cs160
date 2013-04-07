package edu.berkeley.cs160.gsale;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import android.view.View;
import android.widget.TextView;

public class GarageSale implements java.io.Serializable{
	public static String SALE_ID_KEY = "SALE_ID_KEY";
	public static HashMap<Integer, GarageSale> mapIdToSale;
	
	public Calendar startDate;
	public Calendar endDate;
	public Calendar startTime;
	public Calendar endTime;
	public String title;
	public String description;
	public String location;
	public User planner;
	public ArrayList<Photo> photos;
	public int id; //Unique identifier
	
	public GarageSale() {
		photos = new ArrayList<Photo>();
	}
	
	public static void loadDetailsIntoView(View detailsView, GarageSale sale) {
		/* Title */
		TextView detailsTitleTextView = (TextView) detailsView.findViewById(R.id.DetailsTitleTextView);
		detailsTitleTextView.setText(sale.title);
		/* Time */
		TextView detailsTimeTextView = (TextView) detailsView.findViewById(R.id.DetailsTimeTextView);
		detailsTimeTextView.setText(sale.timeString(true) + " - " + sale.timeString(false));
		/* Date */
		TextView detailsDateTextView = (TextView) detailsView.findViewById(R.id.DetailsDateTextView);
		detailsDateTextView.setText(sale.dateString(true) + " - " + sale.dateString(false));
		/* Location */
		TextView detailsLocationTextView = (TextView) detailsView.findViewById(R.id.DetailsLocationTextView);
		detailsLocationTextView.setText(sale.location);
		/* Description */
		TextView detailsDescriptionTextView = (TextView) detailsView.findViewById(R.id.DetailsDescriptionTextView);
		detailsDescriptionTextView.setText(sale.description);
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
