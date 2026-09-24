package com.example.LibraryManagementSystem.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.LibraryManagementSystem.dto.book.BookRequest;
import com.example.LibraryManagementSystem.dto.book.BookResponse;
import com.example.LibraryManagementSystem.service.BookService;
import com.example.LibraryManagementSystem.dto.common.PageResponse;


import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springdoc.core.annotations.ParameterObject;



import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
    name = "Books",
    description = "Book catalog management"
)
@RestController
@RequestMapping("/books")
public class BookController {
    
    private final BookService bookService;

    public BookController(BookService bookService){
        this.bookService = bookService;
    }
    
    @Operation(
        summary = "Create a book",
        description = "Creates a new book. Requires ADMIN role."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Book created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "Category or author not found"),
        @ApiResponse(responseCode = "409", description = "A book with this ISBN already exists")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse create(@Valid @RequestBody BookRequest request) {

        return BookResponse.from(bookService.save(request));
    }
    
    @Operation(
        summary = "Update a book",
        description = "Updates an existing book using its ID. Requires ADMIN role."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Book updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "Book, category or author not found"),
        @ApiResponse(responseCode = "409", description = "ISBN already belongs to another book")
    })
    @PutMapping("/{id}")
    public BookResponse update(
        @PathVariable Long id,
        @Valid @RequestBody BookRequest request){

        return BookResponse.from(bookService.update(id, request));
    }
    
    @Operation(
        summary = "Get all books",
        description = "Returns all books in the catalog."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Books returned successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping
    public PageResponse<BookResponse> getAll(
        @ParameterObject
        @PageableDefault(size = 10, sort = "title")
        Pageable pageable
    ) {
        return PageResponse.from(
            bookService.findAll(pageable)
                .map(BookResponse::from)
        );
    }

    @Operation(
        summary = "Get book by ID",
        description = "Returns a specific book using its ID."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Book returned successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @GetMapping("/{id}")
    public BookResponse getById(@PathVariable Long id) {
        return BookResponse.from(
            bookService.findById(id)
        );
    }
    
    @Operation(
        summary = "Find book by exact title",
        description = "Returns a book whose title exactly matches the given title."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Book returned successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @GetMapping("/search/full-title")
    public BookResponse findByTitle(@RequestParam String title){
        return BookResponse.from(bookService.findByTitle(title));
    }
    
    @Operation(
        summary = "Search books by title",
        description = "Returns all books whose titles contain the given text, ignoring letter case."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Search completed successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/search")
    public List<BookResponse> searchLoose(@RequestParam String title){
        return bookService.findByTitleLoose(title)
            .stream()
            .map(BookResponse::from)
            .toList();
    }

    @Operation(
        summary = "Delete a book",
        description = "Deletes an existing book using its ID. Requires ADMIN role."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Book deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        bookService.delete(id);
    }
}
