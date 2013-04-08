package edu.berkeley.cs160.gsale;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Activity implements OnMarkerClickListener,
		OnInfoWindowClickListener {
	static final LatLng SALE1 = new LatLng(37.875192, -122.266932);
	static final LatLng SALE2 = new LatLng(37.877665,-122.25925);
	private GoogleMap map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		UiSettings settings = map.getUiSettings();
		map.setMyLocationEnabled(true);
		settings.setMyLocationButtonEnabled(true);


		Marker sale1 = map.addMarker(new MarkerOptions().position(SALE1).title("Bob's Moving Sale!!").snippet("1780 Spruce St. 10am-2pm"));
		Marker sale2 = map.addMarker(new MarkerOptions().position(SALE2).title("Alice's Garage Sale TODAY").snippet("1500 LeRoy St. 9am-4pm"));


		// Move the camera instantly to soda with a zoom of 15.
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(SALE1, 15));

		// Zoom in, animating the camera.
		map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

		map.setInfoWindowAdapter(new InfoWindowAdapter() {

			// Use default InfoWindow frame
			@Override
			public View getInfoWindow(Marker marker) {
				return null;
			}

			// Defines the contents of the InfoWindow
			@Override
			public View getInfoContents(Marker marker) {

				// Getting view from the layout file info_window_layout
				View v = getLayoutInflater().inflate(
						R.layout.custom_infowindow_contents, null);

				// Getting the position from the marker
				LatLng latLng = marker.getPosition();

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
				
				tvImage.setImageResource(R.drawable.saleiconone);

				// Returning the view containing InfoWindow contents
				return v;

			}
		});

		map.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng arg0) {
				// Clears any existing markers from the GoogleMap
				
			}
		});

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

	@Override
	public void onInfoWindowClick(Marker marker) {
		Intent intent = new Intent(this, DetailsActivity.class);
		startActivity(intent);

	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		// TODO Auto-generated method stub
		marker.showInfoWindow();

		return true;
	}

}
