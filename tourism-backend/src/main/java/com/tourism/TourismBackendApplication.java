package com.tourism;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TourismBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(TourismBackendApplication.class, args);
    }
}
