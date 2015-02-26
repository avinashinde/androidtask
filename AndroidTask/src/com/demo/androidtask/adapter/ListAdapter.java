package com.demo.androidtask.adapter;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.demo.androidtask.DetailActivity;
import com.demo.androidtask.ListActivity;
import com.demo.androidtask.R;
import com.demo.androidtask.models.ListModel;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

	private ArrayList<ListModel> listModels;
	private Activity activity;

	public ListAdapter(ArrayList<ListModel> listModels, Activity activity) {
		super();
		this.listModels = listModels;
		this.activity = activity;
	}

	class ViewHolder extends RecyclerView.ViewHolder {

		ImageView imgPlace;
		TextView txtPlace;

		public ViewHolder(View v) {
			super(v);
			imgPlace = (ImageView) v.findViewById(R.id.img_place);
			txtPlace = (TextView) v.findViewById(R.id.txt_place);
		}

	}

	public void add(int position, ListModel item) {
		listModels.add(position, item);
		notifyItemInserted(position);
	}

	public void remove(ListModel item) {
		int position = listModels.indexOf(item);
		listModels.remove(position);
		notifyItemRemoved(position);
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return listModels.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {

		if (new File(listModels.get(position).getImageLocalPath()).exists()) {
			holder.imgPlace.setImageURI(Uri.parse(listModels.get(position).getImageLocalPath()));
		}

		holder.txtPlace.setText(listModels.get(position).getName());
		holder.txtPlace.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, DetailActivity.class);
				intent.putExtra("parcel", listModels.get(position));
				activity.startActivity(intent);
				activity.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);
			}
		});

	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
		ViewHolder vh = new ViewHolder(v);
		return vh;
	}

}
