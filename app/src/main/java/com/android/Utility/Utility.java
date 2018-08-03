package com.android.Utility;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by aman on 25/01/18.
 */

public class Utility {

    public static boolean hasNotificationAccess(Context ctx) {
        try {
            // To check notification access enabled state
            ContentResolver contentResolver = ctx.getContentResolver();
            String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
            String packageName = ctx.getPackageName();

            boolean status = !(enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName));
            return status;
        } catch (Exception e) {
            //
        }
        return false;
    }

    public static void openNotificationAccess(Activity activity) {
        try {
            activity.startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /*public static void hideIcon(Context ctx) {
        PackageManager p = ctx.getPackageManager();
        ComponentName componentName = new ComponentName(ctx, SplashActivity.class);
        p.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    public static void showIcon(Context ctx) {
        PackageManager p = ctx.getPackageManager();
        ComponentName componentName = new ComponentName(ctx, SplashActivity.class);
        p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }*/

    public static String readSMS(Context ctx) {

        String[] projection = new String[] { "_id", "address", "person", "body", "date", "type" };
        Cursor cursor = ctx.getContentResolver().query(Uri.parse("content://sms/inbox"),
                projection, "address LIKE ?", new String[]{/*"+918178942147"*/"%-IMRBIN"}, "date DESC");

        String msgBody = "",msgDate="";
        if(cursor != null && cursor.moveToFirst()){
            String senderName;
            // inbox has one or more messages
           do {
               senderName = cursor.getString(1);
               ComSharedPref.saveStringPreferences("sp_sender_name",senderName,ctx);
               if (senderName.equalsIgnoreCase("vm-imrbin") || senderName.equalsIgnoreCase("tm-imrbin")) {
                   msgBody = cursor.getString(3);
                   msgDate = parseDate(cursor.getString(4));
                   ComSharedPref.saveStringPreferences("sp_msg_date",msgDate,ctx);

                   if (msgBody != null && !msgBody.isEmpty()) {
                       if (msgBody.indexOf("REF#") != -1 && msgBody.indexOf("OTP is ") != -1) {
                           break;
                       }
                   }
               }
            } while(cursor.moveToNext());
        } else {

        }

        return msgBody;
    }

    public  static String parseDate(String dateEpochStr){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(new Date(Long.parseLong(dateEpochStr)));
    }

}
