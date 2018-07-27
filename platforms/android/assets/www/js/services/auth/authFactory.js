/**
* Register and verify otp factory
*
* @Author Semusi Tech
* @Copyright all rights reserved by semusi technologies Pvt Ltd. For information visit: www.semusi.com
**/
FMRadioApp.factory('AuthService', function($http, $q, $Toaster) {
	var authRemoteService = new AuthRemoteService($http, deviceInfo.getDeviceID());
	var userDataObj = {};
	function setData(data){
        userDataObj = data;
	}

	function getData(){
	    return userDataObj;
	}

	// sign up process
	function register(userObj){
		var deferred = $q.defer();
		if(deviceInfo.checkConnection()){
			authRemoteService.registerUser(userObj).then(function(response) {
				if (response.data.msg == "success") {
				    setData(userObj);
					userInfo.setUserId(response.data.userid);
					userInfo.setMno(userObj.mobile);
					userInfo.setEmail(userObj.email);

					// Send registration event to appice
					var appice = cordova.require("appice-cordova-plugin.AppICE");
                    userObj.status = "Success";
                    appice.tagEvent("Registration", userObj);

                    // Set User custom variables
                    appice.setCustomVariable("Mobile",userObj.mobile);
                    appice.setCustomVariable("FName",userObj.fname);
                    appice.setCustomVariable("LName",userObj.lname);
                    appice.setCustomVariable("Email",userObj.email);
                    appice.setCustomVariable("City",userObj.city);

					deferred.resolve(true);
				} else {
					$Toaster.showError(response.data.msg);

					var appice = cordova.require("appice-cordova-plugin.AppICE");
                    userObj.status = "Failed";
                    userObj.msg = response.data.msg;
                    appice.tagEvent("Registration", userObj);

					deferred.resolve(false);
				}
			}, function(error) {
				$Toaster.showError(error);

				var appice = cordova.require("appice-cordova-plugin.AppICE");
                userObj.status = "Failed";
                userObj.error = error;
                appice.tagEvent("Registration", userObj);

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
	function verifyOtp(otp, isToast, userData){
		var deferred = $q.defer();
		if(deviceInfo.checkConnection()){
			console.log("isToast "+isToast);
			var mno = (!isToast)? parseInt(userData.mno) : userInfo.getMno();
			console.log("mno "+mno);

			authRemoteService.verifyOtp(userInfo.getUserId(), mno, otp).then(function(response) {
				if (response.data.msg == "success") {
					if(isToast){
						$Toaster.showSuccess(response.data.msg);
						userData = getData();
						//userInfo.setToken(response.data.token);
					}

                    if (userData) {
					    var appice = cordova.require("appice-cordova-plugin.AppICE");
                        userData.status = "Success";
                        if (isToast)
                            appice.tagEvent("OTP", userData);
                        else {
                            var tmpObj = {
                                email:userData.em,
                                fname:userData.fn,
                                lname:userData.ln,
                                mobile:userData.mno,
                                city:userData.cty,
                                status:'Success'
                            }
                            appice.tagEvent("Profile", tmpObj);
                        }

                        // Set User custom variables
                        appice.setCustomVariable("Mobile",userData.mobile);
                        appice.setCustomVariable("FName",userData.fname);
                        appice.setCustomVariable("LName",userData.lname);
                        appice.setCustomVariable("Email",userData.email);
                        appice.setCustomVariable("City",userData.city);
                    }

					deferred.resolve(true);
				} else {
					$Toaster.showError(response.data.msg);

					if (userData) {
                    	var appice = cordova.require("appice-cordova-plugin.AppICE");
                        userData.status = "Failed";
                        userData.msg = response.data.msg;
                        if (isToast)
                            appice.tagEvent("OTP", userData);
                        else
                            appice.tagEvent("Profile", userData);
                    }

					deferred.resolve(false);
				}
			}, function(error) {
				$Toaster.showError("Something is wrong");

				if (userData) {
                    var appice = cordova.require("appice-cordova-plugin.AppICE");
                    userData.status = "Failed";
                    userData.error = error;
                    if (isToast)
                        appice.tagEvent("OTP", userData);
                    else
                        appice.tagEvent("Profile", userData);
                }

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
	function resendOtp(otp){
		var deferred = $q.defer();
		if(deviceInfo.checkConnection()){
			authRemoteService.resendOtp(userInfo.getUserId(), userInfo.getMno(), userInfo.getEmail()).then(function(response) {
				if (response.data.msg == "success") {
					$Toaster.showSuccess(response.data.msg);
					//userInfo.setToken(response.data.token);
					deferred.resolve(true);
				} else {
					$Toaster.showError(response.data.msg);
					deferred.resolve(false);
				}
			}, function(error) {
				$Toaster.showError("Something is wrong");
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
		register:register,
		verifyOtp:verifyOtp,
		resendOtp:resendOtp
	}
});