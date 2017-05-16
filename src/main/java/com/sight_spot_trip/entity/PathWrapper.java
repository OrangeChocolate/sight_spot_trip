package com.sight_spot_trip.entity;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.internal.InternalNode;
import org.neo4j.driver.internal.InternalRelationship;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class PathWrapper implements Serializable {
	private static final long serialVersionUID = 1L;
	@JsonSerialize(using = InternalNodeListSerializer.class)
	private List<InternalNode> nodes;
	@JsonSerialize(using = InternalRelationshipListSerializer.class)
	private List<InternalRelationship> edges;
	private double totalWeight;
	
	public PathWrapper(List<InternalNode> nodes, List<InternalRelationship> edges, double totalWeight) {
		this.nodes = nodes;
		this.edges = edges;
		this.totalWeight = totalWeight;
	}
	
	public PathWrapper(Iterable<Node> nodes, Iterable<Relationship> edges, double totalWeight) {
		this.nodes = iterableToList(nodes);
		this.edges = iterableToList(edges);
		this.totalWeight = totalWeight;
	}
	
	public List<InternalNode> getNodes() {
		return nodes;
	}
	public void setNodes(List<InternalNode> nodes) {
		this.nodes = nodes;
	}
	public List<InternalRelationship> getEdges() {
		return edges;
	}
	public void setEdges(List<InternalRelationship> edges) {
		this.edges = edges;
	}
	
	public double getTotalWeight() {
		return totalWeight;
	}

	public void setTotalWeight(double totalWeight) {
		this.totalWeight = totalWeight;
	}

	static <T,V> List<V> iterableToList(Iterable<T> iterable) {
		List<V> list = new ArrayList<>();
		iterable.forEach(entry -> {
			list.add((V)entry);
		});
		return list;
	}
	
	static class InternalNodeListSerializer extends JsonSerializer<List<InternalNode>> {

		@Override
		public void serialize(List<InternalNode> nodes, JsonGenerator gen, SerializerProvider serializers)
				throws IOException, JsonProcessingException {
	        gen.writeStartArray();
	        for (InternalNode node : nodes) {
	            gen.writeStartObject();
	            gen.writeObjectField("id", node.id());
	            gen.writeObjectFieldStart("properties");
	            Map<String, Object> asMap = node.asMap();
	            asMap.forEach((key, value) -> {
		            try {
						gen.writeObjectField(key, value);
					} catch (IOException e) {
						e.printStackTrace();
					}
	            });
	            gen.writeEndObject(); 
	            gen.writeEndObject();    
	        }
	        gen.writeEndArray();
		}

		
	}
	
	static class InternalRelationshipListSerializer extends JsonSerializer<List<InternalRelationship>> {

		@Override
		public void serialize(List<InternalRelationship> relationshops, JsonGenerator gen, SerializerProvider serializers)
				throws IOException, JsonProcessingException {
	        gen.writeStartArray();
	        for (InternalRelationship relationshop : relationshops) {
	            gen.writeStartObject();
	            gen.writeObjectField("id", relationshop.id());
	            gen.writeObjectField("type", relationshop.type());
	            gen.writeObjectField("source", relationshop.startNodeId());
	            gen.writeObjectField("target", relationshop.endNodeId());
	            gen.writeObjectFieldStart("properties");
	            Map<String, Object> asMap = relationshop.asMap();
	            asMap.forEach((key, value) -> {
		            try {
						gen.writeObjectField(key, value);
					} catch (IOException e) {
						e.printStackTrace();
					}
	            });
	            gen.writeEndObject(); 
	            gen.writeEndObject();    
	        }
	        gen.writeEndArray();
		}

		
	}
}
