package com.example.LibraryManagementSystem.controller;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import com.example.LibraryManagementSystem.dto.common.PageResponse;
import com.example.LibraryManagementSystem.service.AuthorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Authors", description = "Author catalog management")
@RestController
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService){
        this.authorService = authorService;
    } 

    @Operation(summary = "Create an author", description = "Creates a new author. Requires ADMIN role.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Author created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "ADMIN role required"),
        @ApiResponse(responseCode = "409", description = "Author already exists")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorResponse create(@Valid @RequestBody AuthorRequest request){
        return AuthorResponse.from(
            authorService.save(request));
    }    

    @Operation(summary = "Update an author", description = "Updates an author using its ID. Requires ADMIN role.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Author updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "Author not found"),
        @ApiResponse(responseCode = "409", description = "Author already exists")
    })
    @PutMapping("/{id}") //PUT /authors/1
    public AuthorResponse update(@PathVariable Long id, @Valid @RequestBody AuthorRequest request){
        return AuthorResponse.from(
            authorService.update(id, request));
    }

    @Operation(summary = "Get all authors", description = "Returns all authors in the catalog.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Authors returned successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping
    public PageResponse<AuthorResponse> findAll(
        @ParameterObject
        @PageableDefault(size = 10, sort = "lastName")
        Pageable pageable
    ){
        return PageResponse.from(
            authorService.findAll(pageable)
                .map(AuthorResponse::from)
        );
    }

    @Operation(summary = "Get author by ID", description = "Returns a specific author using its ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Author returned successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "404", description = "Author not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> findById(@PathVariable Long id){
        return authorService.findById(id)
            .map(AuthorResponse::from)
            .map(ResponseEntity::ok)
            .orElseGet(() ->
                ResponseEntity.notFound().build()
        );
    }

    @Operation(summary = "Find author by full name", description = "Returns an author matching the given first and last name.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Author returned successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "404", description = "Author not found")
    })
    @GetMapping("/search/full-name")
    public AuthorResponse findByFullName(@RequestParam String firstName, @RequestParam String lastName) {
        return AuthorResponse.from(
            authorService.findByAuthorFullName(
                firstName, 
                lastName
            )
        );
    }

    @Operation(summary = "Search authors by name", description = "Returns authors whose names contain the given text, ignoring letter case.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Search completed successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/search")
    public List<AuthorResponse> searchLoose(@RequestParam String name) {
        return authorService.findByAuthorNameLoose(name)
            .stream()
            .map(AuthorResponse::from)
            .toList();
    }    

    @Operation(summary = "Delete an author", description = "Deletes an author using its ID. Requires ADMIN role.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Author deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "Author not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    } 
}
