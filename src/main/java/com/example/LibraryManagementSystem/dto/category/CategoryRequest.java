package com.example.LibraryManagementSystem.dto.category;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
    @NotBlank(message = "Category name is required!")
    String name) {
    
    
}
