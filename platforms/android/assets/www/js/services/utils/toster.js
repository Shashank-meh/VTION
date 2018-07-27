/**
* Toster factory manage all toast
* 
* @Author Semusi Tech
* @Copyright all rights reserved by semusi technologies Pvt Ltd. For information visit: www.semusi.com
**/
FMRadioApp.factory('$Toaster', function($q, $mdToast) {
	function showSuccess(success) {
		//console.log("Remote call succeed: " + JSON.stringify(success));
		message = null;
		if (typeof success === "string") {
			message = success;
		} else {
			message = success.data.message;
		}
		$mdToast.show($mdToast.simple().textContent(message).theme("success-toast"));
	}

	function showError(error) {
		//console.log(typeof error);
		//console.log("Remote call failed: " + JSON.stringify(error));
		message = null;
		if (typeof error === "string") {
			message = error;
		} else {
			if (error.data == null) {
				message = "You seem to have lost internet connection";
			} else {
				message = error.data.message;
			}
		}
		$mdToast.show($mdToast.simple().textContent(message).theme("error-toast"));
	}

	return {
		showSuccess : showSuccess,
		showError : showError
	};
});