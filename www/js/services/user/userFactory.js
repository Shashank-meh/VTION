/**
* Register and verify otp factory
*
* @Author Semusi Tech
* @Copyright all rights reserved by semusi technologies Pvt Ltd. For information visit: www.semusi.com
**/
FMRadioApp.factory('UserService', function($http, $q, $Toaster, AuthService) {
	var userRemoteService = new UserRemoteService($http, deviceInfo.getDeviceID());
	var authRemoteService = new AuthRemoteService($http, deviceInfo.getDeviceID());
	var userData = {};

	function setUserData(data){
		userData = data;
	}

	function getUserData(){
		return userData;
	}

	// sign up process
	function getUser(){
		var deferred = $q.defer();
		if(userInfo.getMno()){
			if(deviceInfo.checkConnection()){
				userRemoteService.getUser(userInfo.getMno()).then(function(response) {
					if (response.data.msg == "success") {
						deferred.resolve(response.data.data);
					} else {
						$Toaster.showError(response.data.msg);
						deferred.resolve(false);
					}
				}, function(error) {
					$Toaster.showError(error);
					deferred.resolve(false);
				});
			}
			else{
				$Toaster.showError('No internet connection');
				deferred.resolve(false);
			}
		}
		else{
			deferred.resolve(false);
		}
		return deferred.promise;
	}
	// sign up process
	function updateUser(){
		var userjOb = userData;
		userjOb.oldmno = userInfo.getMno();
		var deferred = $q.defer();
		if(deviceInfo.checkConnection()){
			userRemoteService.updateUser(userjOb).then(function(response) {
				if (response.data.msg == "success") {
					$Toaster.showSuccess(response.data.msg);
					userInfo.setMno(userjOb.mno);
                    userInfo.setEmail(userjOb.em);
					deferred.resolve(true);
				} else {
					$Toaster.showError(response.data.msg);
					deferred.resolve(false);
				}
			}, function(error) {
				$Toaster.showError(error);
				deferred.resolve(false);
			});
		}
		else{
			$Toaster.showError('No internet connection');
			deferred.resolve(false);
		}

		return deferred.promise;
	}

	// sign up process
	function sendUserOtp(userjOb){
		// set user data
		setUserData(userjOb);
		AuthService.setData(userjOb);
		
		var deferred = $q.defer();
		if(deviceInfo.checkConnection()){
			authRemoteService.resendOtp(userInfo.getUserId(), parseInt(userjOb.mno), userjOb.em, userjOb.code).then(function(response) {
				if (response.data.msg == "success") {
					//$Toaster.showSuccess(response.data.msg);
					deferred.resolve(true);
				} else {
					$Toaster.showError(response.data.msg);
					deferred.resolve(false);
				}
			}, function(error) {
				$Toaster.showError(error);
				deferred.resolve(false);
			});
		}
		else{
			$Toaster.showError('No internet connection');
			deferred.resolve(false);
		}

		return deferred.promise;
	}

	// return signup function object
	return{
		getUser:getUser,
		updateUser:updateUser,
		sendUserOtp:sendUserOtp,
		getUserData:getUserData
	}
});