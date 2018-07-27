package com.vtion.kantarradio;

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

        Api.startContext(this, new SdkConfig());
    }
}
