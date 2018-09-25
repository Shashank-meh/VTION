package vtion.context.counthandler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import vtion.activitysdk.Api;
import vtion.context.utility.Utility;

/*
    BootReceiver - class to handle device boot state
 */
public class DeviceBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            // gather the action string
            String action = intent.getAction();

            // Check for boot complete action
            Api.startContext(context);

            Utility.loginfo("Vtion got restart-event : " + action);
        } catch (Exception e) {}
    }
}
