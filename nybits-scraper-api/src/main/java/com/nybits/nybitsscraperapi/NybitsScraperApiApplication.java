package com.nybits.nybitsscraperapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({ "com.nybits.nybitsscraperapi", "com.source.endpoints" })
@SpringBootApplication
public class NybitsScraperApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(NybitsScraperApiApplication.class, args);
	}

}
