package com.example.LibraryManagementSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.LibraryManagementSystem.entity.Author;

public interface AuthorRepository extends JpaRepository<Author, Long>{
    
}
