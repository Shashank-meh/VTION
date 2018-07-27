/**
* Activate user account controller
* Show recent activity of users
* 
* @Author Semusi Tech
* @Copyright all rights reserved by semusi technologies Pvt Ltd. For information visit: www.semusi.com
**/
FMRadioApp.controller('ActivateController', function($scope, $state, $stateParams, AuthService, UserService) {
  	// define register object
  	$scope.verify = {}
  	$scope.otpError = false;
  	$scope.isDashboard = ($stateParams.isUpdate)? false : true;

  	$scope.verifyOTP = function(){
  		//Set show error msg flag
        $scope.otpError = false;

	    // mobile validation
        if($scope.verify.otp == '' || $scope.verify.otp == undefined) {
            $scope.otpErrorMsg = "Please enter otp code";
            $scope.otpError = true;
            return false;
        }
        else if($scope.verify.otp){ 
        	if($scope.verify.otp.length < 4) {
	            $scope.otpErrorMsg = "Please enter 4 digit otp code";
	            $scope.otpError = true;
	            return false;
	        }
	        if(isNaN($scope.verify.otp)) {
	            $scope.otpErrorMsg = "Please enter numeric digit in otp code";
	            $scope.otpError = true;
	            return false;
	        }
        }

  		// show loader
		$scope.$parent.showLoader();
		var isToast = ($stateParams.isUpdate)? false : true;
	    var promise = AuthService.verifyOtp($scope.verify.otp, isToast, UserService.getUserData());
	    promise.then(function(res){
	    	$scope.$parent.hideLoader();
	        if(res){
	        	if($stateParams.isUpdate){
	        		$scope.$parent.showLoader();
	        		UserService.updateUser().then(function(res){
	        			$scope.$parent.hideLoader();
	        			if(res){
	        				$state.go('app.user-details');
	        			}
	        		});	        		
	        	}else{
	        		$state.go('app.start');
	        	}	            
	        }            
	    });
  	}

  	$scope.resendOTP = function(){
  		// show loader
		$scope.$parent.showLoader();
	    var promise = AuthService.resendOtp();
	    promise.then(function(res){
	    	$scope.$parent.hideLoader();            
	    });
  	}
});