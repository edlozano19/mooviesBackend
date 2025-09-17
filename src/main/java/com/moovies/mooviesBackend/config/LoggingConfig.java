package com.moovies.mooviesBackend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * Logging configuration and utilities for the Movies Backend application.
 * This class provides centralized logging configuration and helper methods.
 */
@Configuration
public class LoggingConfig {
    
    /**
     * Creates a logger for the specified class.
     * This is a utility method to ensure consistent logger creation across the application.
     * 
     * @param clazz The class for which to create the logger
     * @return SLF4J Logger instance
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
    
    /**
     * Logs method entry with parameters.
     * Useful for debugging method calls and parameter values.
     * 
     * @param logger The logger instance
     * @param methodName The name of the method being entered
     * @param params The parameters passed to the method
     */
    public static void logMethodEntry(Logger logger, String methodName, Object... params) {
        if (logger.isDebugEnabled()) {
            logger.debug("Entering method: {} with parameters: {}", methodName, params);
        }
    }
    
    /**
     * Logs method exit with return value.
     * Useful for debugging method returns.
     * 
     * @param logger The logger instance
     * @param methodName The name of the method being exited
     * @param returnValue The value being returned (can be null)
     */
    public static void logMethodExit(Logger logger, String methodName, Object returnValue) {
        if (logger.isDebugEnabled()) {
            logger.debug("Exiting method: {} with return value: {}", methodName, returnValue);
        }
    }
    
    /**
     * Logs business operation with context.
     * Useful for tracking business logic execution.
     * 
     * @param logger The logger instance
     * @param operation The business operation being performed
     * @param context Additional context information
     */
    public static void logBusinessOperation(Logger logger, String operation, String context) {
        logger.info("Business Operation: {} - Context: {}", operation, context);
    }
    
    /**
     * Logs security-related events.
     * Important for audit trails and security monitoring.
     * 
     * @param logger The logger instance
     * @param event The security event
     * @param userId The user ID involved (can be null)
     * @param details Additional details
     */
    public static void logSecurityEvent(Logger logger, String event, Long userId, String details) {
        logger.warn("Security Event: {} - User ID: {} - Details: {}", event, userId, details);
    }
}
