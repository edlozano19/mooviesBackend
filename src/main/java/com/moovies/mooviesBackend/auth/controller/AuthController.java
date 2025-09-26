package com.moovies.mooviesBackend.auth.controller;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.moovies.mooviesBackend.auth.dto.LoginRequest;
import com.moovies.mooviesBackend.auth.dto.LoginResponse;
import com.moovies.mooviesBackend.auth.service.AuthService;
import com.moovies.mooviesBackend.config.LoggingConfig;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggingConfig.getLogger(AuthController.class);
    
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        logger.debug("Login attempt for user: {}", loginRequest.getUsernameOrEmail());
        
        try {
            LoginResponse loginResponse = authService.login(loginRequest);

            logger.info("Login successful for user: {}", loginRequest.getUsernameOrEmail());
            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            logger.error("Login failed for user: {}", loginRequest.getUsernameOrEmail(), e.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Invalid credentials"));
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorResponse {
        private String error;
        private String timestamp;

        public ErrorResponse(String error) {
            this.error = error;
            this.timestamp = java.time.Instant.now().toString();
        }

        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }

    public static class SuccessResponse {
        private String message;
        
        public SuccessResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
