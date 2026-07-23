package com.example.LibraryManagementSystem.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.LibraryManagementSystem.entity.Book;
import com.example.LibraryManagementSystem.service.BookService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/books")
public class BookController {
    
    private final BookService bookService;

    public BookController(BookService bookService){
        this.bookService = bookService;
    }
    
    @PostMapping
    public Book create(@Valid @RequestBody Book book, 
                    @RequestParam Long categoryId, 
                    @RequestParam List<Long> authorIds) {
        return bookService.save(book, categoryId, authorIds);
    }
    
    @PutMapping("/{id}")
    public Book update(@PathVariable Long id,
                    @Valid @RequestBody Book book,
                    @RequestParam Long categoryId,
                    @RequestParam List<Long> authorIds){

        return bookService.update(id, book, categoryId, authorIds);
    }
    
    @GetMapping
    public List<Book> getAll(){
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Book> getById(@PathVariable Long id) {
        return bookService.findById(id);
    }
    

    @GetMapping("/search/full-title")
    public Book findByTitle(@RequestParam String title){
        return bookService.findByTitle(title);
    }
    
    @GetMapping("/search")
    public List<Book> searchLoose(@RequestParam String title){
        return bookService.findByTitleLoose(title);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        bookService.delete(id);
    }
}
