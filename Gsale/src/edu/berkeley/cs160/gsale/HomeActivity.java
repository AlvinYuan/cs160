package edu.berkeley.cs160.gsale;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class HomeActivity extends Activity {
	/*
	 * Do not encode state in this activity.
	 * Pressing the Up button recreates this activity.
	 * Encode state in other classes as static variables.
	 * Example: User.currentUser
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		/*
		 * PROTOTYPING: For now, just set User to new User with id = 0
		 */
		if (User.justStartedApp) {
			onAppStartup();
		}		
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
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
		Log.i("googleplay", status+"");
		    switch (status) {
		        case ConnectionResult.SUCCESS:
		            startActivity(intent);
		            break;

		        default:
		            int requestCode = 10;
		            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
		            Log.i("errordialog", dialog+"");
		            dialog.show();
		            break;
		    }
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
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

	/*
	 * Method: onAppStartup
	 * Runs only once per launching of the app
	 */
	public void onAppStartup() {
		System.out.println("Just Started");
		User.justStartedApp = false;
		User.currentUser = new User();			
		GarageSale.idToSaleMap = new HashMap<Integer, GarageSale>();
		GarageSale.allSales = new ArrayList<GarageSale>();
		GarageSale.generateAllSales(this);
		Storage store = new Storage(this);
		User.currentUser.plannedSales = store.getSales(Storage.PLANNED_SALES);
	}
}
