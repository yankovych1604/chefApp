package com.maksy.chefapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.maksy.chefapp")
public class ChefappApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChefappApplication.class, args);
    }
}