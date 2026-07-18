package com.example.LibraryManagementSystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.LibraryManagementSystem.entity.Author;

public interface AuthorRepository extends JpaRepository<Author, Long>{

    boolean existsByFirstNameAndLastName(String nomralizedFirstName, String normalizedLastName);

    Optional<Author> findByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);
    
    List<Author> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
}
