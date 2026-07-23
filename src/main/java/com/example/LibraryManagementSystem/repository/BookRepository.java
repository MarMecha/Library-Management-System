package com.example.LibraryManagementSystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.LibraryManagementSystem.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbnIgnoreCase(String isbn);
    
    boolean existsByIsbnIgnoreCaseAndIdNot(String isbn, long id);
    
    Optional<Book> findByTitleIgnoreCase(String title);

    List<Book> findByTitleContainingIgnoreCase(String title);
    
}
