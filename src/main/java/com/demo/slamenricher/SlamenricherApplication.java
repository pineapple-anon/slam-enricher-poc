package com.demo.slamenricher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.config.EnableIntegration;

@SpringBootApplication
@EnableIntegration
public class SlamenricherApplication {

	public static void main(String[] args) {
		SpringApplication.run(SlamenricherApplication.class, args);
	}

}
