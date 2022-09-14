package com.source.yelpscraperapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

// need to handle run only once task later
@ComponentScan({ "com.source.yelpscraperapi", "com.source.endpoints" })
@SpringBootApplication
public class YelpScraperApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(YelpScraperApiApplication.class, args);
	}

}
