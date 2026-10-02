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
    List<CategoryResponse> categories,
    List<AuthorResponse> authors
) {
    
    public static BookResponse from(Book book){
        List<AuthorResponse> authorResponses =
            book.getAuthors()
                .stream()
                .map(AuthorResponse::from)
                .toList();

        List<CategoryResponse> categoryResponses =
            book.getCategories()
                .stream()
                .map(CategoryResponse::from)
                .toList();

        return new BookResponse(
            book.getId(), 
            book.getTitle(), 
            book.getIsbn(), 
            book.getPublicationYear(), 
            book.getTotalCopies(), 
            categoryResponses,
            authorResponses
        );
    }
}
