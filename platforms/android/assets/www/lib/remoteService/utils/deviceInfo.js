(function(deviceInfo){
	deviceInfo.checkConnection = function() {
		try{
			var networkState = navigator.connection.type;
			console.log('networkState::'+networkState);
			if(networkState != '' || networkState != 'No network connection'){
				return true;
			}
		  	else{
		  		return false;
		  	}
		}
		catch(e){
			console.log(JSON.stringify(e));
			return true;
		} 	
	}

	deviceInfo.getDeviceInfo = function($cordovaDevice) {
		var data = {};
		try{
			data.device = $cordovaDevice.getDevice();
			data.model = $cordovaDevice.getModel();
			data.platform = $cordovaDevice.getPlatform();
			data.uuid = $cordovaDevice.getUUID();
			data.version = $cordovaDevice.getVersion();
		}
		catch(e){
			console.log(JSON.stringify(e));
		}
		return data;
	}

	deviceInfo.getDeviceID = function($cordovaDevice) {
		var uuid = 11111;
		try{
			uuid = $cordovaDevice.getUUID();
		}
		catch(e){
			console.log(JSON.stringify(e));
		}
		return uuid;
	}

}(window.deviceInfo = window.deviceInfo || {}));