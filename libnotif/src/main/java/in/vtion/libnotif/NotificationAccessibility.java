package in.vtion.libnotif;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Created by aman on 30/11/16.
 */

public class NotificationAccessibility extends AccessibilityService {

    String deviceFmResourceId[] = new String[]{};

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        final int eventType = event.getEventType();
        final int changeType = event.getContentChangeTypes();
        final int actionType = event.getAction();

        if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED/* && NotificationService.checkNotificationFrIssue*/) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                AccessibilityNodeInfo nodeInfo = event.getSource();
                if (nodeInfo != null) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        String album = "";
                        String song = "";

                        List<AccessibilityNodeInfo> nodeAlbum = nodeInfo.findAccessibilityNodeInfosByViewId("in.startv.hotstar:id/metadata_title");
                        for (AccessibilityNodeInfo info : nodeAlbum) {
                            if (info != null && info.getClassName().equals("android.widget.TextView")) {
                                album = info.getText().toString();
                                break;
                            }
                        }
                        List<AccessibilityNodeInfo> nodeSong = nodeInfo.findAccessibilityNodeInfosByViewId("in.startv.hotstar:id/metadata_subtitle");
                        for (AccessibilityNodeInfo info : nodeSong) {
                            if (info != null && info.getClassName().equals("android.widget.TextView")) {
                                song = info.getText().toString();
                                break;
                            }
                        }

                        System.out.println("Accessibility Info : " + album + " , Song : " + song );
                    }
                }
            }
        }
    }

    @Override
    public void onInterrupt() {
//        System.out.println("FMRadio : Accessi : interrupt");
    }

    private final AccessibilityServiceInfo info = new AccessibilityServiceInfo();

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onServiceConnected() {
//        System.out.println("FMRadio : Accessi : onConnected");
        // Set the type of events that this service wants to listen to.
        //Others won't be passed to this service.
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;

        // If you only want this service to work with specific applications, set their
        // package names here.  Otherwise, when the service is activated, it will listen
        // to events from all applications.
        //String[] pkgs = NotificationService.getAccessibiltyAppPkg(this.getApplicationContext());
        info.packageNames = new String[]{"in.startv.hotstar"};

//        if (pkgs != null && pkgs.length > 0) {
//            deviceFmResourceId = NotificationService.getFMAppResourceId(pkgs[0]);
//        }

        // Set the type of feedback your service will provide.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        } else {
            info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        }

        // Default services are invoked only if no package-specific ones are present
        // for the type of AccessibilityEvent generated.  This service *is*
        // application-specific, so the flag isn't necessary.  If this was a
        // general-purpose service, it would be worth considering setting the
        // DEFAULT flag.
//        info.flags = AccessibilityServiceInfo.DEFAULT;

        this.setServiceInfo(info);
    }
//
//    /**
//     * Check if Accessibility Service is enabled.
//     *
//     * @param mContext
//     * @return <code>true</code> if Accessibility Service is ON, otherwise <code>false</code>
//     */
//    public static boolean isAccessibilitySettingsOn(Context mContext) {
//        int accessibilityEnabled = 0;
//        final String service = mContext.getPackageName() + "/semusi.analytics.handler.NotificationAccessibility";
//
//        boolean accessibilityFound = false;
//        try {
//            accessibilityEnabled = Settings.Secure.getInt(
//                    mContext.getApplicationContext().getContentResolver(),
//                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
//        } catch (Exception e) {
////            e.printStackTrace();
//        }
//        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
//
//        if (accessibilityEnabled == 1) {
//            // Accessibility enabled
//            String settingValue = Settings.Secure.getString(
//                    mContext.getApplicationContext().getContentResolver(),
//                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
//            if (settingValue != null) {
//                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
//                splitter.setString(settingValue);
//                while (splitter.hasNext()) {
//                    String splittedService = splitter.next();
//                    if (splittedService.equalsIgnoreCase(service)) {
//                        return true;
//                    }
//                }
//            }
//        } else {
//            // Accessibility disabled
//        }
//
//        return accessibilityFound;
//    }
}
