package vtion.analytics.handler;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import vtion.activitysdk.ContextSdk;
import vtion.util.constants.ValueConstants;

/*
    DeviceInfo - utility class for gathering device details
 */
public class DeviceInfo {

    public static String getOS() {
        return "Android";
    }

    public static String getOSVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    public static String getDevice() {
        return android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL;
    }

    public static String getDMake() {
        return android.os.Build.MANUFACTURER;
    }

    public static String getDModel() {
        return android.os.Build.MODEL;
    }

    public static String appVersion(Context context) {
        String result = "1.0";
        try {
            result = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException ignored) {
        }

        return result;
    }

    public static String appCode(Context context) {
        int result = 0;
        try {
            result = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException ignored) {
        }
        return result + "";
    }

    public static String appPackage(Context context) {
        String result = "1.0";
        try {
            result = context.getPackageName();
        } catch (Exception ignored) {
        }
        return result;
    }

    public static JSONObject getFMetricsMap(Context context) {

        JSONObject json = new JSONObject();

        try {
            json.put("_device", getDevice());
            json.put("_make", getDMake());
            json.put("_model", getDModel());
            json.put("_os", getOS());
            json.put("_os_version", getOSVersion());

            json.put("_app_version", appVersion(context));
            json.put("_app_code", appCode(context));
            json.put("_app_package", appPackage(context));
        } catch (JSONException ignored) {
        }

        return json;
    }

    public static String getMetrics(Context context) {
        String result = "";
        JSONObject json = new JSONObject();
        try {
            json.put("_device", getDevice());
            json.put("_make", getDMake());
            json.put("_model", getDModel());
            json.put("_os", getOS());
            json.put("_os_version", getOSVersion());

            json.put("_app_version", appVersion(context));
            json.put("_app_code", appCode(context));
            json.put("_app_package", appPackage(context));

            json.put("_ref", ContextSdk.getReferrer(context));
            json.put("_installer", ContextSdk.getInstaller(context));
        } catch (JSONException ignored) {
        }

        result = json.toString();
        try {
            result = java.net.URLEncoder.encode(result, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {

        }
        return result;
    }

    private static JSONObject getUserContextJson(Context context) {

        JSONObject root = new JSONObject();
        try {
            try {
                root.put("d", getDevice());
                root.put("ma", getDMake());
                root.put("mo", getDModel());
                root.put("p", getOS());
                root.put("pv", getOSVersion());
                root.put("sdkv", ValueConstants.sdklibraryIntVersion);
                root.put("av", appVersion(context));
                root.put("ac", appCode(context));
                root.put("ap", appPackage(context));
            } catch (Exception ignored) {
            }
        } catch (Exception ignored) {
        }

        return root;
    }

    public static String getUserContext(Context context, boolean urlEncoded) {
        JSONObject topRoot = getUserContextJson(context);

        String result = "";

        try {
            result = topRoot.toString();
            if (urlEncoded) {
                result = java.net.URLEncoder.encode(result, "UTF-8");
            }
        } catch (UnsupportedEncodingException ignored) {
        } catch (Exception ignored) {
        }
        return result;
    }
}
