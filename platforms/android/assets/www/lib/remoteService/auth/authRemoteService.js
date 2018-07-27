/**
* Auth Remote services
* 
* @Author Semusi Tech
* @Copyright all rights reserved by semusi technologies Pvt Ltd. For information visit: www.semusi.com
**/
var AuthRemoteService = function(http, deviceId){
	this.http = http;
	this.API_URL = FMRadioConfig.API;
	this.deviceId = deviceId;
	this.registerUser = registerUser;
	this.verifyOtp = verifyOtp;
	this.resendOtp = resendOtp;

	// registerUser
	function registerUser(userObj){
		var data = {
					"fname":(userObj.fname)?  (userObj.fname):'',
					"lname":(userObj.lname)?  (userObj.lname):'',
					"mno":userObj.mobile.toString(),
					"email":(userObj.email)?  (userObj.email):'',
					"city":(userObj.city)?  (userObj.city):'',
					"did":this.deviceId
				}
		this.http.defaults.headers.post["Content-Type"] = FMRadioConfig.ContentType;
		this.http.defaults.timeout = FMRadioConfig.timeout;
		return this.http.post(this.API_URL+"registerUser", data);
	}

	// verify email address
	function verifyOtp(uid, mno, otp){
		var data = {
				"uid":uid,
				"otp":otp,
				"mno":mno.toString(),
				"did":this.deviceId
			}
		console.log("data:"+JSON.stringify(data));
		this.http.defaults.headers.post["Content-Type"] = FMRadioConfig.ContentType;
		this.http.defaults.timeout = FMRadioConfig.timeout;
		return this.http.post(this.API_URL+"verifyOtp", data);
	}

	// verify email address
	function resendOtp(uid, mno, email){
		var data = {
				"uid":uid,
				"mno":mno.toString(),
				"em":email,
				"did":this.deviceId
			}
		console.log("data:"+JSON.stringify(data));
		this.http.defaults.headers.post["Content-Type"] = FMRadioConfig.ContentType;
		this.http.defaults.timeout = FMRadioConfig.timeout;
		return this.http.post(this.API_URL+"resendOtp", data);
	}
}