/**
* FM Radio App
* Show Recent radio tuned activity and manage user registration information
* 
* @Author Semusi Tech
* @Copyright all rights reserved by semusi technologies Pvt Ltd. For information visit: www.semusi.com
**/
var db, appice;

var FMRadioApp = angular.module('starter', ['ionic','ngMaterial', 'ngCordova'])
.run(function($ionicPlatform, $cordovaSQLite, $state, $stateParams) {
  $ionicPlatform.ready(function() {
    if(window.cordova && window.cordova.plugins.Keyboard) {
      cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
    }
    if(window.StatusBar) {
      StatusBar.styleDefault();
    }

//    $ionicPlatform.onHardwareBackButton(function() {
//               event.preventDefault();
//               event.stopPropagation();
//               alert('going back now y'all');
//            });

    $ionicPlatform.registerBackButtonAction(function (event) {
        if($state.current.name=="app.start"){
            navigator.app.exitApp();
        }
        else if($state.current.name=="app.activate"){
            if($stateParams.isUpdate){
                $state.go("app.user-details")
            }
            else{
                $state.go("app.start")
            }
        }
        else if($state.current.name=="app.user-details"){
            $state.go("app.start");
        }
        else {
            navigator.app.backHistory();
        }
        //alert('state: '+$state.current.name);
    }, 100);

    appice = cordova.require("appice-cordova-plugin.AppICE");
    appice.startContext("");

    // for opening a background db:
    db = $cordovaSQLite.openDB({ name: "eventDb.db", iosDatabaseLocation:'default'});//$cordovaSQLite.openDB({ name: "eventDb.db", bgType: 1 });
  });

//  FMRadioConfig.initilizeConnection(); // initialize db connection
//  FMRadioConfig.createDefaultTable(database); // create default table
})
.config(function($stateProvider, $urlRouterProvider, $ionicConfigProvider, $mdThemingProvider) {
  $ionicConfigProvider.views.maxCache(0);
  $mdThemingProvider.theme('success-toast');
  $mdThemingProvider.theme('error-toast');
  $stateProvider.state('app', {
      url: "/app",
      abstract: true,
      templateUrl: "views/app.html",
      controller: 'AppController'
    })
    .state('app.start', {
      url: "/start",
      views: {
        'Content' :{
          templateUrl: "views/dashboard/dashboard.html",
          controller: "DashboardController"
        }
      }
    }).state('app.register', {
      url: "/register",
      views: {
        'Content' :{
          templateUrl: "views/auth/register.html",
          controller: "RegistrerController"
        }
      }
    }).state('app.activate', {
      url: "/activate/:isUpdate",
      views: {
        'Content' :{
          templateUrl: "views/auth/otp.html",
          controller: "ActivateController"
        }
      }
    }).state('app.user-details', {
      url: "/user-details",
      views: {
        'Content' :{
          templateUrl: "views/user/user-details.html",
          controller: "UserController"
        }
      }
    }).state('app.update-details', {
      url: "/update-details/",
      views: {
        'Content' :{
          templateUrl: "views/user/update-details.html",
          controller: "UserController"
        }
      }
    }).state('app.settings', {
      url: "/settings",
      views: {
        'Content' :{
          templateUrl: "views/setting/settings.html",
          controller: "SettingsController"
        }
      }
    });    
    // if none of the above states are matched, use this as the fallback
    $urlRouterProvider.otherwise('/app/start');
});

// create app controller
FMRadioApp.controller('AppController', function($scope, $ionicLoading){
    $scope.showLoader = function() {
        $ionicLoading.show({
            template: 'Loading...'
        });
    }
    $scope.hideLoader = function() {
        $ionicLoading.hide();
    }

    deviceInfo.checkConnection()
});