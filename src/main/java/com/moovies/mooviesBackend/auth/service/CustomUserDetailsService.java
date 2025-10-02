package com.moovies.mooviesBackend.auth.service;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.moovies.mooviesBackend.config.LoggingConfig;
import com.moovies.mooviesBackend.user.entity.User;
import com.moovies.mooviesBackend.user.service.UserService;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger logger = LoggingConfig.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Loading user by username: {}", username);

        try {
            User user = userService.getUserByUsernameOrEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: {}" + username));

            logger.debug("User found: {}, Active: {}, Role: {}", user.getUsername(), user.getIsActive(), user.getRole());
            return new UserPrincipal(user);
        } catch (Exception e) {
            logger.error("Error loading user by username: {}", username, e);
            throw new UsernameNotFoundException("User not found: " + username, e);
        }
    }
}
