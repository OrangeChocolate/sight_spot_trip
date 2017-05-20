package com.sight_spot_trip.api;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping("/")
public class SightSpotController {

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

	@RequestMapping(value = "nodes", method = RequestMethod.PUT)
	public SightSpotNode updateNode(
			@RequestBody SightSpotNode node) {
		SightSpotNode savedNode = sightSpotService.updateNode(node);
		return savedNode;
	}

	@RequestMapping(value = "edges", method = RequestMethod.POST)
	public SightSpotEdge addEdge(
			@RequestBody SightSpotEdge edge) {
		SightSpotEdge savedEdge = sightSpotService.addEdge(edge);
		return savedEdge;
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
