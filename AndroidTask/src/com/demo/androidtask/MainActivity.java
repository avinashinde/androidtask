package com.demo.androidtask;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.demo.androidtask.content.CreateOrUpgradeDbTask;
import com.demo.androidtask.content.CreateOrUpgradeDbTask.DbTaskDelegate;
import com.demo.androidtask.models.ListModel;
import com.demo.androidtask.utils.AppPreferences;
import com.demo.androidtask.utils.FileUtils;
import com.demo.androidtask.utils.GetnearbyLocationTask;
import com.demo.androidtask.utils.GetnearbyLocationTask.NearByLocation;

public class MainActivity extends Activity implements OnItemClickListener, NearByLocation {

	private ListView lvType;
	private String[] typeOfPlaces = { "Favorites", "Food", "Gym", "School", "Hospital", "Spa", "Restaurant" };
	private AppPreferences appPreferences;
	private String url;
	public static String DIR;

	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_right);

		appPreferences = new AppPreferences(this);

		new CreateOrUpgradeDbTask(new DbDelegate(), this.getApplicationContext()).execute();

		LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		LocationListener mlocListener = new MyLocationListener();
		mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);

		lvType = (ListView) findViewById(R.id.lv_places);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, typeOfPlaces);

		lvType.setAdapter(adapter);
		lvType.setOnItemClickListener(this);
	}

	public void processResult(String result) {
		try {
			if (result.equals("No places Found NearyBy")) {
				Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
				return;
			}

			JSONObject jsonObject = new JSONObject(result);
			JSONArray jsonArray = jsonObject.getJSONArray("results");
			ArrayList<ListModel> listModels = new ArrayList<ListModel>();

			DIR = createAlbumDir();

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = jsonArray.getJSONObject(i);

				String url = object.getString("icon");
				String imageLocalPath = FileUtils.generateFileNameFromUrl(object.getString("icon"));
				String placeName = object.getString("name");
				String lat = object.getJSONObject("geometry").getJSONObject("location").getString("lat").toString();
				String lon = object.getJSONObject("geometry").getJSONObject("location").getString("lng").toString();
				String id = object.getString("id");
				boolean isOpen = object.has("opening_hours") ? object.getJSONObject("opening_hours").has("open_now") ? object.getJSONObject("opening_hours").getBoolean("open_now") : false : false;
				int rating = object.has("rating") ? object.getInt("rating") : 0;
				String reference = object.has("photos") ? object.getJSONArray("photos").getJSONObject(0).has("photo_reference") ? object.getJSONArray("photos").getJSONObject(0).getString("photo_reference") : object.getString("reference") : null;
				String vicinity = object.getString("vicinity");

				ListModel listModel = new ListModel();
				listModel.setImageLocalPath(DIR + imageLocalPath);
				listModel.setImageURL(url);
				listModel.setName(placeName);
				listModel.setLat(lat);
				listModel.setLon(lon);
				listModel.setId(id);
				listModel.setOpen(String.valueOf(isOpen));
				listModel.setRating(rating);
				listModel.setReference(reference);
				listModel.setVicinity(vicinity);

				listModels.add(listModel);

			}

			Intent intent = new Intent(MainActivity.this, ListActivity.class);
			intent.putExtra("parcelarray", listModels);
			startActivity(intent);
			overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String createAlbumDir() {
		String folderName = "demo" + "_temp";
		String albumDir = Environment.getExternalStorageDirectory() + File.separator + folderName + File.separator;
		FileUtils.getInstance().createDirIfNotExists(albumDir);
		FileUtils.getInstance().createNoMedia(albumDir);

		return albumDir;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		String type = "";

		switch (position) {
		case 0:
			type = "Favorites";
			DIR = createAlbumDir();
			ArrayList<ListModel> listModels = ListModel.getFavoritesModel();
			
			if (listModels.size() < 1) {
				Toast.makeText(getApplicationContext(), "No favorites added.", Toast.LENGTH_SHORT).show();
				return;
			}

			
			Intent intent = new Intent(MainActivity.this, ListActivity.class);
			intent.putExtra("parcelarray", listModels);
			startActivity(intent);
			overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);
			return;
		case 1:
			type = "food";
			break;
		case 2:
			type = "gym";
			break;
		case 3:
			type = "school";
			break;
		case 4:
			type = "hospital";
			break;
		case 5:
			type = "spa";
			break;
		case 6:
			type = "restaurant";
			break;
		}

		showRadiusDialog(type);

	}

	private void showRadiusDialog(final String type) {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.radius);
		dialog.setTitle("Please enter radius");
		final EditText etRadius = (EditText) dialog.findViewById(R.id.et_radius);
		etRadius.setImeOptions(EditorInfo.IME_ACTION_DONE);
		etRadius.setHint("100 meters");
		etRadius.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_DONE) {

					url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";

					String lat = appPreferences.getLat();
					String lon = appPreferences.getLon();
					String radius = etRadius.getText().toString().trim();
					String types = type;
					String apiKey = "AIzaSyCAKwmpQGU_NO9QJhsrP6zj2SOyKEV2IHs";
					url += String.format("location=%s,%s&radius=%s&types=%s&key=%s", lat, lon, radius, types, apiKey);

					new GetnearbyLocationTask(MainActivity.this, MainActivity.this).execute(url);

					handled = true;
					dialog.dismiss();
				}
				return handled;
			}
		});

		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialogInterface) {
				dialog.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_HIDDEN);
			}
		});

		dialog.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		dialog.show();
	}

	public class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location loc)
		{
			appPreferences.setLat(String.valueOf(loc.getLatitude()));
			appPreferences.setLon(String.valueOf(loc.getLongitude()));

			Log.d(TAG, "Lat : " + loc.getLatitude());
			Log.d(TAG, "Lat : " + loc.getLongitude());

		}

		@Override
		public void onProviderDisabled(String provider)
		{
			Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onProviderEnabled(String provider)
		{
			Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{

		}
	}

	public class DbDelegate implements DbTaskDelegate {

		@Override
		public void dbTaskCompletedWithResult(Boolean result) {
			// TODO Auto-generated method stub

		}

	}

}
