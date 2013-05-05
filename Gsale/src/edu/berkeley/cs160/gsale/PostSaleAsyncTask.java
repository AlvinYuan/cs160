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
			HttpPost post = new HttpPost(Server.URL + Server.POST_SALE_SUFFIX);
			post.setEntity(sale.HttpPostEntity());
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
			try {
				sale.id = result.getInt("id");
				GarageSale.allSales.put(sale.id, sale);
				User.currentUser.plannedSales.add(sale);
				System.out.println("PostSaleAsyncTask: SALE ID - " + sale.id);
				Storage.storeList(context, User.currentUser.plannedSales, Storage.PLANNED_SALES);
				for (int i = 0; i < sale.photoIds.size(); i++) {
					Photo p = Photo.allPhotos.get(sale.photoIds.get(i));
					PostSalePhotoAsyncTask postSalePhotoTask = new PostSalePhotoAsyncTask(sale.id, p.id);
					postSalePhotoTask.execute();
				}
				Toast.makeText(context, "Published!", Toast.LENGTH_SHORT).show();
				((EditSaleActivity) context).finish();
			} catch (JSONException e) {
				e.printStackTrace();
			}

	}

}