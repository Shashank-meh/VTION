package vtion.analytics.handler;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import vtion.activitysdk.Api;
import vtion.context.utility.Utility;
import vtion.util.constants.ValueConstants;

/*
    AnalyticsHandler - class for packaging of event data and hostEvent to app level for custom handling
 */
public class AnalyticsHandler {

    public static void recordEvent(String key, Map<String, Object> segmentation, Context ctx) {
        try {
            ContextAnalytics.sharedInstance(ctx).recordEvent(key, segmentation, 1, 1);
        } catch (Exception e) {
        }

        // Host custom events to client
        try {
            if (segmentation != null && segmentation.size() > 0)
                hostEvent(ctx, key, (HashMap<String, Object>) segmentation);
            else
                hostEvent(ctx, key, null);
        } catch (Exception e) {
        }
    }

    private static final String CustomEventActionStr = "com.vtion.Events";

    private static void hostEvent(final Context mContext, final String key, final HashMap<String, Object> segmentation) {
        try {
            Intent intent = new Intent();
            intent.setPackage(mContext.getPackageName());
            intent.setAction(CustomEventActionStr);
            intent.putExtra("key", key);
            if (segmentation != null) {
                intent.putExtra("data", new JSONObject(segmentation).toString());
            }
            mContext.sendBroadcast(intent);
        } catch (Exception e) {
        }
    }

    public static void initContextAnalytics(Context mContext) {
        try {
            String analyticsAppId = "";
            try {
                ApplicationInfo ai = Utility.getAppInfo(mContext);
                Bundle bundle = ai.metaData;
                analyticsAppId = bundle.getString(ValueConstants.metadataAppID);
            } catch (NullPointerException e) {
            }

            String deviceId = "";
            try {
                deviceId = Api.getDeviceId(mContext);
            } catch (Exception e) {
            }

            ContextAnalytics.sharedInstance(mContext).init(mContext, analyticsAppId, deviceId);

        } catch (Exception e) {
        }
    }
}