package com.example.LibraryManagementSystem.exception;

public class LoanAlreadyReturnedException extends RuntimeException {
    
    public LoanAlreadyReturnedException(String message){
        super(message);
    }
}
