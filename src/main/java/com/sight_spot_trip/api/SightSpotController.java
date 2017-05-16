package com.sight_spot_trip.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}
