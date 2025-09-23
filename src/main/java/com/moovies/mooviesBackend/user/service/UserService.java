package com.moovies.mooviesBackend.user.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.moovies.mooviesBackend.user.entity.User;
import com.moovies.mooviesBackend.user.repository.UserRepository;

import com.moovies.mooviesBackend.exception.UserAlreadyActiveException;
import com.moovies.mooviesBackend.exception.UserAlreadyInactiveException;
import com.moovies.mooviesBackend.exception.UserEmailAlreadyExistsException;
import com.moovies.mooviesBackend.exception.UserNotFoundException;
import com.moovies.mooviesBackend.exception.UserPasswordMismatchException;
import com.moovies.mooviesBackend.exception.UserUsernameAlreadyExists;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(User user, String plainPassword) {
        logger.debug("Creating user with username: {} and email: {}", user.getUsername(), user.getEmail());

        if (userRepository.existsByUsername(user.getUsername())) {
            logger.warn("User creation failed: Username '{}' already exists", user.getUsername());
            throw new UserUsernameAlreadyExists();
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            logger.warn("User creation failed: Email '{}' already exists", user.getEmail());
            throw new UserEmailAlreadyExistsException();
        }

        user.setPasswordHash(passwordEncoder.encode(plainPassword));
        User savedUser = userRepository.save(user);
        logger.info("Successfully created user with ID: {} and username: {}", 
            savedUser.getId(), savedUser.getUsername());
        
        return savedUser;
    }

    public Optional<User> getUserById(Long id) {
        logger.debug("Getting user by ID: {}", id);
        Optional<User> user = userRepository.findById(id);
        
        if (user.isEmpty()) {
            logger.warn("User not found with ID: {}", id);
            throw new UserNotFoundException(id);
        }
        else {
            return userRepository.findById(id);
        }
    }

    public Optional<User> getUserByUsernameOrEmail(String usernameOrEmail) {
        logger.debug("Getting user by username or email: {}", usernameOrEmail);

        Optional<User> user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);

        if (user.isEmpty()) {
            logger.warn("User not found with username or email: {}", usernameOrEmail);
            throw new UserNotFoundException(usernameOrEmail);
        }
        else {
            return user;
        }
    }

    public User updateUser(Long userId, User updatedUser) {
        logger.debug("Updating user with ID: {}", userId);
        
        // Find existing user
        User existingUser = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        // Update username if provided
        if (updatedUser.getUsername() != null) {
            // Check if new username is already taken by another user
            if (userRepository.existsByUsername(updatedUser.getUsername()) && 
                !existingUser.getUsername().equals(updatedUser.getUsername())) {
                throw new UserUsernameAlreadyExists();
            }
            existingUser.setUsername(updatedUser.getUsername());
        }
        
        // Update email if provided
        if (updatedUser.getEmail() != null) {
            // Check if new email is already taken by another user
            if (userRepository.existsByEmail(updatedUser.getEmail()) && 
                !existingUser.getEmail().equals(updatedUser.getEmail())) {
                throw new UserEmailAlreadyExistsException();
            }
            existingUser.setEmail(updatedUser.getEmail());
        }
        
        // Update other fields if provided
        if (updatedUser.getFirstName() != null) {
            existingUser.setFirstName(updatedUser.getFirstName());
        }
        
        if (updatedUser.getLastName() != null) {
            existingUser.setLastName(updatedUser.getLastName());
        }
        
        // JPA automatically saves changes due to @Transactional
        logger.info("Successfully updated user with ID: {}", userId);
        return existingUser;
    }

    @Transactional
    public boolean updatePassword(Long userId, String currentPassword, String newPassword) {
        logger.debug("Updating password for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            logger.warn("Password update failed for user ID {}: current password is incorrect", userId);
            throw new UserPasswordMismatchException();
        }
        
        // Update password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        logger.info("Successfully updated password for user ID: {}", userId);
        return true;
    }

    @Transactional
    public boolean deactivateUser(Long userId) {
        logger.debug("Deactivating user with ID: {}", userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        if (!user.getIsActive()) {
            logger.warn("Attempted to deactivate user with ID {} who is already inactive", userId);
            throw new UserAlreadyInactiveException(userId);
        }

        user.setIsActive(false);
        logger.info("Successfully deactivated user with ID: {}", userId);
        return true;
    }

    @Transactional
    public boolean reactivateUser(Long userId) {
        logger.debug("Reactivating user with ID: {}", userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.getIsActive()) {
            logger.warn("Attempted to reactivate user with ID {} who is already active", userId);
            throw new UserAlreadyActiveException(userId);
        }
        
        user.setIsActive(true);
        logger.info("Successfully reactivated user with ID: {}", userId);
        return true;
    }

    @Transactional
    public boolean deactivateBulkUsers(List<Long> userIds) {
        logger.debug("Deactivating bulk users with IDs: {}", userIds);
        
        try {
            List<User> existingUsers = userRepository.findAllById(userIds);

            if (existingUsers.size() != userIds.size()) {
                Set<Long> foundIds = existingUsers.stream()
                    .map(User::getId)
                    .collect(Collectors.toSet());

                List<Long> missingIds = userIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toList());

                throw new RuntimeException("Some users not found: " + missingIds);
            }

            else {
                existingUsers.forEach(user -> user.setIsActive(false));
                logger.info("Successfully deactivated bulk users with IDs: {}", userIds);
                return true;
            }
            
        } catch (Exception e ) {
            logger.error("Error deactivating bulk users with IDs {}: {}", userIds, e.getMessage(), e);
            throw e;
        }
    }


    public List<User> getAllUsers() {
        return userRepository.findAllByIsActiveTrue();
    }

    public List<User> getInactiveUsers() {
        return userRepository.findAllByIsActiveFalse();
    }
    
}
