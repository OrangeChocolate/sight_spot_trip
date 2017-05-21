(function(){
    'use strict';

    angular
        .module('app')
        .config(function ($stateProvider, $urlRouterProvider, $locationProvider) {
            $urlRouterProvider.otherwise("/home");
//            $locationProvider.html5Mode(true);
            //
            // Now set up the states
            $stateProvider
                .state('defaults', {
                    url: "/home",
                    templateUrl: "templates/home.html",
                    controller: 'homeController'
                })
                .state('about', {
                    url: "/about",
                    templateUrl: "templates/about.html",
                    controller: 'aboutController'
                })
                .state('adminNode', {
                    url: "/adminNode",
                    templateUrl: "templates/admin-node.html",
                    controller: 'adminNodeController'
                })
                .state('adminEdge', {
                    url: "/adminEdge",
                    templateUrl: "templates/admin-edge.html",
                    controller: 'adminEdgeController'
                })
                .state('adminBus', {
                    url: "/adminBus",
                    templateUrl: "templates/admin-bus.html",
                    controller: 'adminBusController'
                });
        });
})();