package com.sight_spot_trip.entity;

import org.springframework.data.neo4j.annotation.QueryResult;

// see https://graphaware.com/neo4j/2016/04/06/mapping-query-entities-sdn.html
// see http://docs.spring.io/spring-data/neo4j/docs/current/reference/html/#reference:annotating-entities:property
@QueryResult
public class SightSpotEdgeWrapper {
	SightSpotNode s1;
	SightSpotEdge r;
	SightSpotNode s2;
	
	public SightSpotNode getS1() {
		return s1;
	}
	public void setS1(SightSpotNode s1) {
		this.s1 = s1;
	}
	public SightSpotEdge getR() {
		return r;
	}
	public void setR(SightSpotEdge r) {
		this.r = r;
	}
	public SightSpotNode getS2() {
		return s2;
	}
	public void setS2(SightSpotNode s2) {
		this.s2 = s2;
	}
	
	
}