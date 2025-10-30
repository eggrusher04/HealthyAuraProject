package com.FeedEmGreens.HealthyAura;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HealthyAuraApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthyAuraApplication.class, args);
    }
    
    // REMOVE THIS METHOD IF IT EXISTS:
    // @Bean
    // public PasswordEncoder passwordEncoder() {
    //     return new BCryptPasswordEncoder();
    // }
}