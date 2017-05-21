package com.sight_spot_trip.api;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sight_spot_trip.entity.BusData;
import com.sight_spot_trip.entity.PathWrapper;
import com.sight_spot_trip.entity.SightSpotEdge;
import com.sight_spot_trip.entity.SightSpotEdgeWrapper;
import com.sight_spot_trip.entity.SightSpotNode;
import com.sight_spot_trip.service.SightSpotService;
import com.sight_spot_trip.util.Utils;

@RestController
@RequestMapping("/api/")
public class SightSpotController {
	
	public static final Logger logger = LoggerFactory.getLogger(SightSpotController.class);

	@Autowired
	private SightSpotService sightSpotService;

	@RequestMapping(value = "nodes", method = RequestMethod.GET)
	public List<SightSpotNode> findAllNodes(
			@RequestParam(name = "limit", required = false, defaultValue = "100") int limit) {
		List<SightSpotNode> nodes = sightSpotService.findAllNodes(limit);
		return nodes;
	}

	@RequestMapping(value = "edges", method = RequestMethod.GET)
	public List<SightSpotEdge> findAllEdges(
			@RequestParam(name = "limit", required = false, defaultValue = "100") int limit) {
		List<SightSpotEdgeWrapper> edgeWrappers = sightSpotService.findAllEdges(limit);
		List<SightSpotEdge> edges = edgeWrappers.stream().map(sightSpotEdgeWrapper -> sightSpotEdgeWrapper.getR())
				.collect(Collectors.toList());
		return edges;
	}

	@RequestMapping(value = "shortestpath", method = RequestMethod.GET)
	public PathWrapper findShortestPathDistanceWeighted(@RequestParam(name = "node1Id") String node1Id,
			@RequestParam(name = "node2Id") String node2Id, @RequestParam(name = "weight") String weight,
			@RequestParam(name = "distanceWeight", required = false, defaultValue = "0") double distanceWeight,
			@RequestParam(name = "timeWeight", required = false, defaultValue = "0") double timeWeight,
			@RequestParam(name = "costWeight", required = false, defaultValue = "0") double costWeight) {
		PathWrapper shortestPathDistanceWeighted = sightSpotService.findShortestPathDistanceWeighted(node1Id, node2Id,
				weight, distanceWeight, timeWeight, costWeight);
		return shortestPathDistanceWeighted;
	}

	@RequestMapping(value = "nodes", method = RequestMethod.POST)
	public SightSpotNode addNode(
			@RequestBody SightSpotNode node) {
		SightSpotNode savedNode = sightSpotService.addNode(node);
		return savedNode;
	}

	@RequestMapping(value = "nodes/{nodeId}", method = RequestMethod.DELETE)
	public SightSpotNode deleteNode(@PathVariable String nodeId) {
		SightSpotNode deletedNode = sightSpotService.deleteNodeByNodeId(nodeId);
		return deletedNode;
	}

	@RequestMapping(value = "nodes/{nodeId}", method = RequestMethod.PUT)
	public SightSpotNode updateNode(@PathVariable String nodeId, @RequestBody SightSpotNode node) {
		SightSpotNode updateNode = sightSpotService.findByNodeId(nodeId);
		updateNode = Utils.mergeObjects(updateNode, node);
		return sightSpotService.updateNode(updateNode);
	}


	@RequestMapping(value = "edges", method = RequestMethod.POST)
	public SightSpotEdge addEdge(
			@RequestBody SightSpotEdge edge) {
		SightSpotEdge savedEdge = sightSpotService.addEdge(edge);
		return savedEdge;
	}

	@RequestMapping(value = "edges/{nodeId1}/{nodeId2}", method = RequestMethod.DELETE)
	public SightSpotEdge deleteEdge(@PathVariable String nodeId1, @PathVariable String nodeId2) {
		SightSpotEdge deletedEdge = sightSpotService.deleteEdgeByNodeId(nodeId1, nodeId2);
		return deletedEdge;
	}

	@RequestMapping(value = "edges/{nodeId1}/{nodeId2}", method = RequestMethod.PUT)
	public SightSpotEdge updateEdge(@PathVariable String nodeId1, @PathVariable String nodeId2, @RequestBody SightSpotEdge edge) {
		return sightSpotService.updateEdge(nodeId1,nodeId2,edge);
	}

	@RequestMapping(value = "bus", method = RequestMethod.GET)
	public PathWrapper findBusPath(
			@RequestParam(name = "busName") String busName) {
		PathWrapper busPath = sightSpotService.findBusPath(busName);
		return busPath;
	}

	@RequestMapping(value = "bus", method = RequestMethod.POST)
	public PathWrapper addBus(
			@RequestBody BusData busData) {
		PathWrapper busPath = sightSpotService.addBus(busData);
		return busPath;
	}

	@RequestMapping(value = "busNames", method = RequestMethod.GET)
	public Set<String> findBusNames() {
		Set<String> busNames = sightSpotService.findBusNames();
		return busNames;
	}
	

}
