package com.android.vtionmaster;

import semusi.activitysdk.Api;
import semusi.activitysdk.ContextApplication;
import semusi.activitysdk.SdkConfig;

public class MainApplication extends ContextApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        Api.startContext(this, new SdkConfig());
    }
}
