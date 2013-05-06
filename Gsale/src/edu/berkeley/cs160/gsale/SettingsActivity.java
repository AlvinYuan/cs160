package edu.berkeley.cs160.gsale;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_settings, menu);
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
	 * Method: UnhideAllSalesButtonOnClick
	 */
	public void UnhideAllSalesButtonOnClick(View view) {
		User.currentUser.hiddenSales.clear();
		Storage.storeList(this, User.currentUser.hiddenSales, Storage.HIDDEN_SALES);
		Toast.makeText(this,"Unhid all sales",Toast.LENGTH_SHORT).show();
	}
	
	public void UnhideAllSalesButtonOnClick() {
		User.currentUser.hiddenSales.clear();
		Storage.storeList(this, User.currentUser.hiddenSales, Storage.HIDDEN_SALES);
		Toast.makeText(this,"Unhid all sales",Toast.LENGTH_SHORT).show();
	}
	
	public void clearData(View view) {
		User.currentUser.plannedSales.clear();
		User.currentUser.followedSales.clear();
		User.currentUser.hiddenSales.clear();
		Storage.clearStorage(this);
		Toast.makeText(this,"Cleared SharedPreferences",Toast.LENGTH_SHORT).show();
	}
	
	public void clearData() {
		User.currentUser.plannedSales.clear();
		User.currentUser.followedSales.clear();
		User.currentUser.hiddenSales.clear();
		Storage.clearStorage(this);
		Toast.makeText(this,"Cleared SharedPreferences",Toast.LENGTH_SHORT).show();
	}
	
	public static class SettingsFragment extends PreferenceFragment {
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        // Load the preferences from an XML resource
	        addPreferencesFromResource(R.xml.preferences);
	        Preference unhide = (Preference)findPreference("unhide_sales");
	        unhide.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
	                        @Override
	                        public boolean onPreferenceClick(Preference arg0) { 
	                            //code for what you want it to do
	                        	SettingsActivity settings = (SettingsActivity)getActivity();
	                        	settings.UnhideAllSalesButtonOnClick();
	                            return true;
	                        }
	                    });
	        Preference clearPreferences = (Preference)findPreference("clear_preferences");
	        clearPreferences.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
	                        @Override
	                        public boolean onPreferenceClick(Preference arg0) { 
	                            //code for what you want it to do
	                        	SettingsActivity settings = (SettingsActivity)getActivity();
	                        	settings.clearData();
	                            return true;
	                        }
	                    });
	    }
	}
}
