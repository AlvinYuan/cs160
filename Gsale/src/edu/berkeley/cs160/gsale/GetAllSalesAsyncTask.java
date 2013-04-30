package edu.berkeley.cs160.gsale;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.os.AsyncTask;

public class GetAllSalesAsyncTask extends AsyncTask<Void, Void, JSONArray> {
	public Context context;

	public GetAllSalesAsyncTask(Context context) {
		super();
		this.context = context;
	}
	@Override
	protected JSONArray doInBackground(Void... params) {
		try {
			/* Prepare Request */
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet get = new HttpGet(Server.URL + Server.GET_ALL_SALES_SUFFIX);
			System.out.println("GetAllSalesAsyncTask: SENDING GET REQUEST");
			HttpResponse response = httpclient.execute(get);
			
			/* Handle Response */
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				System.out.println("GetAllSalesAsyncTask: RECEIVED OK RESPONSE");
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				String responseString = out.toString();
				JSONArray JSONsales = new JSONArray(responseString);
				return JSONsales;
			} else {
				//Closes the connection.
				response.getEntity().getContent().close();
				throw new IOException(response.getStatusLine().getReasonPhrase());
			}
		} catch (Exception e) {
			System.out.println("GetAllSalesAsyncTask: " + e.toString());
		}

		return null;
	}

	@Override
	protected void onPostExecute(JSONArray result) {
			try {
				for (int i = 0; i < result.length(); i++) {
					GarageSale sale = new GarageSale(result.getJSONArray(i));
					GarageSale.allSales.put(sale.id, sale);
					System.out.println("GetAllSalesAsyncTask: GOT SALE " + sale.id);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

	}
}
