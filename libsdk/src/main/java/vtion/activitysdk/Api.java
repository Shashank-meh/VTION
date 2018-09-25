package vtion.activitysdk;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import vtion.analytics.handler.DeviceInfo;
import vtion.context.counthandler.CountManager;
import vtion.context.counthandler.DataSyncReceiver;
import vtion.context.utility.CommonSharedPreferences;
import vtion.context.utility.Utility;
import vtion.util.commlayer.HttpRequestHandler;
import vtion.util.constants.UrlConstants;
import vtion.util.constants.ValueConstants;

/*
    Api - Service class to start modules of countmanager, install api sending
 */
public class Api extends Service {

    private static Context mContext = null;
    private static Api thisObj = null;

    private CountManager countMgrObj = null;

    public static boolean isApiServiceStopped = true;

    public static String deviceIdVal = "";
    public static boolean isPlayServiceWorking = true;

    public static String getDeviceId(final Context ctx) {
        try {
            if (deviceIdVal == null || deviceIdVal.length() <= 0) {
                String savedDeviceId = CommonSharedPreferences
                        .loadStringSavedPreferences(CommonSharedPreferences.sdkDeviceId, ctx);

                if (savedDeviceId != null && savedDeviceId.length() > 0) {
                    deviceIdVal = savedDeviceId;
                } else {
                    AsyncTask<Void, Void, Void> object = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                AdvertisingIdClient.Info adinfo = AdvertisingIdClient.getAdvertisingIdInfo(ctx);
                                deviceIdVal = adinfo.getId();
                                isPlayServiceWorking = true;
                            } catch (IllegalStateException e) {
                                isPlayServiceWorking = false;
                            } catch (GooglePlayServicesRepairableException e) {
                                isPlayServiceWorking = false;
                            } catch (IOException e) {
                                isPlayServiceWorking = false;
                            } catch (GooglePlayServicesNotAvailableException e) {
                                isPlayServiceWorking = false;
                            } catch (Exception e) {
                                isPlayServiceWorking = false;
                            }

                            if (deviceIdVal == null || deviceIdVal.length() <= 0) {
                                try {
                                    deviceIdVal = UUID.randomUUID().toString();
                                } catch (Exception e) {
                                }
                            }

                            if (deviceIdVal != null && deviceIdVal.length() > 0) {
                                CommonSharedPreferences.saveStringPreferences(CommonSharedPreferences.sdkDeviceId,
                                        deviceIdVal, ctx);
                            }
                            return null;
                        }
                    };
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        object.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        object.execute();
                    }
                }
            }
        } catch (Exception e) {
        }
        return deviceIdVal;
    }

    @Override
    public void onCreate() {
        try {
            mContext = this.getApplicationContext();
            thisObj = Api.this;
        } catch (Exception e) {
            //
        }
    }

    @Override
    public void onDestroy() {
        try {
            stopContextFunc();
        } catch (Exception e) {
            //
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        try {
            Utility.loginfo("Vtion : Task removed, attempting restart in 3 seconds");

            Intent restartService = new Intent(this.getApplicationContext(), this.getClass());
            restartService.setPackage(this.getPackageName());
            PendingIntent restartServiceIntent = PendingIntent.getService(this.getApplicationContext(), 1, restartService, PendingIntent.FLAG_ONE_SHOT);

            AlarmManager alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            alarmMgr.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 3000L, restartServiceIntent);
        } catch (Exception e) {
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            mContext = this.getApplicationContext();
            thisObj = Api.this;

            Utility.loginfo("Vtion Service invoked : " + flags + " , " + startId);

            Api.isApiServiceStopped = false;

            startFirstRegister();
        } catch (Exception e) {
        }

        return Service.START_STICKY;
    }

    public void initAppModules() {
        try {
            getDeviceId(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // Initialize count manager
            if (countMgrObj == null) {
                countMgrObj = new CountManager(mContext);
                countMgrObj.setDebugCount(false);
                countMgrObj.startCountManager();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startFirstRegister() {
        try {
            boolean isSent = CommonSharedPreferences
                    .loadBooleanSavedPreferences(CommonSharedPreferences.deviceFirstRegister, mContext);
            if (isSent == false) {
                Utility.loginfo("Vtion register device");
                SendFirstDeviceData object = new SendFirstDeviceData(mContext, thisObj);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    object.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    object.execute();
                }
            } else {
                Utility.loginfo("Vtion device registered");
                initAppModules();
            }
        } catch (Exception e) {
        }
    }

    public static boolean isDeviceRegistered(Context ctx) {
        boolean isRegistered = false;
        try {
            isRegistered = CommonSharedPreferences
                    .loadBooleanSavedPreferences(CommonSharedPreferences.deviceFirstRegister, ctx);
        } catch (Exception e) {
        }
        return isRegistered;
    }

    /*
     * checks if the sdk service is running
     */
    public static boolean isServiceRunning(String serviceClassName, Context ctx) {
        try {
            String appPckgName = ctx.getApplicationContext().getPackageName();
            ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClassName.equals(service.service.getClassName())
                        && appPckgName.equals(service.service.getPackageName())) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static void startContext(Context ctx) {
        mContext = ctx;

        try {
            // start the service
            Intent apiIntent = new Intent(ctx, Api.class);
            ctx.startService(apiIntent);

            setDefaultInstallRef(ctx);

        } catch (Exception e) {
        }
    }

    public static void setDefaultInstallRef(Context ctx) {
        try {
            String refVal = ContextSdk.getReferrer(ctx);
            if (refVal == null || refVal.length() <= 0) {
                ContextSdk.setReferrer("self", ctx);
            }
        } catch (Exception e) {
        }

        try {
            String installerVal = ContextSdk.getInstaller(ctx);
            if (installerVal == null || installerVal.length() <= 0) {
                String installer = ctx.getPackageManager().getInstallerPackageName(ctx.getPackageName());
                if (installer != null && installer.length() > 0) {
                    ContextSdk.setInstaller(installer, ctx);
                } else {
                    ContextSdk.setInstaller("self", ctx);
                }
            }
        } catch (Exception e) {
        }
    }

    public static Api getInstance() {

        try {
            if (thisObj == null) {
                thisObj = new Api();
                thisObj.onStartCommand(null, 0, 0);

                Api.isApiServiceStopped = true;

            } else {
                Api.isApiServiceStopped = false;

            }
        } catch (Exception e) {
        }

        return thisObj;
    }

    public CountManager getCountMgrObj() {
        return thisObj.countMgrObj;
    }

    /**
     * Stop API service Will be called from either of following - DemoCache
     * Required - stop only on (demographics refreshed when force demo update OR
     * demo cache full first time) AND no other run reason active Rule based -
     * stop only on rule execution done AND no other run reason active Developer
     * - stop only by explicit stop call AND if no other run reason active
     * This function is called by dev
     */
    public static void stopContext(Context ctx) {
        try {
            Api.isApiServiceStopped = true;
            mContext = ctx;
            // Stopped by dev. set flag in sp
            if (mContext != null) {
                Api.getInstance().stopApiProcess();
            }
        } catch (Exception e) { //
        }
    }

    public static void handlePhoneRestartState(Context ctx) {

        try {
            if (isServiceRunning(Api.class.getName(), ctx) == false) {
                Api.startContext(ctx);
            }
        } catch (Exception e) { //
        }
    }

    private void stopContextFunc() {
        try {
            if (thisObj != null) {
                Api.isApiServiceStopped = true;

                stopApiProcess();
            }
        } catch (Exception e) { //
        }
    }

    private void stopApiProcess() {
        try {
            Api.getInstance().getCountMgrObj().stopCountManager();
        } catch (Exception e) {
            //
        }

        try {
            stopSelf();

            thisObj = null;
        } catch (Exception e) {
            //
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return new Binder();
    }

    private static boolean isInitRunning = false;

    class SendFirstDeviceData extends AsyncTask<Void, Void, Void> {

        private StringBuilder urldata = new StringBuilder();
        private boolean isSuccess = false;
        private boolean initModules = true;
        private Context mContext = null;
        private Api mApi = null;

        private boolean isSkipped = false;

        public SendFirstDeviceData(Context ctx, Api api) {
            initModules = true;
            mContext = ctx;
            mApi = api;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (isInitRunning) {
                    Utility.loginfo("Already Running device Registration");
                    isSkipped = true;
                    return null;
                }
                isInitRunning = true;

                String deviceId = Api.getDeviceId(mContext);
                Utility.loginfo("Vtion register device id : " + deviceId);
                if (deviceId == null || deviceId.length() <= 0) {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Utility.loginfo("Vtion send register call");
                            SendFirstDeviceData object = new SendFirstDeviceData(mContext, mApi);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                object.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            } else {
                                object.execute();
                            }
                        }
                    }, 1000);

                    isSkipped = true;
                    isInitRunning = false;

                    return null;
                }

                Utility.loginfo("Vtion send register init");
                startInitAnalytics();
            } catch (Exception e) {
            }

            return null;
        }

        private void startInitAnalytics() {
            // fetch app meta-data keys
            String analyticsAppId = "";
            try {
                ApplicationInfo ai = Utility.getAppInfo(mContext);
                Bundle bundle = ai.metaData;
                analyticsAppId = bundle.getString(ValueConstants.metadataAppID);
            } catch (Exception e) {
            }

            try {
                urldata.append("app_id=").append(analyticsAppId);
                urldata.append("&device_id=").append(Api.getDeviceId(mContext));
                urldata.append("&timestamp=").append((long) (System.currentTimeMillis() / 1000.0));
                urldata.append("&sdkv=").append(ValueConstants.sdklibraryIntVersion);
                urldata.append("&metrics=").append(DeviceInfo.getMetrics(mContext));

                StringBuilder url = new StringBuilder();
                url.append(UrlConstants.ApiServerBaseUrl).append(UrlConstants.DeviceRegisterUrl);

                HttpRequestHandler req = new HttpRequestHandler();
                String serverRes = req.sendGet(url.toString(), urldata.toString(), null);
                if (req.getStatusCode() == 200) {
                    if (serverRes != null && serverRes.length() > 2) {
                        try {
                            JSONObject resRoot = new JSONObject(serverRes);
                            String result = resRoot.getString("result").trim();
                            if (result != null && result.length() > 0 && (result.equalsIgnoreCase("Success") || result.equalsIgnoreCase("Data saved SuccessFully"))) {
                                // Update shared preference that data is uploaded
                                isSuccess = true;
                                CommonSharedPreferences.saveBooleanPreferences(CommonSharedPreferences.deviceFirstRegister,
                                        true, mContext);
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            } catch (Exception e) {
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            if (!isSkipped) {
                Utility.loginfo("Vtion register end : " + isSuccess + " , " + initModules);
                if (isSuccess == false) {
                    // Cache the api call if not sent to the server
                }

                // Update shared preference that data is uploaded
                try {
                    JSONObject fMetricMap = DeviceInfo.getFMetricsMap(mContext);
                    CommonSharedPreferences.saveStringPreferences(CommonSharedPreferences.lastDeviceMetaDataSaved, fMetricMap.toString(), mContext);
                    if (initModules) {
                        // Initialize vtion system
                        mApi.initAppModules();

                        // Pull users's profile from server
                        DataSyncReceiver.doCombineGetRequest(mContext);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
