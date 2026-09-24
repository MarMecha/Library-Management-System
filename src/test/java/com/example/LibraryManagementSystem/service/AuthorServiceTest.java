package com.example.LibraryManagementSystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

    @Test
    void findAll_shouldReturnRequestedPage() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 2);

        Author firstAuthor = new Author();
        firstAuthor.setId(1L);
        firstAuthor.setFirstName("GEORGE");
        firstAuthor.setLastName("ORWELL");

        Author secondAuthor = new Author();
        secondAuthor.setId(2L);
        secondAuthor.setFirstName("DAN");
        secondAuthor.setLastName("BROWN");

        Page<Author> repositoryPage = new PageImpl<>(
            List.of(firstAuthor, secondAuthor),
            pageable,
            4
        );

        when(authorRepository.findAll(pageable))
            .thenReturn(repositoryPage);

        // Act
        Page<Author> result = authorService.findAll(pageable);

        // Assert
        assertEquals(2, result.getContent().size());
        assertEquals(4, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertEquals(0, result.getNumber());
        assertSame(firstAuthor, result.getContent().get(0));
        assertSame(secondAuthor, result.getContent().get(1));

        verify(authorRepository).findAll(pageable);
    }
}
