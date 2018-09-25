package vtion.context.counthandler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import vtion.activitysdk.Api;
import vtion.activitysdk.ContextSdk;
import vtion.analytics.handler.DeviceInfo;
import vtion.context.utility.CommonSharedPreferences;
import vtion.context.utility.Utility;
import vtion.util.commlayer.HttpRequestHandler;
import vtion.util.constants.UrlConstants;
import vtion.util.constants.ValueConstants;

/*
    DataSyncReceiver - class to handle broadcast from data upload handler - via alarm-manager
 */
public class DataSyncReceiver extends BroadcastReceiver {
    static final String TAG = "DataSyncReceiver";

    public void onReceive(Context context, Intent intent) {
        try {
            // Save last ticked time in shared preff
            CommonSharedPreferences.saveLongPreferences(CommonSharedPreferences.appLastTickedTime, System.currentTimeMillis(), context);

            // Handle app not running state
            Api.handlePhoneRestartState(context);

            // Do combined Get api calls
            try {
                doCombineGetRequest(context);
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }
    }


    private static boolean isGetReqRunning = false;

    public static void doCombineGetRequest(Context ctx) {
        isGetReqRunning = false;

        doCombineGetRequest(ctx, "", "", "");
    }

    public static void doCombineGetRequest(final Context ctx, final String forRuleId, final String forRuleAction, final String isForced) {
        if (isGetReqRunning) {
            Utility.loginfo("DoComineGetRequest - allready running");
            return;
        }
        isGetReqRunning = true;

        try {
            String deviceId = Api.getDeviceId(ctx);
            if (deviceId == null || deviceId.length() <= 0) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        doCombineGetRequest(ctx);
                    }
                }, 1000);
                isGetReqRunning = false;
                Utility.loginfo("DoComineGetRequest - null did reschedule");
                return;
            }

            AsyncTask<Object, Void, Void> object = new AsyncTask<Object, Void, Void>() {

                @Override
                protected void onPostExecute(Void param) {
                    isGetReqRunning = false;

                    Utility.loginfo("DoComineGetRequest - running end");

                    Runtime.getRuntime().gc();
                }

                @Override
                protected Void doInBackground(Object... params) {
                    try {
                        Utility.loginfo("DoComineGetRequest - running start");
                        Context ctx = (Context) params[0];

                        // fetch app meta-data keys
                        ContextSdk sdk = new ContextSdk(ctx);

                        StringBuilder serverUrl = new StringBuilder();
                        serverUrl.append(UrlConstants.ApiServerBaseUrl).append(UrlConstants.GetAppUserDataUrl);

                        StringBuilder serverData = new StringBuilder();
                        serverData.append("app_id=").append(sdk.getAppId());
                        serverData.append("&sdkv=").append(ValueConstants.sdklibraryIntVersion);
                        serverData.append("&device_id=").append(Api.getDeviceId(ctx));
                        serverData.append("&app_version=").append(DeviceInfo.appVersion(ctx));
                        serverData.append("&pv=").append(Utility.getOsVersion());

                        // Check for competing apps fetch timing
                        try {
                            // Send data related to competing apps
                            serverData.append("&trackingApps=true");
                        } catch (Exception e) {
                            //
                        }

                        boolean netState = Utility.isNetWorking(ctx);
                        if (netState) {
                            HttpRequestHandler req = new HttpRequestHandler();
                            String serverResponse = req.sendGet(serverUrl.toString(), serverData.toString(), null);
                            if (req.getStatusCode() == 200) {
                                if (serverResponse != null && serverResponse.length() > 0) {
                                    try {
                                        JSONObject root = new JSONObject(serverResponse);
                                        if (root != null && root.length() > 0) {
                                            try {
                                                JSONArray competingArr = root.optJSONArray("trackingApps");
                                                CommonSharedPreferences.saveStringPreferences(CommonSharedPreferences.competingAppLastRecv, competingArr.toString(), ctx);
                                            } catch (Exception e) {
                                                //
                                            }
                                        }
                                    } catch (Exception e) {
                                        //
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        //
                    }

                    return null;
                }
            };
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                object.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ctx);
            } else {
                object.execute(ctx);
            }
        } catch (Exception e) {
            isGetReqRunning = false;
        }
    }
}
