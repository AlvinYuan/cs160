package edu.berkeley.cs160.gsale;

import java.util.ArrayList;

public class User {
	public static final int NOT_LOGGED_IN = -1;
	public static final String GUEST = "guest";
	public static User currentUser = null;
	public static boolean justStartedApp = true; //set to false in HomeActivity

	/* Fields */
	public int id; //Unique Identifier
	public String email;
	public ArrayList<GarageSale> followedSales;
	public ArrayList<GarageSale> plannedSales;
	public ArrayList<GarageSale> hiddenSales;
	
	public User() {
		// Default = Guest User
		id = NOT_LOGGED_IN;
		email = GUEST;
		followedSales = new ArrayList<GarageSale>();
		plannedSales = new ArrayList<GarageSale>();
		hiddenSales = new ArrayList<GarageSale>();
	}
	
	public String toString() {
		return id + "," + email;
	}
}
