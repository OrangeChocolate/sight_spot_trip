package com.sight_spot_trip.entity;

import java.util.List;

public class BusData {
	private String busName;
	private List<String> nodeNames;

	public String getBusName() {
		return busName;
	}

	public void setBusName(String busName) {
		this.busName = busName;
	}

	public List<String> getNodeNames() {
		return nodeNames;
	}

	public void setNodeNames(List<String> nodeNames) {
		this.nodeNames = nodeNames;
	}

}