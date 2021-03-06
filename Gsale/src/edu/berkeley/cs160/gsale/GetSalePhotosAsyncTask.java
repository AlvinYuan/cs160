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

public class GetSalePhotosAsyncTask extends AsyncTask<Void, Void, Void> {
	public Context context;

	public GetSalePhotosAsyncTask(Context context) {
		super();
		this.context = context;
	}
	@Override
	protected Void doInBackground(Void... params) {
		try {
			/* Prepare Request */
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet get = new HttpGet(Server.URL + Server.GET_SALE_PHOTOS_SUFFIX);
			System.out.println("GetSalePhotosAsyncTask: SENDING GET REQUEST");
			HttpResponse response = httpclient.execute(get);
			
			/* Handle Response */
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				System.out.println("GetSalePhotosAsyncTask: RECEIVED OK RESPONSE");
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				String responseString = out.toString();
				JSONArray result = new JSONArray(responseString);
				for (int i = 0; i < result.length(); i++) {
					JSONArray salePhoto = result.getJSONArray(i);
					GarageSale sale = GarageSale.allSales.get(salePhoto.getInt(0));
					sale.photoIds.add(salePhoto.getInt(1));
				}
				System.out.println("GetSalePhotosAsyncTask: GOT " + result.length() + " SALEPHOTOS");
			} else {
				//Closes the connection.
				response.getEntity().getContent().close();
				throw new IOException(response.getStatusLine().getReasonPhrase());
			}
		} catch (Exception e) {
			System.out.println("GetSalePhotosAsyncTask: " + e.toString());
		}

		return null;
	}

}
