package com.example.LibraryManagementSystem.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, Object>> handleValidationErrors(
                MethodArgumentNotValidException exception) {

                Map<String, String> validationErrors = new HashMap<>();

                exception.getBindingResult()
                        .getFieldErrors()
                        .forEach(error ->
                                validationErrors.put(
                                        error.getField(),
                                        error.getDefaultMessage()
                                )
                        );

                Map<String, Object> response = new HashMap<>();
                response.put("timestamp", LocalDateTime.now());
                response.put("status", HttpStatus.BAD_REQUEST.value());
                response.put("error", "Validation failed");
                response.put("validationErrors", validationErrors);

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(response);
        }

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<Map<String, Object>> handleDatabaseConstraint(
                DataIntegrityViolationException exception) {

                Map<String, Object> response = new HashMap<>();
                response.put("timestamp", LocalDateTime.now());
                response.put("status", HttpStatus.CONFLICT.value());
                response.put("error", "Database constraint violation");
                response.put("message", "Email already exists");

                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(response);
        }

        @ExceptionHandler({
                NoAvailableCopiesException.class,
                LoanAlreadyReturnedException.class
                })
        public ResponseEntity<Map<String, Object>> handleLoanConflict(
                RuntimeException exception) {

                Map<String, Object> response = new HashMap<>();
                response.put("timestamp", LocalDateTime.now());
                response.put("status", HttpStatus.CONFLICT.value());
                response.put("error", "Loan conflict");
                response.put("message", exception.getMessage());

                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(response);
        }
        @ExceptionHandler(EmailAlreadyExistsException.class)
        public ResponseEntity<Map<String, Object>> 
                handleEmailAlreadyExists(
                        EmailAlreadyExistsException exception){
                
                Map<String, Object> response = new HashMap<>();

                response.put("timestamp", LocalDateTime.now());
                response.put("status", HttpStatus.CONFLICT.value());
                response.put("error", "Email already registered!");
                response.put("message", exception.getMessage());

                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(response);
        }

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<Map<String, Object>>
                handleBadCredentials(BadCredentialsException exception){

                Map<String, Object> response = new HashMap<>();

                response.put("timestamp", LocalDateTime.now());
                response.put("status", HttpStatus.UNAUTHORIZED.value());
                response.put("error", "Invalid credentials");
                response.put("message", "Email or password is incorrect");

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
        }

        @ExceptionHandler({
        CategoryAlreadyExistsException.class,
        AuthorAlreadyExistsException.class,
        BookAlreadyExistsException.class,
        })
        public ResponseEntity<Map<String, Object>>
                handleCatalogConflict(RuntimeException exception) {

                Map<String, Object> response = new HashMap<>();

                response.put("timestamp", LocalDateTime.now());
                response.put("status", HttpStatus.CONFLICT.value());
                response.put("error", "Resource already exists");
                response.put("message", exception.getMessage());

                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(response);
        }

        @ExceptionHandler({
        BookNotFoundException.class,
        CategoryNotFoundException.class,
        AuthorNotFoundException.class,
        LoanNotFoundException.class
        })
        public ResponseEntity<Map<String, Object>> handleResourceNotFound(
                RuntimeException exception) {

                Map<String, Object> response = new HashMap<>();

                response.put("timestamp", LocalDateTime.now());
                response.put("status", HttpStatus.NOT_FOUND.value());
                response.put("error", "Resource not found");
                response.put("message", exception.getMessage());

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(response);
        }
}