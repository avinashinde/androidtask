package com.demo.androidtask.models;

import java.util.ArrayList;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Parcel;
import android.os.Parcelable;

import com.demo.androidtask.content.AndroidTaskDao;
import com.demo.androidtask.content.DbHelper;

public class ListModel implements Parcelable {

	String imageURL;
	String imageLocalPath;
	String Name;
	String lat;
	String lon;
	String id;
	String isOpen;
	int rating;
	String reference;
	String vicinity;

	public ListModel() {

	}

	public ListModel(Parcel in) {
		imageURL = in.readString();
		imageLocalPath = in.readString();
		Name = in.readString();
		lat = in.readString();
		lon = in.readString();
		id = in.readString();
		isOpen = in.readString();
		rating = in.readInt();
		reference = in.readString();
		vicinity = in.readString();
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String isOpen() {
		return isOpen;
	}

	public void setOpen(String isOpen) {
		this.isOpen = isOpen;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getVicinity() {
		return vicinity;
	}

	public void setVicinity(String vicinity) {
		this.vicinity = vicinity;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public String getImageLocalPath() {
		return imageLocalPath;
	}

	public void setImageLocalPath(String imageLocalPath) {
		this.imageLocalPath = imageLocalPath;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(imageURL);
		parcel.writeString(imageLocalPath);
		parcel.writeString(Name);
		parcel.writeString(lat);
		parcel.writeString(lon);
		parcel.writeString(id);
		parcel.writeString(isOpen);
		parcel.writeInt(rating);
		parcel.writeString(reference);
		parcel.writeString(vicinity);
	}

	public static final Parcelable.Creator<ListModel> CREATOR = new Parcelable.Creator<ListModel>() {
		public ListModel createFromParcel(Parcel in) {
			return new ListModel(in);
		}

		public ListModel[] newArray(int size) {
			return new ListModel[size];
		}
	};

	public static void insertFavorite(ListModel listModel) {
		SQLiteDatabase db = DbHelper.sharedDbHelper().getWritableDatabase();
		try {
			AndroidTaskDao androidTaskDao = new AndroidTaskDao(db);
			androidTaskDao.insertFavorite(listModel);

		} catch (SQLiteException sqLiteException) {
			sqLiteException.printStackTrace();

		} finally {

			db.close();
		}
	}

	public static ArrayList<ListModel> getFavoritesModel() {
		SQLiteDatabase db = DbHelper.sharedDbHelper().getWritableDatabase();
		ArrayList<ListModel> listModels = null;
		try {
			AndroidTaskDao androidTaskDao = new AndroidTaskDao(db);
			listModels = androidTaskDao.getFavoritesModel();
		} catch (SQLiteException sqLiteException) {
			sqLiteException.printStackTrace();

		} finally {

			db.close();
		}
		return listModels;
	}

}
