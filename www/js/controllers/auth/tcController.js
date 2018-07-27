/**
* T&C controller
* Show recent activity of users
* 
* @Author Semusi Tech
* @Copyright all rights reserved by semusi technologies Pvt Ltd. For information visit: www.semusi.com
**/
FMRadioApp.controller('TcController', function($scope, $state, AuthService) {
    $scope.tcData = "";
  	$scope.getTcContent = function(){
        $scope.$parent.showLoader();
        var promise = AuthService.getTcContent();
        promise.then(function(data){
            $scope.$parent.hideLoader();
            if(data){
                $scope.tcData = data;
            }
        })
    }

    $scope.getTcContent();

    $scope.acceptTermCondition = function(){
        AuthService.setAccept(true);
        $state.go('app.register');
    }
});