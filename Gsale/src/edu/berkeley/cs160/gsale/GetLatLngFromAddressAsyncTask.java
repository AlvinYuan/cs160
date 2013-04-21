package edu.berkeley.cs160.gsale;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class GetLatLngFromAddressAsyncTask extends AsyncTask<Void, Void, JSONObject> {
	public Context context;
	public GarageSale sale;

	public GetLatLngFromAddressAsyncTask(Context context, GarageSale sale) {
		super();
		this.context = context;
		this.sale = sale;
	}

	protected JSONObject doInBackground(Void... params) {
		JSONObject jsonObject = null;
		/* Prepare Request */
		try {
		HttpGet httpGet = new HttpGet(
				"http://maps.google.com/maps/api/geocode/json?address="
						+ sale.location + "&sensor=false");
		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		StringBuilder stringBuilder = new StringBuilder();
		response = client.execute(httpGet);
		HttpEntity entity = response.getEntity();
		InputStream stream = entity.getContent();
		int b;
		while ((b = stream.read()) != -1) {
			stringBuilder.append((char) b);
		}
		jsonObject = new JSONObject(stringBuilder.toString());
		
		} catch (Exception e) {
			System.out.println("PostSaleAsyncTask: " + e.toString());
		}
		return jsonObject;
	}

	protected void onPostExecute(JSONObject result) {
		LatLng coordinates = null;
		double lng = (Double) null;
		try {
			lng = ((JSONArray) result.get("results")).getJSONObject(0)
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lng");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		double lat = (Double) null;
		try {
			lat = ((JSONArray) result.get("results")).getJSONObject(0)
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lat");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		coordinates = new LatLng(lat, lng);
		sale.coords = coordinates;
		Log.i("latitude", lat + "");
		Log.i("longitude", lng + "");
	}
}
