package com.sight_spot_trip.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.driver.internal.InternalPath;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sight_spot_trip.entity.BusData;
import com.sight_spot_trip.entity.PathWrapper;
import com.sight_spot_trip.entity.SightSpotEdge;
import com.sight_spot_trip.entity.SightSpotEdgeWrapper;
import com.sight_spot_trip.entity.SightSpotNode;
import com.sight_spot_trip.repository.SightSpotRepository;
import com.sight_spot_trip.util.Utils;

@Service
public class SightSpotService {
	
	public static final Logger logger = LoggerFactory.getLogger(SightSpotService.class);

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

	public PathWrapper findBusPath(String busName) {
		List<Map<String, Object>> busPath = sightSpotRepository.findBusPath(busName);
		PathWrapper pathWrapper = null;
		if(busPath != null && !busPath.isEmpty()) {
			Map<String, Object> map = (Map<String, Object>) busPath.get(0);
			InternalPath path = (InternalPath) map.get("path");
			Iterable<Node> nodes = path.nodes();
			Iterable<Relationship> relationships = path.relationships();
			pathWrapper = new PathWrapper(nodes, relationships, path.length());
		}
		return pathWrapper;
	}

	public SightSpotNode addNode(SightSpotNode node) {
		SightSpotNode saveDNode = sightSpotRepository.save(node);
		return saveDNode;
	}

	public SightSpotNode updateNode(SightSpotNode node) {
		SightSpotNode saveDNode = sightSpotRepository.save(node);
		return saveDNode;
	}

	@Transactional
	public SightSpotEdge addEdge(SightSpotEdge edge) {
		String nodeId1 = edge.getNode1().getNodeId();
		String nodeId2 = edge.getNode2().getNodeId();
		if(nodeId1.compareTo(nodeId2) > 0) {
			nodeId1 = edge.getNode2().getNodeId();
			nodeId2 = edge.getNode1().getNodeId();
		}
		SightSpotNode node1 = sightSpotRepository.findByNodeId(nodeId1);
		SightSpotNode node2 = sightSpotRepository.findByNodeId(nodeId2);
		edge.setNode1(node1);
		edge.setNode2(node2);
		node1.addNeighborEdge(edge);
		SightSpotNode savedNode = sightSpotRepository.save(node1);
		Set<SightSpotEdge> neighborEdges = savedNode.getNeighborEdges();
		SightSpotEdge savedEdge = null;
		for (SightSpotEdge neighborEdge : neighborEdges) {
			if ((neighborEdge.getNode1().getNodeId().equals(edge.getNode1().getNodeId())
					&& neighborEdge.getNode2().getNodeId().equals(edge.getNode2().getNodeId()))
					|| (neighborEdge.getNode1().getNodeId().equals(edge.getNode2().getNodeId())
							&& neighborEdge.getNode2().getNodeId().equals(edge.getNode1().getNodeId()))) {
				savedEdge = neighborEdge;
			}
		}
		return savedEdge;
	}

	@Transactional
	public PathWrapper addBus(BusData busData) {
		String busName = busData.getBusName();
		List<String> nodeNames = busData.getNodeNames();
		for(int i = 0; i < nodeNames.size() - 1; i++) {
			SightSpotNode node1 = sightSpotRepository.findByNodeId(nodeNames.get(i));
			SightSpotNode node2 = sightSpotRepository.findByNodeId(nodeNames.get(i + 1));
			node1.addBus(busName);
			node2.addBus(busName);
			Set<SightSpotEdge> neighborEdges = node1.getNeighborEdges();
			SightSpotEdge relatedEdge = null;
			for (SightSpotEdge neighborEdge : neighborEdges) {
				if ((neighborEdge.getNode1().getNodeId().equals(nodeNames.get(i))
						&& neighborEdge.getNode2().getNodeId().equals(nodeNames.get(i + 1)))
						|| (neighborEdge.getNode1().getNodeId().equals(nodeNames.get(i + 1))
								&& neighborEdge.getNode2().getNodeId().equals(nodeNames.get(i)))) {
					relatedEdge = neighborEdge;
					relatedEdge.addBus(busName);
				}
			}
			sightSpotRepository.save(node1);
			sightSpotRepository.save(node2);
		}
		
		List<Map<String, Object>> busPath = sightSpotRepository.findBusPath(busName);
		PathWrapper pathWrapper = null;
		if(busPath != null && !busPath.isEmpty()) {
			Map<String, Object> map = (Map<String, Object>) busPath.get(0);
			InternalPath path = (InternalPath) map.get("path");
			Iterable<Node> nodes = path.nodes();
			Iterable<Relationship> relationships = path.relationships();
			pathWrapper = new PathWrapper(nodes, relationships, path.length());
		}
		return pathWrapper;
	}

	public Set<String> findBusNames() {
		Set<String> busNames = new HashSet<>();
		Iterable<SightSpotNode> nodes = sightSpotRepository.findAll();
		nodes.forEach(node -> {
			busNames.addAll(node.getBuses());
		});
		return busNames;
	}

	public SightSpotNode deleteNodeByNodeId(String nodeId) {
		SightSpotNode node = sightSpotRepository.findByNodeId(nodeId);
		sightSpotRepository.deleteNodeByNodeId(nodeId);
		return node;
	}

	public SightSpotEdge deleteEdgeByNodeId(String nodeId1, String nodeId2) {
		SightSpotEdge edge = sightSpotRepository.findEdgeByNodeId(nodeId1, nodeId2);
		sightSpotRepository.deleteEdgeByNodeId(nodeId1, nodeId2);
		return edge;
	}

	public SightSpotNode findByNodeId(String nodeId) {
		return sightSpotRepository.findByNodeId(nodeId);
	}

	@Transactional
	public SightSpotEdge updateEdge(String nodeId1, String nodeId2, SightSpotEdge edge) {
		String startNode = nodeId1;
		if(nodeId1.compareTo(nodeId2) > 0) {
			startNode = nodeId2;
		}
		SightSpotNode node1 = sightSpotRepository.findByNodeId(startNode);
		Set<SightSpotEdge> neighborEdges = node1.getNeighborEdges();
		SightSpotEdge needToUpdateEdge = null;
		for(SightSpotEdge neighborEdge: neighborEdges) {
			if(neighborEdge.getNode1().getNodeId().equals(nodeId1) && neighborEdge.getNode2().getNodeId().equals(nodeId2)) {
				needToUpdateEdge = neighborEdge;
			}
		}
		if(needToUpdateEdge == null) {
			logger.error("updateEdge error, could not find specified edge! {}-{}", nodeId1, nodeId2);
			return null;
		}
		neighborEdges.remove(needToUpdateEdge);
		needToUpdateEdge = Utils.mergeObjects(needToUpdateEdge, edge);
		neighborEdges.add(needToUpdateEdge);
		node1.setNeighborEdges(neighborEdges);
		SightSpotNode updatedNode = sightSpotRepository.save(node1);
		for(SightSpotEdge neighborEdge: updatedNode.getNeighborEdges()) {
			if(neighborEdge.getNode1().getNodeId().equals(nodeId1) && neighborEdge.getNode2().getNodeId().equals(nodeId2)) {
				needToUpdateEdge = neighborEdge;
			}
		}
		return needToUpdateEdge;
	}
}
