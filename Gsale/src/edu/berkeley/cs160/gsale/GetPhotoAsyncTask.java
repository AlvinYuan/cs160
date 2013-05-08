package edu.berkeley.cs160.gsale;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import android.content.Context;
import android.os.AsyncTask;

public class GetPhotoAsyncTask extends AsyncTask<Integer, Void, Void> {
	public Context context;

	public GetPhotoAsyncTask(Context context) {
		super();
		this.context = context;
	}
	@Override
	protected Void doInBackground(Integer... params) {
		try {
			/* Prepare Request */
			HttpClient httpclient = new DefaultHttpClient();
			int photoId = params[0];
			HttpGet get = new HttpGet(Server.URL + Server.GET_PHOTO_SUFFIX + "/" + photoId);
			System.out.println("GetPhotoAsyncTask: SENDING GET REQUEST");
			HttpResponse response = httpclient.execute(get);
			
			/* Handle Response */
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				System.out.println("GetPhotoAsyncTask: RECEIVED OK RESPONSE");
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				String responseString = out.toString();
				JSONArray result = new JSONArray(responseString);
				Photo photo = new Photo(result);
				Photo.allPhotos.put(photo.id, photo);
				System.out.println("GetPhotoAsyncTask: GOT PHOTO " + photo.id);
			} else {
				//Closes the connection.
				response.getEntity().getContent().close();
				throw new IOException(response.getStatusLine().getReasonPhrase());
			}
		} catch (Exception e) {
			System.out.println("GetPhotoAsyncTask: " + e.toString());
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		if (context instanceof ViewPhotosActivity) {
			((ViewPhotosActivity) context).photoLoaded();
		}
		if (context instanceof EditSaleActivity) {
			((EditSaleActivity) context).photoLoaded();
		}
		if (context instanceof HomeActivity) {
			if (Photo.allPhotos.size() == GarageSale.mainPhotoIds().size()) {
				Photo.mainPhotosLoaded = true;
				((HomeActivity) context).checkReady();
			}
		}
	}
	
}
