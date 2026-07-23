package com.example.LibraryManagementSystem.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

        @ExceptionHandler(LoanNotFoundException.class)
        public ResponseEntity<Map<String, Object>> handleLoanNotFound(
                LoanNotFoundException exception) {

                Map<String, Object> response = new HashMap<>();
                response.put("timestamp", LocalDateTime.now());
                response.put("status", HttpStatus.NOT_FOUND.value());
                response.put("error", "Loan not found");
                response.put("message", exception.getMessage());

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(response);
        }
        @ExceptionHandler({
                NoAvailableCopiesException.class,
                LoadAlreadyReturnedException.class
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
}