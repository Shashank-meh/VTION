package in.vtion.ui;

import android.content.Context;

import vtion.activitysdk.Api;
import vtion.activitysdk.ContextApplication;

/**
 * Extend Application class with ContextApplication class
 *
 * @author mac
 */
public class MainApplication extends ContextApplication {

    private static MainApplication mInstance = null;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        restartSdk(this.getApplicationContext());
    }

    public static void restartSdk(Context ctx) {
        ContextApplication.initSdk(ctx, mInstance);

        // Init sdk with your config
        Api.startContext(ctx);
    }

    public static void stopSdk(Context ctx) {
        Api.stopContext(ctx);
    }
}
