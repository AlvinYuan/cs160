package edu.berkeley.cs160.gsale;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

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
		
	}
	
	// Testing
	public static GarageSale[] generateSales() {
		GarageSale sales[] = {new GarageSale(), new GarageSale()};
		sales[0].title = "Sale this saturday!";
		sales[1].title = "Moving Sale - Sunday!";
		return sales;
	}
	
}
