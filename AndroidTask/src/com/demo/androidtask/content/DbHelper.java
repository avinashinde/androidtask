package com.demo.androidtask.content;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

	public static final String DB_NAME = "androidtaskdb";

	public static final int DB_VERSION = 1;

	private static DbHelper dbHelper;

	public Context context;

	private OperationResult migrationResult = new OperationResult(
			OperationResult.OPERATION_SUCCESSFUL);

	public static DbHelper sharedDbHelper() {
		if (dbHelper == null) {
			throw new IllegalStateException(
					"DbHelper is not initialized. Did you forget to call DbHelper.init(Context context) method?");
		}

		return dbHelper;
	}

	public static void init(Context context) {
		dbHelper = new DbHelper(context);
	}

	private DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		DbMigrationHelper migrationHelper = new DbMigrationHelper(db);
		OperationResult result = migrationHelper.migrateToDbVersion(1);
		migrationResult = result;

		int currentRquiredVersion = DB_VERSION;

		if (currentRquiredVersion > 1) {
			result = migrationHelper.upgradeToVersion(1, currentRquiredVersion);
			migrationResult = result;
		}

		if (migrationResult.getResult() == OperationResult.OPERATION_SUCCESSFUL) {
			Logger.info("Successfully created DB with starting version: "
					+ currentRquiredVersion);
		} else {
			Logger.info("Failed to create DB with starting version: "
					+ currentRquiredVersion);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		DbMigrationHelper migrationHelper = new DbMigrationHelper(db);
		OperationResult result = migrationHelper.upgradeToVersion(oldVersion,
				newVersion);
		migrationResult = result;

		if (migrationResult.getResult() == OperationResult.OPERATION_SUCCESSFUL) {
			Logger.info("Successfully migrated DB with from version: "
					+ oldVersion + " to " + newVersion);
		} else {
			Logger.info("Failed to migrated DB with from version: "
					+ oldVersion + " to " + newVersion);
		}
	}

	public OperationResult getMigrationResult() {
		return migrationResult;
	}

}
