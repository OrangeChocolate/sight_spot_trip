package com.sight_spot_trip.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

// see http://stackoverflow.com/questions/27109953/jackson-with-spring-mvc-duplicate-nested-objects-not-deserializing
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "nodeId")
@NodeEntity
public class SightSpotNode implements Serializable {

	private static final long serialVersionUID = 1L;

	@GraphId
	private Long id;

	private String nodeId;

	private String label;
	
	private String description;

	@Relationship(type = "CONNECT")
	private Set<SightSpotEdge> neighborEdges = new HashSet<>();

	@Relationship(type = "CONNECT", direction = Relationship.UNDIRECTED)
	private Set<SightSpotNode> neighbors = new HashSet<>();

	public SightSpotNode() {
	}

	public SightSpotNode(String nodeId, String label, String description) {
		this.nodeId = nodeId;
		this.label = label;
		this.description = description;
	}

	public void link(SightSpotNode neighbor) {
		neighbors.add(neighbor);
	}

	// @JsonSerialize(using = ToStringSerializer.class)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@JsonIgnore
	public Set<SightSpotNode> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(Set<SightSpotNode> neighbors) {
		this.neighbors = neighbors;
	}

	@JsonIgnore
	public Set<SightSpotEdge> getNeighborEdges() {
		return neighborEdges;
	}

	public void setNeighborEdges(Set<SightSpotEdge> neighborEdges) {
		this.neighborEdges = neighborEdges;
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
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((nodeId == null) ? 0 : nodeId.hashCode());
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
		SightSpotNode other = (SightSpotNode) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (nodeId == null) {
			if (other.nodeId != null)
				return false;
		} else if (!nodeId.equals(other.nodeId))
			return false;
		return true;
	}

	// https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/builder/ToStringBuilder.html
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
