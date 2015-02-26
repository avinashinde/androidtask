package com.demo.androidtask;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.demo.androidtask.adapter.ListAdapter;
import com.demo.androidtask.imagedownload.ImageDownloadModel;
import com.demo.androidtask.imagedownload.ImageLoadAsyncTaskListDelegate;
import com.demo.androidtask.imagedownload.ImageLoadAsynkTask;
import com.demo.androidtask.models.ListModel;
import com.demo.androidtask.utils.FileUtils;

public class ListActivity extends Activity implements ImageLoadAsyncTaskListDelegate {

	private RecyclerView lvPlaces;
	private RecyclerView.Adapter listAdapter;
	private ArrayList<ListModel> listModels;
	private LinearLayoutManager mLayoutManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		
		overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_right);

		lvPlaces = (RecyclerView) findViewById(R.id.lv_details);
		lvPlaces.setHasFixedSize(true);

		mLayoutManager = new LinearLayoutManager(this);
		lvPlaces.setLayoutManager(mLayoutManager);

		listModels = getIntent().getParcelableArrayListExtra("parcelarray");

		listAdapter = new ListAdapter(listModels, this);

		lvPlaces.setAdapter(listAdapter);

		processResult();

	}

	public void processResult() {

		ArrayList<ImageDownloadModel> downloadModels = new ArrayList<ImageDownloadModel>();

		for (ListModel listModel : listModels) {

			ImageDownloadModel downloadModel = new ImageDownloadModel();
			downloadModel.setImageLocalPath(listModel.getImageLocalPath());
			downloadModel.setImageUrl(listModel.getImageURL());
			downloadModels.add(downloadModel);
		}

		new ImageLoadAsynkTask(ListActivity.this, downloadModels, MainActivity.DIR, ListActivity.this).execute();
		listAdapter.notifyDataSetChanged();

	}


	@Override
	public void imageLoadAsyncTaskListUpdates(String localPath) {
		listAdapter.notifyDataSetChanged();
	}

	@Override
	public void imageLoadAsyncTaskListOnComplete() {

	}
}
