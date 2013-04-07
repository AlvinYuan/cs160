package edu.berkeley.cs160.gsale;

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
	public static User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		/*
		 * PROTOTYPING: For now, just set User to new User with id = 0
		 */
		user = new User();
		System.out.println("CUSTOM: USER ID " + user.id);
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
		            finish();
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
		Intent intent = new Intent(this, DummyCamera.class);
		startActivity(intent);
	}


}
