/**
* Dashboard controller
* Show recent activity of users
* 
* @Author Semusi Tech
* @Copyright all rights reserved by semusi technologies Pvt Ltd. For information visit: www.semusi.com
**/
FMRadioApp.controller('DashboardController', function($scope, $ionicPopup, $cordovaSQLite, $timeout, $interval) {
    $scope.isRegistered = (userInfo.getMno() != '')? false : true;
    $scope.channels = [];

 	$scope.execute = function() {
	    var query = "SELECT * FROM eventsData order by time desc";
	    $cordovaSQLite.execute(db, query, []).then(function(dbRecord) {
		  if(dbRecord.rows){
		    $scope.channels = [];
		    for(i=0;i<dbRecord.rows.length;i++){
		        console.log(" type of data "+typeof dbRecord.rows.item(i).data);
		        var obj = {
		            date:moment(dbRecord.rows.item(i).time*1000).format("DD-MM-YYYY"),
		            event:dbRecord.rows.item(i).key+" "+dbRecord.rows.item(i).data,
		            time: moment(dbRecord.rows.item(i).time*1000).format("hh:mm:ss a")
		        }
		        $scope.channels.push(obj);
		    }
		  }
		  $scope.$broadcast('scroll.refreshComplete');
		}, function (err) {
//		  console.error("err:", JSON.stringify(err));
		});
    };

	$interval(function(){
		$scope.execute();
	}, 10000);

	$scope.doRefresh = function() {
        $scope.execute();
        $timeout(function(){
         	$scope.$broadcast('scroll.refreshComplete');
        }, FMRadioConfig.refreshTimeout);
    }

    // A confirm dialog for notification access
	$scope.showConfirm = function() {
	    var confirmFlag = true;
	   	var confirmPopup = $ionicPopup.confirm({
		     title: 'Notification Access',
		     template: 'Please enable Notification Access',
		     buttons: [
		      {
		        text: '<b>Yes</b>',
		        type: 'button-positive',
		        onTap: function( e ) {
		            if(confirmFlag){
		                confirmFlag = false;
                        var appice = cordova.require("appice-cordova-plugin.AppICE");
                        appice.openNotificationAccess();

                        appice.hasNotificationAccess(function(){
                            var appice = cordova.require("appice-cordova-plugin.AppICE");
                            var tmpObj = {
                                state:"true"
                            }
                            appice.tagEvent("NotificationState", tmpObj);
                        }, function(){
                            var appice = cordova.require("appice-cordova-plugin.AppICE");
                            var tmpObj = {
                                state:"false"
                            }
                            appice.tagEvent("NotificationState", tmpObj);
                        });
		            }
		        }
		      }
		    ]
	    });
	};

    $timeout(function(){
		    var appice = cordova.require("appice-cordova-plugin.AppICE");
        	appice.hasNotificationAccess(function(){
        	    console.log("notification is true")
        	}, function(){
        	    $scope.showConfirm();
        	});
	}, 1000);


});
