(function(){
    'use strict';

    angular.module('app').factory("utils", function() {                                                                                                                                                   
        return {                                                                                                                                                                                                              
        	encodeQueryData: function(data) {
        		   let ret = [];
        		   for (let d in data)
        		     ret.push(encodeURIComponent(d) + '=' + encodeURIComponent(data[d]));
        		   return ret.join('&');
        		},
        };
    });
})();  