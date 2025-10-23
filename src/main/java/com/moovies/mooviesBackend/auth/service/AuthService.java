package com.moovies.mooviesBackend.auth.service;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moovies.mooviesBackend.auth.dto.LoginRequest;
import com.moovies.mooviesBackend.auth.dto.LoginResponse;
import com.moovies.mooviesBackend.auth.dto.RegisterRequest;
import com.moovies.mooviesBackend.user.repository.UserRepository;
import com.moovies.mooviesBackend.config.LoggingConfig;
import com.moovies.mooviesBackend.exception.UserEmailAlreadyExistsException;
import com.moovies.mooviesBackend.exception.UserNotFoundException;
import com.moovies.mooviesBackend.exception.UserPasswordMismatchException;
import com.moovies.mooviesBackend.exception.UserUsernameAlreadyExists;
import com.moovies.mooviesBackend.user.controller.UserController.CreateUserRequest;
import com.moovies.mooviesBackend.user.entity.User;
import com.moovies.mooviesBackend.user.entity.UserRole;
import com.moovies.mooviesBackend.user.service.UserService;

import lombok.Builder;

@Service
@Transactional
public class AuthService {
    private static final Logger logger = LoggingConfig.getLogger(AuthService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest loginRequest) {
       logger.debug("Attempting to login for user or email: {}", loginRequest.getUsernameOrEmail()); 

       try {
        User user = userService.getUserByUsernameOrEmail(loginRequest.getUsernameOrEmail())
         .orElseThrow(() -> new UserNotFoundException(loginRequest.getUsernameOrEmail()));

         if (!user.getIsActive()) {
            logger.warn("Login attempt for inactive user: {}", loginRequest.getUsernameOrEmail());
            throw new RuntimeException("Invalid credentials");
         }

         if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            logger.warn("Invalid password for user: {}", loginRequest.getUsernameOrEmail());
            throw new UserPasswordMismatchException();
         }

         String token = jwtService.generateToken(user);
         logger.info("Succesful login for user: {}", loginRequest.getUsernameOrEmail());

         return new LoginResponse(token, "Bearer", user.getId(), user.getUsername(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getRole());
        } catch (Exception e) {
            logger.error("Login failed for user or email: {}", loginRequest.getUsernameOrEmail(), e.getMessage());
            throw e;
        }
    }

    public User register(RegisterRequest registerRequest) {
        logger.debug("Attempting to register user: {}", registerRequest.getUsername());

        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            logger.warn("User registration failed: Username '{}' already exists", registerRequest.getUsername());
            throw new UserUsernameAlreadyExists();
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            logger.warn("User registration failed: Email '{}' already exists", registerRequest.getEmail());
            throw new UserEmailAlreadyExistsException();
        }

        User newUser = User.builder()
            .username(registerRequest.getUsername())
            .email(registerRequest.getEmail())
            .passwordHash(passwordEncoder.encode(registerRequest.getPassword()))
            .firstName(registerRequest.getFirstName())
            .lastName(registerRequest.getLastName())
            .isActive(true)
            .role(UserRole.USER)
            .build();

        User savedUser = userRepository.save(newUser);

        logger.info("User registered successfully: {}", registerRequest.getUsername());
        return savedUser;
    }

    public User authenticate(String username, String password) {
        logger.debug("Attempting to authenticate user: {}", username);

        User user = userService.getUserByUsernameOrEmail(username)
            .orElseThrow(() -> new UserNotFoundException(username));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        if (!user.getIsActive()) {
            throw new RuntimeException("User is inactive");
        }

        logger.debug("User authenticated successfully {}", username);
        return user;
    }
}
