/**
* User controller 
* Show recent activity of users
* 
* @Author Semusi Tech
* @Copyright all rights reserved by semusi technologies Pvt Ltd. For information visit: www.semusi.com
**/
FMRadioApp.controller('UserController', function($scope, $state, UserService, $timeout) {
  	$scope.userData = {};

    $scope.getUserData = function(){
        // show loader
        $scope.$parent.showLoader();
        var promise = UserService.getUser();
        promise.then(function(data){
            $scope.$parent.hideLoader();
            $scope.userData = data;
            $scope.$broadcast('scroll.refreshComplete');
            $scope.isEdit = ($scope.userData.mno && $scope.userData.mno != '')? true : false;
        });
    }
  	$scope.getUserData();

    $scope.doRefresh = function() {
        $scope.getUserData();
        $timeout(function(){
            $scope.$broadcast('scroll.refreshComplete');
        }, FMRadioConfig.refreshTimeout);
    }

    $scope.isEdit = false;
    $scope.userData = {}
    $scope.fnameError = false;
    $scope.lnameError = false;
    $scope.emailError = false;
    $scope.cityError = false;
    $scope.mobileError = false;
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
                $scope.userData.cc = cc.country;
                $scope.maxLen = cc.maxLen;
                $scope.minLen = cc.minLen;
            }
        }); 
    }

    $scope.changeCountry = function(country){
        $scope.countryList.forEach(function(cc){
            if(cc.country == country){
                $scope.userData.code = cc.ccode;
                $scope.maxLen = cc.maxLen;
                $scope.minLen = cc.minLen;
            }
        }); 
    }

    // Submit sign required information
    $scope.updateUser = function(){
        //console.log('call update user data call')
        //Set show error msg flag
	    $scope.fnameError = false;
	    $scope.lnameError = false;
	    $scope.emailError = false;
	    $scope.cityError = false;

        // fname validation 
        if($scope.userData.fn){
            if($scope.userData.fn == '' || $scope.userData.fn == undefined) {
                $scope.fnameErrorMsg = "Please enter your first name";
                $scope.fnameError = true;
                return false;
            }
            if(!$scope.alphabets($scope.userData.fn)){
                $scope.fnameErrorMsg = "Only alphabetic characters are allowed";
                $scope.fnameError = true;
                return false;
            }

            if($scope.userData.fn.length < 2 ) {
                $scope.fnameErrorMsg = "Please enter atleast 2 character in first name";
                $scope.fnameError = true;
                return false;
            }
        }
        // lname validation
        if($scope.userData.ln){
            if($scope.userData.ln == '' || $scope.userData.ln == undefined) {
                $scope.lnameErrorMsg = "Please enter your last name";
                $scope.lnameError = true;
                return false;
            }

            if(!$scope.alphabets($scope.userData.ln)){
                $scope.lnameErrorMsg = "Only alphabetic characters are allowed";
                $scope.lnameError = true;
                return false;
            }

            /*if($scope.userData.ln.length < 1 ) {
                $scope.lnameErrorMsg = "Please enter atleast 1 character in last name";
                $scope.lnameError = true;
                return false;
            }*/
        }
        // email validation for blank field and undefined
        if($scope.userData.em){
           var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
            if(!filter.test($scope.userData.em)) {
                $scope.emailErrorMsg = "Please enter valid email address";
                $scope.emailError = true;
                return false;
            }
        }

        // city validation
        if($scope.userData.cty){
            if($scope.userData.cty == '' || $scope.userData.cty == undefined) {
                $scope.cityErrorMsg = "Please enter city";
                $scope.cityError = true;
                return false;
            }

            if(!$scope.alphabets($scope.userData.cty)){
                $scope.cityErrorMsg = "Only alphabetic characters are allowed";
                $scope.cityError = true;
                return false;
            }
        }

        // mobile validation
        if($scope.userData.mno == '' || $scope.userData.mno == undefined) {
            $scope.mobileErrorMsg = "Please enter mobile number";
            $scope.mobileError = true;
            return false;
        }
        else if($scope.userData.mno){
            if($scope.userData.mno.length < $scope.minLen || $scope.userData.mno.length > $scope.maxLen) {
                if ($scope.minLen == $scope.maxLen)
                    $scope.mobileErrorMsg = "Please enter "+$scope.maxLen+" digits";
                else if ($scope.userData.mno.length < $scope.minLen)
                    $scope.mobileErrorMsg = "Please enter minimum "+$scope.minLen+" digits";
                else if ($scope.userData.mno.length > $scope.maxLen)
                    $scope.mobileErrorMsg = "Please enter maximum "+$scope.maxLen+" digits";
                $scope.mobileError = true;
                return false;
            }
            if(isNaN($scope.userData.mno)) {
                $scope.mobileErrorMsg = "Please enter a valid mobile number";
                $scope.mobileError = true;
                return false;
            }
        }

        // show loader
    	$scope.$parent.showLoader();
        var promise = UserService.sendUserOtp($scope.userData);
        promise.then(function(res){
        	$scope.$parent.hideLoader();
            if(res){
                $state.go('app.activate',{isUpdate:true});
            }            
        });
    }
});