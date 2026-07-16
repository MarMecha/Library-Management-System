package com.example.LibraryManagementSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.LibraryManagementSystem.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
    
}
