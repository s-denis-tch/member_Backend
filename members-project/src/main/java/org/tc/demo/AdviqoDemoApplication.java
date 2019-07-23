package org.tc.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AdviqoDemoApplication {

	public static void main(String[] args) {
	  SpringApplication app =  new SpringApplication(AdviqoDemoApplication.class);
	  app.setAdditionalProfiles("server");
		app.run(args);
	}
}
