package com.demo.androidtask.content;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.demo.androidtask.models.ListModel;

public class AndroidTaskDao extends AbstractDao {

	public AndroidTaskDao(SQLiteDatabase db) {
		this.db = db;
	}

	public boolean insertFavorite(ListModel listModel) {

		// @formatter:off
		String sql = "INSERT INTO " + "Favorites" + " VALUES (" + "\""
				+ listModel.getId() + "\"," + "\""
				+ listModel.getName() + "\"," + "\""
				+ listModel.getImageURL() + "\"," + "\""
				+ listModel.getImageLocalPath() + "\"," + "\""
				+ listModel.getLat() + "\"," + "\""
				+ listModel.getLon() + "\"," + "\""
				+ listModel.isOpen() + "\"," + "\""
				+ listModel.getRating() + "\"," + "\""
				+ listModel.getReference() + "\"," + "\""
				+ listModel.getVicinity() + "\")";

		// @formatter:on

		Logger.debug(sql);

		db.execSQL(sql);

		return true;
	}

	public ArrayList<ListModel> getFavoritesModel() {

		// @formatter:off
		String sql = "SELECT * FROM " + "Favorites";

		Logger.debug("sql  " + sql);

		ArrayList<ListModel> listModels = new ArrayList<ListModel>();
		// @formatter:on
		Cursor cursor = db.rawQuery(sql, null);
		
		if (cursor.moveToFirst()) {
			
			do {
				ListModel listModel = new ListModel();
				listModel.setId(cursor.getString(0));
				listModel.setName(cursor.getString(1));
				listModel.setImageURL(cursor.getString(2));;
				listModel.setImageLocalPath(cursor.getString(3));
				listModel.setLat(cursor.getString(4));
				listModel.setLon(cursor.getString(5));
				listModel.setOpen(String.valueOf(cursor.getString(6)));
				listModel.setRating(Integer.valueOf(cursor.getString(7)));
				listModel.setReference(cursor.getString(8));
				listModel.setVicinity(cursor.getString(9));

				listModels.add(listModel);

			} while (cursor.moveToNext());

		}

		return listModels;
	}

}
