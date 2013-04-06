package edu.berkeley.cs160.gsale;

import java.util.ArrayList;


import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class MessagesActivity extends Activity {
	public Storage data;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messages);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		GarageSale testSale1 = new GarageSale();
		testSale1.title = "SALE SALE SALE";
		testSale1.id = 123456;
		
		GarageSale testSale2 = new GarageSale();
		testSale2.title = "Sale this Saturday";
		testSale2.id = 98765;
		
		data = new Storage(this);
		data.storeSale(testSale1, testSale1.id);
		data.storeSale(testSale2, testSale2.id);
		
		data.storeId(testSale1.id, Storage.FOLLOWED_SALES);
		data.storeId(testSale2.id, Storage.FOLLOWED_SALES);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_messages, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		TextView result = (TextView)findViewById(R.id.storage_test);
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.store_button1:
			Toast.makeText(this, "You have chosen retrieve.", Toast.LENGTH_SHORT).show();
			GarageSale temp = data.getSale(123456);
			result.append(temp.title + "\n");
			return true;
		case R.id.store_button2:
			Toast.makeText(this, "You have chosen list.", Toast.LENGTH_SHORT).show();
			result.setText("");
			ArrayList<GarageSale> tempSales = data.getSales(Storage.FOLLOWED_SALES);
			for(GarageSale g : tempSales) {
				result.append(g.title + "\n");
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void testList(View view) {
		TextView result = (TextView)findViewById(R.id.storage_test);
		result.setText("");
		ArrayList<Integer> test = data.getIds(Storage.FOLLOWED_SALES);
		
		ArrayList<GarageSale> tempSales = data.getSales(Storage.FOLLOWED_SALES);
		for(GarageSale g : tempSales) {
			result.append(g.title + "\n");
		}
	}
	
	public void testRetrieve(View view) {
		TextView result = (TextView)findViewById(R.id.storage_test);
		GarageSale temp = data.getSale(123456);
		result.append(temp.title + "\n");
	}
}