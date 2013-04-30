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
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class LogInAsyncTask extends AsyncTask<Void, Void, JSONObject> {
	public Context context;
	public String email;
	
	public LogInAsyncTask(Context context, String email) {
		super();
		this.context = context;
		this.email = email;
	}
	
	@Override
	protected JSONObject doInBackground(Void... params) {
		try {
			/* Prepare Request */
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost post = new HttpPost(Server.URL + Server.LOG_IN_SUFFIX);
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("email", email));
			post.setEntity(new UrlEncodedFormEntity(postParameters));
			System.out.println("LogInAsyncTask: SENDING POST REQUEST");
			HttpResponse response = httpclient.execute(post);
			
			/* Handle Response */
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				System.out.println("LogInAsyncTask: RECEIVED OK RESPONSE");
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
			System.out.println("LogInAsyncTask: " + e.toString());
		}

		return null;
	}

	@Override
	protected void onPostExecute(JSONObject result) {
			try {
				int id = result.getInt("id");
				boolean isNew = result.getBoolean("new");
				User.currentUser.id = id;
				String toastString;
				if (isNew) {
					toastString = "Created new account " + id;
				} else {
					toastString = "Logged into account " + id;
					
				}
				Toast.makeText(context, toastString, Toast.LENGTH_SHORT).show();
			} catch (JSONException e) {
				e.printStackTrace();
			}

	}
}
