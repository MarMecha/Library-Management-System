package com.example.LibraryManagementSystem.dto.book;

import java.util.List;

import com.example.LibraryManagementSystem.dto.author.AuthorResponse;
import com.example.LibraryManagementSystem.dto.category.CategoryResponse;
import com.example.LibraryManagementSystem.entity.Book;

public record BookResponse(
    Long id,
    String title,
    String isbn,
    Integer publicationYear,
    Integer totalCopies,
    CategoryResponse category,
    List<AuthorResponse> authors
) {
    
    public static BookResponse from(Book book){
        List<AuthorResponse> authorResponses =
            book.getAuthors()
                .stream()
                .map(AuthorResponse::from)
                .toList();

        return new BookResponse(
            book.getId(), 
            book.getTitle(), 
            book.getIsbn(), 
            book.getPublicationYear(), 
            book.getTotalCopies(), 
            CategoryResponse.from(book.getCategory()), 
            authorResponses
        );
    }
}
