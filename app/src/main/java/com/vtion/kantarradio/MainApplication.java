package com.vtion.kantarradio;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import semusi.activitysdk.Api;
import semusi.activitysdk.ContextApplication;
import semusi.activitysdk.SdkConfig;

/**
 * Created by aman on 25/01/18.
 */

public class MainApplication extends ContextApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        Api.startContext(this, new SdkConfig());
    }
}
