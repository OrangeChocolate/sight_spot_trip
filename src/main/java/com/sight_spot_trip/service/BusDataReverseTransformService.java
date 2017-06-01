package com.sight_spot_trip.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import com.sight_spot_trip.entity.PairOrderIndependent;
import com.sight_spot_trip.entity.SightSpotEdge;
import com.sight_spot_trip.entity.SightSpotNode;
import com.sight_spot_trip.repository.SightSpotRepository;
import com.sight_spot_trip.util.Utils;

@Service
public class BusDataReverseTransformService {
	
	public static final Logger logger = LoggerFactory.getLogger(BusDataReverseTransformService.class);

	@Value(value = "classpath:data/nodes.csv")
	private Resource nodeResources;

	@Value(value = "classpath:data/edges.csv")
	private Resource edgeResources;

	@Value(value = "classpath:data/buses.csv")
	private Resource busResources;


	private Map<String, SightSpotNode> cachedNodes = new HashMap<>();
	private Map<String, SightSpotEdge> cachedEdges = new HashMap<>();
	private Map<PairOrderIndependent<String>, SightSpotEdge> cachedPairEdges = new HashMap<>();
	private Map<String, List<String>> cachedBuses = new HashMap<>();
	private Map<String, Set<SightSpotNode>> cachedRelatedNodes = new HashMap<>();
	private Map<String, Set<SightSpotEdge>> cachedRelatedEdges = new HashMap<>();
	private Map<PairOrderIndependent<String>, List<String>> cachedRelatedBuses = new HashMap<>();

	private void parseNode() {
		logger.info("Loaded resource: {}", nodeResources.toString());
		logger.info("Resource exists: {}", nodeResources.exists());
		try {
			logger.info("Resource URI: {}", nodeResources.getURI());
			logger.info("Resource File: {}", nodeResources.getFile().getAbsolutePath());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try (Stream<String> lines = Files.lines(Paths.get(nodeResources.getFile().getAbsolutePath()), Charset.defaultCharset())) {
			lines.forEach(line -> {
				if (line != null && !line.equals("") && !line.startsWith("#")) {
					String[] parts = line.split("\\s*,\\s*");
					if (parts.length < 2) {
						logger.error("error parsing the node line {}", line);
						return;
					}
					String nodeId = parts[0];
					String label = parts[1];
					String description = "";
					cachedNodes.put(nodeId, new SightSpotNode(nodeId, label, description));
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parseEdge() {
		Stream<String> edgeResourceStrean = Utils.getResourceContentStream(edgeResources);
		edgeResourceStrean.forEach(line -> {
			if (line != null && !line.equals("") && !line.startsWith("#")) {
				String[] parts = line.split("\\s*,\\s*");
				if (parts.length < 3) {
					logger.error("error parsing the edge line {}", line);
					return;
				}
				String relationId = parts[0];
				String label = "";
				String node1Id = parts[1];
				String node2Id = parts[2];
				int distance = 1;
				int time = 1;
				int cost = 1;
				String description = "";
				SightSpotEdge edge = new SightSpotEdge(relationId, label, cachedNodes.get(node1Id),
						cachedNodes.get(node2Id), distance, time, cost, description);
				cachedEdges.put(relationId, edge);
				cachedPairEdges.put(new PairOrderIndependent<>(node1Id, node2Id), edge);
				updateCachedRelatedNodes(cachedNodes.get(node1Id), cachedNodes.get(node2Id));
				updateCachedRelatedEdges(cachedNodes.get(node1Id), edge);
			}
		});
	}

	private void parseBus() {
		Stream<String> busResourceStrean = Utils.getResourceContentStream(busResources);
		busResourceStrean.forEach(line -> {
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

	
	public void initilization() {
		// parse nodes
		parseNode();
		// initCachedRelatedNodes
		initCachedRelatedNodesAndEdges();
		// parse edges
		parseEdge();

		parseBus();
		
		parseBusReverse();
		
		// write reverse data to file
		writeBusReverseDataToCSV();
	}

	private void writeBusReverseDataToCSV() {
		Path nodePath = Paths.get("d:/bus_node.csv");
		Path edgePath = Paths.get("d:/bus_edge.csv");
		
		// Use try-with-resource to get auto-closeable writer instance
		// Write bus_node.csv
		try (BufferedWriter writer = Files.newBufferedWriter(nodePath)) 
		{
			// insert csv header
		    writer.write("#nodeId,buses\n");
			Set<Entry<String, List<String>>> nodeEntrySet = nodeBusesMap.entrySet();
			for(Entry<String, List<String>> entry: nodeEntrySet) {
				writer.write(String.format("%s,%s\n", entry.getKey(), StringUtils.join(entry.getValue(), "|")));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Write bus_edge.csv
		try (BufferedWriter writer = Files.newBufferedWriter(edgePath)) 
		{
			// insert csv header
		    writer.write("#node1Id,#node2Id,buses\n");
			Set<Entry<PairOrderIndependent<String>, List<String>>> edgeEntrySet = edgeBusesMap.entrySet();
			for(Entry<PairOrderIndependent<String>, List<String>> entry: edgeEntrySet) {
				writer.write(String.format("%s,%s,%s\n", entry.getKey().getElement1(), entry.getKey().getElement2(), StringUtils.join(entry.getValue(), "|")));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
