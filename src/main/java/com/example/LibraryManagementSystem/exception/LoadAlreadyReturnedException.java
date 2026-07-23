package com.example.LibraryManagementSystem.exception;

public class LoadAlreadyReturnedException extends RuntimeException {
    
    public LoadAlreadyReturnedException(String message){
        super(message);
    }
}
