package com.example.LibraryManagementSystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.LibraryManagementSystem.entity.Author;

public interface AuthorRepository extends JpaRepository<Author, Long>{

    boolean existsByFirstNameIgnoreCaseAndLastNameIgnoreCase(String normalizedFirstName, String normalizedLastName);

    boolean existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndIdNot( String firstName, String lastName, Long id);
    
    Optional<Author> findByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);
    
    List<Author> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
}
