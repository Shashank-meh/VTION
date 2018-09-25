package vtion.context.utility;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

/*
    Utility - class containing utility functions
 */
public class Utility {

    public static boolean isNetWorking(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null && info.length > 0) {
                    for (NetworkInfo networkInfo : info) {
                        if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static void loginfo(String msg) {
        try {
            Log.i("VtionSdk", msg);
        } catch (Exception e) {
        }
    }

    public static String getOsVersion() {
        return Build.VERSION.RELEASE;
    }

    private static ApplicationInfo mAppInfo = null;

    public static ApplicationInfo getAppInfo(Context ctx) {
        if (mAppInfo == null) {
            if (ctx != null) {
                try {
                    mAppInfo = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                } catch (Exception e) {
                }
            }
        }
        return mAppInfo;
    }
}
