package com.example.LibraryManagementSystem.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.LibraryManagementSystem.dto.category.CategoryRequest;
import com.example.LibraryManagementSystem.dto.category.CategoryResponse;
import com.example.LibraryManagementSystem.dto.common.PageResponse;
import com.example.LibraryManagementSystem.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Categories", description = "Book category management")
@RestController
@RequestMapping("/categories")
public class CategoryController {
    
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService){
        this.categoryService = categoryService;
    }

    @Operation(summary = "Create a category", description = "Creates a new category. Requires ADMIN role.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Category created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "ADMIN role required"),
        @ApiResponse(responseCode = "409", description = "Category already exists")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(@Valid @RequestBody CategoryRequest request) {

        return CategoryResponse.from(categoryService.save(request));
    }

    @Operation(summary = "Get all categories", description = "Returns all book categories.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categories returned successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping
    public PageResponse<CategoryResponse> findAll(
        @ParameterObject
        @PageableDefault(size = 10, sort = "name")
        Pageable pageable
    ){
        return PageResponse.from(
            categoryService.findAll(pageable)
                .map(CategoryResponse::from)
        );
    }

    @Operation(summary = "Get category by ID", description = "Returns a specific category using its ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Category returned successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> findById(@PathVariable Long id) {
        return categoryService.findById(id)
            .map(CategoryResponse::from)
            .map(ResponseEntity::ok)
            .orElseGet(() -> 
                ResponseEntity.notFound().build()
            );
    }

    @Operation(summary = "Update a category", description = "Updates a category using its ID. Requires ADMIN role.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Category updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "Category not found"),
        @ApiResponse(responseCode = "409", description = "Category already exists")
    })
    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request){

        return CategoryResponse.from(categoryService.update(id, request));
    }

    @Operation(summary = "Delete a category", description = "Deletes a category using its ID. Requires ADMIN role.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    
    
}
