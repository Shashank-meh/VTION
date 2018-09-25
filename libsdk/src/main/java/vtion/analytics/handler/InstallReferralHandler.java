package vtion.analytics.handler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;

import java.util.List;

import vtion.activitysdk.ContextSdk;
import vtion.context.utility.Utility;

/*
    InstallReferralHandler - class to handle referral data during app install
 */
public class InstallReferralHandler extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean useIntent = false;
        try {
            String lastSavedRef = ContextSdk.getReferrer(context);
            if (lastSavedRef != null && lastSavedRef.length() > 0) {
                String referrer = intent.getStringExtra("referrer");
                if (referrer != null && referrer.length() > 0) {
                    if (referrer.equalsIgnoreCase(lastSavedRef) == false)
                        useIntent = true;
                }
            } else {
                useIntent = true;
            }
        } catch (Exception e) {
        }

        if (useIntent) {
            // Handle intent for Vtion
            try {
                String referrer = intent.getStringExtra("referrer");

                Utility.loginfo("Vtion Current Referrer info : " + referrer);

                if (referrer != null) {
                    ContextSdk.setReferrer(referrer, context);
                }

                String installer = context.getPackageManager().getInstallerPackageName(
                        context.getPackageName());
                if (installer != null && installer.length() > 0) {
                    ContextSdk.setInstaller(installer, context);
                } else {
                    ContextSdk.setInstaller("self", context);
                }
            } catch (Exception e) {
            }

            // Pass intent to other sdks
            try {
                List<ResolveInfo> receivers = context.getPackageManager()
                        .queryBroadcastReceivers(
                                new Intent("com.android.vending.INSTALL_REFERRER"), 0);
                for (ResolveInfo resolveInfo : receivers) {
                    String action = intent.getAction();
                    if ((resolveInfo.activityInfo.packageName.equals(context
                            .getPackageName()))
                            && ("com.android.vending.INSTALL_REFERRER".equals(action))
                            && (!getClass().getName().equals(
                            resolveInfo.activityInfo.name))) {
                        try {
                            Utility.loginfo("Vtion Passing install referrer to : " + resolveInfo.activityInfo.name);
                            BroadcastReceiver broadcastReceiver = (BroadcastReceiver) Class
                                    .forName(resolveInfo.activityInfo.name)
                                    .newInstance();
                            broadcastReceiver.onReceive(context, intent);
                        } catch (Throwable e) {
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
    }
}
