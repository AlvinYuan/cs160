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
import android.widget.ProgressBar;
import android.widget.TextView;

public class ViewPhotosActivity extends Activity implements OnItemClickListener  {
	public GarageSale sale;
	public ListView viewPhotosListView;
	public PhotoAdapter photoAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		System.out.println("Got to ViewPhotosActivity");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_photos);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Bundle extras = this.getIntent().getExtras();
		int id = extras.getInt(GarageSale.SALE_ID_KEY);
		sale = GarageSale.allSales.get(id);
		
		Photo.getPhotosFromServer(this, sale.photoIds);
		/* Further set up happens in photoLoaded called by GetPhotoAsyncTask */
		// Also call photoLoaded here in case all photos are already loaded
		photoLoaded();
	}
	
	/* Called when a photo has been pulled from the server */
	public void photoLoaded() {
		if (sale.photos() == null) {
			return;
		}
		/* All photos are loaded into running program */
		((ProgressBar) findViewById(R.id.LoadingPhotosProgressBar)).setVisibility(View.INVISIBLE);
		((TextView) findViewById(R.id.LoadingPhotosTextView)).setVisibility(View.INVISIBLE);
		viewPhotosListView = (ListView) findViewById(R.id.ViewPhotosListView);
		photoAdapter = new PhotoAdapter(this, android.R.layout.simple_list_item_1, sale.photos());
		viewPhotosListView.setAdapter(photoAdapter);
		viewPhotosListView.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_view_photos, menu);
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
	 * AdapterView.OnItemClickListener Interface
	 * Specifically for @+id/MySalesListView
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		//Photo selectedPhoto = (Photo) photoAdapter.getItem(position);
		//CreateEditSale(true, editingSale);
		System.out.println("Got here");
		Intent i = new Intent(this, PhotoPreviewActivity.class);
        i.putExtra("image", photoAdapter.getItem(position).bitmap);
        i.putExtra("description", photoAdapter.getItem(position).description);
        startActivity(i);
		
	}
}
