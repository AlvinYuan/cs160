package edu.berkeley.cs160.gsale;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Activity implements OnMarkerClickListener,
		OnInfoWindowClickListener, InfoWindowAdapter, OnMapClickListener {
	private GoogleMap map;
	public HashMap<String, GarageSale> MarkerIdMap = new HashMap<String, GarageSale>();
	public static Marker sourceMarker = null;
	public boolean hasFocusedSale;
	public GarageSale focusedSale = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		Bundle extras = this.getIntent().getExtras();
		hasFocusedSale = extras.getBoolean(GarageSale.HAS_SALE_ID_KEY);
		if (hasFocusedSale) {
			int id = extras.getInt(GarageSale.SALE_ID_KEY);
			focusedSale = GarageSale.idToSaleMap.get(id);
		}
		
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		UiSettings settings = map.getUiSettings();
		map.setMyLocationEnabled(true);
		settings.setMyLocationButtonEnabled(true);
		map.setInfoWindowAdapter(this);
		map.setOnInfoWindowClickListener(this);
		map.setOnMapClickListener(this);

		for (int i = 0; i < GarageSale.allSales.size(); i++) {
			GarageSale sale = GarageSale.allSales.get(i);
			if (sale.coords != null) {
				addSaleToMap(GarageSale.allSales.get(i));				
			}
		}

		// Move the camera instantly to soda with a zoom of 15.
		//TODO: Center camera on current location
		if (hasFocusedSale && focusedSale.coords != null) {
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(focusedSale.coords, 15));	
		} else {
			/* Not perfectly centered for some reason, but very close. Good enough I guess. */
			LatLng location = new LatLng(HomeActivity.currentLocation.getLatitude(), HomeActivity.currentLocation.getLongitude());
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));			
		}

		// Zoom in, animating the camera.
		map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_map, menu);
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
	 * OnInfoWindowClickListener
	 */

	@Override
	public void onInfoWindowClick(Marker marker) {
		GarageSale sale = MarkerIdMap.get(marker.getId());
		sourceMarker = marker;
		sale.startDetailsActivity(this);
	}
	
	/*
	 * OnMarkerClickListener
	 */

	@Override
	public boolean onMarkerClick(Marker marker) {
		marker.showInfoWindow();
		return true;
	}

	public void addSaleToMap(GarageSale sale) {
		Marker saleMarker = map.addMarker(new MarkerOptions().position(sale.coords)
				.title(sale.title).snippet(sale.location));
		MarkerIdMap.put(saleMarker.getId(), sale);
		if (hasFocusedSale && sale.equals(focusedSale)) {
			saleMarker.showInfoWindow();
		}
	}

	/*
	 * InfoWindowAdapter Methods
	 */

	// Defines the contents of the InfoWindow
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
		tvTitle.setText(marker.getTitle());

		// Setting the details
		tvDetails.setText(marker.getSnippet());

		// Setting the image

		tvImage.setImageBitmap(MarkerIdMap.get(marker.getId()).mainPhoto.bitmap);

		// Returning the view containing InfoWindow contents
		return v;

	}
	
	// Use default InfoWindow frame
	@Override
	public View getInfoWindow(Marker marker) {
		return null;
	}

	/*
	 * OnMapClickListener Methods
	 */
	@Override
	public void onMapClick(LatLng point) {
		// Clears any existing markers from the GoogleMap
	}
	
}
