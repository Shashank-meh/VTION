(function(userInfo){
	userInfo.setFirstName = function(firstName){
		localStorage.setItem('vtion_fn',firstName);
	}

	userInfo.getFirstName = function(){
		var _fn = localStorage.getItem('vtion_fn');
		return  (_fn)? _fn:'';
	}

	userInfo.setLastName = function(lastName){
		localStorage.setItem('vtion_ln',lastName);
	}
	
	userInfo.getLastName = function(){
		var _ln = localStorage.getItem('vtion_ln');
		return  (_ln)? _ln:'';
	}

	userInfo.setEmail = function(email){
		localStorage.setItem('vtion_e',email);
	}
	
	userInfo.getEmail = function(){
		var _e = localStorage.getItem('vtion_e');
		return  (_e)? _e:'';
	}

	userInfo.setToken = function(token){
		localStorage.setItem('vtion_t',token);
	}
	
	userInfo.getToken = function(){
		var _t = localStorage.getItem('vtion_t');
		return  (_t)? _t : '';
	}
	
	userInfo.setUserId = function(userId){
		localStorage.setItem('vtion_u',userId);
	}
	
	userInfo.getUserId = function(){
		var _u = localStorage.getItem('vtion_u');
		return  (_u)? _u : '';
	}

	userInfo.setMno = function(mno){
		localStorage.setItem('vtion_mno',mno);
	}
	
	userInfo.getMno = function(){
		var _u = localStorage.getItem('vtion_mno');
		return  (_u)? _u : '';
	}

}(window.userInfo = window.userInfo || {}));