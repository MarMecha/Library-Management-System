package com.example.LibraryManagementSystem.dto.category;

import com.example.LibraryManagementSystem.entity.Category;

public record CategoryResponse(Long id, String name) {
    
    public static CategoryResponse from(Category category ){
        return new CategoryResponse(
            category.getId(),
            category.getName()
        );
    }
}
