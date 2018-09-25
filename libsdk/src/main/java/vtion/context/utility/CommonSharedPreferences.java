package vtion.context.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


/*
    CommonSharedPreferences - class to store cached data in string, long and boolean values
 */
public class CommonSharedPreferences {

    private static final String sharedPrefsFile = "CommonSharedPrefs";

    public static final String sdkDeviceId = "sdkDeviceId";

    public static final String appLastTickedTime = "appLastTickedTime";

    public static final String deviceFirstRegister = "deviceFirstRegister";

    public static final String lastDeviceMetaDataSaved = "lastDeviceMetaDataSaved";

    public static final String competingAppLastRecv = "competingAppLastRecv";

    public static void saveStringPreferences(String key, String value,
                                             Context ctx) {
        try {
            SharedPreferences sp = ctx.getSharedPreferences(sharedPrefsFile,
                    Context.MODE_PRIVATE);
            Editor edit = sp.edit();
            edit.putString(key, value);
            edit.apply();
        } catch (Exception e) {
        }
    }

    public static String loadStringSavedPreferences(String key, Context ctx) {
        String name = "";
        try {
            SharedPreferences sp = ctx.getSharedPreferences(sharedPrefsFile,
                    Context.MODE_PRIVATE);
            name = sp.getString(key, "");
        } catch (Exception e) {
        }
        return name;
    }

    public static void saveLongPreferences(String key, long value, Context ctx) {
        try {
            SharedPreferences sp = ctx.getSharedPreferences(sharedPrefsFile,
                    Context.MODE_PRIVATE);
            Editor edit = sp.edit();
            edit.putLong(key, value);
            edit.apply();
        } catch (Exception e) {
        }
    }

    public static long loadLongSavedPreferences(String key, Context ctx) {
        long name = -1;
        try {
            SharedPreferences sp = ctx.getSharedPreferences(sharedPrefsFile,
                    Context.MODE_PRIVATE);
            name = sp.getLong(key, -1);
        } catch (Exception e) {
        }
        return name;
    }

    public static void saveBooleanPreferences(String key, boolean value, Context ctx) {
        try {
            SharedPreferences sp = ctx.getSharedPreferences(sharedPrefsFile,
                    Context.MODE_PRIVATE);
            Editor edit = sp.edit();
            edit.putBoolean(key, value);
            edit.apply();
        } catch (Exception e) {
        }
    }

    public static boolean loadBooleanSavedPreferences(String key, Context ctx) {
        boolean name = false;
        try {
            SharedPreferences sp = ctx.getSharedPreferences(sharedPrefsFile,
                    Context.MODE_PRIVATE);
            name = sp.getBoolean(key, false);
        } catch (Exception e) {
        }
        return name;
    }
}
