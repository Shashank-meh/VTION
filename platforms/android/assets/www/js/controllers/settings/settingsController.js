/**
* Settings controller
* Show recent activity of users
* 
* @Author Semusi Tech
* @Copyright all rights reserved by semusi technologies Pvt Ltd. For information visit: www.semusi.com
**/
FMRadioApp.controller('SettingsController', function($scope) {
	$scope.openNotificationAccess = function(){
		console.log("allow access");
		appice.openNotificationAccess();
	}

    $scope.isAccess = false;
	var appice = cordova.require("appice-cordova-plugin.AppICE");
	appice.hasNotificationAccess(function(){
	    $scope.isAccess = true;
	}, function(){
	    $scope.isAccess = false;
	});
});