package com.dash.undefdash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

//"com.dash.task" 
@ComponentScan({ "com.emailing.providers", "com.dash.undefdash", "com.dash.resource", "com.dash.jwt",
		"com.dash.scheduledtasks"})
@SpringBootApplication
@EnableScheduling
@EnableAutoConfiguration
public class Main {

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

}
