package com.example.LibraryManagementSystem.exception;

public class BookNotFoundException extends RuntimeException{
    
    public BookNotFoundException(String message){
        super(message);
    }
}
