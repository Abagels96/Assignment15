package com.coderscampus.Assignment15;

import com.coderscampus.Assignment15.config.DatabasePlatformInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Assignment15MomTrackerAbigailApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Assignment15MomTrackerAbigailApplication.class);
		app.addInitializers(new DatabasePlatformInitializer());
		app.run(args);
	}

}
