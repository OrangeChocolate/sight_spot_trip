package com.sight_spot_trip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.env.StandardEnvironment;

import com.github.ulisesbocchio.jar.resources.JarResourceLoader;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		StandardEnvironment environment = new StandardEnvironment();
        new SpringApplicationBuilder()
            .sources(Application.class)
            .environment(environment)
            .resourceLoader(new JarResourceLoader())
            .build()
            .run(args);
//		SpringApplication.run(Application.class, args);
	}
}
