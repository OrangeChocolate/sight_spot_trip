package com.sight_spot_trip.entity;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

//@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="relationId")
@RelationshipEntity(type = "CONNECT")
public class SightSpotEdge implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@GraphId
	private Long id;
	
	private String relationId;
	
	private String label;

	@StartNode
	private SightSpotNode node1;

	@EndNode
	private SightSpotNode node2;
	
	private int distance;
	
	private int time;
	
	private int cost;
	
	private String description;

	public SightSpotEdge() {
	}

	public SightSpotEdge(String relationId, String label, SightSpotNode node1, SightSpotNode node2, int distance, int time, int cost, String description) {
		this.relationId = relationId;
		this.label = label;
		this.node1 = node1;
		this.node2 = node2;
		this.distance = distance;
		this.time = time;
		this.cost = cost;
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRelationId() {
		return relationId;
	}

	public void setRelationId(String relationId) {
		this.relationId = relationId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public SightSpotNode getNode1() {
		return node1;
	}

	public void setNode1(SightSpotNode node1) {
		this.node1 = node1;
	}

	public SightSpotNode getNode2() {
		return node2;
	}

	public void setNode2(SightSpotNode node2) {
		this.node2 = node2;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + cost;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + distance;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((relationId == null) ? 0 : relationId.hashCode());
		result = prime * result + time;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SightSpotEdge other = (SightSpotEdge) obj;
		if (cost != other.cost)
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (distance != other.distance)
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (relationId == null) {
			if (other.relationId != null)
				return false;
		} else if (!relationId.equals(other.relationId))
			return false;
		if (time != other.time)
			return false;
		return true;
	}
	
	// https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/builder/ToStringBuilder.html
	@Override
	public String toString() {
		return new ToStringBuilder(this).
			       append("id", id).
			       append("relationId", relationId).
			       append("node1", node1.getId()).
			       append("node2", node2.getId()).
			       append("distance", distance).
			       append("time", time).
			       append("cost", cost).
			       toString();
	}
	
}
