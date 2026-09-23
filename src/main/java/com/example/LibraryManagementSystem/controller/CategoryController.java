package com.example.LibraryManagementSystem.controller;

import java.util.List;

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
import com.example.LibraryManagementSystem.service.CategoryService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/categories")
public class CategoryController {
    
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService){
        this.categoryService = categoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(@Valid @RequestBody CategoryRequest request) {

        return CategoryResponse.from(categoryService.save(request));
    }

    @GetMapping
    public List<CategoryResponse> findAll(){
        return categoryService.findAll()
            .stream()
            .map(CategoryResponse::from)
            .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> findById(@PathVariable Long id) {
        return categoryService.findById(id)
            .map(CategoryResponse::from)
            .map(ResponseEntity::ok)
            .orElseGet(() -> 
                ResponseEntity.notFound().build()
            );
    }

    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request){

        return CategoryResponse.from(categoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    
    
}
