package com.example.LibraryManagementSystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.LibraryManagementSystem.dto.author.AuthorRequest;
import com.example.LibraryManagementSystem.entity.Author;
import com.example.LibraryManagementSystem.exception.AuthorAlreadyExistsException;
import com.example.LibraryManagementSystem.exception.AuthorNotFoundException;
import com.example.LibraryManagementSystem.repository.AuthorRepository;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorService authorService;

    @Test
    void save_shouldCreateAuthor_whenAuthorDoesNotExist() {
        // Arrange
        AuthorRequest request = new AuthorRequest(
            "  George  ",
            "  Orwell  "
        );

        when(authorRepository
            .existsByFirstNameIgnoreCaseAndLastNameIgnoreCase(
                "GEORGE",
                "ORWELL"
            )
        ).thenReturn(false);

        when(authorRepository.save(any(Author.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Author result = authorService.save(request);

        // Assert
        assertEquals("GEORGE", result.getFirstName());
        assertEquals("ORWELL", result.getLastName());

        verify(authorRepository)
            .save(any(Author.class));
    }

    @Test
    void save_shouldThrowException_whenAuthorAlreadyExists() {
        // Arrange
        AuthorRequest request = new AuthorRequest(
            " George ",
            " Orwell "
        );

        when(authorRepository
            .existsByFirstNameIgnoreCaseAndLastNameIgnoreCase(
                "GEORGE",
                "ORWELL"
            )
        ).thenReturn(true);

        // Act & Assert
        assertThrows(
            AuthorAlreadyExistsException.class,
            () -> authorService.save(request)
        );

        verify(authorRepository, never())
            .save(any(Author.class));
    }

    @Test
    void update_shouldUpdateAuthor_whenRequestIsValid() {
        // Arrange
        Long authorId = 1L;

        AuthorRequest request = new AuthorRequest(
            "  Franz  ",
            "  Kafka  "
        );

        Author existingAuthor = new Author();
        existingAuthor.setId(authorId);
        existingAuthor.setFirstName("OLD");
        existingAuthor.setLastName("NAME");

        when(authorRepository.findById(authorId))
            .thenReturn(Optional.of(existingAuthor));

        when(authorRepository
            .existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndIdNot(
                "FRANZ",
                "KAFKA",
                authorId
            )
        ).thenReturn(false);

        when(authorRepository.save(any(Author.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Author result =
            authorService.update(authorId, request);

        // Assert
        assertSame(existingAuthor, result);
        assertEquals("FRANZ", result.getFirstName());
        assertEquals("KAFKA", result.getLastName());

        verify(authorRepository).save(existingAuthor);
    }

    @Test
    void update_shouldThrowException_whenAuthorDoesNotExist() {
        // Arrange
        Long authorId = 999L;

        AuthorRequest request = new AuthorRequest(
            "Franz",
            "Kafka"
        );

        when(authorRepository.findById(authorId))
            .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
            AuthorNotFoundException.class,
            () -> authorService.update(authorId, request)
        );

        verify(authorRepository, never())
            .save(any(Author.class));
    }

    @Test
    void update_shouldThrowException_whenNameBelongsToAnotherAuthor() {
        // Arrange
        Long authorId = 1L;

        AuthorRequest request = new AuthorRequest(
            " George ",
            " Orwell "
        );

        Author existingAuthor = new Author();
        existingAuthor.setId(authorId);

        when(authorRepository.findById(authorId))
            .thenReturn(Optional.of(existingAuthor));

        when(authorRepository
            .existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndIdNot(
                "GEORGE",
                "ORWELL",
                authorId
            )
        ).thenReturn(true);

        // Act & Assert
        assertThrows(
            AuthorAlreadyExistsException.class,
            () -> authorService.update(authorId, request)
        );

        verify(authorRepository, never())
            .save(any(Author.class));
    }
}