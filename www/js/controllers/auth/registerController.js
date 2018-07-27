/**
* Registration controller
* Show recent activity of users
* 
* @Author Semusi Tech
* @Copyright all rights reserved by semusi technologies Pvt Ltd. For information visit: www.semusi.com
**/
FMRadioApp.controller('RegistrerController', function($scope, $state, AuthService) {

  	// define register object
  	$scope.register = AuthService.getData();
    $scope.register.tc = AuthService.getAccept();
    $scope.mobileError = false;
    $scope.fnameError = false;
    $scope.lnameError = false;
    $scope.emailError = false;
    $scope.cityError = false;
    $scope.tcError = false;
    $scope.maxLen = 10;
    $scope.minLen = 10;
    $scope.countryList = [
                            {
                                id:1,
                                country:'India',
                                ccode:'+91',
                                maxLen:10,
                                minLen:10
                            },
                            {
                                id:2,
                                country:'Germany',
                                ccode:'+49',
                                maxLen:14,
                                minLen:10
                            }
                        ];

    $scope.alphabets = function(inputtxt){
        var letters = /^[A-Za-z ]+$/;
        if(inputtxt.match(letters)){
          return true;
        }
        else{
          return false;
        }
    }

    $scope.changeCcode = function(ccode){
        $scope.countryList.forEach(function(cc){
            if(cc.ccode == ccode){
                $scope.register.country = cc.country;
                $scope.maxLen = cc.maxLen;
                $scope.minLen = cc.minLen;
            }
        }); 
    }

    $scope.changeCountry = function(country){
        $scope.countryList.forEach(function(cc){
            if(cc.country == country){
                $scope.register.ccode = cc.ccode;
                $scope.maxLen = cc.maxLen;
                $scope.minLen = cc.minLen;
            }
        }); 
    }

    $scope.acceptTerm = function(){
        AuthService.setAccept($scope.register.tc);
    }

    $scope.viewTC = function(){
        AuthService.setData($scope.register);
        $state.go('app.tc');
    }

    // Submit sign required information
    $scope.registerUser = function(){
        //console.log('call')
        //Set show error msg flag
        $scope.mobileError = false;
	    $scope.fnameError = false;
	    $scope.lnameError = false;
	    $scope.emailError = false;
	    $scope.cityError = false;
        $scope.tcError = false;

	    // mobile validation
        if($scope.register.mobile == '' || $scope.register.mobile == undefined) {
            $scope.mobileErrorMsg = "Please enter a valid number";
            $scope.mobileError = true;
            return false;
        }
        else if($scope.register.mobile){
        	if($scope.register.mobile.length < $scope.minLen || $scope.register.mobile.length > $scope.maxLen) {
	            if ($scope.minLen == $scope.maxLen)
	                $scope.mobileErrorMsg = "Please enter "+$scope.maxLen+" digits";
	            else if ($scope.register.mobile.length < $scope.minLen)
	                $scope.mobileErrorMsg = "Please enter minimum "+$scope.minLen+" digits";
	            else if ($scope.register.mobile.length > $scope.maxLen)
                	$scope.mobileErrorMsg = "Please enter maximum "+$scope.maxLen+" digits";
	            $scope.mobileError = true;
	            return false;
	        }
	        if(isNaN($scope.register.mobile)) {
	            $scope.mobileErrorMsg = "Please enter a valid number";
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
                $scope.fnameErrorMsg = "Only alphabetic characters are allowed";
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
                $scope.lnameErrorMsg = "Only alphabetic characters are allowed";
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
                $scope.cityErrorMsg = "Only alphabetic characters are allowed";
                $scope.cityError = true;
                return false;
            }
        }

        // Term and condition validation
        if(!$scope.register.tc) {
            $scope.tcErrorMsg = "Please accept Terms & Conditions";
            $scope.tcError = true;
            return false;
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