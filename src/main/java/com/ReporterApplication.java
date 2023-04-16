package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public abstract class ReporterApplication {
    public static void main(final String[] args) {
        SpringApplication.run(ReporterApplication.class, args);
    }
}
