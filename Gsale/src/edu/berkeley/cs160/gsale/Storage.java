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
        	followedSales.concat(toAdd);        	
        } else {
        	followedSales.concat("," + toAdd);
        }
        
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString(key, followedSales);
        ed.commit();
	}
	
	//For getting the IDs (multiple!) for displaying followed sales or sales that you've created.
	//For example, getting your list of followed sales would be getIDs(FOLLOWED_SALES)
	//key is the list of sales (followed sales or planned sales)
	public ArrayList<Integer> getIds(String key){
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String followedSales = prefs.getString(key, "");
		ArrayList<String> stringOfIds =  new ArrayList(Arrays.asList(followedSales.split(",")));
		ArrayList<Integer> toReturn = new ArrayList<Integer>();
		for(String id : stringOfIds) {
			toReturn.add(Integer.parseInt(id));
		}
		return toReturn;
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
	}
	
	//Get objects by the ID of the garage sale 
	public GarageSale getSale(int id) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		byte[] bytes = prefs.getString(Integer.toString(id), "{}").getBytes();
	    if (bytes.length == 0) {
	        return null;
	    }
	    ByteArrayInputStream byteArray = new ByteArrayInputStream(bytes);
	    Base64InputStream base64InputStream = new Base64InputStream(byteArray, Base64.DEFAULT);
	    ObjectInputStream in;
	    GarageSale myObject = new GarageSale(); 
	    try {
		    in = new ObjectInputStream(base64InputStream);
		    myObject = (GarageSale) in.readObject();
		    in.close();
	    } catch (IOException e) { 
	    } catch (ClassNotFoundException e) {}
	    return myObject;
	}
	
	//Returns an ArrayList of GarageSales
	//key is the list that you want to get (followed sales or planned sales)
	public ArrayList<GarageSale> getSales(String key) {
		ArrayList<Integer> saleList = getIds(key);
		ArrayList<GarageSale> toReturn = new ArrayList<GarageSale>();
		for(int id : saleList) {
			toReturn.add(getSale(id));
		}
		return toReturn;
	}
}