/**
* User Remote services
* 
* @Author Semusi Tech
* @Copyright all rights reserved by semusi technologies Pvt Ltd. For information visit: www.semusi.com
**/
var UserRemoteService = function(http, deviceId){
	this.http = http;
	this.API_URL = FMRadioConfig.API;
	this.deviceId = deviceId;
	this.getUser = getUser;
	this.updateUser = updateUser;
	this.updateUserOtp = updateUserOtp;

	// get user data
	function getUser(mno){
		var data = {
					"mno":mno,
					"did":this.deviceId
				}
		this.http.defaults.headers.post["Content-Type"] = FMRadioConfig.ContentType;
		this.http.defaults.timeout = FMRadioConfig.timeout;
		return this.http.post(this.API_URL+"user/"+mno, data);
	}

	// update user information
	function updateUser(userObj){
		var data = {
					"fname":userObj.fn,
					"lname":userObj.ln,
					"mno":userObj.mno.toString(),
					"oldmno":userObj.oldmno.toString(),
					"email":userObj.em,
					"city":userObj.cty,
					"did":this.deviceId
				}
		this.http.defaults.headers.post["Content-Type"] = FMRadioConfig.ContentType;
		this.http.defaults.timeout = FMRadioConfig.timeout;
		return this.http.post(this.API_URL+"updateUser", data);
	}

	// update user information
	function updateUserOtp(userObj){
		var data = {
					"mno":userObj.mno.toString(),
					"email":userObj.em,
					"did":this.deviceId
				}
		this.http.defaults.headers.post["Content-Type"] = FMRadioConfig.ContentType;
		this.http.defaults.timeout = FMRadioConfig.timeout;
		return this.http.post(this.API_URL+"resendOtp", data);
	}
}