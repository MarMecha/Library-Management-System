package com.example.LibraryManagementSystem.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.LibraryManagementSystem.dto.book.BookRequest;
import com.example.LibraryManagementSystem.dto.book.BookResponse;
import com.example.LibraryManagementSystem.service.BookService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse create(@Valid @RequestBody BookRequest request) {

        return BookResponse.from(bookService.save(request));
    }
    
    @PutMapping("/{id}")
    public BookResponse update(
        @PathVariable Long id,
        @Valid @RequestBody BookRequest request){

        return BookResponse.from(bookService.update(id, request));
    }
    
    @GetMapping
    public List<BookResponse> getAll(){
        return bookService.findAll()
            .stream()
            .map(BookResponse::from)
            .toList();
    }

    @GetMapping("/{id}")
    public BookResponse getById(@PathVariable Long id) {
        return BookResponse.from(
            bookService.findById(id)
        );
    }
    

    @GetMapping("/search/full-title")
    public BookResponse findByTitle(@RequestParam String title){
        return BookResponse.from(bookService.findByTitle(title));
    }
    
    @GetMapping("/search")
    public List<BookResponse> searchLoose(@RequestParam String title){
        return bookService.findByTitleLoose(title)
            .stream()
            .map(BookResponse::from)
            .toList();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        bookService.delete(id);
    }
}
