(function(){
    'use strict';

    angular.module('app').controller('adminNodeController', function ($scope, cytoData, $http, $timeout, $state, utils){
        $scope.defaults = {
            zoomingEnabled: true,
            userPanningEnabled: true
        };
        $scope.styles = [
            {
                selector: 'node',
                style:{
                   content: 'data(nodeId)',
                   'background-color': 'mapData(weight, 0, 100, blue, red)'
                }
            },
            {
                selector: 'edge',
                style:{
                   content: 'data(relationId)',
                }
            },
            {
                selector: '.highlighted',
                style:{
                    'background-color': '#61bffc',
                    'line-color': '#61bffc',
                    'target-arrow-color': '#61bffc',
                    'transition-property': 'background-color, line-color, target-arrow-color',
                    'transition-duration': '0.5s'
                }
            },
            
            {
                selector: '.node-nodeId',
                style:{
                   content: 'data(nodeId)',
                }
            },
            {
                selector: '.node-label',
                style:{
                   content: 'data(label)',
                }
            },
            {
                selector: '.node-description',
                style:{
                   content: 'data(description)',
                }
            },
            {
                selector: '.edge-relationId',
                style:{
                   content: 'data(relationId)',
                }
            },
            {
                selector: '.edge-label',
                style:{
                   content: 'data(label)',
                }
            },
            {
                selector: '.edge-description',
                style:{
                   content: 'data(description)',
                }
            },
            {
                selector: '.edge-distance',
                style:{
                   content: 'data(distance)',
                }
            },
            {
                selector: '.edge-time',
                style:{
                   content: 'data(time)',
                }
            },
            {
                selector: '.edge-cost',
                style:{
                   content: 'data(cost)',
                }
            },
        ];

        $scope.elements = [];
        
        var nodes, edges, elements = [];
        $http.get("/api/nodes").then(function (response) {
            $scope.nodes = nodes = response.data;
            $scope.startNode = nodes[0];
            $scope.endNode = nodes[4];
            
            $http.get("/api/edges").then(function (response) {
            	$scope.edges = edges = response.data;
            	
                for(var i = 0; i < nodes.length; i++) {
                	var node = nodes[i];
                	elements.push({ group:'nodes',data: node });
                }
                for(var i = 0; i < edges.length; i++) {
                	var edge = edges[i];
                	elements.push({ group:'edges',data: angular.extend(edge, {source: edge.node1.id, target: edge.node2.id})});
                }
                console.info(elements);
                $scope.elements = elements;
                cytoData.getGraph('default').then(function(graph){
                    $scope.graph = graph;
                    $scope.graph.center();
                });
                $timeout(function() {
                    $scope.layout = {name:'circle'};
                }, 0);
            });
        });
        
        $scope.node = {
        		nodeId: "",
        		label: "",
        		description: ""
        };
        

		$scope.addNode = function() {
			delete $scope.node.id;
			var data = JSON.stringify($scope.node);
			$http.post("/api/nodes", data).then(
					function(data, status, headers, config) {
						console.log(data);
						$state.go($state.current, {}, {reload: true});
					}, function(data, status, headers, config) {
						console.log(data);
					});
		};
        

		$scope.deleteNode = function() {
			$http.delete("/api/nodes/" + $scope.node.nodeId).then(
					function(data, status, headers, config) {
						console.log(data);
						$state.go($state.current, {}, {reload: true});
					}, function(data, status, headers, config) {
						console.log(data);
					});
		};
        

		$scope.updateNode = function() {
			var data = JSON.stringify($scope.node);
			$http.put("/api/nodes/" + $scope.node.nodeId, data).then(
					function(data, status, headers, config) {
						console.log(data);
						$state.go($state.current, {}, {reload: true});
					}, function(data, status, headers, config) {
						console.log(data);
					});
		};
		

        $scope.edge = {
            relationId: "",
            label: "",
            description: "",
        	distance: 0,
        	time: 0,
        	cost: 0,
    		node1: {
    			nodeId: ""
    		},
    		node2: {
    			nodeId: ""
    		}
    	};
        

		$scope.addEdge = function() {
			delete $scope.edge.id;
			var data = JSON.stringify($scope.edge);
			$http.post("/api/edges", data).then(
					function(data, status, headers, config) {
						console.log(data);
						$state.go($state.current, {}, {reload: true});
					}, function(data, status, headers, config) {
						console.log(data);
					});
		};

		$scope.deleteEdge = function() {
			var data = JSON.stringify($scope.edge);
			$http.delete("/api/edges/" + $scope.edge.node1.nodeId + "/" + $scope.edge.node2.nodeId).then(
					function(data, status, headers, config) {
						console.log(data);
						$state.go($state.current, {}, {reload: true});
					}, function(data, status, headers, config) {
						console.log(data);
					});
		};
		
		$scope.updateEdge = function() {
			var data = JSON.stringify($scope.edge);
			$http.put("/api/edges/" + $scope.edge.node1.nodeId + "/" + $scope.edge.node2.nodeId, data).then(
					function(data, status, headers, config) {
						console.log(data);
						$state.go($state.current, {}, {reload: true});
					}, function(data, status, headers, config) {
						console.log(data);
					});
		};
		

        $scope.bus = {
        	busName: "",
        	nodeNames: []
    	};
        

		$scope.addBus = function() {
			$scope.bus.nodeNames = $scope.bus.nodeNames.split(/\s*,\s*/);
			var data = JSON.stringify($scope.bus);
			$http.post("/api/bus", data).then(
					function(data, status, headers, config) {
						console.log(data);
						$state.go($state.current, {}, {reload: true});
					}, function(data, status, headers, config) {
						console.log(data);
					});
		};
        
        var highlightedEdges;
        $scope.animate_highlight = function() {
            $timeout(function() {
                //var bfs = $scope.graph.elements().bfs('#' + $scope.graph.elements()[0].id(), function(){}, true);
            	var shortestPathEdges = $scope.pathEdges.map(function(edge) {
            		return edge.id;
            	}).map(function(id) {
            		return $scope.graph.getElementById(id);
            	});
            	highlightedEdges = shortestPathEdges;
            	
            	for (var i = 1; i <= shortestPathEdges.length; i++) {
            	    (function(index, shortestPathEdges) {
            	        setTimeout(function() {
            	        	shortestPathEdges[index - 1].addClass('highlighted');
            	        }, index * 1000);
            	    })(i, shortestPathEdges);
            	}
            }, 100);
        };
        
        $scope.reset = function() {
        	for(var i = 0; i < highlightedEdges.length; i++) {
        		highlightedEdges[i].removeClass('highlighted');
        	}
        };
        
        var findNodeByNodeId = function(nodeId) {
        	for(var node of $scope.nodes) {
        		if(node.nodeId == nodeId) {
        			return node;
        		}
        	}
        	return null;
        }

        $scope.selected_element = null;
        $scope.$on('cy:node:click', function(ng,cy){
        	$scope.selected_element = angular.extend(angular.extend({}, cy.cyTarget.attr()),{isNode: cy.cyTarget.isNode(), isEdge: cy.cyTarget.isEdge()});
        	$scope.node = {
            		id: $scope.selected_element.id,
            		nodeId: $scope.selected_element.nodeId,
            		label: $scope.selected_element.label,
            		description: $scope.selected_element.description
            };
            $scope.$apply();
        });
        $scope.$on('cy:edge:click', function(ng,cy){
        	$scope.selected_element = angular.extend(angular.extend({}, cy.cyTarget.attr()),{isNode: cy.cyTarget.isNode(), isEdge: cy.cyTarget.isEdge()});
            $scope.edge = {
                id: $scope.selected_element.id,
                relationId: $scope.selected_element.relationId,
        		label: $scope.selected_element.label,
        		description: $scope.selected_element.description,
            	distance: $scope.selected_element.distance,
            	time: $scope.selected_element.time,
            	cost: $scope.selected_element.cost,
        		node1: findNodeByNodeId($scope.selected_element.node1.nodeId),
        		node2: findNodeByNodeId($scope.selected_element.node2.nodeId)
        	};
            $scope.$apply();
        });
        
        $scope.nodeAttributes = [
        	{name: "nodeId", value: "节点ID"},
        	{name: "label", value: "节点标签"},
        	{name: "description", value: "节点描述"},
        ];
        $scope.edgeAttributes = [
        	{name: "relationId", value: "边ID"},
        	{name: "label", value: "边标签"},
        	{name: "description", value: "边描述"},
        	{name: "distance", value: "边距离"},
        	{name: "time", value: "时间"},
        	{name: "cost", value: "费用"},
        ];
        $scope.nodeAttribute = $scope.nodeAttributes[0];
        $scope.edgeAttribute = $scope.edgeAttributes[0];
        
        var lastNodeContentStyle = 'node-nodeId';
        var lastEdgeContentStyle = 'edge-relationId';
        $scope.node_display_attr_update = function() {
        	var allNodes = $scope.graph.nodes();
        	for(var i = 0; i < allNodes.length; i++) {
        		allNodes[i].removeClass(lastNodeContentStyle);
        		allNodes[i].addClass('node-' + $scope.nodeAttribute.name);
        		lastNodeContentStyle = 'node-' + $scope.nodeAttribute.name;
        	}
        };
        $scope.edge_display_attr_update = function() {
        	var allEdges = $scope.graph.edges();
        	for(var i = 0; i < allEdges.length; i++) {
        		allEdges[i].removeClass(lastEdgeContentStyle);
        		allEdges[i].addClass('edge-' + $scope.edgeAttribute.name);
        		lastEdgeContentStyle = 'edge-' + $scope.edgeAttribute.name;
        	}
        };
        
        
        
    });
  
    
})();