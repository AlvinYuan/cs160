package edu.berkeley.cs160.gsale;

import java.util.ArrayList;

public class User {
	public static int largestExistingUserId = 0; //Should be replaced with database User creation
	public ArrayList<GarageSale> followedSales;
	public ArrayList<GarageSale> plannedSales;
	public ArrayList<GarageSale> hiddenSales;
	public int id; //Unique Identifier
	public static User currentUser = null;
	public static boolean justStartedApp = true; //set to false in HomeActivity
		
	public User() {
		id = User.largestExistingUserId++;
		followedSales = new ArrayList<GarageSale>();
		plannedSales = new ArrayList<GarageSale>();
		hiddenSales = new ArrayList<GarageSale>();
	}
}
