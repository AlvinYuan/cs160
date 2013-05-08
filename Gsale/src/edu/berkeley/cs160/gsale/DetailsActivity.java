package edu.berkeley.cs160.gsale;

import java.util.HashMap;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DetailsActivity extends Activity implements OnMarkerClickListener,
		OnInfoWindowClickListener, InfoWindowAdapter, OnMapClickListener {
	public GarageSale sale;
	public String parentActivity;

	public View detailsView;
	public Button followButton;
	public Button hideButton;

	public GoogleMap mapD;
	public HashMap<String, GarageSale> MarkerIdMapDetails = new HashMap<String, GarageSale>();
	public static Marker sourceMarkerDetails = null;
	public boolean hasFocusedSaleDetails;
	public GarageSale focusedSaleDetails = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		

		Bundle extras = this.getIntent().getExtras();
		int id = extras.getInt(GarageSale.SALE_ID_KEY);
		sale = GarageSale.allSales.get(id);

		parentActivity = extras
				.getString(GarageSale.DETAILS_ACTIVITY_PARENT_KEY);

		RelativeLayout detailsLayout = (RelativeLayout) findViewById(R.id.DetailsLayout);
		LayoutInflater inflater = getLayoutInflater();
		detailsView = inflater.inflate(R.layout.garage_sale_details_view, null);
		sale.loadDetailsIntoView(detailsView, this);
		detailsLayout.addView(detailsView);

		followButton = (Button) findViewById(R.id.FollowButton);
		hideButton = (Button) findViewById(R.id.HideButton);
		updateFollowButton();

		
		hasFocusedSaleDetails = extras.getBoolean(GarageSale.HAS_SALE_ID_KEY);
		if (hasFocusedSaleDetails) {
			focusedSaleDetails = sale;
		}
		
		mapD = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.mapDetails)).getMap();
		UiSettings settingsD = mapD.getUiSettings();
		mapD.setMyLocationEnabled(true);
		settingsD.setMyLocationButtonEnabled(true);
		mapD.setInfoWindowAdapter(this);
		mapD.setOnInfoWindowClickListener(this);
		mapD.setOnMapClickListener(this);

		if (!User.currentUser.hiddenSales.contains(sale)
				&& !sale.coords.equals(GarageSale.INVALID_COORDS)) {
			addSaleToMap(sale);
		}
		
		if (hasFocusedSaleDetails && focusedSaleDetails.coords != null) {
			mapD.moveCamera(CameraUpdateFactory.newLatLngZoom(focusedSaleDetails.coords, 15));	
		} else {
			/* Not perfectly centered for some reason, but very close. Good enough I guess. */
			LatLng location = new LatLng(HomeActivity.currentLocation.getLatitude(), HomeActivity.currentLocation.getLongitude());
			mapD.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));			
		}

		// Zoom in, animating the camera.
		mapD.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
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
	 * Method: updateFollowButton Change Text to "Follow" or "Unfollow"
	 */
	public void updateFollowButton() {
		Drawable followDrawable;
		String followText;
		if (User.currentUser.followedSales.contains(sale)) {
			followText = "Unfollow";
			followDrawable = getResources().getDrawable(
					R.drawable.followedbutton);
		} else {
			followText = "Follow";
			followDrawable = getResources().getDrawable(
					R.drawable.unfollowedbutton);
		}
		followButton.setText(followText);
		followButton.setCompoundDrawablesWithIntrinsicBounds(null,
				followDrawable, null, null);
	}

	/*
	 * Method: HideButtonOnClick TODO: Ask for confirmation via pop-up (possibly
	 * inform user can be undone in Settings)
	 */
	public void HideButtonOnClick(View view) {
		final Context self = this;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Confirm Hide");
		builder.setMessage("Are you sure you want to hide this sale?\nIt will not show up in searches or on the map.\nYou can unhide all sales in Settings.");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				User.currentUser.hiddenSales.add(sale);
				Storage.storeList(self, User.currentUser.hiddenSales,
						Storage.HIDDEN_SALES);
				if (User.currentUser.followedSales.remove(sale)) {
					Storage.storeList(self, User.currentUser.followedSales,
							Storage.FOLLOWED_SALES);
				}
				if (parentActivity.equals(GarageSale.MAP_ACTIVITY)) {
					MapActivity.sourceMarker.remove();
				}
				if (parentActivity.equals(GarageSale.SEARCH_ACTIVITY)) {
					SearchActivity.allSalesList.remove(sale);
				}
				finish();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.show();
	}

	/*
	 * Method: ViewOnMapButtonOnClick
	 */
	public void ViewOnMapButtonOnClick(View view) {
		if (parentActivity.equals(GarageSale.MAP_ACTIVITY)) {
			/*
			 * If user presses ViewOnMap from Details screen coming from Map
			 * screen it is better to just close the Details screen than to
			 * start a new Map screen. The old Map screen will already be in the
			 * right state. Closing the Details screen prevents infinite nesting
			 * of Details and Map screens.
			 */
			finish();
			return;
		}
		/* Copied from HomeActivity */
		Intent intent = new Intent(this, MapActivity.class);
		intent.putExtra(GarageSale.HAS_SALE_ID_KEY, true);
		intent.putExtra(GarageSale.SALE_ID_KEY, sale.id);
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());
		Log.i("googleplay", status + "");
		switch (status) {
		case ConnectionResult.SUCCESS:
			startActivity(intent);
			break;

		default:
			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
					requestCode);
			Log.i("errordialog", dialog + "");
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

	/*
	 * Method: MessagesButtonOnClick
	 */
	public void MessagesButtonOnClick(View view) {
		Message.startMessagesActivity(this);
	}

	@Override
	public void onMapClick(LatLng point) {
		// TODO Auto-generated method stub

	}

	@Override
	public View getInfoContents(Marker marker) {
		// Getting view from the layout file info_window_layout
		View v = getLayoutInflater().inflate(
				R.layout.custom_infowindow_contents, null);

		// Getting the position from the marker
		//LatLng latLng = marker.getPosition();

		// Getting reference to the TextView to set title
		TextView tvTitle = (TextView) v.findViewById(R.id.tv_title);

		// Getting reference to the TextView to set details
		TextView tvDetails = (TextView) v.findViewById(R.id.tv_details);

		// Getting reference to the ImageView to set details
		ImageView tvImage = (ImageView) v.findViewById(R.id.tv_image);

		// Setting the title
		if (marker.getTitle() != null) {
			tvTitle.setText(marker.getTitle());
		} else {
			tvTitle.setText(" ");
		}

		// Setting the details
		if (marker.getSnippet() != null) {
			tvDetails.setText(marker.getSnippet());
		} else {
			tvDetails.setText(" ");
		}

		// Setting the image
		tvImage.setImageBitmap(MarkerIdMapDetails.get(marker.getId()).mainPhotoBitmap(this));
		// Returning the view containing InfoWindow contents
		return v;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		return;

	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		marker.showInfoWindow();
		return true;
	}
	
	public void addSaleToMap(GarageSale sale) {
		Marker saleMarker = mapD.addMarker(new MarkerOptions().position(sale.coords)
				.title(sale.title).snippet(sale.location));
		MarkerIdMapDetails.put(saleMarker.getId(), sale);
		if (hasFocusedSaleDetails && sale.equals(focusedSaleDetails)) {
			saleMarker.showInfoWindow();
		}
	}
}
