package vtion.analytics.handler;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import vtion.activitysdk.Api;
import vtion.activitysdk.ContextSdk;
import vtion.context.utility.Utility;
import vtion.util.commlayer.HttpRequestHandler;
import vtion.util.constants.UrlConstants;

/*
    ContextAnalytics - class to handle data for managing and syncing data with backend services
 */
public class ContextAnalytics {

    private static ContextAnalytics sharedInstance_;

    private ConnectionQueue queue_;

    private AnalyticsDB analyticsDB_;
    public static Context mContext;

    static public ContextAnalytics sharedInstance(Context context) {
        try {
            mContext = context;

            if (sharedInstance_ == null)
                sharedInstance_ = new ContextAnalytics();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sharedInstance_;
    }

    private ContextAnalytics() {
        try {
            queue_ = new ConnectionQueue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init(Context context, String appId, String deviceId) {
        try {
            mContext = context;

            analyticsDB_ = new AnalyticsDB(context);

            queue_.setSuper(this);
            queue_.setContext(context);
            queue_.setAppId(appId);
            queue_.setDeviceId(deviceId);
            queue_.setAnalyticsDB(analyticsDB_);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reLoadDataBase() {
        try {
            analyticsDB_ = new AnalyticsDB(mContext);
            queue_.setAnalyticsDB(analyticsDB_);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveEventToQueue(Event event) {
        try {
            JSONObject json = null;

            try {
                json = new JSONObject(DeviceInfo.getUserContext(mContext, false));
                json.put("key", event.key);

                json.put("timestamp", event.timestamp);

                if (event.militime > 0)
                    json.put("mtimestamp", event.militime);

                try {
                    if (event.appid != null) {
                        json.put("appid", event.appid);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (event.segmentation != null) {
                    json.put("segmentation", new JSONObject(event.segmentation));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String result = "";
            try {
                result = java.net.URLEncoder.encode(json.toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                queue_.checkObjectState();
            } catch (Exception e) {
                //
            }

            queue_.recordEvents(result, event.key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void recordEvent(String key, Map<String, Object> segmentation, int count, double sum) {
        try {
            Event event = new Event();
            event.key = key;
            event.segmentation = segmentation;
            long time = System.currentTimeMillis();
            event.timestamp = time / 1000;
            event.militime = time;
            saveEventToQueue(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ConnectionQueue {

    private AnalyticsDB dbQueue_;
    private String appId_;
    private String deviceId_;
    private Context context_;
    private ContextAnalytics superObj;

    public void setSuper(ContextAnalytics superObj) {
        this.superObj = superObj;
    }

    public void setAppId(String appId) {
        appId_ = appId;
    }

    public void setDeviceId(String deviceId) {
        deviceId_ = deviceId;
    }

    public void setContext(Context context) {
        context_ = context;
    }

    public void setAnalyticsDB(AnalyticsDB analyticsDB) {
        dbQueue_ = analyticsDB;
    }

    public void checkObjectState() {
        try {
            ContextSdk sdk = new ContextSdk(context_);
            if (appId_ == null || appId_.length() <= 0)
                appId_ = sdk.getAppId();
            if (deviceId_ == null || deviceId_.length() <= 0)
                deviceId_ = sdk.getDeviceId();
        } catch (Exception e) {
            //
        }
    }

    public void recordEvents(String events, String key) {
        try {
            long time = System.currentTimeMillis();

            StringBuilder data = new StringBuilder();
            data.append("app_id=").append(appId_);
            data.append("&device_id=").append(deviceId_);
            data.append("&timestamp=").append((long) (time / 1000.0));
            data.append("&mtimestamp=").append(time);
            data.append("&events=").append(events);
            try {
                if (dbQueue_ == null && superObj != null) {
                    superObj.reLoadDataBase();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            dbQueue_.offer(data.toString());
            tick();

        } catch (java.lang.InternalError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class SendEventToServerTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (dbQueue_ == null || dbQueue_.isEmpty()) {
                    return null;
                }

                sendEvents();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            processRunning = false;

            Runtime.getRuntime().gc();
        }

        private void sendEvents() {
            try {
                boolean netState = Utility.isNetWorking(ContextAnalytics.mContext);
                if (netState == false) {
                    return;
                }
                boolean isRegistered = Api.isDeviceRegistered(ContextAnalytics.mContext);
                if (isRegistered == false) {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            ContextSdk sdk = null;
            try {
                sdk = new ContextSdk(context_);
            } catch (Exception e) {
            }

            while (true) {
                try {
                    String[] qItem = dbQueue_.peek();

                    String connId = qItem[0];
                    String data = qItem[1];

                    if (data == null)
                        break;

                    File fileObject = null;

                    String apiUrl = "";
                    if (data.contains("events=")) {
                        apiUrl += UrlConstants.ApiServerBaseUrl + "" + UrlConstants.SessionEventsUrl;

                        // add up upload time
                        data += "&uptimestamp=" + (long) (System.currentTimeMillis() / 1000.0);
                    }

                    try {
                        HttpRequestHandler req = null;
                        String res = "";
                        try {
                            req = new HttpRequestHandler();
                            res = req.sendGet(apiUrl, data, null);
                        } catch (java.lang.InternalError e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (req != null && req.getStatusCode() == 200) {
                            try {
                                JSONObject root = new JSONObject(res);
                                if (root != null && root.keys().hasNext()) {
                                    String result = root.optString("result");
                                    if (result != null && result.length() > 0 && result.equalsIgnoreCase("success")) {
                                        dbQueue_.delete(connId);

                                        // Check for any file object to be deleted
                                        try {
                                            if (fileObject != null) {
                                                if (fileObject.exists()) {
                                                    fileObject.delete();
                                                }
                                            }
                                        } catch (Exception e) {
                                            //
                                        }

                                        // Wait for some time gap to pick next api call
                                        try {
                                            Thread.sleep(100);
                                        } catch (java.lang.InternalError e) {
                                            e.printStackTrace();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        break;
                                    }
                                }
                            } catch (java.lang.InternalError e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            break;
                        }
                    } catch (java.lang.InternalError e) {
                        e.printStackTrace();
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                } catch (java.lang.InternalError e) {
                    e.printStackTrace();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    private static boolean processRunning = false;

    private void tick() {
        try {
            if (processRunning) {
                return;
            }

            processRunning = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            SendEventToServerTask object = new SendEventToServerTask();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                object.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                object.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Event {
    public String key = null;
    public String cid = null;
    public String appid = null;
    public int duration = 0;
    public Map<String, Object> segmentation = null;
    public long timestamp = 0;
    public long militime = 0;

    public String toString() {
        StringBuilder strData = new StringBuilder();
        strData.append("key= ").append(key);
        strData.append(", cid= ").append(cid);
        strData.append(", appid= ").append(appid);
        strData.append(", duration= ").append(duration);
        strData.append(", timestamp= ").append(timestamp);
        strData.append(", mili= ").append(militime);

        return strData.toString();
    }
}

class AnalyticsDB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ContextAnalytics";
    private static final String CONNECTIONS_TABLE_NAME = "CONNECTIONS";
    private static final String EVENTS_TABLE_NAME = "EVENTS";
    private static final String CONNECTIONS_TABLE_CREATE = "CREATE TABLE " + CONNECTIONS_TABLE_NAME
            + " (ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, CONNECTION TEXT NOT NULL);";
    private static final String EVENTS_TABLE_CREATE = "CREATE TABLE " + EVENTS_TABLE_NAME
            + " (ID INTEGER UNIQUE NOT NULL, EVENT TEXT NOT NULL);";

    private SQLiteDatabase db = null;

    AnalyticsDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        initDb();
    }

    private void initDb() {
        try {
            if (db == null || db.isOpen() == false) {
                db = this.getReadableDatabase();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finalize() {
        try {
            if (db != null && db.isOpen()) {
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CONNECTIONS_TABLE_CREATE);
            db.execSQL(EVENTS_TABLE_CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {

    }

    public String[] peek() {
        synchronized (this) {
            initDb();
            String[] connection = new String[2];
            Cursor cursor = null;
            try {
                cursor = db.query(CONNECTIONS_TABLE_NAME, null, null, null, null, null, "ID DESC", "1");

                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    connection[0] = cursor.getString(0);
                    connection[1] = cursor.getString(1);
                }
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null && cursor.isClosed() == false) {
                    cursor.close();
                }
            }

            return connection;
        }
    }

    public void delete(String connId) {
        try {
            initDb();
            db.execSQL("DELETE FROM " + CONNECTIONS_TABLE_NAME + " WHERE ID = " + connId + ";");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void offer(String data) {

        try {
            initDb();
            db.execSQL("INSERT INTO " + CONNECTIONS_TABLE_NAME + "(CONNECTION) VALUES('" + data + "');");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isEmpty() {
        boolean isEmpty = false;

        Cursor cursor = null;
        try {
            initDb();
            cursor = db.query(CONNECTIONS_TABLE_NAME, null, null, null, null, null, "ID DESC", "1");

            isEmpty = !(cursor != null && cursor.getCount() > 0);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && cursor.isClosed() == false) {
                cursor.close();
            }
        }

        return isEmpty;
    }
}
