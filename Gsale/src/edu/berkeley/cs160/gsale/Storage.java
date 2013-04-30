package edu.berkeley.cs160.gsale;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import android.widget.Toast;


//Don't call the methods in this class, 
public class Storage {
	public static final String FOLLOWED_SALES = "followed";
	public static final String PLANNED_SALES = "planned";
	public static final String HIDDEN_SALES = "hidden";
	public static final String OTHER_SALES = "other";
	public static final String LOGIN = "log in";
	public static final String PREFS_NAME = "MyPrefsFile";
	
	/*
	 * Overwrites stored list (does not append).
	 */
	public static void storeList(Context context, ArrayList<GarageSale> sales, String key) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String salesString = "";
        for (int i = 0; i < sales.size(); i++) {
        	salesString += sales.get(i).id;
        	salesString += i != (sales.size() - 1) ? "," : "";
        }
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString(key, salesString);
        ed.commit();
        System.out.println("STORAGE: PUT " + key + " : " + salesString);
	}
	/*
	 * Stores User.currentUser
	 */
	public static void storeLogin(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor ed = prefs.edit();
        String loginString = User.currentUser.toString();
        ed.putString(LOGIN, loginString);
        ed.commit();
        System.out.println("STORAGE: PUT " + LOGIN + " : " + loginString);
	}
	/*
	 * Modifies User.currentUser
	 */
	public static void getLastLogin(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		String loginString = prefs.getString(LOGIN, User.currentUser.toString());
        System.out.println("STORAGE: GET " + LOGIN + " : " + loginString);
		ArrayList<String> fields =  new ArrayList<String>(Arrays.asList(loginString.split(",")));
		User.currentUser.id = Integer.parseInt(fields.get(0));
		User.currentUser.email = fields.get(1);
		Toast.makeText(context, "Logged in as " + User.currentUser.email, Toast.LENGTH_SHORT).show();			
	}
	//For storing the ID of a sale. Used when following a sale or when creating a new sale 
	//For example, you should call this when you click a "follow" button and call storeID("12335", FOLLOWED_SALES)
	//toAdd is the ID that you want to store, and key is the list that it is part of (followed sales or planned sales)
	public static void storeId(Context context, int id, String key){ 
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String followedSales = prefs.getString(key, "");
        String toAdd = Integer.toString(id);
        if (followedSales.length() == 0){
        	followedSales = toAdd;        	
        } else if (!followedSales.contains(toAdd)){
        	followedSales = followedSales + "," + toAdd;
        }
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString(key, followedSales);
        ed.commit();
        System.out.println("The sale id was stored");
	}
	
	//For getting the IDs (multiple!) for displaying followed sales or sales that you've created.
	//For example, getting your list of followed sales would be getIDs(FOLLOWED_SALES)
	//key is the list of sales (followed sales or planned sales)
	public static ArrayList<Integer> getIds(Context context, String key){
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String followedSales = prefs.getString(key, "");
        System.out.println("STORAGE: GET " + key + " : " + followedSales);
		ArrayList<String> stringOfIds =  new ArrayList<String>(Arrays.asList(followedSales.split(",")));
		ArrayList<Integer> saleIds = new ArrayList<Integer>();
		if(!(stringOfIds.size() == 1 && stringOfIds.get(0).length() == 0)){
			for(String id : stringOfIds) {
				saleIds.add(Integer.parseInt(id));
			}
		}
		
		return saleIds;
	}
	
	//Save objects so that the "key" for getString/putString is the ID of the garage sale 
	//toAdd is the GarageSale object you want to store and id is the ID of the sale
	public static void storeSale(Context context, GarageSale toAdd, int id) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
	    SharedPreferences.Editor ed = prefs.edit();
	    ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();

	    ObjectOutputStream objectOutput;
	    try {
	        objectOutput = new ObjectOutputStream(arrayOutputStream);
	        objectOutput.writeObject(toAdd);
	        byte[] data = arrayOutputStream.toByteArray();
	        objectOutput.close();
	        arrayOutputStream.close();

	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        Base64OutputStream b64 = new Base64OutputStream(out, Base64.DEFAULT);
	        b64.write(data);
	        b64.close();
	        out.close();

	        ed.putString(Integer.toString(id), new String(out.toByteArray()));

	        ed.commit();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    System.out.println("The sale was stored");
	}
	
	//Get objects by the ID of the garage sale 
	public static GarageSale getSale(Context context, int id) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		byte[] bytes = prefs.getString(Integer.toString(id), "{}").getBytes();
	    if (bytes.length == 0) {
	        return null;
	    }
	    ObjectInputStream in;
	    GarageSale mySale = new GarageSale();
	    try {
	    	ByteArrayInputStream byteArray = new ByteArrayInputStream(bytes);
	    	Base64InputStream base64InputStream = new Base64InputStream(byteArray, Base64.DEFAULT);
	    	byteArray.close();
	    
		    in = new ObjectInputStream(base64InputStream);
		    base64InputStream.close();
		    mySale = (GarageSale) in.readObject();
		    in.close();
	    } catch (IOException e) { 
	    	e.printStackTrace();
	    } catch (ClassNotFoundException e) {
	    	e.printStackTrace();
	    }
	    System.out.println("The title of the sale is: " + mySale.title);
		System.out.println("The location of the sale is: " + mySale.location);
		System.out.println("The description of the sale is: " + mySale.description);
	    System.out.println("Exiting getSale()");
	    return mySale;
	}
	
	//Returns an ArrayList of GarageSales
	//key is the list that you want to get (followed sales or planned sales)
	public static ArrayList<GarageSale> getSales(Context context, String key) {
		ArrayList<Integer> saleIds = getIds(context, key);
		System.out.println("Got ids");

		ArrayList<GarageSale> mySales = new ArrayList<GarageSale>();
		for(int id : saleIds) {
			System.out.println("Getting sale with id: " + id);
			mySales.add(getSale(context, id));
			System.out.println("Got the sale");
			System.out.println("The Garage sale is called: " + getSale(context, id).title);
		}
		System.out.println("The size of mySales is: " + mySales.size());
		return mySales;
	}
	
	public static void clearStorage(Context context){
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor ed = prefs.edit();
	    ed.clear();
	    ed.commit();
	    System.out.println("SharedPreferences are cleared");
	}
}