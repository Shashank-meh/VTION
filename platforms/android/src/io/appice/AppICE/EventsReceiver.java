//
// AppICE.java
//
// AppICE, 16/02/17.
//
// AppICE events Plugin for Cordova
// www.appice.io
//

package io.appice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;

import io.appice.db.DataSourceHandler;
import io.appice.db.DbAppDataObject;

/**
 * Created by aman on 16/02/17.
 */

public class EventsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String eventKey = null;
        boolean storeData = false;
        try {
            // Fetch data from intent
            eventKey = intent.getStringExtra("key");

            if (eventKey.equalsIgnoreCase("FM_Tuned") || eventKey.equalsIgnoreCase("FM_Off"))
                storeData = true;

            Log.i("EventsReceiver", "Intent Key : " + eventKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Check if storing data is required or not
        if (storeData) {
            // Fetch channel data
            HashMap<String, String> hashMap = null;
            try {
                try {
                    hashMap = (HashMap<String, String>) intent.getSerializableExtra("data");
                    Log.i("EventsReceiver", "Intent Data : " + hashMap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                DataSourceHandler dbObj = DataSourceHandler.getInstance(context.getApplicationContext());

                DbAppDataObject dataObj = new DbAppDataObject();
                dataObj.setKey(eventKey);
                if (hashMap != null) {
                    JSONObject root = new JSONObject(hashMap);
                    try {
                        dataObj.setData(root.toString());
                    } catch (Exception e) {
                        dataObj.setData("");
                    }
                } else {
                    dataObj.setData("");
                }
                dataObj.setTimeStamp(System.currentTimeMillis() / 1000);

                dbObj.insertAppDataObject(dataObj);
                dbObj.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
