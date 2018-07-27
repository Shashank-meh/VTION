package io.appice.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataStoreHandler extends SQLiteOpenHelper {

	public static final String COLUMN_ID = "_id";

	public static final String TBL_EVENT_DATA = "eventsData";
	public static final String COL_EVENT_KEY = "key";
	public static final String COL_EVENT_DATA = "data";
	public static final String COL_EVENT_TIME = "time";
	public static final String[] allEventCols = { COLUMN_ID,
			COL_EVENT_KEY, COL_EVENT_DATA, COL_EVENT_TIME };

	// database info
	private static final String DATABASE_NAME = "eventDb.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DB_CREATE_EVENT_TBL = "create table "
			+ TBL_EVENT_DATA + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COL_EVENT_KEY
			+ " text, " + COL_EVENT_DATA + " text, " + COL_EVENT_TIME
			+ " integer );";

	public DataStoreHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DB_CREATE_EVENT_TBL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}