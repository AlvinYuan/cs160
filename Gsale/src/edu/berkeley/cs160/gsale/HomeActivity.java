package edu.berkeley.cs160.gsale;

import java.util.HashMap;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class HomeActivity extends Activity implements LocationListener {
	/*
	 * Do not encode state in this activity.
	 * Pressing the Up button recreates this activity.
	 * Encode state in other classes as static variables.
	 * Example: User.currentUser
	 */
	public static Location currentLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		if (User.justStartedApp) {
			onAppStartup();
		}
		/* Outside onAppStartup() so currentLocation can be updated even after pressing Up button. */
		LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		String provider = manager.getBestProvider(new Criteria(), true);
		manager.requestLocationUpdates(provider, 0, 0, this);
		onLocationChanged(manager.getLastKnownLocation(provider));
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
		intent.putExtra(GarageSale.HAS_SALE_ID_KEY, false);
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
	 * For now, just set User to new User with id = 0
	 */
	public void onAppStartup() {
		System.out.println("Just Started");
		User.justStartedApp = false;
		User.currentUser = new User();			
		GarageSale.allSales = new HashMap<Integer, GarageSale>();
		GarageSale.generateAllSales(this);
		GetAllSalesAsyncTask getTask = new GetAllSalesAsyncTask(this);
		getTask.execute();
		Storage store = new Storage(this);
		User.currentUser.plannedSales = store.getSales(Storage.PLANNED_SALES);
	}

	/*
	 * LocationListener Methods
	 */
	@Override
	public void onLocationChanged(Location location) {
		currentLocation = location;		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}
