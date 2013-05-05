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

public class PostMessageAsyncTask extends AsyncTask<Void, Void, JSONObject> {
	public Context context;
	public Message message;

	public PostMessageAsyncTask(Context context, Message message) {
		super();
		this.context = context;
		this.message = message;
	}
	@Override
	protected JSONObject doInBackground(Void... params) {
		try {
			/* Prepare Request */
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost post = new HttpPost(Server.URL + Server.POST_MESSAGE_SUFFIX);
			post.setEntity(message.HttpPostEntity());
			System.out.println("PostMessageAsyncTask: SENDING POST REQUEST");
			HttpResponse response = httpclient.execute(post);
			
			/* Handle Response */
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				System.out.println("PostMessageAsyncTask: RECEIVED OK RESPONSE");
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
			System.out.println("PostMessageAsyncTask: " + e.toString());
		}

		return null;
	}

	@Override
	protected void onPostExecute(JSONObject result) {
			try {
				message.id = result.getInt("id");
				Message.allMessages.put(message.id, message);
				System.out.println("PostMessageAsyncTask: ID - " + message.id);
				Toast.makeText(context, "Sent!", Toast.LENGTH_SHORT).show();
				((SendMessageActivity) context).finish();
			} catch (JSONException e) {
				e.printStackTrace();
			}

	}

}