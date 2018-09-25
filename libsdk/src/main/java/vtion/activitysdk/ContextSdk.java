package vtion.activitysdk;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import vtion.analytics.handler.AnalyticsHandler;
import vtion.context.utility.CommonSharedPreferences;
import vtion.context.utility.Utility;
import vtion.util.constants.ValueConstants;

/*
    ContextSdk - public class for interaction with sdk
 */
public class ContextSdk {

    Context mContext = null;

    public ContextSdk(Context ctx) {
        this.mContext = ctx;
    }

    public static int getSdkVersion() {
        return ValueConstants.sdklibraryIntVersion;
    }

    public static void setDeviceId(Context ctx, String deviceId) {
        try {
            if (deviceId != null && deviceId.length() > 0) {
                // compare last and new deviceId if they are same then no need to send
                String lastDeviceId = CommonSharedPreferences.loadStringSavedPreferences(CommonSharedPreferences.sdkDeviceId, ctx);
                boolean saveNSend = true;
                if (lastDeviceId != null && lastDeviceId.length() > 0) {
                    if (deviceId.equalsIgnoreCase(lastDeviceId))
                        saveNSend = false;
                }

                if (saveNSend) {
                    // store new deviceid
                    Api.deviceIdVal = deviceId;
                    CommonSharedPreferences.saveStringPreferences(CommonSharedPreferences.sdkDeviceId,
                            deviceId, ctx);
                }
            }
        } catch (Exception e) {
        }
    }

    public String getDeviceId() {
        String data = "";
        try {
            data = Api.getDeviceId(mContext);
        } catch (Exception e) {
        }
        return data;
    }

    public String getAppId() {
        String analyticsAppId = "";
        try {
            ApplicationInfo ai = Utility.getAppInfo(mContext);
            Bundle bundle = ai.metaData;
            analyticsAppId = bundle.getString(ValueConstants.metadataAppID);
        } catch (Exception e) {
        }
        return analyticsAppId;
    }

    public static void setReferrer(String value, Context ctx) {
        try {
            CommonSharedPreferences.saveStringPreferences("_ref", value, ctx);
        } catch (Exception e) {
        }
    }

    public static String getReferrer(Context ctx) {
        String val = "";
        try {
            val = CommonSharedPreferences.loadStringSavedPreferences("_ref", ctx);
        } catch (Exception e) {
        }
        return val;
    }

    public static void setInstallReferrer(String value, Context ctx) {
        try {
            CommonSharedPreferences.saveStringPreferences("_installAtt", value, ctx);
        } catch (Exception e) {
        }
    }

    public static String getInstallReferrer(Context ctx) {
        String val = "";
        try {
            val = CommonSharedPreferences.loadStringSavedPreferences("_installAtt", ctx);
        } catch (Exception e) {
            //
        }
        return val;
    }

    public static void setInstaller(String value, Context ctx) {
        try {
            CommonSharedPreferences.saveStringPreferences("_installer", value, ctx);
        } catch (Exception e) {
            //
        }
    }

    public static String getInstaller(Context ctx) {
        String val = "";
        try {
            val = CommonSharedPreferences.loadStringSavedPreferences("_installer", ctx);
        } catch (Exception e) {
            //
        }
        return val;
    }

    public static void tagEventObj(String key, Map<String, Object> segmentation, Context ctx) {
        try {
            AsyncTask<Object, Void, Void> object = new AsyncTask<Object, Void, Void>() {

                @Override
                protected Void doInBackground(Object... object) {
                    try {
                        String key = (String) object[0];
                        Map<String, Object> segmentation = (Map<String, Object>) object[1];
                        Context ctx = (Context) object[2];

                        try {
                            if (ContextApplication.appContext == null) {
                                ContextApplication.appContext = ctx.getApplicationContext();
                            }

                            AnalyticsHandler.initContextAnalytics(ctx);
                            AnalyticsHandler.recordEvent(key, segmentation, ctx);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return null;
                }
            };
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                object.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, key, segmentation, ctx);
            } else {
                object.execute(key, segmentation, ctx);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static HashMap<String, Object> gatherDeepLinkData(Intent intent) {
        HashMap<String, Object> data = null;
        try {
            if (null != intent) {
                final Uri uri = intent.getData();
                data = gatherDeepLinkData(uri);
            }
        } catch (Exception e) {//
        }
        return data;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static HashMap<String, Object> gatherDeepLinkData(Uri uri) {
        HashMap<String, Object> data = null;
        try {
            if (uri != null && uri.toString().length() > 0) {
                // Parse the deep link
                String deepLink = uri.getPath();
                if (deepLink != null) {
                    Set<String> queryParams = uri.getQueryParameterNames();
                    if (queryParams != null && queryParams.size() > 0) {
                        data = new HashMap<>();
                        for (String key : queryParams) {
                            data.put(key, uri.getQueryParameter(key));
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return data;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static String gatherDeepLinkPath(Uri uri) {
        HashMap<String, Object> data = null;
        try {
            if (uri != null && uri.toString().length() > 0) {
                // Parse the deep link
                String deepLink = uri.getPath();
                if (deepLink != null) {
                    return uri.getPath();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
