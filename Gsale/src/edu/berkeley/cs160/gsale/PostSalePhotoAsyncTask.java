package edu.berkeley.cs160.gsale;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.os.AsyncTask;

public class PostSalePhotoAsyncTask extends AsyncTask<Void, Void, JSONObject> {
	public int saleid;
	public int photoid;

	public PostSalePhotoAsyncTask(int saleid, int photoid) {
		super();
		this.saleid = saleid;
		this.photoid = photoid;
	}
	@Override
	protected JSONObject doInBackground(Void... params) {
		try {
			/* Prepare Request */
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost post = new HttpPost(Server.URL + Server.POST_SALE_PHOTO_SUFFIX);

			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("saleid", ""+saleid));
			postParameters.add(new BasicNameValuePair("photoid",""+photoid));
			post.setEntity(new UrlEncodedFormEntity(postParameters));

			System.out.println("PostSalePhotoAsyncTask: SENDING POST REQUEST");
			HttpResponse response = httpclient.execute(post);
			
			/* Handle Response */
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				System.out.println("PostSalePhotoAsyncTask: RECEIVED OK RESPONSE");
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				String responseString = out.toString();
				JSONObject JSONsale = new JSONObject(responseString);
				return JSONsale;
			} else {
				//Closes the connection.
				response.getEntity().getContent().close();
				throw new IOException(response.getStatusLine().getReasonPhrase());
			}
		} catch (Exception e) {
			System.out.println("PostSalePhotoAsyncTask: " + e.toString());
		}

		return null;
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		System.out.println("PostSalePhotoAsyncTask: Posted SalePhoto");
	}

}