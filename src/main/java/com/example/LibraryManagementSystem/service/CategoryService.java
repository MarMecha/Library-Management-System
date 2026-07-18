package com.example.LibraryManagementSystem.service;


import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.LibraryManagementSystem.entity.Category;
import com.example.LibraryManagementSystem.exception.CategoryAlreadyExistsException;
import com.example.LibraryManagementSystem.repository.CategoryRepository;


@Service
public class CategoryService {
    private CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    public Category save(Category category){
        String normalizedName = category.getName()
                .trim()
                .toUpperCase();

        if (categoryRepository.existsByName(normalizedName)) {
            throw new CategoryAlreadyExistsException(
                    "Category already exists"
            );
        }

        category.setName(normalizedName);

        return categoryRepository.save(category);
    }

    public List<Category> findAll(){
        return categoryRepository.findAll();
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
                new RuntimeException("Category not found!")
            );
    }

    public List<Category> findByCategoryNameLoose(String name){
        return categoryRepository
                .findByNameContainingIgnoreCase(name.trim());
    }

    public Category update(Long id, Category category){
        
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        String normalizedName = category.getName()
                .trim()
                .toUpperCase();

        if (categoryRepository.existsByNameAndIdNot(
                normalizedName, id)) {

            throw new CategoryAlreadyExistsException(
                    "Category already exists"
            );
        }

        existingCategory.setName(normalizedName);

        return categoryRepository.save(existingCategory);
    }
}
