package com.example.LibraryManagementSystem.exception;

public class LoanNotFoundException extends RuntimeException {
    
    public LoanNotFoundException(String message){
        super(message);
    }
}
