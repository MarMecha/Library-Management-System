package com.example.LibraryManagementSystem.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.LibraryManagementSystem.entity.Author;
import com.example.LibraryManagementSystem.entity.Book;
import com.example.LibraryManagementSystem.entity.Category;
import com.example.LibraryManagementSystem.exception.BookAlreadyExistsException;
import com.example.LibraryManagementSystem.repository.AuthorRepository;
import com.example.LibraryManagementSystem.repository.BookRepository;
import com.example.LibraryManagementSystem.repository.CategoryRepository;


@Service
public class BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository, 
                    CategoryRepository categoryRepository,
                    AuthorRepository authorRepository){

        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.authorRepository = authorRepository;
    }

    
    public Book save(Book book, Long categoryId, List<Long> authorIds){
        String normalizedIsbn = book.getIsbn()
            .trim()
            .toUpperCase();

        if (bookRepository.existsByIsbnIgnoreCase(normalizedIsbn)) {
            throw new BookAlreadyExistsException(
                    "Book with this ISBN already exists"
            );
        }
        
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new RuntimeException("Category not found!")
                );

        Set<Author> authors = new HashSet<>();

        for (Long authorId : authorIds){
            Author author = authorRepository.findById(authorId)
                .orElseThrow(()->
                        new RuntimeException("Author not found!")
                );
            authors.add(author);
        }
        

        book.setIsbn(normalizedIsbn);
        book.setTitle(book.getTitle().trim());
        book.setCategory(category);
        book.setAuthors(authors);

        return bookRepository.save(book);
    }

    public Book update(Long id, Book book, Long categoryId, List<Long> authorIds){

        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() ->
                    new RuntimeException("Book not found!")
            );

        String normalizedIsbn = book.getIsbn()
                .trim()
                .toUpperCase();

        if (bookRepository.existsByIsbnIgnoreCaseAndIdNot(normalizedIsbn, id)){
            throw new BookAlreadyExistsException("Book with this ISBN already exists!");
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> 
                    new RuntimeException("Category not found!")
            );

        Set<Author> authors = new HashSet<>();

        for (Long authorId : authorIds){
            Author author = authorRepository.findById(authorId)
                    .orElseThrow(() ->
                        new RuntimeException("Author not found!")
                );

            authors.add(author);
        }

        existingBook.setTitle(book.getTitle().trim());
        existingBook.setIsbn(normalizedIsbn);
        existingBook.setPublicationYear(book.getPublicationYear());
        existingBook.setTotalCopies(book.getTotalCopies());
        existingBook.setCategory(category);
        existingBook.setAuthors(authors);

        return bookRepository.save(existingBook);
    }

    public void delete(Long id){
        bookRepository.deleteById(id);
    }

    public List<Book> findAll(){
        return bookRepository.findAll();
    }

    public Optional<Book> findById(Long id){
        return bookRepository.findById(id);
    }

    public Book findByTitle(String title){
        return bookRepository
            .findByTitleIgnoreCase(
                title.trim()
            )
            .orElseThrow(()->
                new RuntimeException("Book not found!")
            );
    }

    public List<Book> findByTitleLoose(String title){
        return bookRepository
            .findByTitleContainingIgnoreCase(
                title.trim()
            );
    }

}
