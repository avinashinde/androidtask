package com.demo.androidtask.content;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

public class CreateOrUpgradeDbTask extends AsyncTask<Void, Void, Boolean> {

	private DbTaskDelegate delegate;
	private Context context;

	public CreateOrUpgradeDbTask(DbTaskDelegate delegate, Context context) {
		this.delegate = delegate;
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		DbHelper.init(context.getApplicationContext());

		try {
			SQLiteDatabase db = DbHelper.sharedDbHelper().getWritableDatabase();
			db.close();
			return true;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			Logger.error("CreateOrUpgradeDbTask.doInBackground Exception Occured: "
					+ sqle.getMessage());
			return false;
		}
	}

	@Override
	protected void onPostExecute(Boolean result) {
		delegate.dbTaskCompletedWithResult(result);
	}

	public static interface DbTaskDelegate {
		public void dbTaskCompletedWithResult(Boolean result);
	}

}
