package com.demo.androidtask;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.androidtask.imagedownload.ImageDownloadModel;
import com.demo.androidtask.imagedownload.ImageLoadAsyncTaskListDelegate;
import com.demo.androidtask.imagedownload.ImageLoadAsynkTask;
import com.demo.androidtask.models.ListModel;

public class DetailActivity extends Activity implements ImageLoadAsyncTaskListDelegate, OnClickListener {

	private ListModel listModel;
	private ImageView imgPlace;
	private TextView txtPlaceName;
	private TextView txtIsOpen;
	private TextView txtAddress;
	private Button btnAddTOFav;
	private Button btnShowOnMap;
	private RatingBar ratingBar;

	String url = "https://maps.googleapis.com/maps/api/place/photo?";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		
		overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_right);

		imgPlace = (ImageView) findViewById(R.id.img_place);
		txtPlaceName = (TextView) findViewById(R.id.txt_place_name);
		txtIsOpen = (TextView) findViewById(R.id.txt_is_open);
		txtAddress = (TextView) findViewById(R.id.txt_address);
		btnAddTOFav = (Button) findViewById(R.id.btn_fav);
		btnShowOnMap = (Button) findViewById(R.id.btn_loc);
		ratingBar = (RatingBar) findViewById(R.id.ratingBar);

		btnAddTOFav.setOnClickListener(this);
		btnShowOnMap.setOnClickListener(this);

		listModel = getIntent().getParcelableExtra("parcel");

		downloadImage();
		setData();

	}

	private void setData() {
		txtPlaceName.setText(listModel.getName());
		txtIsOpen.setText(Boolean.valueOf(listModel.isOpen()) ? "Is Open Now" : "Is Closed Now");
		txtAddress.setText(listModel.getVicinity());
		ratingBar.setRating(Float.valueOf(listModel.getRating()));
	}

	private void downloadImage() {

		url += "maxwidth=400&photoreference=" + listModel.getReference() + "&key=AIzaSyCAKwmpQGU_NO9QJhsrP6zj2SOyKEV2IHs";

		ImageDownloadModel downloadModel = new ImageDownloadModel();
		downloadModel.setImageUrl(url);
		downloadModel.setImageLocalPath(MainActivity.DIR + listModel.getId() + ".png");

		new ImageLoadAsynkTask(this, downloadModel, MainActivity.DIR, this).execute();
	}

	@Override
	public void imageLoadAsyncTaskListUpdates(String localPath) {
		// TODO Auto-generated method stub

	}

	@Override
	public void imageLoadAsyncTaskListOnComplete() {
		imgPlace.setImageURI(Uri.parse(MainActivity.DIR + listModel.getId() + ".png"));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_loc:
			Intent intent = new Intent(DetailActivity.this, MapActivity.class);
			intent.putExtra("lat", listModel.getLat());
			intent.putExtra("lon", listModel.getLon());
			intent.putExtra("name", listModel.getName());

			startActivity(intent);
			overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);

			break;

		case R.id.btn_fav:
			ListModel.insertFavorite(listModel);
			Toast.makeText(DetailActivity.this, "Added to Favorite", Toast.LENGTH_SHORT).show();

		default:
			break;
		}
	}
}
