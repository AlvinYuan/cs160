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

public class GetAllPhotosAsyncTask extends AsyncTask<Void, Void, Void> {
	public Context context;

	public GetAllPhotosAsyncTask(Context context) {
		super();
		this.context = context;
	}
	@Override
	protected Void doInBackground(Void... params) {
		try {
			/* Prepare Request */
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet get = new HttpGet(Server.URL + Server.GET_ALL_PHOTOS_SUFFIX);
			System.out.println("GetAllPhotosAsyncTask: SENDING GET REQUEST");
			HttpResponse response = httpclient.execute(get);
			
			/* Handle Response */
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				System.out.println("GetAllPhotosAsyncTask: RECEIVED OK RESPONSE");
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				String responseString = out.toString();
				JSONArray result = new JSONArray(responseString);

				for (int i = 0; i < result.length(); i++) {
					Photo photo = new Photo(result.getJSONArray(i));
					Photo.allPhotos.put(photo.id, photo);
				}
				System.out.println("GetAllPhotosAsyncTask: GOT " + Photo.allPhotos.size() + " PHOTOS");
			} else {
				//Closes the connection.
				response.getEntity().getContent().close();
				throw new IOException(response.getStatusLine().getReasonPhrase());
			}
		} catch (Exception e) {
			System.out.println("GetAllPhotosAsyncTask: " + e.toString());
		}
		return null;
	}
}
