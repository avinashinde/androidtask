package com.demo.androidtask;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

	private double lat;
	private double lon;
	private String name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_right);

		lat = Double.valueOf(getIntent().getStringExtra("lat"));
		lon = Double.valueOf(getIntent().getStringExtra("lon"));
		name = getIntent().getStringExtra("name");

		MapFragment mapFragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		googleMap.addMarker(new MarkerOptions()
				.position(new LatLng(lat, lon))
				.title(name));
		
		googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 15));

	}
}
