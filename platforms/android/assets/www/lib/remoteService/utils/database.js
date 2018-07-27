(function(database){
	// create table 
	database.createTables = function(tableName, columns){
		var columns = columns.join(',');
		FMRadioConfig.db.transaction(function (tx) {
		    tx.executeSql('CREATE TABLE IF NOT EXISTS '+tableName+' ('+columns+')');
	    });
	}

	/**
	 * execute query
	 * @param first is query 
	 * @param second is callback
	 **/
	database.executeQuery = function(query, callback){
		// call db transaction
		FMRadioConfig.db.transaction(function (tx) {
	        tx.executeSql(query, [], 
	        	function(tx, results){
                	callback(null, results);
            	},function(tx, err){
                	callback(err, null);
            	}
            );
	    });
	}

}(window.database = window.database || {}));