package com.agnesmaria.retail_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.agnesmaria.retail_service")
public class RetailServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RetailServiceApplication.class, args);
    }
}
