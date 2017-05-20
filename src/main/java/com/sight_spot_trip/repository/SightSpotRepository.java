package com.sight_spot_trip.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.sight_spot_trip.entity.SightSpotEdgeWrapper;
import com.sight_spot_trip.entity.SightSpotNode;

@RepositoryRestResource(collectionResourceRel = "sightspots", path = "sightspots")
public interface SightSpotRepository extends GraphRepository<SightSpotNode> {

	// see http://stackoverflow.com/questions/14252591/delete-all-nodes-and-relationships-in-neo4j-1-8
	// remove all nodes and edges using "match (n) optional match (n)-[r]-() delete n,r"
	// or "MATCH (n) DETACH DELETE n" as of 2.3.0
	@Query("MATCH (n:SightSpotNode) OPTIONAL MATCH (n)-[r:CONNECT]-() DELETE n,r")
	void clean();
	
	SightSpotNode findByNodeId(@Param("nodeId") String nodeId);
	
	SightSpotNode findByLabel(@Param("label") String label);

	List<SightSpotNode> findByLabelLike(@Param("label") String label);
	
	@Query("MATCH (s1:SightSpotNode)  RETURN distinct s1 LIMIT {limit}")
	List<SightSpotNode> findAllNodes(@Param("limit") int limit);
	
	@Query("MATCH (s1:SightSpotNode)-[r:CONNECT]-(s2:SightSpotNode) where s1.nodeId < s2.nodeId RETURN distinct s1, r, s2 LIMIT {limit}")
	List<SightSpotEdgeWrapper> findAllEdges(@Param("limit") int limit);
	
	// http://neo4j.com/docs/developer-manual/current/cypher/clauses/match/#query-shortest-path
	// https://neo4j.com/graphgist/92064364-ae62-405b-bfa7-a46e5e85e22b
	// http://stackoverflow.com/questions/32016353/neo4j-cypher-count-total-paths-found-from-allshortestpaths
	// https://neo4j.com/blog/graph-compute-neo4j-algorithms-spark-extensions/
	// http://stackoverflow.com/questions/33063408/how-can-i-get-shortest-path-cypher-query
	@Query("MATCH (from:SightSpotNode { nodeId:{node1Id} }), (to:SightSpotNode { nodeId:{node2Id} }) , path = (from)-[:CONNECT*]->(to) " +
			"RETURN path AS shortestPath, " +
		    "reduce(distance = 0.0, r in relationships(path) | distance + r.distance * 1.0) AS totalWeight " +
		    "ORDER BY totalWeight ASC " +
		    "LIMIT 1")
	public List<Map<String, Object>> findShortestPathDistanceWeightedDistance(@Param("node1Id") String node1Id, @Param("node2Id") String node2Id);

	@Query("MATCH (from:SightSpotNode { nodeId:{node1Id} }), (to:SightSpotNode { nodeId:{node2Id} }) , path = (from)-[:CONNECT*]->(to) " +
			"RETURN path AS shortestPath, " +
		    "reduce(distance = 0.0, r in relationships(path) | distance + r.time * 1.0) AS totalWeight " +
		    "ORDER BY totalWeight ASC " +
		    "LIMIT 1")
	public List<Map<String, Object>> findShortestPathDistanceWeightedTime(@Param("node1Id") String node1Id, @Param("node2Id") String node2Id);

	@Query("MATCH (from:SightSpotNode { nodeId:{node1Id} }), (to:SightSpotNode { nodeId:{node2Id} }) , path = (from)-[:CONNECT*]->(to) " +
			"RETURN path AS shortestPath, " +
		    "reduce(distance = 0.0, r in relationships(path) | distance + r.cost * 1.0) AS totalWeight " +
		    "ORDER BY totalWeight ASC " +
		    "LIMIT 1")
	public List<Map<String, Object>> findShortestPathDistanceWeightedCost(@Param("node1Id") String node1Id, @Param("node2Id") String node2Id);

	@Query("MATCH (from:SightSpotNode { nodeId:{node1Id} }), (to:SightSpotNode { nodeId:{node2Id} }) , path = (from)-[:CONNECT*]->(to) " +
			"RETURN path AS shortestPath, " +
		    "reduce(distance = 0.0, r in relationships(path) | distance + r.distance * {distanceWeight} + r.time * {timeWeight} + r.cost * {costWeight}) AS totalWeight " +
		    "ORDER BY totalWeight ASC " +
		    "LIMIT 1")
	public List<Map<String, Object>> findShortestPathDistanceWeightedMix(@Param("node1Id") String node1Id, @Param("node2Id") String node2Id, @Param("distanceWeight") double distanceWeight, @Param("timeWeight") double timeWeight, @Param("costWeight") double costWeight);
	
	// http://blog.bruggen.com/2016/08/orienteering-with-neo4j-moving-from.html
	// https://neo4j-contrib.github.io/neo4j-apoc-procedures/index31.html#_overview_of_apoc_procedures_functions
	// http://www.cnblogs.com/biyeymyhjob/archive/2012/07/31/2615833.html
	// Two classics shortest path algorithm Dijkstra/Floyd
	@Query("MATCH (from:SightSpotNode { nodeId:{node1Id} }), (to:SightSpotNode { nodeId:{node2Id} }) " +
			"call apoc.algo.dijkstra(from, to, 'CONNECT', 'distance') YIELD path as shortestPath, weight as totalWeight " +
			"return shortestPath,totalWeight;")
	public List<Map<String, Object>> findShortestPathDistanceWeightedDistanceDijkstra(@Param("node1Id") String node1Id, @Param("node2Id") String node2Id);

	@Query("MATCH (from:SightSpotNode { nodeId:{node1Id} }), (to:SightSpotNode { nodeId:{node2Id} }) " +
			"call apoc.algo.dijkstra(from, to, 'CONNECT', 'time') YIELD path as shortestPath, weight as totalWeight " +
			"return shortestPath,totalWeight;")
	public List<Map<String, Object>> findShortestPathDistanceWeightedTimeDijkstra(@Param("node1Id") String node1Id, @Param("node2Id") String node2Id);

	@Query("MATCH (from:SightSpotNode { nodeId:{node1Id} }), (to:SightSpotNode { nodeId:{node2Id} }) " +
			"call apoc.algo.dijkstra(from, to, 'CONNECT', 'cost') YIELD path as shortestPath, weight as totalWeight " +
			"return shortestPath,totalWeight;")
	public List<Map<String, Object>> findShortestPathDistanceWeightedCostDijkstra(@Param("node1Id") String node1Id, @Param("node2Id") String node2Id);
	
	@Query("match (a)-[r:CONNECT]->(b) set r.calculated=r.distance * {distanceWeight} + r.time * {timeWeight} + r.cost * {costWeight}")
	public void updateCalcuateValue(@Param("distanceWeight") double distanceWeight, @Param("timeWeight") double timeWeight, @Param("costWeight") double costWeight);

	@Query("MATCH (from:SightSpotNode { nodeId:{node1Id} }), (to:SightSpotNode { nodeId:{node2Id} }) " +
			"call apoc.algo.dijkstra(from, to, 'CONNECT', 'calculated') YIELD path as shortestPath, weight as totalWeight " +
			"return shortestPath,totalWeight;")
	public List<Map<String, Object>> findShortestPathDistanceWeightedMixDijkstra(@Param("node1Id") String node1Id, @Param("node2Id") String node2Id);
	
	
	@Query("MATCH (from:SightSpotNode), (to:SightSpotNode), path = (from)-[rels:CONNECT*]-(to) WHERE ALL(r IN rels(path) WHERE {busName} in r.buses) " +
			"WITH COLLECT(path) AS paths, MAX(length(path)) AS maxLength " +
			"RETURN FILTER(path IN paths WHERE length(path)= maxLength)[0] AS path")
	public List<Map<String, Object>> findBusPath(@Param("busName") String busName);

//	@Query( "START u1=node:User(key= {0}), u2=node:User(key = {1}) " +
//	        "MATCH p = shortestPath(u1-[*]-u2) " +
//	        "RETURN p")
//	public Result<EntityPath<SightSpotNode, SightSpotNode>> findShortestPath(@Param("node1Id") String node1Id, @Param("node2Id") String node2Id);
}
