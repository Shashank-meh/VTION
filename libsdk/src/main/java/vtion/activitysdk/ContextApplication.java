package vtion.activitysdk;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;

import vtion.context.utility.Utility;

/*
    Custom Application class to handle sdk init functionality
 */
@SuppressLint("NewApi")
public class ContextApplication extends Application {

    public static Context appContext = null;

    public static Application appApplication = null;

    @Override
    public void onCreate() {
        super.onCreate();

        // Init sdk
        try {
            initSdk(getApplicationContext(), this);
        } catch (Exception e) {
        }
    }

    public static void handleUncaughtException(Thread thread, String platform, Throwable e) {
        // Convert crash to string map
        String exeStr = null;
        try {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            exeStr = sw.toString();
        } catch (Exception e1) {
        }

        try {
            if (appContext == null)
                appContext = appApplication.getApplicationContext();

            dumpUncaughtException(appContext, platform, exeStr);
        } catch (Exception e2) {
        }
    }

    public static void dumpUncaughtException(Context ctx, String platform, String exeStr) {
        try {
            HashMap<String, Object> map = new HashMap<>();
            if (exeStr != null && exeStr.length() > 0) {
                map.put("stack", exeStr);
                map.put("platform", platform);
            }
            Utility.loginfo("Traced crash Event : " + Arrays.asList(map));

            // Send event to server
            ContextSdk.tagEventObj("_app_crash", map, ctx);
        } catch (Exception e) {
        }
    }


    public static boolean isInitDone = false;

    @TargetApi(14)
    public static void initSdk(Context ctx, Application app) {
        // Store context of either application context or ctx provided
        if (app != null && app.getApplicationContext() != null)
            ContextApplication.appContext = app.getApplicationContext();
        else if (ctx != null)
            ContextApplication.appContext = ctx;

        // Store application object for usage in appsflyer
        if (app != null)
            ContextApplication.appApplication = app;

        // Make sure init is done single time otherwise lifecycle callback are getting added twice
        if (isInitDone) {
            return;
        }
        isInitDone = true;

        // Setup handler for uncaught exceptions.
        final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {

                handleUncaughtException(thread, "native", e);

                defaultHandler.uncaughtException(thread, e);
            }
        });
    }
}
