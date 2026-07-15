package com.example.LibraryManagementSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.LibraryManagementSystem.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>{
    
}
