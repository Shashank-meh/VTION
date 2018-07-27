/**
* Settings controller
* Show recent activity of users
* 
* @Author Semusi Tech
* @Copyright all rights reserved by semusi technologies Pvt Ltd. For information visit: www.semusi.com
**/
FMRadioApp.controller('SettingsController', function($scope) {
	$scope.openNotificationAccess = function(){
		var appice = cordova.require("appice-cordova-plugin.AppICE");
		appice.openNotificationAccess();

        // Send current state of notification access
		var appice = cordova.require("appice-cordova-plugin.AppICE");
        appice.hasNotificationAccess(function(){
            var appice = cordova.require("appice-cordova-plugin.AppICE");
            var tmpObj = {
                state:"true"
            }
            appice.tagEvent("NotificationState", tmpObj);
        }, function(){
            var appice = cordova.require("appice-cordova-plugin.AppICE");
            var tmpObj = {
                state:"false"
            }
        	appice.tagEvent("NotificationState", tmpObj);
        });
	}

    $scope.isAccess = false;
	var appice = cordova.require("appice-cordova-plugin.AppICE");
	appice.hasNotificationAccess(function(){
	    $scope.isAccess = true;
	}, function(){
	    $scope.isAccess = false;
	});
});