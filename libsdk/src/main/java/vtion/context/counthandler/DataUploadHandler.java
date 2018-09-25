package vtion.context.counthandler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.sql.Date;
import java.util.Timer;
import java.util.TimerTask;

import vtion.activitysdk.Api;
import vtion.context.utility.CommonSharedPreferences;
import vtion.context.utility.Utility;
import vtion.util.constants.UrlConstants;

/*
    DataUploadHandler - class to handle data upload/sync on backend services
 */
public class DataUploadHandler {

    private static Context mContext = null;

    // Constructor of class
    public DataUploadHandler(Context context) {
        // Store context for usage
        mContext = context;
    }

    public void startUploadProcess() {
        // Start process of gather info
        try {
            if (true) {
                String deviceId = Api.getDeviceId(mContext);
                if (deviceId == null || deviceId.length() <= 0) {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            startUploadProcess();
                        }
                    }, 1000);
                    return;
                }

                long firstExecution = 0;
                long nextExecution = UrlConstants.nextTickTime * UrlConstants.dataSyncTimeMultiplier;
                long lastTickTime = CommonSharedPreferences
                        .loadLongSavedPreferences(CommonSharedPreferences.appLastTickedTime, mContext);
                long currentTime = System.currentTimeMillis();
                if (lastTickTime <= 0) {
                    // No last update time found
                    firstExecution = 10 * 1000;
                } else if (currentTime - lastTickTime >= nextExecution) {
                    // Last execution is over due
                    firstExecution = 10 * 1000;
                } else if (currentTime - lastTickTime < nextExecution) {
                    // Last execution is under due
                    firstExecution = currentTime - lastTickTime;
                }

                Utility.loginfo("Vtion startUploadProcess data repeat time : " + firstExecution
                        + " , " + nextExecution + " , "
                        + (currentTime - lastTickTime) + " , " + new
                        Date(lastTickTime).toLocaleString() + " , "
                        + new Date(currentTime).toLocaleString());


                int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT;

                // setup data upload timing
                Intent intent = new Intent(mContext, DataSyncReceiver.class);
                PendingIntent pintent = PendingIntent.getBroadcast(mContext, 100, intent, flags);
                AlarmManager alarm = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstExecution, nextExecution, pintent);

            }
        } catch (Exception e) {
        }
    }

    public void stopUploadProcess() {
        try {
            int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT;

            Intent intent = new Intent(mContext, DataSyncReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, flags);
            AlarmManager alarm = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            alarm.cancel(pendingIntent);
        } catch (Exception e) {
        }
    }
}
