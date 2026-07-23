package com.example.LibraryManagementSystem.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.LibraryManagementSystem.entity.Author;
import com.example.LibraryManagementSystem.service.AuthorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService){
        this.authorService = authorService;
    } 

    @PostMapping
    public Author create(@Valid @RequestBody Author author){
        return authorService.save(author);
    }    

    @PutMapping("/{id}") //PUT /authors/1
    public Author update(@PathVariable Long id, @Valid @RequestBody Author author){
        return authorService.update(id, author);
    }

    @GetMapping
    public List<Author> findAll(){
        return authorService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Author> findById(@PathVariable Long id){
        return authorService.findById(id);
    }

    @GetMapping("/search/full-name")
    public Author findByFullName(@RequestParam String firstName, @RequestParam String lastName) {
        return authorService.findByAuthorFullName(firstName, lastName);
    }

    @GetMapping("/search")
    public List<Author> searchLoose(@RequestParam String name) {
        return authorService.findByAuthorNameLoose(name);
    }    

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    } 
}
