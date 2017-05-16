package com.sight_spot_trip;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.sight_spot_trip.entity.SightSpotEdge;
import com.sight_spot_trip.entity.SightSpotNode;
import com.sight_spot_trip.repository.SightSpotRepository;
import com.sight_spot_trip.service.DataImportService;

@Component
public class ServerInitializer implements ApplicationRunner {

	public static final Logger logger = LoggerFactory.getLogger(ServerInitializer.class);
	
	@Autowired
	private DataImportService dataImportService;

	@Override
	public void run(ApplicationArguments applicationArguments) throws Exception {
		
		dataImportService.initilization();
	}


}