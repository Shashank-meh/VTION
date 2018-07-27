package io.appice.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class DataSourceHandler {

    private SQLiteDatabase myDataBase;
    private DataStoreHandler dbHelper;
    
    private static DataSourceHandler mInstance = null;

    private DataSourceHandler() {
    }

    public static DataSourceHandler getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new DataSourceHandler();
            mInstance.getDataSourceHandler(ctx);
        }
        return mInstance;
    }

    private void getDataSourceHandler(Context context) {
        try {
            dbHelper = new DataStoreHandler(context);
        } catch (SQLiteException e) {
            close();
        } catch (Exception e) {
            close();
        }

        try {
            myDataBase = dbHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            close();
        } catch (Exception e) {
            close();
        }
    }

    @Override
    public void finalize() {
        try {
            if (myDataBase != null && myDataBase.isOpen()) {
                myDataBase.close();
            }
        } catch (Exception e) {
        }
    }

    public void close() {
        try {
            myDataBase.close();
        } catch (Exception e) {
        }
        try {
            dbHelper.close();
        } catch (Exception e) {
        }
        try {
            mInstance = null;
        } catch (Exception e) {
        }
    }

    /**
     * Current AppData base functions
     *
     * @param obj database object
     * @return rowid of data object created
     */
    public long insertAppDataObject(DbAppDataObject obj) {
        long insertId = -1;

        try {
            ContentValues values = new ContentValues();
            values.put(DataStoreHandler.allEventCols[1], obj.getKey());
            values.put(DataStoreHandler.allEventCols[2], obj.getData());
            values.put(DataStoreHandler.allEventCols[3], obj.getTimeStamp());
            
            try {
                insertId = myDataBase.insert(DataStoreHandler.TBL_EVENT_DATA,
                        null, values);
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }

        return insertId;
    }

    public int updateAppDataObject(DbAppDataObject obj) {
        int updated = 0;
        try {
            ContentValues values = new ContentValues();
            values.put(DataStoreHandler.allEventCols[1], obj.getKey());
            values.put(DataStoreHandler.allEventCols[2], obj.getData());
            values.put(DataStoreHandler.allEventCols[3], obj.getTimeStamp());

            updated = myDataBase.update(DataStoreHandler.TBL_EVENT_DATA, values,
                    DataStoreHandler.allEventCols[0] + "=" + obj.getId(),
                    null);
        } catch (Exception e) {
        }
        return updated;
    }

    public void deleteAppDataObject(DbAppDataObject object) {
        long id = object.getId();
        try {
            myDataBase.delete(DataStoreHandler.TBL_EVENT_DATA,
                    DataStoreHandler.COLUMN_ID + " = " + id, null);
        } catch (Exception e) {
        }
    }

    public List<DbAppDataObject> getAllAppDataObject() {
        List<DbAppDataObject> dataArr = new ArrayList<DbAppDataObject>();

        try {
            String whereClause = "";
            String[] whereArgs = null;
            String orderBy = null;
    
            Cursor cursor = myDataBase.query(DataStoreHandler.TBL_EVENT_DATA,
                    DataStoreHandler.allEventCols, whereClause, whereArgs,
                    null, null, orderBy);

            if (cursor != null && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    DbAppDataObject data = cursorToCurrentAppDataObject(cursor);
                    dataArr.add(data);
                    cursor.moveToNext();
                }
            }

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        } catch (Exception e) {
        }

        return dataArr;
    }

    public DbAppDataObject getOneWhereAppDataObject(long rowId) {
        DbAppDataObject dbobj = new DbAppDataObject();

        try {
            String whereClause = "";
            String[] whereArgs = null;

            whereClause = DataStoreHandler.COLUMN_ID + " = ? ";
            whereArgs = new String[]{"" + rowId};

            Cursor cursor = myDataBase.query(DataStoreHandler.TBL_EVENT_DATA,
                    DataStoreHandler.allEventCols, whereClause, whereArgs,
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    dbobj = cursorToCurrentAppDataObject(cursor);
                }
            }

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        } catch (Exception e) {
        }

        return dbobj;
    }

    private DbAppDataObject cursorToCurrentAppDataObject(Cursor cursor) {
        DbAppDataObject data = new DbAppDataObject();
        try {
            data.setId(cursor.getLong(0));
            data.setKey(cursor.getString(1));
            data.setData(cursor.getString(2));
            data.setTimeStamp(cursor.getLong(3));
        } catch (Exception e) {
        }
        return data;
    }
}
