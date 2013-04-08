package edu.berkeley.cs160.gsale;

import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;

public class DetailsActivity extends Activity {
	public View detailsView;
	public GarageSale sale;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		Bundle extras = this.getIntent().getExtras();
		int id = extras.getInt(GarageSale.SALE_ID_KEY);
		sale = GarageSale.mapIdToSale.get(id);
		
		RelativeLayout detailsLayout = (RelativeLayout) findViewById(R.id.DetailsLayout);
		LayoutInflater inflater = getLayoutInflater();
		detailsView = inflater.inflate(R.layout.garage_sale_details_view, null);
		sale.loadDetailsIntoView(detailsView);
		detailsLayout.addView(detailsView);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.details, menu);
		return true;
	}

}
