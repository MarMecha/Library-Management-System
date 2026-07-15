package com.example.LibraryManagementSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.LibraryManagementSystem.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
    
}
