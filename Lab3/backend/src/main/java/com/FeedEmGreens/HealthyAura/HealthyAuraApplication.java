package com.FeedEmGreens.HealthyAura;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//Flow of how the API works
// HTTP requests -> Controller -> Service -> Repository -> Database

@SpringBootApplication
public class 	HealthyAuraApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthyAuraApplication.class, args);
	}

}
