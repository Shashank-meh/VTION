package in.vtion.vtion_sdk;

import semusi.activitysdk.Api;
import semusi.activitysdk.ContextApplication;
import semusi.activitysdk.SdkConfig;

public class MainApplication extends ContextApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        startSdk();
    }

    private void startSdk() {
        Api.startContext(getApplicationContext(), new SdkConfig());
    }
}
