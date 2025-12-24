package ru.fitness.server.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/api/ping")
    public String ping() {
        return "SERVER_OK";
    }

    @GetMapping("/api/health")
    public String health() {
        return "UP";
    }
}
