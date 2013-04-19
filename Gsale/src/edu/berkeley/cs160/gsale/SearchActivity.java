package edu.berkeley.cs160.gsale;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.NavUtils;

public class SearchActivity extends Activity implements OnItemClickListener {
	public GarageSaleAdapter SearchSalesAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		/* ListView */
		ListView l = (ListView) findViewById(R.id.SearchSalesListView);
		SearchSalesAdapter = new GarageSaleAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<GarageSale>(GarageSale.allSales.values()), false);
		l.setAdapter(SearchSalesAdapter);
		l.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_search, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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
		}
		return super.onOptionsItemSelected(item);
	}

	/*
	 * Activity Override
	 */
	public void onResume() {
		super.onResume();
		SearchSalesAdapter.notifyDataSetChanged();
	}

	/*
	 * AdapterView.OnItemClickListener Interface
	 * Specifically for @+id/MySalesListView
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		GarageSale sale = (GarageSale) SearchSalesAdapter.getItem(position);
		sale.startDetailsActivity(this);
	}
}
