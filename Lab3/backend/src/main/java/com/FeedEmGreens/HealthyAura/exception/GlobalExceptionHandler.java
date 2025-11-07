package com.FeedEmGreens.HealthyAura.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import com.FeedEmGreens.HealthyAura.service.AuthService.DuplicateUserException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for all controllers in the HealthyAura application.
 *
 * <p>This class provides centralized exception handling across all REST controllers
 * using Spring’s {@link ControllerAdvice}. It ensures consistent, user-friendly error
 * responses in JSON format with standard HTTP status codes.</p>
 *
 * <p>Handled exception categories include:</p>
 * <ul>
 *   <li>404 - Missing endpoints ({@link NoHandlerFoundException})</li>
 *   <li>403 - Access violations ({@link AccessDeniedException})</li>
 *   <li>400 - Invalid or duplicate user operations ({@link DuplicateUserException})</li>
 *   <li>400 - General runtime errors ({@link RuntimeException})</li>
 *   <li>500 - Unhandled or internal exceptions ({@link Exception})</li>
 * </ul>
 *
 * <p>Each response body includes a timestamp, status code, and descriptive message
 * for easier debugging and standardized frontend handling.</p>
 *
 * @see com.FeedEmGreens.HealthyAura.service.AuthService
 * @see com.FeedEmGreens.HealthyAura.controller.loginController
 * @see org.springframework.web.bind.annotation.ControllerAdvice
 * @see org.springframework.web.bind.annotation.ExceptionHandler
 *
 * @version 1.0
 * @since 2025-11-07
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles 404 errors when a client requests an endpoint that does not exist.
     *
     * @param ex the {@link NoHandlerFoundException} thrown by Spring
     * @return a structured JSON response with status 404 (NOT_FOUND)
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoHandlerFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", 404);
        body.put("message", "The requested endpoint does not exist.");
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles 403 errors when a user tries to access a resource they do not have permissions for.
     *
     * @param ex the {@link AccessDeniedException} thrown by Spring Security
     * @return a structured JSON response with status 403 (FORBIDDEN)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", 403);
        body.put("message", "You do not have permission to access this resource.");
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles duplicate registration attempts where the username or email already exists.
     *
     * @param ex the custom {@link DuplicateUserException} thrown by {@link com.FeedEmGreens.HealthyAura.service.AuthService}
     * @return a structured JSON response with status 400 (BAD_REQUEST)
     */
    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateUser(DuplicateUserException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", 400);
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles general runtime exceptions that occur during application execution.
     * <p>This includes cases such as entity not found, invalid arguments, or null references.</p>
     *
     * @param ex the thrown {@link RuntimeException}
     * @return a structured JSON response with status 400 (BAD_REQUEST)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", 400);
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Fallback handler for any unhandled exceptions in the application.
     * <p>This ensures that unexpected errors still return a clean, readable JSON
     * response rather than a default stack trace.</p>
     *
     * @param ex the thrown {@link Exception}
     * @return a structured JSON response with status 500 (INTERNAL_SERVER_ERROR)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", 500);
        body.put("message", "An unexpected error occurred.");
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
