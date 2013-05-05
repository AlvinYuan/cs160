package edu.berkeley.cs160.gsale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class CreateEditActivity extends Activity implements OnItemClickListener {

	public GarageSaleAdapter mySalesAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_edit);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		/* ListView */
		ListView l = (ListView) findViewById(R.id.MySalesListView);
		mySalesAdapter = new GarageSaleAdapter(this, android.R.layout.simple_list_item_1, User.currentUser.plannedSales, true);
		
		// What I am attempting to add
		View header = (View)getLayoutInflater().inflate(R.layout.listview_header_row, null);
        l.addHeaderView(header);
        
        //End of what I am attempting to add for header
        
		l.setAdapter(mySalesAdapter);
		
		l.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_create_edit, menu);
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
	 * Method: CreateNewSaleButtonOnClick
	 */
	public void CreateNewSaleButtonOnClick(View view) {
		CreateEditSale(false, null);
	}
	
	public void CreateEditSale(boolean isEditing, GarageSale editingSale) {
		Intent intent = new Intent(this, EditSaleActivity.class);
		intent.putExtra(GarageSale.HAS_SALE_ID_KEY, isEditing);
		if (isEditing) {
			GarageSale.allSales.put(editingSale.id, editingSale);
			intent.putExtra(GarageSale.SALE_ID_KEY, editingSale.id);
		}
		startActivity(intent);
	}

	/*
	 * AdapterView.OnItemClickListener Interface
	 * Specifically for @+id/MySalesListView
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		GarageSale editingSale = (GarageSale) mySalesAdapter.getItem(position);
		CreateEditSale(true, editingSale);
	}
	
	/*
	 * Activity Override
	 */
	public void onResume() {
		super.onResume();
		mySalesAdapter.notifyDataSetChanged();
	}
	
}
