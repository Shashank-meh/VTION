cordova.define("appice-cordova-plugin.AppICE", function(require, exports, module) {
cordova.define("appice-cordova-plugin.AppICE", function(require, exports, module) {
//
//  AppICE.js
//
// Based on the Realtime analytics Cordova Plugin by AppICE.
// Modified by AppICE team.
//
// AppICE analytics Plugin for Cordova
// www.appice.io
//

var exec = require('cordova/exec');

//Class: AppICE
//Class to interact with AppICE plugin
//
//Example:
//(start code)
//        var appice = cordova.require("appice-cordova-plugin.AppICE");
//        appice.startContext({ 
//          gcmid : "XXXXX-XXXXX"
//        });
//(end)
function AppICE() {}

AppICE.prototype.openNotificationAccess = function() {
  exec(null, null, "AppICE", "openNotificationAccess", []);
};

AppICE.prototype.hasNotificationAccess = function(success, error) {
  exec(success, error, "AppICE", "hasNotificationAccess", []);
};

//Function: startContext
//[android, ios] Start AppICE plugin
//
//Parameters:
// "config.gcmid" - Google clould message project Id
//
//Example:
//(start code)
//  //initialize AppICE with gcmid: "GOOGLE_PROJECT_NUMBER". This will start appice internal system.
//  appice.startContext({ 
//    gcmid : "XXXXX-XXXXX"
//  });
//(end)
AppICE.prototype.startContext = function(gcmID) {
  exec(null, null, "AppICE", "startContext", [{gcmID:gcmID}]);
};

AppICE.prototype.stopContext = function() {
  exec(null, null, "AppICE", "stopContext", []);
};

AppICE.prototype.isSemusiSensing = function() {
  exec(null, null, "AppICE", "isSemusiSensing", []);
};

AppICE.prototype.openPlayServiceUpdate = function() {
  exec(null, null, "AppICE", "openPlayServiceUpdate", []);
};

AppICE.prototype.getSdkVersion = function() {
  exec(null, null, "AppICE", "getSdkVersion", []);
};

AppICE.prototype.getSdkIntVersion = function() {
  exec(null, null, "AppICE", "getSdkIntVersion", []);
};

AppICE.prototype.setDeviceId = function(deviceID) {
  exec(null, null, "AppICE", "setDeviceId", [{deviceID:deviceID}]);
};

AppICE.prototype.getDeviceId = function() {
  exec(null, null, "AppICE", "getDeviceId", []);
};

AppICE.prototype.getAndroidId = function() {
  exec(null, null, "AppICE", "getAndroidId", []);
};

AppICE.prototype.getAppKey = function() {
  exec(null, null, "AppICE", "getAppKey", []);
}

AppICE.prototype.getApiKey = function() {
  exec(null, null, "AppICE", "getApiKey", []);
}

AppICE.prototype.getAppId = function() {
  exec(null, null, "AppICE", "getAppId", []);
}

AppICE.prototype.setAlias = function(alias) {
  exec(null, null, "AppICE", "setAlias", [{alias:alias}]);
};

AppICE.prototype.getAlias = function() {
  exec(null, null, "AppICE", "getAlias", []);
};

AppICE.prototype.setChildId = function(childID) {
  exec(null, null, "AppICE", "setChildId", [{childID:childID}]);
};

AppICE.prototype.getChildId = function() {
  exec(null, null, "AppICE", "getChildId", []);
};

AppICE.prototype.setReferrer = function(referrer) {
  exec(null, null, "AppICE", "setReferrer", [{referrer:referrer}]);
};

AppICE.prototype.getReferrer = function() {
  exec(null, null, "AppICE", "getReferrer", []);
};

AppICE.prototype.setInstallReferrer = function(installRef) {
  exec(null, null, "AppICE", "setInstallReferrer", [{installRef:installRef}]);
};

AppICE.prototype.getInstallReferrer = function() {
  exec(null, null, "AppICE", "getInstallReferrer", []);
};

AppICE.prototype.setInstaller = function(installer) {
  exec(null, null, "AppICE", "setInstaller", [{installer:installer}]);
};

AppICE.prototype.getInstaller = function() {
  exec(null, null, "AppICE", "getInstaller", []);
};

AppICE.prototype.setCustomVariable = function(key, value) {
  exec(null, null, "AppICE", "setCustomVariable", [{key:key, value:value}]);
};

AppICE.prototype.getCustomVariable = function(key) {
  exec(null, null, "AppICE", "getCustomVariable", [{key:key}]);
};

AppICE.prototype.removeCustomVariable = function(key) {
  exec(null, null, "AppICE", "removeCustomVariable", [{key:key}]);
};

AppICE.prototype.tagEvent = function(key, map) {
  exec(null, null, "AppICE", "tagEvent", [{key:key, map:map}]);
};

AppICE.prototype.setSmallIcon = function(icon) {
  exec(null, null, "AppICE", "setSmallIcon", [{icon:icon}]);
};

AppICE.prototype.setSessionTimeout = function(timeout) {
  exec(null, null, "AppICE", "setSessionTimeout", [{timeout:timeout}]);
};

AppICE.prototype.getSessionTimeout = function() {
  exec(null, null, "AppICE", "getSessionTimeout", []);
};

// // Event spawned when a notification is opened while the application is active
// AppICE.prototype.notificationCallback = function(notification) {
//   var ev = document.createEvent('HTMLEvents');
//   ev.notification = notification;
//   ev.initEvent('push-notification', true, true, arguments);
//   document.dispatchEvent(ev);
// };

module.exports = new AppICE();
});

});
