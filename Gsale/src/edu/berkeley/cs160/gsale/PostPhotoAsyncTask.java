package edu.berkeley.cs160.gsale;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class PostPhotoAsyncTask extends AsyncTask<Void, Void, JSONObject> {
	public Context context;
	public Photo photo;

	public PostPhotoAsyncTask(Context context, Photo p) {
		super();
		this.context = context;
		photo = p;
	}
	@Override
	protected JSONObject doInBackground(Void... params) {
		try {
			/* Prepare Request */
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost post = new HttpPost(Server.URL + Server.POST_PHOTO_SUFFIX);
			post.setEntity(photo.HttpPostEntity());
			System.out.println("PostPhotoAsyncTask: SENDING POST REQUEST");
			HttpResponse response = httpclient.execute(post);
			/* Handle Response */
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				System.out.println("PostPhotoAsyncTask: RECEIVED OK RESPONSE");
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				String responseString = out.toString();
				JSONObject JSONphoto = new JSONObject(responseString);
				return JSONphoto;
			} else {
				//Closes the connection.
				response.getEntity().getContent().close();
				throw new IOException(response.getStatusLine().getReasonPhrase());
			}
		} catch (Exception e) {
			System.out.println("PostPhotoAsyncTask: " + e.toString());
		}

		return null;
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		try {
			photo.id = result.getInt("id");
			// Add to allPhotos
			Photo.allPhotos.put(photo.id,photo);
			// Add to editingSale
			EditSaleActivity activity = (EditSaleActivity) context;
			if (activity.editingSale.mainPhotoId == GarageSale.INVALID_INT) {
				activity.editingSale.mainPhotoId = photo.id;				
			}
			activity.editingSale.photoIds.add(photo.id);

			System.out.println("PostPhotoAsyncTask: PHOTO ID - " + photo.id);
			Toast.makeText(context, "Photo Uploaded!", Toast.LENGTH_SHORT).show();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
