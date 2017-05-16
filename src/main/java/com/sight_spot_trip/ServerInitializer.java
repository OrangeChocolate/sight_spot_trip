package com.sight_spot_trip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

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