/**
* Registration controller
* Show recent activity of users
* 
* @Author Semusi Tech
* @Copyright all rights reserved by semusi technologies Pvt Ltd. For information visit: www.semusi.com
**/
FMRadioApp.controller('RegistrerController', function($scope, $state, AuthService) {
  	// define register object
  	$scope.register = {}
    $scope.mobileError = false;
    $scope.fnameError = false;
    $scope.lnameError = false;
    $scope.emailError = false;
    $scope.cityError = false;

    $scope.alphabets = function(inputtxt){
        var letters = /^[A-Za-z ]+$/;
        if(inputtxt.match(letters)){
          return true;
        }
        else{
          return false;
        }
    }

    // Submit sign required information
    $scope.registerUser = function(){
        console.log('call')
        //Set show error msg flag
        $scope.mobileError = false;
	    $scope.fnameError = false;
	    $scope.lnameError = false;
	    $scope.emailError = false;
	    $scope.cityError = false;

	    // mobile validation
        if($scope.register.mobile == '' || $scope.register.mobile == undefined) {
            $scope.mobileErrorMsg = "Please enter your mobile number";
            $scope.mobileError = true;
            return false;
        }
        else if($scope.register.mobile){
        	if($scope.register.mobile.length < 10) {
	            $scope.mobileErrorMsg = "Please enter 10 digit mobile number";
	            $scope.mobileError = true;
	            return false;
	        }
	        if(isNaN($scope.register.mobile)) {
	            $scope.mobileErrorMsg = "Please enter numeric digit in mobile number";
	            $scope.mobileError = true;
	            return false;
	        }
        }

        // fname validation
        if($scope.register.fname){
            if($scope.register.fname == '' || $scope.register.fname == undefined) {
                $scope.fnameErrorMsg = "Please enter your first name";
                $scope.fnameError = true;
                return false;
            }

            if(!$scope.alphabets($scope.register.fname)){
                $scope.fnameErrorMsg = "Please enter alphabetic characters in first name";
                $scope.fnameError = true;
                return false;
            }

            if($scope.register.fname.length < 2 ) {
                $scope.fnameErrorMsg = "Please enter atleast 2 character in first name";
                $scope.fnameError = true;
                return false;
            }
        }
        // lname validation
        if($scope.register.lname){
            if($scope.register.lname == '' || $scope.register.lname == undefined) {
                $scope.lnameErrorMsg = "Please enter your last name";
                $scope.lnameError = true;
                return false;
            }

            if(!$scope.alphabets($scope.register.lname)){
                $scope.lnameErrorMsg = "Please enter alphabetic characters in last name";
                $scope.lnameError = true;
                return false;
            }

            /*if($scope.register.fname.length < 1 ) {
                $scope.lnameErrorMsg = "Please enter atleast 1 character in last name";
                $scope.lnameError = true;
                return false;
            }*/
        }
        // email validation for blank field and undefined
        if($scope.register.email){
           var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
            if(!filter.test($scope.register.email)) {
                $scope.emailErrorMsg = "Please enter valid email address";
                $scope.emailError = true;
                return false;
            }
        }

        // city validation
        if($scope.register.city){
            if($scope.register.city == '' || $scope.register.city == undefined) {
                $scope.cityErrorMsg = "Please enter city";
                $scope.cityError = true;
                return false;
            }

            if(!$scope.alphabets($scope.register.city)){
                $scope.cityErrorMsg = "Please enter alpha characters in city";
                $scope.cityError = true;
                return false;
            }
        }

        // show loader
    	$scope.$parent.showLoader();
        var promise = AuthService.register($scope.register);
        promise.then(function(res){
        	$scope.$parent.hideLoader();
            if(res){
                $state.go('app.activate');
            }            
        });
    }
});