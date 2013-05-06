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
import android.widget.Toast;

public class GetAllMessagesAsyncTask extends AsyncTask<Void, Void, JSONArray> {
	public Context context;
	public boolean firstRun;

	public GetAllMessagesAsyncTask(Context context, boolean firstRun) {
		super();
		this.context = context;
		this.firstRun = firstRun;
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
				boolean gotNewMessage = false;
				for (int i = 0; i < result.length(); i++) {
					Message message = new Message(result.getJSONArray(i));
					if (Message.allMessages.get(message.id) == null) {
						gotNewMessage = true;
						Message.allMessages.put(message.id, message);
					}
				}
				if (!firstRun && gotNewMessage) {
					Toast.makeText(context, "Got new Messages!", Toast.LENGTH_SHORT).show();
				}
				System.out.println("GetAllMessagesAsyncTask: GOT " + GarageSale.allSales.size() + " MESSAGES");

				Message.messagesLoaded = true;
				if (context instanceof HomeActivity) {
					((HomeActivity) context).checkReady();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
	}
}
