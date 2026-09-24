package com.example.LibraryManagementSystem.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.example.LibraryManagementSystem.config.SecurityConfig;
import com.example.LibraryManagementSystem.dto.book.BookRequest;
import com.example.LibraryManagementSystem.entity.Book;
import com.example.LibraryManagementSystem.entity.Category;
import com.example.LibraryManagementSystem.exception.BookAlreadyExistsException;
import com.example.LibraryManagementSystem.exception.BookNotFoundException;
import com.example.LibraryManagementSystem.security.JwtAuthenticationFilter;
import com.example.LibraryManagementSystem.security.JwtService;
import com.example.LibraryManagementSystem.service.BookService;

@WebMvcTest(BookController.class)
@Import({
    SecurityConfig.class,
    JwtAuthenticationFilter.class
})
class BookControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void getBooks_shouldReturnUnauthorized_whenUserIsNotAuthenticated()
            throws Exception {

        mockMvc.perform(get("/books"))
            .andExpect(status().isUnauthorized());

        verify(bookService, never())
            .findAll(any(Pageable.class));
    }

    @Test
    @WithMockUser(
        username = "member@example.com",
        roles = "MEMBER"
    )
    void getBooks_shouldReturnOk_whenUserIsAuthenticated()
            throws Exception {

            Pageable pageable = PageRequest.of(
            0,
            2,
            Sort.by("title").ascending()
        );

        when(bookService.findAll(any(Pageable.class)))
            .thenReturn(
                new PageImpl<>(
                    List.of(),
                    pageable,
                    0
                )
            );

        mockMvc.perform(
                get("/books")
                    .param("page", "0")
                    .param("size", "2")
                    .param("sort", "title,asc")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.size").value(2))
            .andExpect(jsonPath("$.totalElements").value(0))
            .andExpect(jsonPath("$.totalPages").value(0))
            .andExpect(jsonPath("$.first").value(true))
            .andExpect(jsonPath("$.last").value(true));

        verify(bookService)
            .findAll(any(Pageable.class));
    }

    @Test
    @WithMockUser(
        username = "member@example.com",
        roles = "MEMBER"
    )
    void createBook_shouldReturnForbidden_whenUserIsMember()
            throws Exception {

        String requestBody = """
            {
              "title": "Clean Code",
              "isbn": "ISBN-123",
              "publicationYear": 2008,
              "totalCopies": 3,
              "categoryId": 1,
              "authorIds": [2]
            }
            """;

        mockMvc.perform(
                post("/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody)
            )
            .andExpect(status().isForbidden());

        verify(bookService, never())
            .save(any(BookRequest.class));
    }

    @Test
    @WithMockUser(
        username = "admin@example.com",
        roles = "ADMIN"
    )
    void createBook_shouldReturnCreated_whenUserIsAdmin()
            throws Exception {

        Category category = new Category();
        category.setId(1L);
        category.setName("PROGRAMMING");

        Book savedBook = new Book();
        savedBook.setId(12L);
        savedBook.setTitle("Clean Code");
        savedBook.setIsbn("ISBN-123");
        savedBook.setPublicationYear(2008);
        savedBook.setTotalCopies(3);
        savedBook.setCategory(category);

        when(bookService.save(any(BookRequest.class)))
            .thenReturn(savedBook);

        String requestBody = """
            {
              "title": "Clean Code",
              "isbn": "ISBN-123",
              "publicationYear": 2008,
              "totalCopies": 3,
              "categoryId": 1,
              "authorIds": [2]
            }
            """;

        mockMvc.perform(
                post("/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody)
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(12))
            .andExpect(jsonPath("$.title").value("Clean Code"))
            .andExpect(jsonPath("$.category.name")
                .value("PROGRAMMING"));

        verify(bookService)
            .save(any(BookRequest.class));
    }

    @Test
    @WithMockUser(
        username = "admin@example.com",
        roles = "ADMIN"
    )
    void createBook_shouldReturnBadRequest_whenBodyIsInvalid()
            throws Exception {

        String invalidBody = """
            {
              "title": "",
              "isbn": "",
              "publicationYear": -1,
              "totalCopies": 0,
              "categoryId": null,
              "authorIds": []
            }
            """;

        mockMvc.perform(
                post("/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidBody)
            )
            .andExpect(status().isBadRequest());

        verify(bookService, never())
            .save(any(BookRequest.class));
    }

    @Test
    @WithMockUser(username = "member@example.com", roles = "MEMBER")
    void getBook_shouldReturnNotFound_whenBookDoesNotExist()
            throws Exception {
        when(bookService.findById(999L))
            .thenThrow(new BookNotFoundException("Book not found!"));

        mockMvc.perform(get("/books/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Resource not found"))
            .andExpect(jsonPath("$.message").value("Book not found!"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void createBook_shouldReturnConflict_whenIsbnAlreadyExists()
            throws Exception {
        when(bookService.save(any(BookRequest.class)))
            .thenThrow(new BookAlreadyExistsException(
                "Book with this ISBN already exists!"
            ));

        String requestBody = """
            {
              "title": "Clean Code",
              "isbn": "ISBN-123",
              "publicationYear": 2008,
              "totalCopies": 3,
              "categoryId": 1,
              "authorIds": [2]
            }
            """;

        mockMvc.perform(
                post("/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody)
            )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error")
                .value("Resource already exists"));
    }
    
}
