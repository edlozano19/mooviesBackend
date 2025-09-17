package com.moovies.mooviesBackend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    
    @GetMapping("/api/test")
    public String test() {
        return "Spring Boot application is running successfully! Database connection working.";
    }
}
