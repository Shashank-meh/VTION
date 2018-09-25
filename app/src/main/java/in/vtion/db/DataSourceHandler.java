package in.vtion.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.List;

public class DataSourceHandler {

    private SQLiteDatabase myDataBase;
    private DataStoreHandler dbHelper;

    private static DataSourceHandler mInstance = null;

    private DataSourceHandler() {
    }

    public static DataSourceHandler getInstance(Context ctx) {
        try {
            if (mInstance == null) {
                mInstance = new DataSourceHandler();
                mInstance.getDataSourceHandler(ctx);
            }
        } catch (OutOfMemoryError | Exception e) {
            System.gc();
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
        try {
            dbHelper.close();
        } catch (Exception e) {
        }
        try {
            mInstance = null;
        } catch (Exception e) {
        }
    }

    private void close() {
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
            values.put(DataStoreHandler.allColumn[1], obj.key);
            values.put(DataStoreHandler.allColumn[2], obj.data);
            values.put(DataStoreHandler.allColumn[3], obj.timestamp);

            try {
                insertId = myDataBase.insert(DataStoreHandler.TABLE_APPDATA,
                        null, values);
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }

        return insertId;
    }

    public void executeSqlQuery(String query) {
        try {
            myDataBase.execSQL(query);
        } catch (Exception e) {
        }
    }

    public int updateAppDataObject(DbAppDataObject obj) {
        int updated = 0;
        try {
            ContentValues values = new ContentValues();
            values.put(DataStoreHandler.allColumn[1], obj.key);
            values.put(DataStoreHandler.allColumn[2], obj.data);
            values.put(DataStoreHandler.allColumn[3], obj.timestamp);

            updated = myDataBase.update(DataStoreHandler.TABLE_APPDATA, values,
                    DataStoreHandler.allColumn[0] + "=" + obj.rowId,
                    null);
        } catch (Exception e) {
        }
        return updated;
    }

    public void deleteAppDataObject(DbAppDataObject object) {
        long id = object.rowId;
        try {
            int val = myDataBase.delete(DataStoreHandler.TABLE_APPDATA,
                    DataStoreHandler.RowId + " = " + id, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<DbAppDataObject> getQueryAppDataObject(String whereClause, String[] whereArgs, String orderBy) {
        List<DbAppDataObject> dataArr = new ArrayList<DbAppDataObject>();

        try {
            Cursor cursor = myDataBase.query(DataStoreHandler.TABLE_APPDATA,
                    DataStoreHandler.allColumn, whereClause, whereArgs,
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

    public int getQueryAppDataObjectCount(String whereClause, String[] whereArgs, String orderBy) {
        int count = 0;
        try {
            Cursor cursor = myDataBase.query(DataStoreHandler.TABLE_APPDATA,
                    DataStoreHandler.allColumn, whereClause, whereArgs,
                    null, null, orderBy);
            if (cursor != null && !cursor.isClosed()) {
                count = cursor.getCount();
                cursor.close();
            }
        } catch (Exception e) {
        }

        return count;
    }

    public List<DbAppDataObject> getAllWhereAppDataObject() {
        List<DbAppDataObject> dataArr = new ArrayList<DbAppDataObject>();

        try {
            Cursor cursor = myDataBase.query(DataStoreHandler.TABLE_APPDATA,
                    DataStoreHandler.allColumn, null, null,
                    null, null, DataStoreHandler.Timestamp + " DESC");

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
        } catch (OutOfMemoryError | Exception e) {
            System.gc();
        }

        return dataArr;
    }

    public DbAppDataObject getOneWhereAppDataObject(long rowId) {
        DbAppDataObject dbobj = new DbAppDataObject();

        try {
            String whereClause = "";
            String[] whereArgs = null;

            whereClause = DataStoreHandler.RowId + " = ? ";
            whereArgs = new String[]{"" + rowId};

            Cursor cursor = myDataBase.query(DataStoreHandler.TABLE_APPDATA,
                    DataStoreHandler.allColumn, whereClause, whereArgs,
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
            data.rowId = cursor.getLong(0);
            data.key = cursor.getString(1);
            data.data = cursor.getString(2);
            data.timestamp = cursor.getLong(3);
        } catch (Exception e) {
        }
        return data;
    }
}
