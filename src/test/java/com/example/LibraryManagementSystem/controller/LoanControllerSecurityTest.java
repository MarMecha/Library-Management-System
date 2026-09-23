package com.example.LibraryManagementSystem.controller;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.LibraryManagementSystem.config.SecurityConfig;
import com.example.LibraryManagementSystem.entity.Book;
import com.example.LibraryManagementSystem.entity.Loan;
import com.example.LibraryManagementSystem.entity.LoanStatus;
import com.example.LibraryManagementSystem.entity.User;
import com.example.LibraryManagementSystem.security.JwtAuthenticationFilter;
import com.example.LibraryManagementSystem.security.JwtService;
import com.example.LibraryManagementSystem.service.LoanService;

@WebMvcTest(LoanController.class)
@Import({
    SecurityConfig.class,
    JwtAuthenticationFilter.class
})
class LoanControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoanService loanService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void getMyLoans_shouldReturnUnauthorized_whenUserIsNotAuthenticated()
            throws Exception {
        mockMvc.perform(get("/loans/me"))
            .andExpect(status().isUnauthorized());

        verify(loanService, never()).findMyLoans("member@example.com");
    }

    @Test
    @WithMockUser(username = "member@example.com", roles = "MEMBER")
    void getMyLoans_shouldUseAuthenticatedEmail_whenUserIsMember()
            throws Exception {
        when(loanService.findMyLoans("member@example.com"))
            .thenReturn(List.of());

        mockMvc.perform(get("/loans/me"))
            .andExpect(status().isOk());

        verify(loanService).findMyLoans("member@example.com");
    }

    @Test
    @WithMockUser(username = "member@example.com", roles = "MEMBER")
    void getAllLoans_shouldReturnForbidden_whenUserIsMember()
            throws Exception {
        mockMvc.perform(get("/loans"))
            .andExpect(status().isForbidden());

        verify(loanService, never()).findAll();
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void getAllLoans_shouldReturnOk_whenUserIsAdmin()
            throws Exception {
        when(loanService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/loans"))
            .andExpect(status().isOk());

        verify(loanService).findAll();
    }

    @Test
    @WithMockUser(username = "member@example.com", roles = "MEMBER")
    void createLoan_shouldUseAuthenticatedEmail_whenUserIsMember()
            throws Exception {
        Loan loan = createLoanEntity();

        when(loanService.createLoan("member@example.com", 12L))
            .thenReturn(loan);

        mockMvc.perform(post("/loans").param("bookId", "12"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(20))
            .andExpect(jsonPath("$.bookId").value(12))
            .andExpect(jsonPath("$.userId").value(5));

        verify(loanService)
            .createLoan("member@example.com", 12L);
    }

    @Test
    @WithMockUser(username = "member@example.com", roles = "MEMBER")
    void returnMyLoan_shouldUseAuthenticatedEmail()
            throws Exception {
        Loan loan = createLoanEntity();
        loan.setLoanStatus(LoanStatus.RETURNED);
        loan.setReturnDate(LocalDate.now());

        when(loanService.returnMyLoan(20L, "member@example.com"))
            .thenReturn(loan);

        mockMvc.perform(patch("/loans/me/20/return"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("RETURNED"));

        verify(loanService)
            .returnMyLoan(20L, "member@example.com");
    }

    private Loan createLoanEntity() {
        Book book = new Book();
        book.setId(12L);
        book.setTitle("Clean Code");

        User user = new User();
        user.setId(5L);
        user.setFullName("Mario Example");

        Loan loan = new Loan();
        loan.setId(20L);
        loan.setLoanStatus(LoanStatus.ACTIVE);
        loan.setFineAmount(0);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(14));
        loan.setBook(book);
        loan.setUser(user);

        return loan;
    }
}
