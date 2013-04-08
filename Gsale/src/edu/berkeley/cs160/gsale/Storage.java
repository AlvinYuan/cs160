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


//Don't call the methods in this class, 
public class Storage {
	public static String FOLLOWED_SALES = "followed";
	public static String PLANNED_SALES = "planned";
	public static String OTHER_SALES = "other";
	public static final String PREFS_NAME = "MyPrefsFile";
	public Context context;
	
	public Storage(Context newContext){
		context = newContext;
	}
	
	//For storing the ID of a sale. Used when following a sale or when creating a new sale 
	//For example, you should call this when you click a "follow" button and call storeID("12335", FOLLOWED_SALES)
	//toAdd is the ID that you want to store, and key is the list that it is part of (followed sales or planned sales)
	public void storeId(int id, String key){ 
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
	public ArrayList<Integer> getIds(String key){
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String followedSales = prefs.getString(key, "");
        System.out.println("The followedSales is currently: " + followedSales);
		ArrayList<String> stringOfIds =  new ArrayList<String>(Arrays.asList(followedSales.split(",")));
		System.out.println("The ID list String has been split. The length of the arraylist is " + stringOfIds.size());
		System.out.println("222The followedSales is currently: " + followedSales);
		for (String s : stringOfIds) {
			System.out.println("A String is: " + s);
		}
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
	public void storeSale(GarageSale toAdd, int id) {
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
	public GarageSale getSale(int id) {
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
	public ArrayList<GarageSale> getSales(String key) {
		ArrayList<Integer> saleIds = getIds(key);
		System.out.println("Got ids");

		ArrayList<GarageSale> mySales = new ArrayList<GarageSale>();
		for(int id : saleIds) {
			System.out.println("Getting sale with id: " + id);
			mySales.add(getSale(id));
			System.out.println("Got the sale");
			System.out.println("The Garage sale is called: " + getSale(id).title);
		}
		System.out.println("The size of mySales is: " + mySales.size());
		return mySales;
	}
	
	public void clearStorage(){
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor ed = prefs.edit();
	    ed.clear();
	    ed.commit();
	    System.out.println("SharedPreferences are cleared");
	}
}