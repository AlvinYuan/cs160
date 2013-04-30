package edu.berkeley.cs160.gsale;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Photo {
	public int id; // Unique Identifier
	public GarageSale sale;
	public Bitmap bitmap = null;
	public String description = null;
	
	public Photo() {
		description = "";
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
	
}
