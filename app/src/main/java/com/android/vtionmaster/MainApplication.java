package com.android.vtionmaster;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import semusi.activitysdk.Api;
import semusi.activitysdk.ContextApplication;
import semusi.activitysdk.SdkConfig;

public class MainApplication extends ContextApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        Api.startContext(this, new SdkConfig());

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }
}
