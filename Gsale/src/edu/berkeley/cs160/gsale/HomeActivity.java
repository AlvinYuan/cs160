package edu.berkeley.cs160.gsale;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class HomeActivity extends Activity implements LocationListener {
	public HomeActivity self;
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
		self = this;
		/* Outside onAppStartup() so currentLocation can be updated even after pressing Up button. */
		LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		String provider = manager.getBestProvider(new Criteria(), true);
		manager.requestLocationUpdates(provider, 0, 0, this);
		onLocationChanged(manager.getLastKnownLocation(provider));
		checkReady();
		
		//Loads default settings options on application startup
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.menu_login:
	            handleLoginRequest();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	/*
	 * handleLoginRequest
	 */
	public void handleLoginRequest() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Log in");
		builder.setMessage("Specify an email");
		final EditText input = new EditText(this);

		builder.setView(input);
		builder.setPositiveButton("Log in", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				LogInAsyncTask logInTask = new LogInAsyncTask(input.getContext(), input.getText().toString());
				logInTask.execute();				
			}
		});
		builder.setNegativeButton("Cancel", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		/* http://stackoverflow.com/questions/2403632/android-show-soft-keyboard-automatically-when-focus-is-on-an-edittext */
		final AlertDialog dialog = builder.create();
		input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		        if (hasFocus) {
		            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		        }
		    }
		});
		dialog.show();
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
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
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
		            //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
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
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	}
	
	/*
	 * Method: SendViewMessagesButtonOnClick
	 */
	public void SendViewMessagesButtonOnClick(View view) {
		Message.startMessagesActivity(this);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	}

	/*
	 * Method: CreateEditSalesButtonOnClick
	 */
	public void CreateEditSalesButtonOnClick(View view) {
		Intent intent = new Intent(this, CreateEditActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	}

	/*
	 * Method: SettingsButtonOnClick
	 */
	public void SettingsButtonOnClick(View view) {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
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
		Storage.getLastLogin(this);
		GarageSale.allSales = new HashMap<Integer, GarageSale>();
		Photo.allPhotos = new HashMap<Integer, Photo>();
		Message.allMessages = new HashMap<Integer, Message>();
		GarageSale.generateAllSales(this);
		GetAllSalesAsyncTask getSalesTask = new GetAllSalesAsyncTask(this);
		getSalesTask.execute();
		GetAllMessagesAsyncTask getMessagesTask = new GetAllMessagesAsyncTask(this, true);
		getMessagesTask.execute();
		/*
		Timer repeat = new Timer("Repeating Get Messages Task");
		repeat.schedule(new TimerTask() {

			@Override
			public void run() {
				GetAllMessagesAsyncTask getMessagesTask = new GetAllMessagesAsyncTask(self, false);
				getMessagesTask.execute();
			}
			
		}, 0, 60000); // 1 minute repeating
		*/
	}
	
	/* Checks if sales, mainPhotos, and messages have been retrieved */
	public void checkReady() {
		TextView initialLoadingTextView = (TextView) findViewById(R.id.InitialLoadingTextView);
		if (GarageSale.salesLoaded) {
			if (Photo.mainPhotosLoaded || GarageSale.mainPhotoIds().size() == 0) {
				if (Message.messagesLoaded) {
					/* all ready */
					((ProgressBar) findViewById(R.id.InitialLoadingProgressBar)).setVisibility(View.INVISIBLE);
					((ImageView) findViewById(R.id.InitialLoadingImageView)).setVisibility(View.INVISIBLE);
					initialLoadingTextView.setVisibility(View.INVISIBLE);

					((LinearLayout) findViewById(R.id.MainHomeLayout)).setVisibility(View.VISIBLE);
					((ImageView) findViewById(R.id.MainHomeImageView)).setVisibility(View.VISIBLE);
				} else {
					initialLoadingTextView.setText("Loading messages...");
				}
			} else {
				initialLoadingTextView.setText("Loading main photos...");				
			}
		} else {
			initialLoadingTextView.setText("Loading sales...");
		}			
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
