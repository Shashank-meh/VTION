package vtion.util.constants;

/*
  UrlConstant file - Base url of backend server
                     Url of install, event and getTracking app
 */
public class UrlConstants {

    // Api base url
    public static final long nextTickTime = 15 * 60 * 1000;

    // production path
    public static final String ApiServerBaseUrl = "https://a.vtion.in/api/";
    public static final long dataSyncTimeMultiplier = 1;

    // staging path
//    public static final String ApiServerBaseUrl = "http://13.127.139.179/api/";
//    public static final long dataSyncTimeMultiplier = 1;

    // Register device one time on server
    public static final String DeviceRegisterUrl = "installdata?";

    // Session api on server
    public static final String SessionEventsUrl = "eventdata?";

    // Get api
    public static final String GetAppUserDataUrl = "getTrackAppList?";
}
