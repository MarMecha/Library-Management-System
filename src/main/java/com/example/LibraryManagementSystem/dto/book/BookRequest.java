package com.example.LibraryManagementSystem.dto.book;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BookRequest(
    @NotBlank(message = "Title is required!")
    String title,

    @NotBlank(message = "ISBN is required!")
    String isbn,

    @NotNull(message = "Publication year is required!")
    @Positive(message = "Publication year must be positive!")
    Integer publicationYear,

    @NotNull(message = "Total copies is required!")
    @Positive(message = "Total copies must be positive")
    Integer totalCopies,

    @NotNull(message = "Category ID is required!")
    Long categoryId,

    @NotEmpty(message = "At least one author is required!")
    List<Long> authorIds
    
) {
    
}
