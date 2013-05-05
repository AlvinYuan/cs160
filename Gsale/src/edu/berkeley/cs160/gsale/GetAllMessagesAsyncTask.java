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

public class GetAllMessagesAsyncTask extends AsyncTask<Void, Void, JSONArray> {
	public Context context;

	public GetAllMessagesAsyncTask(Context context) {
		super();
		this.context = context;
	}
	@Override
	protected JSONArray doInBackground(Void... params) {
		try {
			/* Prepare Request */
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet get = new HttpGet(Server.URL + Server.GET_ALL_MESSAGES_SUFFIX);
			System.out.println("GetAllMessagesAsyncTask: SENDING GET REQUEST");
			HttpResponse response = httpclient.execute(get);
			
			/* Handle Response */
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				System.out.println("GetAllMessagesAsyncTask: RECEIVED OK RESPONSE");
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
			System.out.println("GetAllMessagesAsyncTask: " + e.toString());
		}

		return null;
	}

	@Override
	protected void onPostExecute(JSONArray result) {
			try {
				for (int i = 0; i < result.length(); i++) {
					Message message = new Message(result.getJSONArray(i));
					Message.allMessages.put(message.id, message);
				}
				System.out.println("GetAllMessagesAsyncTask: GOT " + GarageSale.allSales.size() + " MESSAGES");
			} catch (JSONException e) {
				e.printStackTrace();
			}

	}
}
