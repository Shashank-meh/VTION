package in.vtion.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import in.vtion.db.DataSourceHandler;
import in.vtion.db.DbAppDataObject;

public class EventReceiver extends BroadcastReceiver {

    private static int counter = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Fetch data from intent
        try {
            String key = intent.getStringExtra("key");
            String data = intent.getStringExtra("data");

            DataSourceHandler db = DataSourceHandler.getInstance(context);
            DbAppDataObject obj = new DbAppDataObject();
            obj.key = key;
            obj.data = data;
            obj.timestamp = System.currentTimeMillis() + (++counter);
            db.insertAppDataObject(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
