package in.vtion.libnotif;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.text.SpannableString;
import android.util.SparseArray;
import android.widget.RemoteViews;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import semusi.activitysdk.ContextSdk;

/**
 * Created by aman on 29/11/16.
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationService extends NotificationListenerService {

    private static final String sharedPrefsFile = "NCommonSharedPrefs";
    Context context;

    enum StreamType {
        NoStream("NoStream"), FMStream("FMStream"), AudioStream("AudioStream"), VideoStream("VideoStream");
        private String type;

        StreamType(String type) {
            this.type = type;
        }

        public String getType() {
            return this.type;
        }
    }

//    private ArrayList<String> fmPackages = new ArrayList<>();

    private ArrayList<String> fmPackages = new ArrayList<>(Arrays.asList("com.mediatek.FMRadio",
            "com.mediatek.fmradio", "com.miui.fmradio", "com.miui.fm", "com.sonyericsson.fmradio",
            "com.android.fmradio", "com.motorola.android.fmradio", "com.motorola.fmplayer",
            "com.thunderst.radio", "com.caf.fmradio", "com.codeaurora.fmrecording",
            "com.sec.android.app.fm", "com.htc.fm", "com.asus.fmradio", "com.lge.fmradio",
            "com.lenovo.fm", "com.huawei.android.FMRadio", "com.quicinc.fmradio", "com.vivo.FMRadio",
            "com.motorola.mtkfmplayer", "com.google.android.fmradio", "com.smartron.fmui"));

    private static List<String> fmAllowedList = new ArrayList<>();

    private HashMap<String, String[]> fmForePackagesIds = new HashMap<>();

    // Packages for master app
    private ArrayList<String> audioPackages = new ArrayList<>(Arrays.asList("com.saavn.android",
            "com.gaana", "com.soundcloud.android", "tunein.player", "radiotime.player",
            "com.spotify.music", "com.bsbportal.music", "com.google.android.music",
            "com.apple.android.music", "com.hungama.myplay.activity", "com.jio.media.jiobeats",
            "com.amazon.mp3", "au.com.radioapp"));

//    private ArrayList<String> audioPackages = new ArrayList<>(Arrays.asList("com.saavn.android", "com.gaana",
//            "com.google.android.music", "com.bsbportal.music", "com.hungama.myplay.activity",
//            "com.soundcloud.android", "com.apple.android.music",
//            "com.android.bbkmusic", "com.cyanogenmod.eleven", "com.lge.music", "com.htc.music",
//            "net.oneplus.music", "com.musicplayer.player.mp3player.white",
//            "com.spotify.music", "com.hulu.plus", "org.npr.one", "com.stitcher.app", "au.com.radioapp",
//            "tunein.player", "radiotime.player", "com.amazon.mp3"));

//    private ArrayList<String> audioPackages = new ArrayList<>();

    public static String lastEvent = "";
    public static String lastStation = "", lastStation1 = "", lastStation2 = "";
    public static boolean lastPlaying = false;
    public static long lastTime = 0;

    public static boolean isNotificationAccessStatusCleared = false;

    private void fillForePackageId() {
//        if (fmForePackagesIds != null && fmForePackagesIds.size() <= 0) {
//            fmForePackagesIds.put("com.motorola.fmplayer", new String[]{});
//            fmForePackagesIds.put("com.quicinc.fmradio", new String[]{"com.quicinc.fmradio:id/bai", "com.quicinc.fmradio:id/shi", "com.quicinc.fmradio:id/ge", "com.quicinc.fmradio:id/decimal"});
//            fmForePackagesIds.put("com.android.fmradio", new String[]{"com.android.fmradio:id/station_value"});
//            fmForePackagesIds.put("com.asus.fmradio", new String[]{});
//            fmForePackagesIds.put("com.sec.android.app.fm", new String[]{});
//        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        fillForePackageId();

        // Its our own app package
        NotificationManager manager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(53245);
        isNotificationAccessStatusCleared = true;

        checkPackageExists(context);
    }

    // Get competing app list to do dynamic handling
    private HashMap<String, String> getCompetingList(Context ctx) {
        String competingApps = new ContextSdk(ctx).getCurrentContext().getCompetingApps();
        HashMap<String, String> competingList = new HashMap<>();
        if (competingApps != null && competingApps.length() > 0) {
            try {
                JSONArray arr = new JSONArray(competingApps);
                if (arr != null && arr.length() > 0) {
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject element = (JSONObject) arr.get(i);
                        competingList.put(element.getString("package"), element.getString("urlscheme"));
                    }
                }
            } catch (Exception e) {
            }
        }
        return competingList;
    }

    // Get foreground issue based app list
    private HashMap<String, String> getForegroundList() {
        String competingApps = new ContextSdk(context).getCurrentContext().getCompetingApps();
        HashMap<String, String> competingList = new HashMap<>();
        if (competingApps != null && competingApps.length() > 0) {
            try {
                JSONArray arr = new JSONArray(competingApps);
                if (arr != null && arr.length() > 0) {
                    for (int i = 0; i < arr.length(); i++) {
                        String urlScheme = ((JSONObject) arr.get(i)).getString("urlscheme");
                        if (urlScheme != null && urlScheme.length() > 0 && urlScheme.startsWith("FIssue")) {
                            String pkgName = ((JSONObject) arr.get(i)).getString("package");
                            competingList.put(pkgName, pkgName + urlScheme.substring(urlScheme.indexOf(":")));
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
        return competingList;
    }

    // Fetch fm package list - matching name list
    private void checkPackageExists(Context ctx) {
        try {
            // Gather last cached bit of last check packages
            boolean lastUsed = loadBooleanSavedPreferences("NotificationPckSearch", ctx);
            if (!lastUsed) {
                // If not sent data to server - send data as event to server
                HashMap<String, String> competingList = getCompetingList(ctx);

                StringBuilder applist = new StringBuilder();
                boolean foundPkg = false;

                PackageManager pm = getPackageManager();
                List<ApplicationInfo> apps = pm.getInstalledApplications(0);

                // Check for system installed native fm apps
                scanApps:
                for (ApplicationInfo app : apps) {
                    String pack = app.packageName;

                    if (fmPackages.contains(pack) || competingList.keySet().contains(pack)) {
                        // Check for the launch intent of the same package
                        Intent launchIntent = pm.getLaunchIntentForPackage(pack);
                        if (launchIntent != null) {
                            foundPkg = true;

                            if (applist.length() > 0)
                                applist.append(";");
                            applist.append(pack);
                        }
                    }
                }

                if (foundPkg == false && applist.length() <= 0) {
                    for (ApplicationInfo app : apps) {
                        String pack = app.packageName;
                        boolean check1 = pack.toLowerCase().contains("fm");
                        boolean check2 = pack.toLowerCase().contains("radio");
                        //System.out.println("FMRadio check package of apps : " + pack + " , " + check1 + " , " + check2);
                        if ((check1 || check2)
                                && pack.equalsIgnoreCase(context.getPackageName()) == false) {
                            // Check for the launch intent of the same package
                            Intent launchIntent = pm.getLaunchIntentForPackage(pack);
                            if (launchIntent != null) {
                                foundPkg = false;

                                if (applist.length() > 0)
                                    applist.append(";");
                                applist.append(pack);
                            } else {
                                //System.out.println("FMRadio check package intent null :");
                            }
                        }
                    }
                }

                HashMap<String, Object> map = new HashMap<>();
                if (applist.length() > 0) {
                    // send event to server
                    map.put("state", "" + foundPkg);
                } else {
                    // send no package to server
                    map.put("state", "no-match");
                }

                map.put("packages", applist.toString());
                ContextSdk.tagEventObj("FM_CompetingApp", map, context);

                // Store last scanned bit to be stored
                saveBooleanPreferences("NotificationPckSearch", true, context);
            }
        } catch (Exception e) {
        }
    }

    // Get accessibility package list
    public static String[] getAccessibiltyAppPkg(Context ctx) {
        ArrayList<String> trackPkgs = new ArrayList<>();

        NotificationService notifService = new NotificationService();
        try {
            // Gather fm app package for accessibility
            HashMap<String, String> competingList = notifService.getCompetingList(ctx);

            PackageManager pm = ctx.getPackageManager();
            List<ApplicationInfo> apps = pm.getInstalledApplications(0);

            // Check for system installed native fm apps
            scanApps:
            for (ApplicationInfo app : apps) {
                String pack = app.packageName;

                if (notifService.fmPackages.contains(pack) || competingList.keySet().contains(pack) || notifService.audioPackages.contains(pack)) {
                    // Check for the launch intent of the same package
                    Intent launchIntent = pm.getLaunchIntentForPackage(pack);
                    if (launchIntent != null) {
                        trackPkgs.add(pack);
                    }
                }
            }
        } catch (Exception e) {
        }
        return (String[]) trackPkgs.toArray();
    }

    // Get FM app resource id's 
    public static String[] geptFMAppResourceId(String pkg) {
        String resourceId[] = new String[]{};
        try {
            if (pkg != null && pkg.length() > 0) {
                NotificationService notifService = new NotificationService();
                notifService.fillForePackageId();

                resourceId = notifService.fmForePackagesIds.get(pkg);
            }
        } catch (Exception e) {
        }
        return resourceId;
    }

    // Get current play state of music stream on device
    public static boolean getPlayingState(Context ctx) {
        AudioManager manager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        try {
            if (manager != null) {
                Thread.sleep(1000);
                boolean isMusicActive = manager.isMusicActive();
                boolean isMusicMute = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    isMusicMute = manager.isStreamMute(AudioManager.STREAM_MUSIC);
                if (isMusicActive || isMusicMute)
                    return true;
            }
        } catch (Exception e) {//
        }
        return false;
    }

    // Get audio mode of device
    public static String getAudioOutMode(Context ctx) {
        AudioManager manager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);

        int devices = -1;
        try {
            Method method = AudioManager.class.getDeclaredMethod("getDevicesForStream", Integer.TYPE);
            devices = (int) method.invoke(manager, AudioManager.STREAM_MUSIC);

            Field field1 = AudioManager.class.getField("DEVICE_OUT_SPEAKER");
            int val1 = field1.getInt(null);
            Field field2 = AudioManager.class.getField("DEVICE_OUT_WIRED_HEADSET");
            int val2 = field2.getInt(null);
            Field field3 = AudioManager.class.getField("DEVICE_OUT_WIRED_HEADPHONE");
            int val3 = field3.getInt(null);
            Field field4 = AudioManager.class.getField("DEVICE_OUT_BLUETOOTH_A2DP");
            int val4 = field4.getInt(null);
            Field field5 = AudioManager.class.getField("DEVICE_OUT_BLUETOOTH_A2DP_HEADPHONES");
            int val5 = field5.getInt(null);
            Field field6 = AudioManager.class.getField("DEVICE_OUT_BLUETOOTH_A2DP_SPEAKER");
            int val6 = field6.getInt(null);

            if ((devices & val1) == val1)
                return "Speaker";
            else if ((devices & val2) == val2)
                return "Wire_Headset";
            else if ((devices & val3) == val3)
                return "Wire_HeadPhone";
            else if ((devices & val4) == val4)
                return "BT";
            else if ((devices & val5) == val5)
                return "BT_HeadPhone";
            else if ((devices & val6) == val6)
                return "BT_Speaker";

        } catch (Exception e) {
        }

        if (devices == -1) {
            if (manager.isSpeakerphoneOn())
                return "Speaker";
            else if (manager.isWiredHeadsetOn())
                return "Headset";
            else if (manager.isBluetoothA2dpOn())
                return "BT";
        }
        return "UnKnown";
    }

    // Handle new notification posted by system
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        //
        String notificationPkg = sbn.getPackageName();
        if (notificationPkg != null && notificationPkg.length() > 0 && notificationPkg.equalsIgnoreCase(this.getApplicationContext().getPackageName())) {
            // Its our own app package
            NotificationManager manager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
            manager.cancel(53245);
            isNotificationAccessStatusCleared = true;
        } else {
            // Check for 3rd party apps packages
            StreamType currentStreamType = StreamType.NoStream;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    String pack = sbn.getPackageName();
                    HashMap<String, String> competingList = getCompetingList(this.getApplicationContext());

                    if (fmPackages.contains(pack)) {
//                        checkNotificationFrIssue = false;
                        currentStreamType = StreamType.FMStream;
                    } else if (audioPackages.contains(pack)) {
                        currentStreamType = StreamType.AudioStream;
                    } else if (competingList.keySet().contains(pack)) {
                        String issueDetail = competingList.get(pack);
                        if (issueDetail != null && issueDetail.length() > 0) {
                            if (issueDetail.startsWith("FM:"))
                                currentStreamType = StreamType.FMStream;
                            else if (issueDetail.startsWith("Audio:"))
                                currentStreamType = StreamType.AudioStream;
                        }
                    }

                    if (currentStreamType == StreamType.FMStream) {
                        try {
                            // Filter fm allowed list
                            if (competingList.keySet().contains("fm.allowed.list")) {
                                String issueDetail = competingList.get("fm.allowed.list");
                                fmAllowedList.clear();
                                if (issueDetail.length() > 0)
                                    fmAllowedList = Arrays.asList(issueDetail.split(","));
                            }
                        } catch (Exception e) {
                        }
                    }

                    if (currentStreamType != StreamType.NoStream) {
                        checkContentType1(sbn, currentStreamType);
                    }
                }
            } catch (Exception e) {
                checkContentType2(sbn, currentStreamType);
            }
        }
    }

    // Check audio content via Notification payload
    private void checkContentType1(StatusBarNotification sbn, StreamType streamType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Bundle extras = sbn.getNotification().extras;

            String title = "";
            Object titleObj = extras.get("android.title");
            if (titleObj.getClass().equals(android.text.SpannableString.class)) {
                SpannableString str = (SpannableString) titleObj;
                title = str.toString();
            } else if (titleObj.getClass().equals(java.lang.String.class))
                title = (String) titleObj;

            String text = extras.getCharSequence("android.text").toString();
            boolean currentState = getPlayingState(this.getApplicationContext());
            long currentTime = getCurrentTime(false);

            if ((text.contains("off") || text.contains("Off") || text.contains("OFF")) && lastEvent.equalsIgnoreCase("FM_Off")) {
                // For Motorola devices - fmOff is coming over in text content some times
                sendLastFMOffEvent(sbn.getPackageName());

                lastEvent = "FM_Off";
                
                ContextSdk.tagEventObj("FM_Off", null, context);
            } else if (lastEvent.equalsIgnoreCase(title + " , " + text) == false) {
                if (streamType == StreamType.FMStream) {
                    String station = "";
                    if (checkStationPattern(title) != 0)
                        station = checkStationPattern(title) + "";
                    else if (checkStationPattern(text) != 0)
                        station = checkStationPattern(text) + "";

                    if (station != null && station.length() > 0) {
                        if (lastStation != null && lastStation.length() > 0) {
                            int duration = (int) (currentTime - lastTime);

                            sendFMTuned(this.getApplicationContext(), lastStation, currentTime, false, duration, sbn.getPackageName());
                        }

                        lastStation = station;
                        lastEvent = title + " , " + text;
                        lastPlaying = currentState;
                        lastTime = currentTime;

                        sendFMTuned(this.getApplicationContext(), station, currentTime, currentState, 0, sbn.getPackageName());
                    } else {
                        checkContentType2(sbn, streamType);
                    }
                } else if (streamType == StreamType.AudioStream) {

                    if ((title != null && title.length() > 0) || (text != null && text.length() > 0)) {

                        if (title != null && title.length() > 0 && title.contains("i Music is running") == false) {
                            if (lastStation != null && lastStation.length() > 0) {
                                int duration = (int) (currentTime - lastTime);

                                sendAudioTuned(lastStation, lastStation1, false, currentTime, duration, lastStation2);

                                lastStation = "";
                                lastStation1 = "";
                                lastStation2 = "";
                            }

                            lastStation = title;
                            lastStation1 = text;
                            lastStation2 = sbn.getPackageName();

                            lastEvent = title + " , " + text;
                            lastPlaying = currentState;
                            lastTime = currentTime;

                            sendAudioTuned(title, text, currentState, currentTime, 0, sbn.getPackageName());
                        }
                    } else {
                        checkContentType2(sbn, streamType);
                    }
                }
            } else if (lastEvent.equalsIgnoreCase(title + " , " + text) && lastPlaying != currentState) {
                if (streamType == StreamType.FMStream) {
                    String station = "";
                    if (checkStationPattern(title) != 0)
                        station = checkStationPattern(title) + "";
                    else if (checkStationPattern(text) != 0)
                        station = checkStationPattern(text) + "";

                    if (station != null && station.length() > 0) {
                        lastStation = station;
                        lastEvent = title + " , " + text;
                        lastPlaying = currentState;

                        int duration = (int) (currentTime - lastTime);
                        lastTime = currentTime;

                        if (currentState)
                            sendFMTuned(this.getApplicationContext(), station, currentTime, currentState, 0, sbn.getPackageName());
                        else
                            sendFMTuned(this.getApplicationContext(), station, currentTime, currentState, duration, sbn.getPackageName());
                    } else {
                        checkContentType2(sbn, streamType);
                    }
                } else if (streamType == StreamType.AudioStream) {
                    if ((title != null && title.length() > 0) || (text != null && text.length() > 0)) {
                        lastEvent = title + " , " + text;
                        lastPlaying = currentState;

                        int duration = (int) (currentTime - lastTime);
                        lastTime = currentTime;
                        if (currentState)
                            sendAudioTuned(title, text, currentState, currentTime, 0, sbn.getPackageName());
                        else
                            sendAudioTuned(title, text, currentState, currentTime, duration, sbn.getPackageName());
                    } else {
                        checkContentType2(sbn, streamType);
                    }
                }
            }
        }
    }

    // Check content type - via looping internally into notification payload
    private void checkContentType2(StatusBarNotification sbn, StreamType streamType) {
        Notification notification = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
                notification = sbn.getNotification();

            if (notification != null) {

                boolean currentState = getPlayingState(this.getApplicationContext());
                long currentTime = getCurrentTime(false);

                if (streamType == StreamType.FMStream) {

                    RemoteViews rmViews = notification.contentView;
                    if (rmViews == null && Build.VERSION.SDK_INT >= 16) {
                        rmViews = notification.bigContentView;
                    }

                    if (rmViews != null) {
                        String newStation = checkRadioViewContent(rmViews);

                        if (newStation != null && newStation.length() > 0 && newStation.equalsIgnoreCase(lastEvent) == false) {

                            if (lastStation != null && lastStation.length() > 0) {
                                int duration = (int) (currentTime - lastTime);
                                sendFMTuned(this.getApplicationContext(), lastStation, currentTime, false, duration, sbn.getPackageName());
                            }

                            lastStation = newStation;
                            lastEvent = newStation;
                            lastPlaying = currentState;
                            lastTime = currentTime;

                            sendFMTuned(this.getApplicationContext(), newStation, currentTime, currentState, 0, sbn.getPackageName());

                        } else if (lastEvent.equalsIgnoreCase(newStation) && lastPlaying != currentState) {
                            lastEvent = newStation;
                            lastPlaying = currentState;

                            int duration = (int) (currentTime - lastTime);
                            lastTime = currentTime;

                            if (currentState)
                                sendFMTuned(this.getApplicationContext(), newStation, currentTime, currentState, 0, sbn.getPackageName());
                            else
                                sendFMTuned(this.getApplicationContext(), newStation, currentTime, currentState, duration, sbn.getPackageName());

                        } else if (newStation.length() <= 0 && Build.VERSION.SDK_INT >= 19) {
                            // Check for bitmap content
                            checkContentType3(notification, sbn.getPackageName());
                        }
                    }
                } else if (streamType == StreamType.AudioStream) {
                    StringBuilder song = new StringBuilder();
                    StringBuilder album = new StringBuilder();

                    // Check for basic rmView content
                    RemoteViews rmViews = notification.contentView;
                    if (rmViews != null) {
                        checkAudioViewContent(rmViews, song, album, true);
                    }

                    // Check for bitContentView for content
                    if (song == null || song.length() <= 0 || album == null || album.length() <= 0) {
                        if (Build.VERSION.SDK_INT >= 16) {
                            System.out.println("FMRadio : bigContent");
                            rmViews = notification.bigContentView;
                            if (rmViews != null) {
                                checkAudioViewContent(rmViews, song, album, false);
                            }
                        }
                    }

                    if (((song != null && song.length() > 0) || (album != null && album.length() > 0)) && lastEvent.equalsIgnoreCase(song + "," + album) == false) {

                        if (lastStation != null && lastStation.length() > 0) {
                            int duration = (int) (currentTime - lastTime);
                            sendAudioTuned(lastStation, lastStation1, false, currentTime, duration, lastStation2);
                            lastStation = "";
                            lastStation1 = "";
                            lastStation2 = "";
                        }

                        if (currentState != false) {
                            lastStation = song.toString();
                            lastStation1 = album.toString();
                            lastStation2 = sbn.getPackageName();

                            lastEvent = song + "," + album;
                            lastPlaying = currentState;
                            lastTime = currentTime;

                            sendAudioTuned(song.toString(), album.toString(), currentState, currentTime, 0, sbn.getPackageName());
                        }
                    } else if (lastEvent.equalsIgnoreCase(song + "," + album) && lastPlaying != currentState) {
                        lastEvent = song + "," + album;
                        lastPlaying = currentState;

                        int duration = (int) (currentTime - lastTime);
                        lastTime = currentTime;

                        if (currentState)
                            sendAudioTuned(song.toString(), album.toString(), currentState, currentTime, 0, sbn.getPackageName());
                        else
                            sendAudioTuned(song.toString(), album.toString(), currentState, currentTime, duration, sbn.getPackageName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Check radio view content
    private String checkRadioViewContent(RemoteViews rmViews) {
        String newStation = "";
        try {
            if (rmViews != null) {
                try {
                    Class secretClass = rmViews.getClass();
                    Field outerFields[] = secretClass.getDeclaredFields();

                    for (int i = 0; i < outerFields.length; i++) {
                        if (!outerFields[i].getName().equals("mActions"))
                            continue;
                        outerFields[i].setAccessible(true);

                        List<Object> actions = null;
                        if (outerFields[i].get(rmViews).getClass() == ArrayList.class) {
                            actions = (ArrayList<Object>) outerFields[i].get(rmViews);
                        } else {
                            actions = (CopyOnWriteArrayList<Object>) outerFields[i].get(rmViews);
                        }

                        for (Object action : actions) {
                            Field innerFields[] = action.getClass().getDeclaredFields();
                            for (Field field : innerFields) {
                                field.setAccessible(true);
                                if (field.getName().equals("value")) {
                                    try {
                                        String value = (String) field.get(action);
                                        if (value != null && checkStationPattern(value) != 0) {
                                            newStation = checkStationPattern(value) + "";
                                        }
                                    } catch (Exception e1) {
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e2) {
                }
            }
        } catch (Exception e3) {
        }
        return newStation;
    }

    // Check Audio view content
    private String checkAudioViewContent(RemoteViews rmViews, StringBuilder song, StringBuilder album, boolean direction) {
        String newStation = "";
        try {
            if (rmViews != null) {
                try {
                    Class secretClass = rmViews.getClass();
                    Field outerFields[] = secretClass.getDeclaredFields();
                    for (int i = 0; i < outerFields.length; i++) {
                    
                        if (!outerFields[i].getName().equals("mActions"))
                            continue;
                        outerFields[i].setAccessible(true);
                        List<Object> actions = null;
                        if (outerFields[i].get(rmViews).getClass() == ArrayList.class) {
                            actions = (ArrayList<Object>) outerFields[i].get(rmViews);
                        } else {
                            actions = (CopyOnWriteArrayList<Object>) outerFields[i].get(rmViews);
                        }
                        StringBuilder midSong = new StringBuilder(""), midAlbum = new StringBuilder("");
                        if (direction) {
                            outer:
                            for (int j = actions.size() - 1; j >= 0; j--) {
                                boolean checkData = checkAudioInternalContent(actions, j, midAlbum, midSong, song, album);
                                if (checkData) {
                                    break outer;
                                }
                            }
                        } else {
                            outer:
                            for (int j = 0; j < actions.size(); j++) {
                                boolean checkData = checkAudioInternalContent(actions, j, midAlbum, midSong, song, album);
                                if (checkData) {
                                    break outer;
                                }
                            }
                        }
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        } catch (Exception e3) {
            e3.printStackTrace();
        }

        return newStation;
    }

    // Check for audio internal content for fetching list of song-album match
    private boolean checkAudioInternalContent(List<Object> actions, int j, StringBuilder midAlbum, StringBuilder midSong, StringBuilder song, StringBuilder album) {
        Object action = actions.get(j);

        Field innerFields[] = action.getClass().getDeclaredFields();
        inner:
        for (Field field : innerFields) {
            field.setAccessible(true);
            
            if (field.getName().equals("value")) {
                try {
                    String value = "";
                    if (field.get(action).getClass().equals(android.text.SpannableString.class)) {
                        SpannableString str = (SpannableString) field.get(action);
                        value = str.toString();
                    } else if (field.get(action).getClass().equals(java.lang.String.class))
                        value = (String) field.get(action);
                        
                    if (value != null && value.length() > 0) {
                        if ((midAlbum != null && midAlbum.length() <= 0) || (!midAlbum.equals(midSong) && midSong.length() > 0)) {
                            midAlbum = new StringBuilder(value);
                        } else if ((midSong != null && midSong.length() <= 0) || (!midSong.equals(midAlbum) && midAlbum.length() > 0)) {
                            if (!midAlbum.equals(value))
                                midSong = new StringBuilder(value);
                        }
                    }

                    if (midSong != null && midSong.length() > 0 && midAlbum != null && midAlbum.length() > 0 && !midSong.equals(midAlbum)) {

                        song.append(midSong);
                        album.append(midAlbum);

                        return true;
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
        return false;
    }

    // Gather OCR based - fm data from image content : Step3
    private void checkContentType3(Notification notification, String appPkg) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                try {
                    Class.forName("");
                } catch (ClassNotFoundException e) {
                }

                Bitmap ob1 = (Bitmap) notification.extras.get(Notification.EXTRA_LARGE_ICON);
                Bitmap ob2 = (Bitmap) notification.extras.get(Notification.EXTRA_LARGE_ICON_BIG);
                if (ob1 != null || ob2 != null) {
                    TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
                    try {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        if (ob1 != null)
                            ob1.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        else if (ob2 != null)
                            ob2.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

                        if (byteArrayOutputStream != null && byteArrayOutputStream.size() > 0) {
                            byte[] byteArray = byteArrayOutputStream.toByteArray();
                            String text = "";
                            BitmapRegionDecoder bmpAreaDecoder = BitmapRegionDecoder.newInstance(byteArray, 0, byteArray.length, false);
                            Bitmap bmpFromDecodedRegion = bmpAreaDecoder.decodeRegion(new Rect(0, 0, ob1.getWidth(), ob1.getHeight()), null);
                            SparseArray<TextBlock> textList = textRecognizer.detect(new Frame.Builder().setBitmap(bmpFromDecodedRegion).build());
                            for (int i = 0; i < textList.size(); ++i) {
                                TextBlock item = textList.valueAt(i);
                                if (item != null && item.getValue() != null) {
                                    text = item.getValue();
                                }
                            }

                            text = text.replaceAll(",", ".").replaceAll("I", "1");

                            boolean currentState = getPlayingState(this.getApplicationContext());
                            long currentTime = getCurrentTime(false);

                            if ((text.contains("off") || text.contains("Off") || text.contains("OFF")) && lastEvent.equalsIgnoreCase("FM_Off") == false) {
                                sendLastFMOffEvent(appPkg);

                                lastEvent = "FM_Off";
                                //System.out.println("FMRadio : Notifi : FM_Off3");
                                ContextSdk.tagEventObj("FM_Off", null, context);
                            } else if (lastEvent.equalsIgnoreCase(text) == false) {

                                String station = "";
                                if (containsDigit(text))
                                    station = text;

                                if (station != null && station.length() > 0) {

                                    if (lastStation != null && lastStation.length() > 0) {
                                        int duration = (int) (currentTime - lastTime);
                                        sendFMTuned(this.getApplicationContext(), lastStation, currentTime, false, duration, appPkg);
                                    }

                                    lastEvent = text;
                                    lastStation = station;
                                    lastPlaying = currentState;
                                    lastTime = currentTime;

                                    sendFMTuned(this.getApplicationContext(), station, currentTime, currentState, 0, appPkg);
                                }
                            } else if (lastEvent.equalsIgnoreCase(text) && lastPlaying != currentState) {

                                String station = "";
                                if (containsDigit(text))
                                    station = text;

                                lastEvent = station;
                                lastPlaying = currentState;

                                int duration = (int) (currentTime - lastTime);
                                lastTime = currentTime;

                                if (currentState)
                                    sendFMTuned(this.getApplicationContext(), station, currentTime, currentState, 0, appPkg);
                                else
                                    sendFMTuned(this.getApplicationContext(), station, currentTime, currentState, duration, appPkg);

                            }
                        }
                    } catch (Exception e2) {
                    }
                    textRecognizer.release();
                }
            } catch (Exception e2) {
            }
        }
    }

    // Get device time utc or gmt
    public static long getCurrentTime(boolean giveUTC) {
        long currentTime = (System.currentTimeMillis() / 1000);
        if (!giveUTC)
            currentTime += TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 1000;
        return currentTime;
    }

    // Get app name for package name
    public static String getAppName(String packageName, Context ctx) {
        String appName = "";
        try {
            PackageManager packageManager = ctx.getPackageManager();
            appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
        } catch (Exception e) {
        }
        return appName;
    }

    // Send FM Tuned event data to server with map
    public static void sendFMTuned(Context ctx, String station, long time, boolean playingState, int duration, String appPkg) {
        boolean isAllowed = true;
        if (fmAllowedList != null && fmAllowedList.size() > 0) {
            if (!fmAllowedList.contains(station))
                isAllowed = false;
        }
        if (isAllowed) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("Station", station);
//        map.put("Time", "" + time);
            map.put("PState", "" + playingState);
            if (duration > 0)
                map.put("Duration", "" + duration);
//        map.put("Pkg", appPkg);
            map.put("App", getAppName(appPkg, ctx));
            map.put("Source", getAudioOutMode(ctx));
            if (playingState == false && duration <= 0) {
                // Done send pause event with 0 duration
            } else {
                System.out.println("FMRadio : Sending FM_Tunned : " + map.toString());

                ContextSdk.tagEventObj("FM_Tuned", map, ctx);
            }
        }
    }
    
    // Send Audio Tuned event data to server with map
    private void sendAudioTuned(String song, String album, boolean playingState, long time, int duration, String appPkg) {
        HashMap<String, Object> map = new HashMap<>();
        if (song != null && song.length() > 0) {
            try {
                if (appPkg.equalsIgnoreCase("au.com.radioapp"))
                    map.put("Program", album);
                else
                    map.put("Song", song);
            } catch (Exception e) {
            }
        }
        if (album != null && album.length() > 0) {
            try {
                if (appPkg.equalsIgnoreCase("au.com.radioapp"))
                    map.put("Channel", song);
                else
                    map.put("Album", album);
            } catch (Exception e) {
            }
        }
//        map.put("Time", "" + time);
        map.put("PState", "" + playingState);
        if (duration > 0)
            map.put("Duration", "" + duration);
//        map.put("Pkg", appPkg);
        map.put("App", getAppName(appPkg, this.getApplicationContext()));
        map.put("Source", getAudioOutMode(this.getApplicationContext()));
        if (playingState == false && duration <= 0) {
            // Done send pause event with 0 duration
        } else {
            System.out.println("FMRadio : Sending Audio_Tunned : " + map.toString());

            ContextSdk.tagEventObj("Audio_Tuned", map, context);
        }
    }

    // Check for any station pattern in ocr content
    private float checkStationPattern(String text) {
        String pattern = "(\\d{1,3},\\d{3}(,\\d{3})*)(\\.\\d*)?|\\d+\\.?\\d*";
        Matcher m = Pattern.compile(pattern).matcher(text);
        float station = 0;
        while (m.find()) {
            String value = m.group();
            if (value.contains(".")) {
                try {
                    float floatVal = Float.parseFloat(value);
                    if (floatVal > 87.5 && floatVal <= 108) {
                        if (floatVal != station)
                            station = floatVal;
                    }
                } catch (Exception e) {
                }
            }
        }
        return station;
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        handleNotificationRemoved(sbn);
    }

//    public static boolean checkNotificationFrIssue = false;
//    public static String checkNotificationFrNode = "";

    // Handle notification removal state - look for which package remove has happened
    private void handleNotificationRemoved(StatusBarNotification sbn) {
        StreamType currentStreamType = StreamType.NoStream;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String pack = sbn.getPackageName();
                HashMap<String, String> competingList = getCompetingList(this.getApplicationContext());

                if (fmPackages.contains(pack)) {
                    currentStreamType = StreamType.FMStream;
                } else if (audioPackages.contains(pack)) {
                    currentStreamType = StreamType.AudioStream;
                } else if (competingList.keySet().contains(pack)) {
                    String issueDetail = competingList.get(pack);
                    if (issueDetail != null && issueDetail.length() > 0) {
                        if (issueDetail.startsWith("FM:"))
                            currentStreamType = StreamType.FMStream;
                        else if (issueDetail.startsWith("Audio:"))
                            currentStreamType = StreamType.AudioStream;
                    }
                }

                if (currentStreamType == StreamType.FMStream) {
                    // Check for app in foreground
                    String activePackages = ContextSdk.getForegroundAppPkgs(getApplicationContext(), pack);
                    if (activePackages != null && activePackages.length() > 0 && activePackages.equalsIgnoreCase(pack)) {
                        // Do not do anything
                        // Same as last frequency as app mite be brought to front
                        HashMap<String, String> foregroundList = getForegroundList();
                        if (fmForePackagesIds.get(pack) != null) {
                            // We have issue with these package check for other apps
//                            checkNotificationFrIssue = true;
//                            checkNotificationFrNode = fmForePackagesIds.get(pack);
                        } else if (foregroundList.get(pack) != null) {
                            // We have issue with these package check for other apps
//                            checkNotificationFrIssue = true;
//                            checkNotificationFrNode = foregroundList.get(pack);
                        } else if (lastEvent != null && lastEvent.length() > 0 && lastEvent.equalsIgnoreCase("FM_Off") == false) {
                            sendLastFMOffEvent(sbn.getPackageName());

                            lastEvent = "FM_Off";
                            ContextSdk.tagEventObj("FM_Off", null, context);
                        }
                    } else if (lastEvent != null && lastEvent.length() > 0 && lastEvent.equalsIgnoreCase("FM_Off") == false) {
                        sendLastFMOffEvent(sbn.getPackageName());

                        lastEvent = "FM_Off";
                        ContextSdk.tagEventObj("FM_Off", null, context);
                    }
                } else if (currentStreamType == StreamType.AudioStream) {
                    if (lastEvent != null && lastEvent.length() > 0 && lastEvent.equalsIgnoreCase("Audio_Off") == false) {
                        sendLastAudioOffEvent(sbn.getPackageName());

                        lastEvent = "Audio_Off";
                        ContextSdk.tagEventObj("Audio_Off", null, context);
                    }
                }
            } else {
            }
        } catch (Exception e) {
        }
    }

    // Send Last fm off event - for package
    private void sendLastFMOffEvent(String appPkg) {
        long currentTime = getCurrentTime(false);
        if (lastStation != null && lastStation.length() > 0 && lastPlaying) {
            int duration = (int) (currentTime - lastTime);
            sendFMTuned(this.getApplicationContext(), lastStation, currentTime, false, duration, appPkg);
        }

        lastStation = "";
        lastPlaying = false;
        lastTime = currentTime;
    }
    
    // Send Last audio off event - for package
    private void sendLastAudioOffEvent(String appPkg) {
        long currentTime = getCurrentTime(false);
        if (lastStation != null && lastStation.length() > 0 && lastPlaying) {
            int duration = (int) (currentTime - lastTime);
            sendAudioTuned(lastStation, lastStation1, false, currentTime, duration, lastStation2);
        }

        lastStation = "";
        lastStation1 = "";
        lastStation2 = "";

        lastPlaying = false;
        lastTime = currentTime;
    }

	// Binder to connect via bindService
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

	// Check for digit contains
    private boolean containsDigit(String s) {
        boolean containsDigit = false;

        if (s != null && !s.isEmpty()) {
            for (char c : s.toCharArray()) {
                if (containsDigit = Character.isDigit(c)) {
                    break;
                }
            }
        }

        return containsDigit;
    }

	// Store boolean to shared preference
    private void saveBooleanPreferences(String key, boolean value,
                                        Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(sharedPrefsFile,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

	// Load boolean to shared preference
    private boolean loadBooleanSavedPreferences(String key, Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(sharedPrefsFile,
                Context.MODE_PRIVATE);
        boolean name = sp.getBoolean(key, false);
        return name;
    }
}
