package com.example.LibraryManagementSystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private BookService bookService;

    @Test  //Happy path: Έγκυρα δεδομένα → επιτυχής αποθήκευση
    void save_shouldCreateBook_whenRequestIsValid(){
        //Arrange
        BookRequest request = new BookRequest(
            "Clean Code", 
            " isbn-123 ", 
            2008, 
            3, 
            1L, 
            List.of(2L)
        );

        Category category = new Category();
        category.setId(1L);

        Author author = new Author();
        author.setId(2L);

        when(bookRepository.existsByIsbnIgnoreCase("ISBN-123"))
            .thenReturn(false);

        when(categoryRepository.findById(1L))
            .thenReturn(Optional.of(category));
        
        when(authorRepository.findById(2L))
            .thenReturn(Optional.of(author));
        
        when(bookRepository.save(any(Book.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        //act
        Book result = bookService.save(request);

        //Assert
        assertEquals("Clean Code", result.getTitle());
        assertEquals("ISBN-123", result.getIsbn());
        assertEquals(2008, result.getPublicationYear());
        assertEquals(3, result.getTotalCopies());
        assertSame(category, result.getCategory());
        assertTrue(result.getAuthors().contains(author));

        verify(bookRepository).save(any(Book.class));
    }

    @Test //Error path: Duplicate ISBN → αναμενόμενο exception
    void save_shouldThrowException_whenIsbnAlreadyExists(){
        //Arrang
        BookRequest request = new BookRequest(
            "Clean Code", 
            "isbn-123", 
            2008, 
            3, 
            1L, 
            List.of(2L)
        );

        when(bookRepository.existsByIsbnIgnoreCase("ISBN-123"))
            .thenReturn(true);

        //Act & Assert
        assertThrows(
            BookAlreadyExistsException.class, 
            () -> bookService.save(request)
        );

        verify(bookRepository, never())
            .save(any(Book.class));
    }
    
    @Test //ανύπαρκτη κατηγορία -> CategoryNotFoundException.
    void save_shouldThrowException_whenCategoryDoesNotExist(){
        //Arrange
        BookRequest request = new BookRequest(
            "Clean Code", 
            "isbn-123", 
            2008, 
            3, 
            999L, 
            List.of(2L)
        );
        
        when(bookRepository.existsByIsbnIgnoreCase("ISBN-123"))
            .thenReturn(false);

        when(categoryRepository.findById(999L))
            .thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(
            CategoryNotFoundException.class, 
            () -> bookService.save(request)
        );

        verify(bookRepository, never())
            .save(any(Book.class));
    }

    @Test 
    void save_shouldThrowException_whenAuthorDoesNotExist(){
        //Arrange
        BookRequest request = new BookRequest(
            "Clean Code", 
            "isbn-123", 
            2008, 
            3, 
            1L, 
            List.of(999L)
        );

        Category category = new Category();
        category.setId(1L);

        when(categoryRepository.findById(1L))
            .thenReturn(Optional.of(category));

        when(bookRepository.existsByIsbnIgnoreCase("ISBN-123"))
            .thenReturn(false);

        when(authorRepository.findById(999L))
            .thenReturn(Optional.empty());   
            
        //Act & Assert
        assertThrows(AuthorNotFoundException.class, 
            () -> bookService.save(request)
        );
        
        verify(bookRepository, never())
            .save(any(Book.class)
        );
    }
    

    @Test 
    void update_shouldUpdateBook_whenRequestIsValid(){
        //Arrange
        Long bookId = 12L;

        BookRequest request = new BookRequest(
            "Clean Code", 
            " isbn-123 ", 
            2008, 
            3, 
            1L, 
            List.of(2L)
        );

        Book existingBook = new Book();
        existingBook.setId(bookId);
        existingBook.setTitle("Old title");
        existingBook.setIsbn("OLD-ISBN");
        existingBook.setPublicationYear(2000);
        existingBook.setTotalCopies(1);
        
        Category category = new Category();
        category.setId(1L);

        Author author = new Author();
        author.setId(2L);

        when(bookRepository.findById(bookId))
        .thenReturn(Optional.of(existingBook));

        when(bookRepository.existsByIsbnIgnoreCaseAndIdNot("ISBN-123" ,bookId))
            .thenReturn(false);

        when(categoryRepository.findById(1L))
            .thenReturn(Optional.of(category));
        
        when(authorRepository.findById(2L))
            .thenReturn(Optional.of(author));
        
        when(bookRepository.save(any(Book.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        //act
        Book result = bookService.update(12L, request);
        //Assert
        assertEquals("Clean Code", result.getTitle());
        assertEquals("ISBN-123", result.getIsbn());
        assertEquals(2008, result.getPublicationYear());
        assertEquals(3, result.getTotalCopies());
        assertSame(category, result.getCategory());
        assertTrue(result.getAuthors().contains(author));

        verify(bookRepository).save(any(Book.class));
    }

    @Test  
    void update_shouldThrowException_whenBookDoesNotExist(){
        Long bookId = 12L;

        BookRequest request = new BookRequest(
            "Clean Code", 
            " isbn-123 ", 
            2008, 
            3, 
            1L, 
            List.of(2L)
        );

        when(bookRepository.findById(bookId))
        .thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(
            BookNotFoundException.class, 
            () -> bookService.update(bookId, request)
        );

        verify(bookRepository, never())
            .save(any(Book.class));
    }

    @Test 
    void update_ShouldThrowException_whenBookAlreadyExists(){
        //Arrange
        Long bookId = 12L;

        BookRequest request = new BookRequest(
            "Clean Code Updated",
            " duplicate-isbn ",
            2008,
            5,
            1L,
            List.of(2L)
        );

        Book existingBook = new Book();
        existingBook.setId(bookId);

        when(bookRepository.findById(bookId))
            .thenReturn(Optional.of(existingBook));

        when(bookRepository.existsByIsbnIgnoreCaseAndIdNot(
            "DUPLICATE-ISBN",
            bookId
        )).thenReturn(true);

        // Act & Assert
        assertThrows(
            BookAlreadyExistsException.class,
            () -> bookService.update(bookId, request)
        );

        verify(bookRepository, never())
            .save(any(Book.class));
    }
}
