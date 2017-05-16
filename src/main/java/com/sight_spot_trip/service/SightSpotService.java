package com.sight_spot_trip.service;

import java.util.List;
import java.util.Map;

import org.neo4j.driver.internal.InternalPath;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sight_spot_trip.entity.PathWrapper;
import com.sight_spot_trip.entity.SightSpotEdgeWrapper;
import com.sight_spot_trip.entity.SightSpotNode;
import com.sight_spot_trip.repository.SightSpotRepository;

@Service
public class SightSpotService {

	@Autowired
	private SightSpotRepository sightSpotRepository;

	@Transactional(readOnly = true)
	public List<SightSpotNode> findAllNodes(int limit) {
		List<SightSpotNode> result = sightSpotRepository.findAllNodes(limit);
		return result;
	}

	public List<SightSpotEdgeWrapper> findAllEdges(int limit) {
		List<SightSpotEdgeWrapper> result = sightSpotRepository.findAllEdges(limit);
		return result;
	}
	
	@Transactional
	public PathWrapper findShortestPathDistanceWeighted(String node1Id, String node2Id, String weight, double distanceWeight, double timeWeight, double costWeight) {
		List<Map<String, Object>> shortestPathDistanceWeighted = null;
		switch (weight) {
		case "distance":
			shortestPathDistanceWeighted = sightSpotRepository.findShortestPathDistanceWeightedDistanceDijkstra(node1Id, node2Id);
			break;
		case "time":
			shortestPathDistanceWeighted = sightSpotRepository.findShortestPathDistanceWeightedTimeDijkstra(node1Id, node2Id);
			break;
		case "cost":
			shortestPathDistanceWeighted = sightSpotRepository.findShortestPathDistanceWeightedCostDijkstra(node1Id, node2Id);
			break;
		case "mix":
			sightSpotRepository.updateCalcuateValue(distanceWeight, timeWeight, costWeight);
			shortestPathDistanceWeighted = sightSpotRepository.findShortestPathDistanceWeightedMixDijkstra(node1Id, node2Id);
			break;
		default:
			break;
		}
		PathWrapper pathWrapper = null;
		if(shortestPathDistanceWeighted != null && !shortestPathDistanceWeighted.isEmpty()) {
			Map<String, Object> map = (Map<String, Object>) shortestPathDistanceWeighted.get(0);
			InternalPath shortestPath = (InternalPath) map.get("shortestPath");
			double totalWeight = (double) map.get("totalWeight");
			Iterable<Node> nodes = shortestPath.nodes();
			Iterable<Relationship> relationships = shortestPath.relationships();
			pathWrapper = new PathWrapper(nodes, relationships, totalWeight);
		}
		return pathWrapper;
	}
}
