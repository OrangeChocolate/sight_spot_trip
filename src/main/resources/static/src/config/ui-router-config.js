(function(){
    'use strict';

    angular
        .module('app')
        .config(function ($stateProvider, $urlRouterProvider) {
            $urlRouterProvider.otherwise("/home");
            //
            // Now set up the states
            $stateProvider
                .state('defaults', {
                    url: "/home",
                    templateUrl: "templates/home.html",
                    controller: 'homeController'
                })
                .state('core', {
                    url: "/about",
                    templateUrl: "templates/about.html",
                    controller: 'aboutController'
                })
                .state('admin', {
                    url: "/admin",
                    templateUrl: "templates/admin.html",
                    controller: 'adminController'
                });
        });
})();