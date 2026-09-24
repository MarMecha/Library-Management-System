package com.example.LibraryManagementSystem.service;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Locale;

import com.example.LibraryManagementSystem.dto.category.CategoryRequest;
import com.example.LibraryManagementSystem.entity.Category;
import com.example.LibraryManagementSystem.exception.CategoryAlreadyExistsException;
import com.example.LibraryManagementSystem.exception.CategoryNotFoundException;
import com.example.LibraryManagementSystem.repository.CategoryRepository;


@Service
public class CategoryService {
    private CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    public Category save(CategoryRequest request){
        String normalizedName = request.name()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (categoryRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new CategoryAlreadyExistsException(
                "Category already exists"
            );
        }

        Category category = new Category();

        category.setName(normalizedName);

        return categoryRepository.save(category);
    }

    public Page<Category> findAll(Pageable pageable){
        return categoryRepository.findAll(pageable);
    }

    public Optional<Category> findById(Long id){
        return categoryRepository.findById(id);
    }

    public void delete(Long id){
        categoryRepository.deleteById(id);
    }
    
    public Category findByCategoryName(String name){
        return categoryRepository
            .findByNameIgnoreCase(name.trim())
            .orElseThrow(()->
                new CategoryNotFoundException("Category not found!")
            );
    }

    public List<Category> findByCategoryNameLoose(String name){
        return categoryRepository
                .findByNameContainingIgnoreCase(name.trim());
    }

    public Category update(Long id, CategoryRequest request){
        
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new CategoryNotFoundException("Category not found!")
                    );

        String normalizedName = request.name()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(
                normalizedName, id)) {

            throw new CategoryAlreadyExistsException(
                    "Category already exists"
            );
        }

        existingCategory.setName(normalizedName);

        return categoryRepository.save(existingCategory);
    }
}
