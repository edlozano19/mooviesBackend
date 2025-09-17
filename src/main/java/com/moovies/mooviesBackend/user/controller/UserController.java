package com.moovies.mooviesBackend.user.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.moovies.mooviesBackend.user.entity.User;
import com.moovies.mooviesBackend.user.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    // Create a new user
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        logger.debug("Creating user with username: {}", request.getUsername());
        
        try {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            
            User createdUser = userService.createUser(user, request.getPassword());
            logger.info("Successfully created user with ID: {}", createdUser.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
            
        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Failed to create user: " + e.getMessage()));
        }
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        logger.debug("Getting user by ID: {}", id);
        
        try {
            Optional<User> user = userService.getUserById(id);
            
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("User not found with ID: " + id));
            }
            
        } catch (Exception e) {
            logger.error("Error retrieving user by ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal server error"));
        }
    }

    // Get user by username or email
    @GetMapping("/search")
    public ResponseEntity<?> getUserByUsernameOrEmail(@RequestParam String query) {
        logger.debug("Getting user by username or email: {}", query);
        
        try {
            Optional<User> user = userService.getUserByUsernameOrEmail(query);
            
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("User not found with username or email: " + query));
            }
            
        } catch (Exception e) {
            logger.error("Error retrieving user by username or email {}: {}", query, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal server error"));
        }
    }

    // Get all active users
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        logger.debug("Getting all active users");
        
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
            
        } catch (Exception e) {
            logger.error("Error retrieving all users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal server error"));
        }
    }

    // Update user
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        logger.debug("Updating user with ID: {}", id);
        
        try {
            User updateData = new User();
            updateData.setUsername(request.getUsername());
            updateData.setEmail(request.getEmail());
            updateData.setFirstName(request.getFirstName());
            updateData.setLastName(request.getLastName());
            
            User updatedUser = userService.updateUser(id, updateData);
            logger.info("Successfully updated user with ID: {}", id);
            
            return ResponseEntity.ok(updatedUser);
            
        } catch (Exception e) {
            logger.error("Error updating user with ID {}: {}", id, e.getMessage());
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
            } else if (e.getMessage().contains("already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(e.getMessage()));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
            }
        }
    }

    // Update user password
    @PutMapping("/{id}/password")
    public ResponseEntity<?> updatePassword(@PathVariable Long id, @RequestBody UpdatePasswordRequest request) {
        logger.debug("Updating password for user ID: {}", id);
        
        try {
            boolean success = userService.updatePassword(id, request.getCurrentPassword(), request.getNewPassword());
            
            if (success) {
                logger.info("Successfully updated password for user ID: {}", id);
                return ResponseEntity.ok(new SuccessResponse("Password updated successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Current password is incorrect"));
            }
            
        } catch (Exception e) {
            logger.error("Error updating password for user ID {}: {}", id, e.getMessage());
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
            }
        }
    }

    // Deactivate user (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id) {
        logger.debug("Deactivating user with ID: {}", id);
        
        try {
            boolean success = userService.deactivateUser(id);
            
            if (success) {
                logger.info("Successfully deactivated user with ID: {}", id);
                return ResponseEntity.ok(new SuccessResponse("User deactivated successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("User not found with ID: " + id));
            }
            
        } catch (Exception e) {
            logger.error("Error deactivating user with ID {}: {}", id, e.getMessage());
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
            }
        }
    }

    // Reactivate user
    @PutMapping("/{id}/reactivate")
    public ResponseEntity<?> reactivateUser(@PathVariable Long id) {
        logger.debug("Reactivating user with ID: {}", id);
        
        try {
            boolean success = userService.reactivateUser(id);
            
            if (success) {
                logger.info("Successfully reactivated user with ID: {}", id);
                return ResponseEntity.ok(new SuccessResponse("User reactivated successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("User not found with ID: " + id));
            }
            
        } catch (Exception e) {
            logger.error("Error reactivating user with ID {}: {}", id, e.getMessage());
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
            }
        }
    }

    // Inner classes for request/response DTOs
    public static class CreateUserRequest {
        private String username;
        private String email;
        private String password;
        private String firstName;
        private String lastName;

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
    }

    public static class UpdateUserRequest {
        private String username;
        private String email;
        private String firstName;
        private String lastName;

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
    }

    public static class UpdatePasswordRequest {
        private String currentPassword;
        private String newPassword;

        // Getters and setters
        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
        
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
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
