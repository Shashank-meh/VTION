/**
* Auth Remote services
* 
* @Author Semusi Tech
* @Copyright all rights reserved by semusi technologies Pvt Ltd. For information visit: www.semusi.com
**/
var AuthRemoteService = function(http, deviceId){
	this.http = http;
	this.API_URL = FMRadioConfig.API;
	this.API_URL2 = FMRadioConfig.API2;
	this.deviceId = deviceId;
	this.registerUser = registerUser;
	this.verifyOtp = verifyOtp;
	this.resendOtp = resendOtp;
	this.getTcContent = getTcContent;

	// registerUser
	function registerUser(userObj){
		var data = {
					"fname":(userObj.fname)?  (userObj.fname):'',
					"lname":(userObj.lname)?  (userObj.lname):'',
					"code":userObj.ccode,
					"mno":userObj.mobile.toString(),
					"email":(userObj.email)?  (userObj.email):'',
					"city":(userObj.city)?  (userObj.city):'',
					"cc":userObj.country,
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
		this.http.defaults.headers.post["Content-Type"] = FMRadioConfig.ContentType;
		this.http.defaults.timeout = FMRadioConfig.timeout;
		return this.http.post(this.API_URL+"verifyOtp", data);
	}

	// verify email address
	function resendOtp(uid, mno, email, code){
		var data = {
				"uid":uid,
				"mno":mno.toString(),
				"code":code,
				"em":email,
				"did":this.deviceId
			}
		console.log("data:"+JSON.stringify(data));
		this.http.defaults.headers.post["Content-Type"] = FMRadioConfig.ContentType;
		this.http.defaults.timeout = FMRadioConfig.timeout;
		return this.http.post(this.API_URL2+"resendOtp", data);
	}

	// get t&c data
	function getTcContent(){
		var data = {
				"did":this.deviceId
			}
		this.http.defaults.headers.post["Content-Type"] = FMRadioConfig.ContentType;
		this.http.defaults.timeout = FMRadioConfig.timeout;
		return this.http.post(this.API_URL+"getTcContent", data);
	}
}