```
// Steps to use plugin

// Create sample project or use your own
if (cordova-plugin) {
    // Use below to create project 
    // myfolder : directory under which app to create
    // org.apache.cordova.myApp : app package name
    // myApp : app name
    cordova create myfolder org.apache.cordova.myApp myApp
}
else if (ionic-plugin) {
    // Use below to create project
    // myfolder : directory under which app to create
    // sidemenu : ionic framework to use
    ionic start myfolder sidemenu
}

// Switch to myfolder
cd myfolder

// Add android platform to project
if (cordova-plugin) {
    // If you are using plugin as cordova wrapper
    cordova platform add android --save
}
else if (ionic-plugin) {
    // If you are using plugin as ionic wrapper
    ionic platform add android
}

// Add appice plugin to project
if (cordova-plugin) {
    // If you are using plugin as cordova wrapper
    cordova plugin add http://192.168.1.123/Aman.Aggarwal/appice-cordova-plugin.git
}
else if (ionic-plugin) {
    // If you are using plugin as ionic wrapper
    ionic plugin add http://192.168.1.123/Aman.Aggarwal/appice-cordova-plugin.git
}

// Add appice meta-data keys to android platform
// Update below meta-keys of appice with your appice project keys
// Under : myfolder/platforms/android/android.json -> "/manifest/application" tag
{
    "xml": "<meta-data android:name=\"com.semusi.analytics.appid\" android:value=\"58988861f49528de059bb59b\" />",
    "count": 1
},
{
    "xml": "<meta-data android:name=\"com.semusi.analytics.appkey\" android:value=\"111c97dcccfd9aa1b5570f3f59e09e336d6a9d79\" />",
    "count": 1
},
{
    "xml": "<meta-data android:name=\"com.semusi.analytics.apikey\" android:value=\"d985715d1bb48942d36d5d08de3b6a8c\" />",
    "count": 1
}

// Sync project with plugin and platform
if (cordova-plugin) {
    // If you are using plugin as cordova wrapper
    cordova prepare
}
else if (ionic-plugin) {
    // If you are using plugin as ionic wrapper
    ionic prepare
}

// To use appice in your project
var appICESDK = cordova.require("appice-cordova-plugin.AppICE");
appICESDK.startContext("<Custom gcm id to use>");

// To send events via appice
appICESDK.tagEvent("<Key of event>")

// To set custom variables
appICESDK.setCustomVariable("<variable name>","<variable value>");

// Build project and run
if (cordova-plugin) {
    // If you are using plugin as cordova wrapper
    cordova build android
    cordova run android
}
else if (ionic-plugin) {
    // If you are using plugin as ionic wrapper
    ionic run android -l -c
}

```