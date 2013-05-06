package edu.berkeley.cs160.gsale;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.util.Base64;

public class Photo {
	/* allPhotos maps id to object */
	public static HashMap<Integer, Photo> allPhotos = null;
	public static boolean mainPhotosLoaded = false;
	
	public int id; // Unique Identifier
	public Bitmap bitmap = null;
	public String description = null;
	
	public Photo() {
		description = "";
	}
	
	public Photo(JSONArray JSONphoto) {
		this();
		try {
			int i = 0;
			id = JSONphoto.getInt(i++);
			String encodedImage = JSONphoto.getString(i++);
			byte[] bitmapdata = Base64.decode(encodedImage,Base64.URL_SAFE);
			bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
			description = JSONphoto.getString(i++);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}		
	}

	public static ArrayList<Photo> generatePhotos(Context context) {
		ArrayList<Photo> photos = new ArrayList<Photo>();
		Photo photo = new Photo();
		photo.bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_launcher);
		photo.description = "Photo description";
		photos.add(photo);
		photo = new Photo();
		photo.bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.photo);
		photo.description = "This is a photo. Give me money.";
		photos.add(photo);		
		return photos;
	}
	
	public UrlEncodedFormEntity HttpPostEntity() {
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.PNG, 100, stream);
			byte[] bitmapdata = stream.toByteArray();
			String encodedImage = Base64.encodeToString(bitmapdata, Base64.URL_SAFE);
			System.out.println(encodedImage.substring(0,100));
			stream.flush();
			stream.close();

			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("bitmap", encodedImage));
			postParameters.add(new BasicNameValuePair("description", description));
			return new UrlEncodedFormEntity(postParameters);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void getPhotosFromServer(Context context, Collection<Integer> photoIds) {
		for (int photoId : photoIds) {
			if (Photo.allPhotos.get(photoId) == null) {
				GetPhotoAsyncTask getPhotoTask = new GetPhotoAsyncTask(context);
				getPhotoTask.execute(photoId);				
			}
		}
	}
	
	
	
}
