package edu.berkeley.cs160.gsale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Photo {
	public Bitmap bitmap;
	public String description;
	
	public static Photo[] generatePhotos(Context context) {
		Photo photos[] = new Photo[2];
		photos[0] = new Photo();
		photos[0].bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_launcher);
		photos[0].description = "Photo description";
		photos[1] = new Photo();
		photos[1].bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.photo);
		photos[1].description = "This is a photo. Give me money.";
		
		return photos;
	}
}
