package edu.berkeley.cs160.gsale;

import java.util.ArrayList;
import java.util.Date;

public class GarageSale {
	public Date startDate;
	public Date stopDate;
	public String title;
	public String description;
	public User planner;
	public ArrayList<Photo> photos;
	public int id; //Unique identifier
	
	public GarageSale() {
		super();
	}
	
	
	// Testing
	public static GarageSale[] generateSales() {
		GarageSale sales[] = {new GarageSale(), new GarageSale()};
		sales[0].title = "Sale this saturday!";
		sales[1].title = "Moving Sale - Sunday!";
		return sales;
	}
}
