package edu.berkeley.cs160.gsale;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import com.google.android.gms.maps.model.LatLng;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class GarageSale implements java.io.Serializable{
	public static String SALE_ID_KEY = "SALE_ID_KEY";
	public static int INVALID = -1;
	public static HashMap<Integer, GarageSale> mapIdToSale = null;
	public static ArrayList<GarageSale> allSales = null;
	
	/*
	 * http://stackoverflow.com/questions/15661713/android-calendar-serialization-incompatable-with-java-6/15661858#15661858
	 * Cannot serialize Calendar fields. Use ints (hour, day, month, etc.) instead
	 */
	public int startYear = INVALID;
	public int startMonth = INVALID;
	public int startDay = INVALID;
	public int startHour = INVALID;
	public int startMinute = INVALID;
	public int endYear = INVALID;
	public int endMonth = INVALID;
	public int endDay = INVALID;
	public int endHour = INVALID;
	public int endMinute = INVALID;
	public String title = null;
	public String description = null;
	public String location = null;
	public User planner = null;
	public ArrayList<Photo> photos = null;
	public Photo mainPhoto = null;
	public int id; //Unique identifier
	public LatLng coords = null;
	public int image;
	
	public GarageSale() {
		photos = new ArrayList<Photo>();
		
	}
	
	public void loadDetailsIntoView(View detailsView) {
		Calendar startDate = dateTime(true, true);
		Calendar endDate = dateTime(false, true);
		Calendar startTime = dateTime(true, false);
		Calendar endTime = dateTime(false, false);

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
			detailsMainPhotoImageView.setImageResource(R.drawable.photo);
		}
	}
	
	public String dateString(boolean isStartDate) {
		Calendar date = dateTime(isStartDate, true);
		
		String monthString = date.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
		String weekdayString = date.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US);
		int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);
		int year = date.get(Calendar.YEAR);
		String dateString = weekdayString + ", " + monthString + " " + dayOfMonth + ", " + year;
		return dateString;
	}
	
	public String timeString(boolean isStartTime) {
		Calendar time = dateTime(isStartTime, false);

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
	
	public Calendar dateTime(boolean isStart, boolean getDate) {
		int year = isStart ? startYear : endYear;
		int month = isStart ? startMonth : endMonth;
		int day = isStart ? startDay : endDay;
		int hour = isStart ? startHour : endHour;
		int minute = isStart ? startMinute : endMinute;
		if (getDate) {
			if (year == INVALID) {
				return null;
			}
			return new GregorianCalendar(year, month, day);
		} else {
			if (hour == INVALID) {
				return null;
			}
		return new GregorianCalendar(INVALID, INVALID, INVALID, hour, minute, 0);
		}
	}
	
	// Testing
	public static ArrayList<GarageSale> generateSales() {
		ArrayList<GarageSale> sales = new ArrayList<GarageSale>();
		GarageSale sale = new GarageSale();
		sale.title = "Sale this saturday!";
		sale.id = 1244;
		sales.add(sale);
		sale = new GarageSale();
		sale.title = "Moving Sale - Sunday!";
		sale.id = 1245;
		sales.add(sale);
		return sales;
	}
	
}
