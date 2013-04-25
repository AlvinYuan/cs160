package edu.berkeley.cs160.gsale;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class DetailsActivity extends Activity {
	public GarageSale sale;
	public String parentActivity;
	
	public View detailsView;
	public Button followButton;
	public Button hideButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		Bundle extras = this.getIntent().getExtras();
		int id = extras.getInt(GarageSale.SALE_ID_KEY);
		sale = GarageSale.allSales.get(id);

		parentActivity = extras.getString(GarageSale.DETAILS_ACTIVITY_PARENT_KEY);
		
		RelativeLayout detailsLayout = (RelativeLayout) findViewById(R.id.DetailsLayout);
		LayoutInflater inflater = getLayoutInflater();
		detailsView = inflater.inflate(R.layout.garage_sale_details_view, null);
		sale.loadDetailsIntoView(detailsView);
		detailsLayout.addView(detailsView);
		
		followButton = (Button) findViewById(R.id.FollowButton);
		hideButton = (Button) findViewById(R.id.HideButton);
		updateFollowButton();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.details, menu);
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
	 * Method: FollowButtonOnClick
	 */
	public void FollowButtonOnClick(View view) {
		sale.toggleFollowed(this);
		updateFollowButton();
	}
	
	/*
	 * Method: updateFollowButton
	 * Change Text to "Follow" or "Unfollow"
	 */
	public void updateFollowButton() {
		Drawable followDrawable;
		String followText;
		if (User.currentUser.followedSales.contains(sale)) {
			followText = "Unfollow";
			followDrawable = getResources().getDrawable(R.drawable.followed);
		} else {
			followText = "Follow";
			followDrawable = getResources().getDrawable(R.drawable.unfollowed);
		}
		followButton.setText(followText);
		followButton.setCompoundDrawablesWithIntrinsicBounds(null, followDrawable, null, null);
	}

	/*
	 * Method: HideButtonOnClick
	 * TODO: Ask for confirmation via pop-up (possibly inform user can be undone in Settings)
	 */
	public void HideButtonOnClick(View view) {
		if (GarageSale.allSales.remove(sale.id) != null) {
			User.currentUser.hiddenSales.add(sale);
			User.currentUser.followedSales.remove(sale);
			if (parentActivity.equals(GarageSale.MAP_ACTIVITY)) {
				MapActivity.sourceMarker.remove();
			}
			finish();
		}
	}
	
	/*
	 * Method: ViewOnMapButtonOnClick
	 */
	public void ViewOnMapButtonOnClick(View view) {
		if (parentActivity.equals(GarageSale.MAP_ACTIVITY)) {
			/* If user presses ViewOnMap from Details screen coming from Map screen
			 * it is better to just close the Details screen than to start a new Map screen.
			 * The old Map screen will already be in the right state.
			 * Closing the Details screen prevents infinite nesting of Details and Map screens.
			 */
			finish();
			return;
		}
		/* Copied from HomeActivity */
		Intent intent = new Intent(this, MapActivity.class);
		intent.putExtra(GarageSale.HAS_SALE_ID_KEY, true);
		intent.putExtra(GarageSale.SALE_ID_KEY, sale.id);
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
	 * Method: PhotosButtonOnClick
	 */
	public void PhotosButtonOnClick(View view) {
		Intent intent = new Intent(this, ViewPhotosActivity.class);
		intent.putExtra(GarageSale.SALE_ID_KEY, sale.id);
		startActivity(intent);
	}
}
