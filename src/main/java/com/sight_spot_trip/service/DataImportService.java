package com.sight_spot_trip.service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.sight_spot_trip.entity.PairOrderIndependent;
import com.sight_spot_trip.entity.SightSpotEdge;
import com.sight_spot_trip.entity.SightSpotNode;
import com.sight_spot_trip.repository.SightSpotRepository;

@Service
public class DataImportService {
	
	public static final Logger logger = LoggerFactory.getLogger(DataImportService.class);

	@Value(value = "classpath:data/sight_spot_node.csv")
	private Resource nodeResources;

	@Value(value = "classpath:data/sight_spot_edge.csv")
	private Resource edgeResources;

	@Value(value = "classpath:data/sight_spot_bus.csv")
	private Resource busResources;

	@Autowired
	private SightSpotRepository sightSpotRepository;

	private Map<String, SightSpotNode> cachedNodes = new HashMap<>();
	private Map<String, SightSpotEdge> cachedEdges = new HashMap<>();
	private Map<PairOrderIndependent<String>, SightSpotEdge> cachedPairEdges = new HashMap<>();
	private Map<String, List<String>> cachedBuses = new HashMap<>();
	private Map<String, Set<SightSpotNode>> cachedRelatedNodes = new HashMap<>();
	private Map<String, Set<SightSpotEdge>> cachedRelatedEdges = new HashMap<>();
	private Map<PairOrderIndependent<String>, List<String>> cachedRelatedBuses = new HashMap<>();

	private void parseNode() {
		try (Stream<String> lines = Files.lines(Paths.get(nodeResources.getURI()), Charset.defaultCharset())) {
			lines.forEach(line -> {
				if (line != null && !line.equals("") && !line.startsWith("#")) {
					String[] parts = line.split("\\s*,\\s*");
					if (parts.length < 3) {
						logger.error("error parsing the node line {}", line);
						return;
					}
					String nodeId = parts[0];
					String label = parts[1];
					String description = parts[2];
					cachedNodes.put(nodeId, new SightSpotNode(nodeId, label, description));
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parseEdge() {
		try (Stream<String> lines = Files.lines(Paths.get(edgeResources.getURI()), Charset.defaultCharset())) {
			lines.forEach(line -> {
				if (line != null && !line.equals("") && !line.startsWith("#")) {
					String[] parts = line.split("\\s*,\\s*");
					if (parts.length < 8) {
						logger.error("error parsing the edge line {}", line);
						return;
					}
					String relationId = parts[0];
					String label = parts[1];
					String node1Id = parts[2];
					String node2Id = parts[3];
					int distance = Integer.parseInt(parts[4]);
					int time = Integer.parseInt(parts[5]);
					int cost = Integer.parseInt(parts[6]);
					String description = parts[7];
					SightSpotEdge edge = new SightSpotEdge(relationId, label, cachedNodes.get(node1Id),
							cachedNodes.get(node2Id), distance, time, cost, description);
					cachedEdges.put(relationId, edge);
					cachedPairEdges.put(new PairOrderIndependent<>(node1Id, node2Id), edge);
					updateCachedRelatedNodes(cachedNodes.get(node1Id), cachedNodes.get(node2Id));
					updateCachedRelatedEdges(cachedNodes.get(node1Id), edge);
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parseBus() {
		try (Stream<String> lines = Files.lines(Paths.get(busResources.getURI()), Charset.defaultCharset())) {
			lines.forEach(line -> {
				if (line != null && !line.equals("") && !line.startsWith("#")) {
					String[] parts = line.split("\\s*,\\s*");
					if (parts.length < 2) {
						logger.error("error parsing the bus line {}", line);
						return;
					}
					String busName = parts[0];
					List<String> buses = Stream.of(parts[1].split("\\|")).collect(Collectors.toList());
					cachedBuses.put(busName, buses);
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initCachedRelatedNodesAndEdges() {
		Set<Entry<String, SightSpotNode>> entrySet = cachedNodes.entrySet();
		for (Entry<String, SightSpotNode> entry : entrySet) {
			cachedRelatedNodes.put(entry.getKey(), new HashSet<>());
			cachedRelatedEdges.put(entry.getKey(), new HashSet<>());
		}
	}

	private void updateCachedRelatedNodes(SightSpotNode node1, SightSpotNode node2) {
		Set<SightSpotNode> node1RelatedNodes = cachedRelatedNodes.get(node1.getNodeId());
		Set<SightSpotNode> node2RelatedNodes = cachedRelatedNodes.get(node2.getNodeId());
		// only add edge once
		if (node1.getNodeId().compareTo(node2.getNodeId()) < 0) {
			node1RelatedNodes.add(node2);
		}
		if (node1.getNodeId().compareTo(node2.getNodeId()) >= 0) {
			node2RelatedNodes.add(node1);
		}
	}

	private void updateCachedRelatedEdges(SightSpotNode node1, SightSpotEdge edge) {
		Set<SightSpotEdge> node1RelatedEdges = cachedRelatedEdges.get(node1.getNodeId());
		node1RelatedEdges.add(edge);
	}

	private void insertNodes() {
		Set<Entry<String, SightSpotNode>> entrySet = cachedNodes.entrySet();
		for (Entry<String, SightSpotNode> entry : entrySet) {
			SightSpotNode node = entry.getValue();
			SightSpotNode persistedNode = sightSpotRepository.save(node);
			cachedNodes.put(entry.getKey(), persistedNode);
		}
	}

	private void updateNeighbors() {
		Set<Entry<String, SightSpotNode>> entrySet = cachedNodes.entrySet();
		for (Entry<String, SightSpotNode> entry : entrySet) {
			SightSpotNode node = entry.getValue();
			// node.setNeighbors(cachedRelatedNodes.get(entry.getKey()));
			node.setNeighborEdges(cachedRelatedEdges.get(entry.getKey()));
			SightSpotNode persistedNode = sightSpotRepository.save(node);
			cachedNodes.put(entry.getKey(), persistedNode);
		}
	}
	
	Map<String, List<String>> nodeBusesMap = new HashMap<>();
	Map<PairOrderIndependent<String>, List<String>> edgeBusesMap = new HashMap<>();
	
	private void parseBusReverse() {
		Set<Entry<String, List<String>>> entrySet = cachedBuses.entrySet();
		for(Entry<String, List<String>> entry : entrySet) {
			String busName = entry.getKey();
			List<String> busNodes = entry.getValue();
			
			for(String nodeId: busNodes) {
				List<String> list = nodeBusesMap.get(nodeId);
				if(list == null) {
					list = new ArrayList<String>();
				}
				if(!list.contains(busName)) {
					list.add(busName);
				}
				nodeBusesMap.put(nodeId, list);
			}
			
			for(int i = 0; i< busNodes.size() - 1; i++) {
				String node1 = busNodes.get(i);
				String node2 = busNodes.get(i + 1);
				PairOrderIndependent<String> edge = new PairOrderIndependent<String>(node1, node2);
				List<String> list = edgeBusesMap.get(edge);
				if(list == null) {
					list = new ArrayList<String>();
				}
				if(!list.contains(busName)) {
					list.add(busName);
				}
				edgeBusesMap.put(edge, list);
			}
		}
	}

	private void updateBuses() {
		Set<Entry<String, SightSpotNode>> entrySet = cachedNodes.entrySet();
		for (Entry<String, SightSpotNode> entry : entrySet) {
			SightSpotNode node = entry.getValue();
			List<String> nodeBusList = nodeBusesMap.get(node.getNodeId());
			if (nodeBusList != null) {
				node.setBuses(new HashSet<String>(nodeBusesMap.get(node.getNodeId())));
			}
			Set<SightSpotEdge> relatedEdges = cachedRelatedEdges.get(entry.getKey());
			for (SightSpotEdge edge : relatedEdges) {
				List<String> edgeBusList = edgeBusesMap.get(
						new PairOrderIndependent<String>(edge.getNode1().getNodeId(), edge.getNode2().getNodeId()));
				if (edgeBusList != null) {
					edge.setBuses(new HashSet<String>(edgeBusList));
				}
			}
			node.setNeighborEdges(relatedEdges);
			SightSpotNode persistedNode = sightSpotRepository.save(node);
			cachedNodes.put(entry.getKey(), persistedNode);
		}
	}
	

	private void test() {
		Iterable<SightSpotNode> nodes = sightSpotRepository.findAll();
		nodes.forEach(node -> {
			logger.info("{}", node);
		});
	}

	public void initilization() {
		// clean the database
		sightSpotRepository.clean();
		// parse nodes
		parseNode();
		// initCachedRelatedNodes
		initCachedRelatedNodesAndEdges();
		// parse edges
		parseEdge();

		// insert nodes
		insertNodes();
		// update neighbors
		updateNeighbors();
		
		parseBus();
		parseBusReverse();
		updateBuses();

		test();
	}

}
