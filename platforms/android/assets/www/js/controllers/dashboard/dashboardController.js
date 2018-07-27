/**
* Dashboard controller
* Show recent activity of users
* 
* @Author Semusi Tech
* @Copyright all rights reserved by semusi technologies Pvt Ltd. For information visit: www.semusi.com
**/
FMRadioApp.controller('DashboardController', function($scope, $cordovaSQLite) {
  	  $scope.isRegistered = (userInfo.getUserId() != '')? false : true;
  	  $scope.channels = [];

	  $scope.execute = function() {
	    var query = "SELECT * FROM eventsData order by time desc";
	    console.log("query "+query)
	    $cordovaSQLite.execute(db, query, []).then(function(dbRecord) {
		  if(dbRecord.rows){
		    $scope.channels = [];
		    for(i=0;i<dbRecord.rows.length;i++){
//		        console.log(JSON.stringify(data1.rows.item(i)));
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
		  console.error("err:", JSON.stringify(err));
		});
	  };

	  setInterval(function(){
	  	$scope.execute();
	  }, 10000)

	$scope.doRefresh = function() {
        $scope.execute();
    }
});