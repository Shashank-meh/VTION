/**
* User controller 
* Show recent activity of users
* 
* @Author Semusi Tech
* @Copyright all rights reserved by semusi technologies Pvt Ltd. For information visit: www.semusi.com
**/
FMRadioApp.controller('UserController', function($scope, $state, UserService) {
  	$scope.userData = {};

    $scope.getUserData = function(){
        // show loader
        $scope.$parent.showLoader();
        var promise = UserService.getUser();
        promise.then(function(data){
            $scope.$parent.hideLoader();
            $scope.userData = data;
            $scope.$broadcast('scroll.refreshComplete');
        });
    }
  	$scope.getUserData();

    $scope.doRefresh = function() {
        $scope.getUserData();
        /*$timeout( function() {
          //Stop the ion-refresher from spinning
          $scope.$broadcast('scroll.refreshComplete');
        }, 1000);*/
    }

    $scope.isEdit = false;
    $scope.userData = {}
    $scope.fnameError = false;
    $scope.lnameError = false;
    $scope.emailError = false;
    $scope.cityError = false;
    $scope.mobileError = false;

    $scope.userData.mno = userInfo.getMno();
    $scope.isEdit = ($scope.userData.mno && $scope.userData.mno != '')? true : false;
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
    $scope.updateUser = function(){
        console.log('call update user data call')
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
                $scope.fnameErrorMsg = "Please enter alpha characters in first name";
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
                $scope.lnameErrorMsg = "Please enter alpha characters in last name";
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
                $scope.cityErrorMsg = "Please enter alpha characters in city";
                $scope.cityError = true;
                return false;
            }
        }

        // mobile validation
        if($scope.userData.mno == '' || $scope.userData.mno == undefined) {
            $scope.mobileErrorMsg = "Please enter your mobile number";
            $scope.mobileError = true;
            return false;
        }
        else if($scope.userData.mno){
            if($scope.userData.mno.length < 10) {
                $scope.mobileErrorMsg = "Please enter 10 digit mobile number";
                $scope.mobileError = true;
                return false;
            }
            if(isNaN($scope.userData.mno)) {
                $scope.mobileErrorMsg = "Please enter numeric digit in mobile number";
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