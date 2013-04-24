package edu.berkeley.cs160.gsale;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class PostSaleAsyncTask extends AsyncTask<Void, Void, JSONObject> {
	public Context context;
	public GarageSale sale;

	public PostSaleAsyncTask(Context context, GarageSale sale) {
		super();
		this.context = context;
		this.sale = sale;
	}
	@Override
	protected JSONObject doInBackground(Void... params) {
		try {
			/* Prepare Request */
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost post = new HttpPost(GarageSale.SERVER_URL + GarageSale.POST_SALE_URL_SUFFIX);
			post.setEntity(new UrlEncodedFormEntity(sale.constructPostParameters()));
			System.out.println("PostSaleAsyncTask: SENDING POST REQUEST");
			HttpResponse response = httpclient.execute(post);
			
			/* Handle Response */
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				System.out.println("PostSaleAsyncTask: RECEIVED OK RESPONSE");
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
			System.out.println("PostSaleAsyncTask: " + e.toString());
		}

		return null;
	}

	@Override
	protected void onPostExecute(JSONObject result) {
			int id;
			try {
				Log.i("postJson", result+"");
				id = result.getInt("id");
				sale.id = id;
				GarageSale.allSales.put(sale.id, sale);
				System.out.println("PostSaleAsyncTask: SALE ID - " + sale.id);
				/* TODO: Display some Toast */
			} catch (JSONException e) {
				e.printStackTrace();
			}

	}

}