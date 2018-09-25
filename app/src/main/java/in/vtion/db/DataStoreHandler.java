package in.vtion.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataStoreHandler extends SQLiteOpenHelper {

    public static final String RowId = "_id";

    public static final String TABLE_APPDATA = "appdata";

    public static final String Key = "app";
    public static final String Data = "data";
    public static final String Timestamp = "timestamp";
    public static final String[] allColumn = {RowId, Key, Data, Timestamp};

    // database info
    private static final String DATABASE_NAME = "appdata.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE_APPDATA_TBL = "create table "
            + TABLE_APPDATA + "(" + RowId
            + " integer primary key autoincrement, " + Key
            + " text, " + Data + " text, " + Timestamp + " text );";

    public DataStoreHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_APPDATA_TBL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}