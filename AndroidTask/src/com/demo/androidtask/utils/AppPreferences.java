package com.demo.androidtask.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {
	private static final String PREFERENCE_NAME = "preference";
	private static final String LAT = "lat";
	private static final String LON = "lon";

	private SharedPreferences mPreference;

	public static class Editor {
		private android.content.SharedPreferences.Editor mEditor;

		Editor(SharedPreferences preference) {
			mEditor = preference.edit();
		}

		public Editor setLan(String lat) {
			mEditor.putString(LAT, lat);
			return this;
		}

		public Editor setLon(String lon) {
			mEditor.putString(LON, lon);
			return this;
		}

		public void clearAllPreferences() {
			mEditor.clear().commit();
		}

		public boolean commit() {
			return mEditor.commit();
		}
	}

	public AppPreferences(Context context) {
		mPreference = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
	}

	public String getLat() {
		return mPreference.getString(LAT, null);
	}

	public String getLon() {
		return mPreference.getString(LON, null);
	}

	public void setLat(String lat) {
		new Editor(mPreference).setLan(lat).commit();
	}

	public void setLon(String lon) {
		new Editor(mPreference).setLon(lon).commit();
	}

	public void clearAllPreferences() {
		new Editor(mPreference).clearAllPreferences();
	}
}