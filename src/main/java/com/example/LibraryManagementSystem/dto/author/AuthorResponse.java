package com.example.LibraryManagementSystem.dto.author;

import com.example.LibraryManagementSystem.entity.Author;

public record AuthorResponse(
    Long id,
    String firstName,
    String lastName
) {
    
    public static AuthorResponse from(Author author){
        return new AuthorResponse(
            author.getId(), 
            author.getFirstName(),
            author.getLastName()
        );
    }
}
