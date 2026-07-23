package com.example.LibraryManagementSystem.exception;

public class NoAvailableCopiesException extends RuntimeException{
    
    public NoAvailableCopiesException(String message){
        super(message);
    }
}
