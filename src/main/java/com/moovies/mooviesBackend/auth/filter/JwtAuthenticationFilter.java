package com.moovies.mooviesBackend.auth.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.moovies.mooviesBackend.auth.service.JwtService;
import com.moovies.mooviesBackend.config.LoggingConfig;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggingConfig.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        //If we dont have a JWT token, we skip the filter
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.debug("No JWT token found in request");
            filterChain.doFilter(request, response);
            return;
        }

        //Extract the JWT token from the Authorization header (We skip the "Bearer " prefix)
        jwt = authHeader.substring(7);
        logger.debug("JWT extracted from request");

        try {
            username = jwtService.extractUsername(jwt);
            logger.debug("Username extracted from JWT {}", username);

            //If we have a username and the user is not authenticated, we load the user details
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtService.validateToken(jwt, username)) {
                    logger.debug("JWT token is valid for user {}", username);

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken (userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    logger.info("User {} authenticated successfully via JWT");
                }
                else {
                    logger.warn("JWT token is invalid for user {}", username);
                }
            }
        } catch (Exception e) {
            logger.error("Error processing JWT token {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
