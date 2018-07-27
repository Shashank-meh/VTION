//
// AppICE.java
//
// AppICE, 08/11/16.
//
// AppICE analytics Plugin for Cordova
// www.appice.io
//

package io.appice;

import android.content.ContentResolver;
import android.content.Intent;
import android.provider.Settings;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import semusi.activitysdk.Api;
import semusi.activitysdk.ContextSdk;
import semusi.activitysdk.SdkConfig;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class AppICE extends CordovaPlugin {
    private static final String TAG = "AppICE_CP";

    HashMap<String, CallbackContext> callbackIds = new HashMap<String, CallbackContext>();

    private static final Map<String, Method> exportedMethods;

    @Retention(RUNTIME)
    @interface CordovaMethod {

    }

    static {
        HashMap<String, Method> methods = new HashMap<String, Method>();

        final List<Method> allMethods = new ArrayList<Method>(Arrays.asList(AppICE.class.getDeclaredMethods()));
        for (final Method method : allMethods) {
            if (method.isAnnotationPresent(CordovaMethod.class)) {
                methods.put(method.getName(), method);
            }
        }

        exportedMethods = methods;
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
    }

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @CordovaMethod
    private void openNotificationAccess(JSONArray data, CallbackContext callbackContext) {
        try {
            cordova.getActivity().startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @CordovaMethod
    private void hasNotificationAccess(JSONArray data, CallbackContext callbackContext) {
        try {
            // To check notification access enabled state
            ContentResolver contentResolver = cordova.getActivity().getApplicationContext().getContentResolver();
            String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
            String packageName = cordova.getActivity().getApplicationContext().getPackageName();

            boolean status =  !(enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName));
            if (status)
                callbackContext.success();
            else
                callbackContext.error(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @CordovaMethod
    private void startContext(JSONArray data, CallbackContext callbackContext) {
        try {
            SdkConfig config = new SdkConfig();

            try {
                String gcmid = (String) data.getJSONObject(0).get("gcmID");
                if (gcmid != null && gcmid.length() > 0) {
                    config.setGcmSenderId(gcmid);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Init sdk with your config
            Api.startContext(cordova.getActivity().getApplicationContext(), config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @CordovaMethod
    private void stopContext(JSONArray data, CallbackContext callbackContext) {
        // Stop sdk
        try {
            Api.stopContext(cordova.getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @CordovaMethod
    private boolean isSemusiSensing(JSONArray data, CallbackContext callbackContext) {
        // Check whether appice sdk is running or not
        boolean isSensing = false;
        try {
            isSensing = ContextSdk.isSemusiSensing(cordova.getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSensing;
    }

    @CordovaMethod
    private void openPlayServiceUpdate(JSONArray data, CallbackContext callbackContext) {
        // Open up google play service udpate UI for user
        try {
            ContextSdk.openPlayServiceUpdate(cordova.getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @CordovaMethod
    private String getSdkVersion(JSONArray data, CallbackContext callbackContext) {
        // Get sdk version as string value
        String sdkVersion = "";
        try {
            sdkVersion = ContextSdk.getSdkVersion();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sdkVersion;
    }

    @CordovaMethod
    private int getSdkIntVersion(JSONArray data, CallbackContext callbackContext) {
        // Get sdk version as int value
        int sdkVersion = 0;
        try {
            sdkVersion = ContextSdk.getSdkIntVersion();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sdkVersion;
    }

    @CordovaMethod
    private void setDeviceId(JSONArray data, CallbackContext callbackContext) {
        // Set new deviceId in system
        try {
            String deviceID = (String) data.getJSONObject(0).get("deviceID");
            if (deviceID != null && deviceID.length() > 0) {
                ContextSdk.setDeviceId(cordova.getActivity().getApplicationContext(), deviceID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @CordovaMethod
    private String getDeviceId(JSONArray data, CallbackContext callbackContext) {
        // Gather existing device-id from system
        String deviceID = "";
        try {
            ContextSdk sdk = new ContextSdk(cordova.getActivity().getApplicationContext());
            deviceID = sdk.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceID;
    }

    @CordovaMethod
    private String getAndroidId(JSONArray data, CallbackContext callbackContext) {
        // Gather existing android-id from system
        String androidID = "";
        try {
            ContextSdk sdk = new ContextSdk(cordova.getActivity().getApplicationContext());
            androidID = sdk.getAndroidId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return androidID;
    }

    @CordovaMethod
    private String getAppKey(JSONArray data, CallbackContext callbackContext) {
        // Gather existing app-key from system
        String key = "";
        try {
            ContextSdk sdk = new ContextSdk(cordova.getActivity().getApplicationContext());
            key = sdk.getAppKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    @CordovaMethod
    private String getApiKey(JSONArray data, CallbackContext callbackContext) {
        // Gather existing api-key from system
        String key = "";
        try {
            ContextSdk sdk = new ContextSdk(cordova.getActivity().getApplicationContext());
            key = sdk.getApiKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    @CordovaMethod
    private String getAppId(JSONArray data, CallbackContext callbackContext) {
        // Gather existing app-id from system
        String key = "";
        try {
            ContextSdk sdk = new ContextSdk(cordova.getActivity().getApplicationContext());
            key = sdk.getAppId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    @CordovaMethod
    private void openSession(JSONArray data, CallbackContext callbackContext) {
        // Update current activity open session
        try {
            ContextSdk.openSession(cordova.getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @CordovaMethod
    private void closeSession(JSONArray data, CallbackContext callbackContext) {
        // Update current activity close session
        try {
            ContextSdk.closeSession(cordova.getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @CordovaMethod
    private void setAlias(JSONArray data, CallbackContext callbackContext) {
        // Set new deviceId in system
        try {
            String alias = (String) data.getJSONObject(0).get("alias");
            if (alias != null && alias.length() > 0) {
                ContextSdk.setAlias(alias, cordova.getActivity().getApplicationContext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @CordovaMethod
    private String getAlias(JSONArray data, CallbackContext callbackContext) {
        // Get alias value from system
        String key = "";
        try {
            key = ContextSdk.getAlias(cordova.getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    @CordovaMethod
    private void setChildId(JSONArray data, CallbackContext callbackContext) {
        // Set new child-id in system
        try {
            String childID = (String) data.getJSONObject(0).get("childID");
            if (childID != null && childID.length() > 0) {
                ContextSdk.setChildId(childID, cordova.getActivity().getApplicationContext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @CordovaMethod
    private String getChildId(JSONArray data, CallbackContext callbackContext) {
        // Get child-id value from system
        String key = "";
        try {
            key = ContextSdk.getChildId(cordova.getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    @CordovaMethod
    private void setReferrer(JSONArray data, CallbackContext callbackContext) {
        // Set new referrer in system
        try {
            String key = (String) data.getJSONObject(0).get("referrer");
            if (key != null && key.length() > 0) {
                ContextSdk.setReferrer(key, cordova.getActivity().getApplicationContext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @CordovaMethod
    private String getReferrer(JSONArray data, CallbackContext callbackContext) {
        // Get referrer value from system
        String key = "";
        try {
            key = ContextSdk.getReferrer(cordova.getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    @CordovaMethod
    private void setInstallReferrer(JSONArray data, CallbackContext callbackContext) {
        // Set new install referrer in system
        try {
            String key = (String) data.getJSONObject(0).get("installRef");
            if (key != null && key.length() > 0) {
                ContextSdk.setInstallReferrer(key, cordova.getActivity().getApplicationContext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @CordovaMethod
    private String getInstallReferrer(JSONArray data, CallbackContext callbackContext) {
        // Get install referrer value from system
        String key = "";
        try {
            key = ContextSdk.getInstallReferrer(cordova.getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    @CordovaMethod
    private void setInstaller(JSONArray data, CallbackContext callbackContext) {
        // Set new installer in system
        try {
            String key = (String) data.getJSONObject(0).get("installer");
            if (key != null && key.length() > 0) {
                ContextSdk.setInstaller(key, cordova.getActivity().getApplicationContext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @CordovaMethod
    private String getInstaller(JSONArray data, CallbackContext callbackContext) {
        // Get installer value from system
        String key = "";
        try {
            key = ContextSdk.getInstaller(cordova.getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    @CordovaMethod
    private void setCustomVariable(JSONArray data, CallbackContext callbackContext) {
        // Set custom variable in system
        try {
            String key = (String) data.getJSONObject(0).get("key");
            Object value = (Object) data.getJSONObject(0).get("value");
            if (key != null && key.length() > 0 && value != null) {
                Class valueClass = value.getClass();
                if (valueClass == Integer.class)
                    ContextSdk.setCustomVariable(key, (Integer) value, cordova.getActivity().getApplicationContext());
                else if (valueClass == Float.class || valueClass == Double.class)
                    ContextSdk.setCustomVariable(key, (Float) value, cordova.getActivity().getApplicationContext());
                else if (valueClass == Long.class)
                    ContextSdk.setCustomVariable(key, (Long) value, cordova.getActivity().getApplicationContext());
                else if (valueClass == String.class)
                    ContextSdk.setCustomVariable(key, (String) value, cordova.getActivity().getApplicationContext());
                else if (valueClass == Boolean.class)
                    ContextSdk.setCustomVariable(key, (Boolean) value, cordova.getActivity().getApplicationContext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @CordovaMethod
    private Object getCustomVariable(JSONArray data, CallbackContext callbackContext) {
        // Get custom variable value from system
        Object value = null;
        try {
            String key = (String) data.getJSONObject(0).get("key");
            value = ContextSdk.getCustomVariable(key, cordova.getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    @CordovaMethod
    private void removeCustomVariable(JSONArray data, CallbackContext callbackContext) {
        // Get remove custom variable value from system
        try {
            String key = (String) data.getJSONObject(0).get("key");
            ContextSdk.removeCustomVariable(key, cordova.getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @CordovaMethod
    private void tagEvent(JSONArray data, CallbackContext callbackContext) {
        // Set event in system
        try {
            String key = (String) data.getJSONObject(0).get("key");
            if (key != null && key.length() > 0) {
                HashMap<String, String> map = null;
                try {
                    JSONObject root = (JSONObject) data.getJSONObject(0).getJSONObject("map");
                    if (root != null && root.length() > 0) {
                        map = new HashMap<String, String>();
                        Iterator<String> iterator = root.keys();
                        while(iterator.hasNext()) {
                            String key1 = iterator.next();
                            map.put(key1, root.getString(key1));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ContextSdk.tagEvent(key, map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @CordovaMethod
    private void setSmallIcon(JSONArray data, CallbackContext callbackContext) {
        // Set small icon in system
        try {
            String key = (String) data.getJSONObject(0).get("icon");
            if (key != null && key.length() > 0) {
                ContextSdk.setSmallIcon(key, cordova.getActivity().getApplicationContext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @CordovaMethod
    private void setSessionTimeout(JSONArray data, CallbackContext callbackContext) {
        // Set session timeout in system
        try {
            int key = (Integer) data.getJSONObject(0).get("timeout");
            if (key > 0) {
                ContextSdk.setSessionTimeout(key, cordova.getActivity().getApplicationContext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @CordovaMethod
    private int getSessionTimeout(JSONArray data, CallbackContext callbackContext) {
        // Get installer value from system
        int key = 0;
        try {
            key = ContextSdk.getSessionTimeout(cordova.getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }


    // @CordovaMethod
    // private boolean onDeviceReady(JSONArray data, CallbackContext callbackContext)
    // {
    //   JSONObject params = null;
    //   try
    //   {
    //     params = data.getJSONObject(0);
    //   }
    //   catch (JSONException e)
    //   {
    //     PWLog.error(TAG, "No parameters has been passed to onDeviceReady function. Did you follow the guide correctly?", e);
    //     return false;
    //   }

    //   try
    //   {
    //     String packageName = cordova.getActivity().getApplicationContext().getPackageName();
    //     ApplicationInfo ai = cordova.getActivity().getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);

    //     if (ai.metaData != null && ai.metaData.containsKey("PW_NO_BROADCAST_PUSH"))
    //       broadcastPush = !(ai.metaData.getBoolean("PW_NO_BROADCAST_PUSH"));

    //     PWLog.debug(TAG, "broadcastPush = " + broadcastPush);
    //   }
    //   catch (Exception e)
    //   {
    //     PWLog.error(TAG, "Failed to read AndroidManifest");
    //   }

    //   try
    //   {
    //     //make sure the receivers are on
    //     registerReceivers();

    //     startPushData = getPushFromIntent(cordova.getActivity().getIntent());

    //     String appid = null;
    //     if (params.has("appid"))
    //       appid = params.getString("appid");
    //     else
    //       appid = params.getString("pw_appid");

    //     PushManager.initializePushManager(cordova.getActivity(), appid, params.getString("projectid"));
    //     mPushManager = PushManager.getInstance(cordova.getActivity());
    //     mPushManager.onStartup(cordova.getActivity());

    //     NotificationFactory factory = new NotificationFactory();
    //     factory.setPlugin(this);
    //     mPushManager.setNotificationFactory(factory);
    //   }
    //   catch (Exception e)
    //   {
    //     PWLog.error(TAG, "Missing pw_appid parameter. Did you follow the guide correctly?", e);
    //     return false;
    //   }

    //   checkMessage(cordova.getActivity().getIntent());
    //   return true;
    // }

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackId) {
        // PWLog.debug(TAG, "Plugin Method Called: " + action);

        Method method = exportedMethods.get(action);
        if (method == null) {
            // PWLog.debug(TAG, "Invalid action : " + action + " passed");
            return false;
        }

        try {
            Boolean result = (Boolean) method.invoke(this, data, callbackId);
            return result;
        } catch (Exception e) {
            // PWLog.error(TAG, "Failed to execute action : " + action, e);
            return false;
        }
    }

    // public void doOnPushReceived(String notification)
    // {
    //   PWLog.debug(TAG, "push received: " + notification);

    //   String jsStatement = String.format("cordova.require(\"pushwoosh-cordova-plugin.AppICE\").pushReceivedCallback(%s);", passData("data1"));
    //   evalJs(jsStatement);
    // }

    // private String passData(String notification)
    // {
    //   String result = "data2";

    //   return result;
    // }

    // private void evalJs(String statement)
    // {
    //   final String url = "javascript:" + statement;

    //   cordova.getActivity().runOnUiThread(new Runnable()
    //   {
    //     @Override
    //     public void run()
    //     {
    //       try
    //       {
    //         webView.loadUrl(url);
    //       }
    //       catch (Exception e)
    //       {
    //         PWLog.exception(e);
    //       }
    //     }
    //   });
    // }
}