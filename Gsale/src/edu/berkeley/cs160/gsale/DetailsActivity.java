package edu.berkeley.cs160.gsale;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class DetailsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		Bundle extras = this.getIntent().getExtras();
		int id = extras.getInt("SALE_ID");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.details, menu);
		return true;
	}

}
