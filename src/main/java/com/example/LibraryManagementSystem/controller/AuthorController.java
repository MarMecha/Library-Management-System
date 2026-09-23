package com.example.LibraryManagementSystem.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.ResponseStatus;

import com.example.LibraryManagementSystem.dto.author.AuthorRequest;
import com.example.LibraryManagementSystem.dto.author.AuthorResponse;
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
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorResponse create(@Valid @RequestBody AuthorRequest request){
        return AuthorResponse.from(
            authorService.save(request));
    }    

    @PutMapping("/{id}") //PUT /authors/1
    public AuthorResponse update(@PathVariable Long id, @Valid @RequestBody AuthorRequest request){
        return AuthorResponse.from(
            authorService.update(id, request));
    }

    @GetMapping
    public List<AuthorResponse> findAll(){
        return authorService.findAll()
            .stream()
            .map(AuthorResponse::from)
            .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> findById(@PathVariable Long id){
        return authorService.findById(id)
            .map(AuthorResponse::from)
            .map(ResponseEntity::ok)
            .orElseGet(() ->
                ResponseEntity.notFound().build()
        );
    }

    @GetMapping("/search/full-name")
    public AuthorResponse findByFullName(@RequestParam String firstName, @RequestParam String lastName) {
        return AuthorResponse.from(
            authorService.findByAuthorFullName(
                firstName, 
                lastName
            )
        );
    }

    @GetMapping("/search")
    public List<AuthorResponse> searchLoose(@RequestParam String name) {
        return authorService.findByAuthorNameLoose(name)
            .stream()
            .map(AuthorResponse::from)
            .toList();
    }    

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    } 
}
