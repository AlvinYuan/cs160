package edu.berkeley.cs160.gsale;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GarageSale implements java.io.Serializable{
	public static String HAS_SALE_ID_KEY = "HAS_SALE_ID_KEY";
	public static String SALE_ID_KEY = "SALE_ID_KEY";
	
	public static String DETAILS_ACTIVITY_PARENT_KEY = "DETAIL_ACTIVITY_PARENT_KEY";
	public static String MAP_ACTIVITY = "MAP_ACTIVTY";
	public static String SEARCH_ACTIVITY = "SEARCH_ACTIVITY";
	public static String FOLLOWED_ACTIVITY = "FOLLOWED_ACTIVITY";
	
	public static int INVALID_INT = -1;
	public static LatLng INVALID_COORDS = new LatLng(-90, 0);
	public static String INVALID_STRING = "";

	/* allSales maps GarageSale id to object */
	public static HashMap<Integer, GarageSale> allSales = null;
	public static boolean salesLoaded = false;
	
	/* Instance Variables */
	/* General */
	public int id = INVALID_INT; //Unique identifier
	public String title = INVALID_STRING;
	public String description = INVALID_STRING;
	public int plannerId = User.NOT_LOGGED_IN;

	/* Date and Time */
	/*
	 * http://stackoverflow.com/questions/15661713/android-calendar-serialization-incompatable-with-java-6/15661858#15661858
	 * Cannot serialize Calendar fields. Use ints (hour, day, month, etc.) instead
	 */
	public int startYear = INVALID_INT;
	public int startMonth = INVALID_INT;
	public int startDay = INVALID_INT;
	public int startHour = INVALID_INT;
	public int startMinute = INVALID_INT;
	public int endYear = INVALID_INT;
	public int endMonth = INVALID_INT;
	public int endDay = INVALID_INT;
	public int endHour = INVALID_INT;
	public int endMinute = INVALID_INT;
	
	/* Location */
	public String location = INVALID_STRING;
	public LatLng coords = INVALID_COORDS;
	
	/* Photos */
	public ArrayList<Integer> photoIds = null;
	public int mainPhotoId = INVALID_INT;
	
	public GarageSale() {
		photoIds = new ArrayList<Integer>();
	}
	
	public GarageSale(JSONArray JSONsale) {
		this();
		try {
			int i = 0;
			id = JSONsale.getInt(i++);
			title = JSONsale.getString(i++);
			description = JSONsale.getString(i++);
			plannerId = JSONsale.getInt(i++);
			startYear = JSONsale.getInt(i++);
			startMonth = JSONsale.getInt(i++);
			startDay = JSONsale.getInt(i++);
			startHour = JSONsale.getInt(i++);
			startMinute = JSONsale.getInt(i++);
			endYear = JSONsale.getInt(i++);
			endMonth = JSONsale.getInt(i++);
			endDay = JSONsale.getInt(i++);
			endHour = JSONsale.getInt(i++);
			endMinute = JSONsale.getInt(i++);
			location = JSONsale.getString(i++);
			double latitude = JSONsale.getDouble(i++);
			double longitude = JSONsale.getDouble(i++);
			coords = new LatLng(latitude, longitude);
			mainPhotoId = JSONsale.getInt(i++);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}		
	}

	public UrlEncodedFormEntity HttpPostEntity() {
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("id",""+id));
		postParameters.add(new BasicNameValuePair("title", title));
		postParameters.add(new BasicNameValuePair("description", description));
		postParameters.add(new BasicNameValuePair("plannerId", ""+plannerId));
		postParameters.add(new BasicNameValuePair("startYear", ""+startYear));
		postParameters.add(new BasicNameValuePair("startMonth", ""+startMonth));
		postParameters.add(new BasicNameValuePair("startDay", ""+startDay));
		postParameters.add(new BasicNameValuePair("startHour", ""+startHour));
		postParameters.add(new BasicNameValuePair("startMinute", ""+startMinute));
		postParameters.add(new BasicNameValuePair("endYear", ""+endYear));
		postParameters.add(new BasicNameValuePair("endMonth", ""+endMonth));
		postParameters.add(new BasicNameValuePair("endDay", ""+endDay));
		postParameters.add(new BasicNameValuePair("endHour", ""+endHour));
		postParameters.add(new BasicNameValuePair("endMinute", ""+endMinute));
		postParameters.add(new BasicNameValuePair("location", location));
		postParameters.add(new BasicNameValuePair("latitude", ""+coords.latitude));
		postParameters.add(new BasicNameValuePair("longitude", ""+coords.longitude));
		postParameters.add(new BasicNameValuePair("mainPhotoId", ""+mainPhotoId));
		try {
			return new UrlEncodedFormEntity(postParameters);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void loadDetailsIntoView(View detailsView, Context context) {
		Calendar startDate = dateTime(true, true);
		Calendar endDate = dateTime(false, true);
		Calendar startTime = dateTime(true, false);
		Calendar endTime = dateTime(false, false);

		/* Title */
		if (!title.equals(INVALID_STRING)) {
			TextView detailsTitleTextView = (TextView) detailsView.findViewById(R.id.DetailsTitleTextView);
			detailsTitleTextView.setText(title);
		}
		/* Time */
		if (startTime != null && endTime != null) {
			TextView detailsTimeTextView = (TextView) detailsView.findViewById(R.id.DetailsTimeTextView);
			detailsTimeTextView.setText(timeString(true) + " - " + timeString(false));
		}
		/* Date */
		TextView detailsDateTextView = (TextView) detailsView.findViewById(R.id.DetailsDateTextView);
		setDateString(detailsDateTextView);
		/* Location */
		if (!location.equals(INVALID_STRING)) {	
			TextView detailsLocationTextView = (TextView) detailsView.findViewById(R.id.DetailsLocationTextView);
			detailsLocationTextView.setText(location);
		}
		/* Description */
		if (!description.equals(INVALID_STRING)) {
			TextView detailsDescriptionTextView = (TextView) detailsView.findViewById(R.id.DetailsDescriptionTextView);
			detailsDescriptionTextView.setText(description);
		}
		/* Main Photo */
		ImageView detailsMainPhotoImageView = (ImageView) detailsView.findViewById(R.id.DetailsMainPhotoImageView);
		detailsMainPhotoImageView.setImageBitmap(mainPhotoBitmap(context));
	}
	
	public String dateString(boolean isStartDate) {
		Calendar date = dateTime(isStartDate, true);
		
		String monthString = date.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
		String weekdayString = date.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US);
		int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);
		int year = date.get(Calendar.YEAR);
		String dateString = weekdayString + ", " + monthString + " " + dayOfMonth;
		return dateString;
	}
	
	public void setDateString(TextView dateTextView) {
		Calendar startDate = dateTime(true, true);
		Calendar endDate = dateTime(false, true);
		if (startDate != null && endDate != null) {
			if  (startDate.equals(endDate)) {
				dateTextView.setText(dateString(true));
			} else {
				dateTextView.setText(dateString(true) + " - " + dateString(false));
			}
		} else {
			dateTextView.setText(R.string.NoDate);
		}
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
			if (year == INVALID_INT) {
				return null;
			}
			return new GregorianCalendar(year, month, day);
		} else {
			if (hour == INVALID_INT) {
				return null;
			}
		return new GregorianCalendar(INVALID_INT, INVALID_INT, INVALID_INT, hour, minute, 0);
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
		intent.putExtra(DETAILS_ACTIVITY_PARENT_KEY, parentActivity);

		context.startActivity(intent);
	}
	
	public void toggleFollowed(Context context) {
		if (User.currentUser.followedSales.contains(this)) {
			User.currentUser.followedSales.remove(this);
			Toast.makeText(context, "Unfollowed.", Toast.LENGTH_SHORT).show();
		} else {
			User.currentUser.followedSales.add(this);
			Toast.makeText(context, "Followed!", Toast.LENGTH_SHORT).show();
		}
		Storage.storeList(context, User.currentUser.followedSales, Storage.FOLLOWED_SALES);
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
		//p = new Photo();
		//p.bitmap = BitmapFactory.decodeResource(context.getResources(),
        //        R.drawable.saleiconone);
		//SALE1.photos.add(p);
		//SALE1.mainPhoto = p;
		SALE1.description = "I'm moving to SF and I need to get rid of some stuff! I'm selling lots of furniture and awesome stuff!";

		/* GarageSale 2 */
		GarageSale SALE2 = new GarageSale();
		LatLng s2_coord = new LatLng(37.877665, -122.25925);
		SALE2.coords = s2_coord;
		SALE2.id = 2020;
		SALE2.title = "Alice's Garage Sale TODAY";
		SALE2.location = "1500 LeRoy St. Berkeley, CA";
		//p = new Photo();
		//p.bitmap = BitmapFactory.decodeResource(context.getResources(),
        //        R.drawable.saleicontwo);
		//SALE2.photos.add(p);
		//SALE2.mainPhoto = p;
		SALE2.description = "Stop by my garage sale! I have antiques and rare items for sale.";

		/* Add to allSales */
		allSales.put(SALE1.id, SALE1);
		allSales.put(SALE2.id, SALE2);
	}
	
	public Bitmap mainPhotoBitmap(Context context) {
		Photo mainPhoto = Photo.allPhotos.get(mainPhotoId);
		if (mainPhoto != null) {
			return mainPhoto.bitmap;
		}
		return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
	}
	
	/* Returns null if any of the photoIds are not yet in allPhotos */
	public ArrayList<Photo> photos() {
		ArrayList<Photo> photos = new ArrayList<Photo>();
		for (int photoId : photoIds) {
			Photo p  = Photo.allPhotos.get(photoId);
			if (p == null) {
				return null;
			}
			photos.add(p);
		}
		return photos;		
	}
	
	public static HashSet<Integer> mainPhotoIds() {
		HashSet<Integer> mainPhotoIds = new HashSet<Integer>();
		for (GarageSale sale : GarageSale.allSales.values()) {
			mainPhotoIds.add(sale.mainPhotoId);
		}
		mainPhotoIds.remove(GarageSale.INVALID_INT);
		return mainPhotoIds;
	}
	
}
