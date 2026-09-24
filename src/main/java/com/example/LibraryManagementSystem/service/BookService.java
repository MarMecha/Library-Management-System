package com.example.LibraryManagementSystem.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.LibraryManagementSystem.dto.book.BookRequest;
import com.example.LibraryManagementSystem.entity.Author;
import com.example.LibraryManagementSystem.entity.Book;
import com.example.LibraryManagementSystem.entity.Category;
import com.example.LibraryManagementSystem.exception.AuthorNotFoundException;
import com.example.LibraryManagementSystem.exception.BookAlreadyExistsException;
import com.example.LibraryManagementSystem.exception.BookNotFoundException;
import com.example.LibraryManagementSystem.exception.CategoryNotFoundException;
import com.example.LibraryManagementSystem.repository.AuthorRepository;
import com.example.LibraryManagementSystem.repository.BookRepository;
import com.example.LibraryManagementSystem.repository.CategoryRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    
    public Book save(BookRequest request){
        String normalizedIsbn = request.isbn()
            .trim()
            .toUpperCase();

        if (bookRepository.existsByIsbnIgnoreCase(normalizedIsbn)) {
            throw new BookAlreadyExistsException(
                    "Book with this ISBN already exists"
            );
        }
        
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() ->
                        new CategoryNotFoundException("Category not found!")
                );

        Set<Author> authors = new HashSet<>();

        for (Long authorId : request.authorIds()){
            Author author = authorRepository.findById(authorId)
                .orElseThrow(()->
                        new AuthorNotFoundException("Author not found!")
                );
            authors.add(author);
        }
        
        Book book = new Book();

        book.setTitle(request.title().trim());
        book.setIsbn(normalizedIsbn);
        book.setPublicationYear(request.publicationYear());
        book.setTotalCopies(request.totalCopies());
        book.setCategory(category);
        book.setAuthors(authors);

        return bookRepository.save(book);
    }

    public Book update(Long id, BookRequest request){

        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() ->
                    new BookNotFoundException("Book not found!")
            );

        String normalizedIsbn = request.isbn()
                .trim()
                .toUpperCase();

        if (bookRepository.existsByIsbnIgnoreCaseAndIdNot(normalizedIsbn, id)){
            throw new BookAlreadyExistsException(
                "Book with this ISBN already exists!"
            );
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> 
                    new CategoryNotFoundException("Category not found!")
            );

        Set<Author> authors = new HashSet<>();

        for (Long authorId : request.authorIds()){
            Author author = authorRepository.findById(authorId)
                    .orElseThrow(() ->
                        new AuthorNotFoundException("Author not found!")
                );

            authors.add(author);
        }

        existingBook.setTitle(request.title().trim());
        existingBook.setIsbn(normalizedIsbn);
        existingBook.setPublicationYear(request.publicationYear());
        existingBook.setTotalCopies(request.totalCopies());
        existingBook.setCategory(category);
        existingBook.setAuthors(authors);

        return bookRepository.save(existingBook);
    }

    public void delete(Long id){
        bookRepository.deleteById(id);
    }

    public Page<Book> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Book findById(Long id){
        return bookRepository.findById(id)
            .orElseThrow(() ->
                new BookNotFoundException("Book not found!")
        );
    }

    public Book findByTitle(String title){
        return bookRepository
            .findByTitleIgnoreCase(
                title.trim()
            )
            .orElseThrow(()->
                new BookNotFoundException("Book not found!")
            );
    }

    public List<Book> findByTitleLoose(String title){
        return bookRepository
            .findByTitleContainingIgnoreCase(
                title.trim()
            );
    }

}
