package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
@EnableNeo4jRepositories(basePackages = "com.example.repositories")
public class BackendUsersManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendUsersManagementApplication.class, args);
    }
}
