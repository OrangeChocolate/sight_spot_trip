(function(){
    'use strict';
    
    angular.module('app').controller('appCntrl', function ($scope, cytoData, $interval, $cookies){
    	$scope.isAdmin = function() {
    		// ROLE_ADMIN|ROLE_USER
    		var user_role = $cookies.get("user_role");
    		var user_roles = [];
    		if(user_role.indexOf("|") != -1) {
    			user_roles = user_role.split("|");
    		}
    		else {
    			user_roles.push(user_role);
    		}
    		var admin_role = "ROLE_ADMIN";
    		return user_roles.indexOf(admin_role) != -1;
    	};
    });
    
})();
