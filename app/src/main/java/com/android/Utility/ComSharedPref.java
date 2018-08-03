package com.android.Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ComSharedPref {

    private static final String sharedPrefsFile = "VtionSharedPref";
    public static final String sharedPrefsPin = "000000";

    public static final String sharedPrefsUserId = "DefaultUserId";
    public static final String sharedPrefsIsResultGenerated = "IsResultGenerated";

    public static final String isUserSignedIn = "isUserSignedIn";
    public static final String isUserRegistered = "isUserRegistered";

    //social login details
    public static final String userName= "userName";
    public static final String userBirthday= "userBirthday";
    public static final String userLocation = "userLocation";
    public static final String sharedPrefsMobile = "0000000000";
    public static final String sharedPrefsAge = "0";
    public static final String sharedPrefsGender = "Default";
    public static final String sharedPrefsProfession = "DefaultProf";

    public static final String isFirstLaunch = "isFirstLaunch";

    public static void saveStringPreferences(String key, String value,
                                             Context ctx) {
        try {
            SharedPreferences sp = ctx.getSharedPreferences(sharedPrefsFile,
                    Context.MODE_PRIVATE);
            Editor edit = sp.edit();
            edit.putString(key, value);
            edit.apply();
        } catch (Exception e) {
            //
        }
    }

    public static String loadStringSavedPreferences(String key, Context ctx) {
        String name = "";
        try {
            SharedPreferences sp = ctx.getSharedPreferences(sharedPrefsFile,
                    Context.MODE_PRIVATE);
            name = sp.getString(key, "");
        } catch (Exception e) {
            //
        }
        return name;
    }

    public static void saveIntPreferences(String key, int value, Context ctx) {
        try {
            SharedPreferences sp = ctx.getSharedPreferences(sharedPrefsFile,
                    Context.MODE_PRIVATE);
            Editor edit = sp.edit();
            edit.putInt(key, value);
            edit.apply();
        } catch (Exception e) {
            //
        }
    }

    public static int loadIntSavedPreferences(String key, Context ctx) {
        int name = -1;
        try {
            SharedPreferences sp = ctx.getSharedPreferences(sharedPrefsFile,
                    Context.MODE_PRIVATE);
            name = sp.getInt(key, -1);
        } catch (Exception e) {
            //
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
            //
        }
    }

    public static long loadLongSavedPreferences(String key, Context ctx) {
        long name = -1;
        try {
            SharedPreferences sp = ctx.getSharedPreferences(sharedPrefsFile,
                    Context.MODE_PRIVATE);
            name = sp.getLong(key, -1);
        } catch (Exception e) {
            //
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
            //
        }
    }

    public static boolean loadBooleanSavedPreferences(String key, Context ctx) {
        boolean name = false;
        try {
            SharedPreferences sp = ctx.getSharedPreferences(sharedPrefsFile,
                    Context.MODE_PRIVATE);
            name = sp.getBoolean(key, false);
        } catch (Exception e) {
            //
        }
        return name;
    }
}
