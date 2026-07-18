package com.example.LibraryManagementSystem.exception;

public class AuthorAlreadyExistsException extends RuntimeException {
    
    public AuthorAlreadyExistsException(String message){
        super(message);
    }
}
