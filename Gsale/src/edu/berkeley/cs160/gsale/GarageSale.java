package edu.berkeley.cs160.gsale;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class GarageSale implements java.io.Serializable{
	public static String HAS_SALE_ID_KEY = "HAS_SALE_ID_KEY";
	public static String SALE_ID_KEY = "SALE_ID_KEY";
	
	public static String DETAILS_ACTIVITY_PARENT_KEY = "DETAIL_ACTIVITY_PARENT_KEY";
	public static String MAP_ACTIVITY = "MAP_ACTIVTY";
	public static String SEARCH_ACTIVITY = "SEARCH_ACTIVITY";
	public static String FOLLOWED_ACTIVITY = "FOLLOWED_ACTIVITY";
	
	public static String SERVER_URL = "http://alvinyuan.pythonanywhere.com";
	public static String POST_SALE_URL_SUFFIX = "/sale";
	
	public static int INVALID = -1;
	public static HashMap<Integer, GarageSale> idToSaleMap = null;
	public static ArrayList<GarageSale> allSales = null;
	
	/* Instance Variables */
	/* General */
	public int id; //Unique identifier
	public String title = null;
	public String description = null;
	public User planner = null;

	/* Date and Time */
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
	
	/* Location */
	public String location = null;
	public LatLng coords = null;
	
	/* Photos */
	public ArrayList<Photo> photos = null;
	public Photo mainPhoto = null;
	
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
	
	public void startDetailsActivity(Context context) {
		Intent intent = new Intent(context, DetailsActivity.class);
		intent.putExtra(SALE_ID_KEY, id);
		String parentActivity = "";
		if (context instanceof MapActivity) {
			parentActivity = MAP_ACTIVITY;
		} else if (context instanceof SearchActivity) {
			parentActivity = SEARCH_ACTIVITY;
		} else if (context instanceof FollowedActivity) {
			parentActivity = FOLLOWED_ACTIVITY;
		}
		System.out.println(parentActivity);
		intent.putExtra(DETAILS_ACTIVITY_PARENT_KEY, parentActivity);

		context.startActivity(intent);
	}
	
	public ArrayList<NameValuePair> constructPostParameters() {
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("title", title));
		return postParameters;
	}
	/*
	 * Prototyping Purposes Only
	 * Fills in sales (for map view) in GarageSales.allSales
	 */
	public static void generateAllSales(Context context) {
		Photo p;

		/* GarageSale 1 */
		GarageSale SALE1 = new GarageSale();
		LatLng s1_coord = new LatLng(37.875192, -122.266932);
		SALE1.coords = s1_coord;
		SALE1.id = 1010;
		SALE1.title = "Bob's Moving Sale!!";
		SALE1.location = "1780 Spruce St. Berkeley, CA";
		p = new Photo();
		p.bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.saleiconone);
		SALE1.photos.add(p);
		SALE1.mainPhoto = p;
		SALE1.description = "I'm moving to SF and I need to get rid of some stuff! I'm selling lots of furniture and awesome stuff!";

		/* GarageSale 2 */
		GarageSale SALE2 = new GarageSale();
		LatLng s2_coord = new LatLng(37.877665, -122.25925);
		SALE2.coords = s2_coord;
		SALE2.id = 2020;
		SALE2.title = "Alice's Garage Sale TODAY";
		SALE2.location = "1500 LeRoy St. Berkeley, CA";
		p = new Photo();
		p.bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.saleicontwo);
		SALE2.photos.add(p);
		SALE2.mainPhoto = p;
		SALE2.description = "Stop by my garage sale! I have antiques and rare items for sale.";

		/* Add to allSales and mapIdToSale */
		allSales.add(SALE1);
		idToSaleMap.put(SALE1.id, SALE1);
		allSales.add(SALE2);
		idToSaleMap.put(SALE2.id, SALE2);
	}
	
}
