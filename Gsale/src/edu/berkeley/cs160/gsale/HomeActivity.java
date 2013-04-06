package edu.berkeley.cs160.gsale;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class HomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}

	/*
	 * Button onClick methods
	 */

	/*
	 * Method: SearchSalesButtonOnClick
	 */
	public void SearchSalesButtonOnClick(View view) {
		Intent intent = new Intent(this, SearchActivity.class);
		startActivity(intent);
	}

	/*
	 * Method: ViewMapButtonOnClick
	 */
	public void ViewMapButtonOnClick(View view) {
		Intent intent = new Intent(this, MapActivity.class);
		startActivity(intent);
	}

	/*
	 * Method: FollowedSalesButtonOnClick
	 */
	public void FollowedSalesButtonOnClick(View view) {
		Intent intent = new Intent(this, FollowedActivity.class);
		startActivity(intent);
	}
	
	/*
	 * Method: SendViewMessagesButtonOnClick
	 */
	public void SendViewMessagesButtonOnClick(View view) {
		Intent intent = new Intent(this, MessagesActivity.class);
		startActivity(intent);
	}

	/*
	 * Method: CreateEditSalesButtonOnClick
	 */
	public void CreateEditSalesButtonOnClick(View view) {
		Intent intent = new Intent(this, CreateEditActivity.class);
		startActivity(intent);
	}

	/*
	 * Method: SettingsButtonOnClick
	 */
	public void SettingsButtonOnClick(View view) {
		Intent intent = new Intent(this, DummyCamera.class);
		startActivity(intent);
	}


}
