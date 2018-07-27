(function(FMRadioConfig){
	FMRadioConfig.API = 'http://52.70.106.8:83/v1/';
	FMRadioConfig.API2 = 'http://52.70.106.8:83/v2/';
	FMRadioConfig.ContentType = 'application/json';
	FMRadioConfig.timeout = 5000;
	FMRadioConfig.refreshTimeout = 3000;
	// encode and decode functions
	FMRadioConfig.encode = function(string){
		return btoa(string);
	}
	FMRadioConfig.decode = function(string){
		return atob(string);
	}

	// define database tables name
	FMRadioConfig.FM_TUNNED = 'tunned';
	FMRadioConfig.FM_USERINFO = 'userInfo';

	// create connection with database
	FMRadioConfig.initilizeConnection = function(){
		FMRadioConfig.db = window.openDatabase("fm_radio_app.db", "1.0", "Cordova Demo", 5000000);
		console.log('connection open::');
	}
	// create table if not exits
	FMRadioConfig.createDefaultTable = function(database){
		var tunned = [
		              "date",
		              "channel",
		              "pay_time",
		              "createdAt",
		              "updatedAt"
		    ];
		database.createTables(FMRadioConfig.FM_TUNNED, tunned); // create tunned table

		var userInfo = [
		              "fname",
		              "lname",
		              "city",
		              "email",
		              "mob",
		              "status",
		              "createdAt",
		              "updatedAt"
		    ];
		database.createTables(FMRadioConfig.FM_USERINFO, userInfo); // create userinfo table
	}

	FMRadioConfig.getCurrentEpochTime = function(){
		return moment().valueOf()/1000;
	}
}(window.FMRadioConfig = window.FMRadioConfig || {}));