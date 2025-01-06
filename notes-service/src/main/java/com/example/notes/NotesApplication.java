package com.example.notes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication(scanBasePackages = "com.example.notes")
public class NotesApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotesApplication.class, args);
    }

}
