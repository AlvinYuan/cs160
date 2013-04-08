package edu.berkeley.cs160.gsale;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
	static final GarageSale SALE1 = new GarageSale();
	static final GarageSale SALE2 = new GarageSale();
	static final LatLng s1_coord = new LatLng(37.875192, -122.266932);
	static final LatLng s2_coord = new LatLng(37.877665, -122.25925);
	private GoogleMap map;
	public HashMap<String, GarageSale> MarkerIdMap = new HashMap<String, GarageSale>();

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

		
		SALE1.coords = s1_coord;
		SALE1.id = 1010;
		SALE1.title = "Bob's Moving Sale!!";
		SALE1.location = "1780 Spruce St. Berkeley, CA";
		SALE1.image = R.drawable.saleiconone;
		SALE1.description = "I'm moving to SF and I need to get rid of some stuff! I'm selling lots of furniture and awesome stuff!";

		SALE2.coords = s2_coord;
		SALE2.id = 2020;
		SALE2.title = "Alice's Garage Sale TODAY";
		SALE2.location = "1500 LeRoy St. Berkeley, CA";
		SALE2.image = R.drawable.saleicontwo;
		SALE2.description = "Stop by my garage sale! I have antiques and rare items for sale.";

		Marker sale1 = map.addMarker(new MarkerOptions().position(SALE1.coords)
				.title(SALE1.title).snippet(SALE1.location));
		Marker sale2 = map.addMarker(new MarkerOptions().position(SALE2.coords)
				.title(SALE2.title).snippet(SALE2.location));

		MarkerIdMap.put(sale1.getId(), SALE1);
		MarkerIdMap.put(sale2.getId(), SALE2);

		GarageSale.mapIdToSale.put(SALE1.id, SALE1);
		GarageSale.mapIdToSale.put(SALE2.id, SALE2);

		// Move the camera instantly to soda with a zoom of 15.
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(s1_coord, 15));

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

				tvImage.setImageResource(MarkerIdMap.get(marker.getId()).image);

				// Returning the view containing InfoWindow contents
				return v;

			}
		});

		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			public void onInfoWindowClick(Marker marker) {
				int gsaleId = MarkerIdMap.get(marker.getId()).id;
				Intent intent = new Intent(MapActivity.this, DetailsActivity.class);
				intent.putExtra(GarageSale.SALE_ID_KEY, gsaleId);
				startActivity(intent);
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
		// int gsaleId = MarkerIdMap.get(marker.getId());
		// Intent intent = new Intent(this, DetailsActivity.class);
		// intent.putExtra(GarageSale.SALE_ID_KEY, gsaleId);
		// startActivity(intent);
		//Toast.makeText(getApplicationContext(), "this onclick is working",
				//Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		// TODO Auto-generated method stub
		marker.showInfoWindow();

		return true;
	}

}
