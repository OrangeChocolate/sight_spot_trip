package com.sight_spot_trip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.sight_spot_trip.service.BusDataReverseTransformService;
import com.sight_spot_trip.service.DataImportService;
import com.sight_spot_trip.service.DataImportService2;

@Component
public class ServerInitializer implements ApplicationRunner {

	public static final Logger logger = LoggerFactory.getLogger(ServerInitializer.class);

	@Autowired
	private DataImportService dataImportService;
	
	@Autowired
	private DataImportService2 dataImportService2;
	
	@Autowired
	private BusDataReverseTransformService busDataReverseTransformService;

	@Override
	public void run(ApplicationArguments applicationArguments) throws Exception {
		
		// 导入小数据集
		dataImportService.initilization();
		
//		// 反向解析公交数据，生成节点的公交信息和边的公交信息
//		busDataReverseTransformService.initilization();
	}


}


/**
导入节点信息
LOAD CSV WITH HEADERS FROM "file:///sight_spot_node.csv" AS line WITH line
CREATE (m:SightSpotNode { nodeId: line['#nodeId'], label: line.label, description: line.description })

导入便信息
LOAD CSV WITH HEADERS FROM "file:///sight_spot_edge.csv" AS line WITH line
MATCH (m:SightSpotNode { nodeId: line.node1Id }),(n:SightSpotNode { nodeId: line.node2Id })
MERGE (m)-[r:CONNECT { relationId: line['#relationId'], label: line.label, distance: line.distance, time: line.time, cost: line.cost } ]-(n)

初始化节点和边的公交信息
match (m:SightSpotNode) set m.buses=[]
match ()-[r:CONNECT]->() set r.buses=[]

建立nodeId的索引
CREATE CONSTRAINT ON (m:SightSpotNode) ASSERT m.nodeId IS UNIQUE

导入公交节点信息
LOAD CSV WITH HEADERS FROM "file:///sight_spot_bus.csv" AS line WITH line
with line['#bus_name'] AS bus_name, split(line.bus_nodes, "|") AS bus_nodes
UNWIND bus_nodes AS bus_node
match (m:SightSpotNode {nodeId : bus_node})
set m.buses=m.buses+bus_name

导入公交边信息
LOAD CSV WITH HEADERS FROM "file:///sight_spot_bus_edge.csv" AS line WITH line
with line['#node1Id'] AS node1Id, line['#node2Id'] AS node2Id, split(line.buses, "|") AS bus_names
UNWIND bus_names AS bus_name
match (m:SightSpotNode {nodeId : node1Id})-[r:CONNECT]->(n:SightSpotNode {nodeId : node2Id})
set r.buses = r.buses + bus_name
*/