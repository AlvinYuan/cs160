package edu.berkeley.cs160.gsale;

import java.util.ArrayList;

public class User {
	public static int largestExistingUserId = 0; //Should be replaced with database User creation
	public ArrayList<GarageSale> followedSales;
	public ArrayList<GarageSale> plannedSales;
	public int id; //Unique Identifier
		
	public User() {
		id = User.largestExistingUserId++;
		followedSales = new ArrayList<GarageSale>();
		plannedSales = new ArrayList<GarageSale>();
	}
}
