package com.example.LibraryManagementSystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.LibraryManagementSystem.entity.Book;
import com.example.LibraryManagementSystem.entity.Loan;
import com.example.LibraryManagementSystem.entity.LoanStatus;
import com.example.LibraryManagementSystem.entity.User;
import com.example.LibraryManagementSystem.exception.LoanAlreadyReturnedException;
import com.example.LibraryManagementSystem.exception.LoanNotFoundException;
import com.example.LibraryManagementSystem.exception.NoAvailableCopiesException;
import com.example.LibraryManagementSystem.repository.BookRepository;
import com.example.LibraryManagementSystem.repository.LoanRepository;
import com.example.LibraryManagementSystem.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTest {
    @Mock 
    private LoanRepository loanRepository;

    @Mock  
    private UserRepository userRepository;

    @Mock 
    private BookRepository bookRepository;

    @InjectMocks 
    private LoanService loanService;

    @Test //Happy path: Έγκυρα δεδομένα → επιτυχής δανισμός
    void createLoan_shouldCreateActiveLoan_whenCopyIsAvailable(){
        //Arrange
        String email = "member@example.com";
        Long bookId = 12L;

        User user = new User();
        user.setId(5L);
        user.setEmail(email);

        Book book = new Book();
        book.setId(bookId);
        book.setTotalCopies(3);

        when(userRepository.findByEmail(email))
            .thenReturn(Optional.of(user));
        
        when(bookRepository.findById(bookId))
            .thenReturn(Optional.of(book));

        when(loanRepository.countByBookIdAndReturnDateIsNull(bookId))
            .thenReturn(1L);

        when(loanRepository.save(any(Loan.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        LocalDate today = LocalDate.now();
        
        //Act
        Loan result = loanService.createLoan(email, bookId);

        // Assert
        assertSame(user, result.getUser());
        assertSame(book, result.getBook());
        assertEquals(today, result.getLoanDate());
        assertEquals(today.plusDays(14), result.getDueDate());
        assertNull(result.getReturnDate());
        assertEquals(LoanStatus.ACTIVE, result.getLoanStatus());
        assertEquals(0, result.getFineAmount());

        verify(loanRepository).save(any(Loan.class));
    }

    @Test 
    void createLoan_shouldThrowException_whenNoCopiesAreAvailable(){
        //Arrange
        String email = "member@example.com";
        Long bookId = 12L;

        User user = new User();
        user.setId(5L);
        user.setEmail(email);

        Book book = new Book();
        book.setId(bookId);
        book.setTotalCopies(3);

        when(userRepository.findByEmail(email))
            .thenReturn(Optional.of(user));
        
        when(bookRepository.findById(bookId))
            .thenReturn(Optional.of(book));

        when(loanRepository.countByBookIdAndReturnDateIsNull(bookId))
            .thenReturn(3L);

        assertThrows(NoAvailableCopiesException.class,
            () -> loanService.createLoan(email, bookId));
        
        verify(loanRepository, never())
            .save(any(Loan.class));
    }

    @Test 
    void  returnLoan_shouldReturnLoanWithoutFine_whenReturnedOnTime(){
        //Arrange 
        Long loanId = 20L;
        LocalDate today = LocalDate.now();

        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setLoanDate(today.minusDays(5));
        loan.setDueDate(today.plusDays(9));
        loan.setReturnDate(null);
        loan.setLoanStatus(LoanStatus.ACTIVE);
        loan.setFineAmount(0);

        when(loanRepository.findById(loanId))
            .thenReturn(Optional.of(loan));
        
        when(loanRepository.save(any(Loan.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        //Act
        Loan result = loanService.returnLoan(loanId);

        //Assert 
        assertEquals(today, result.getReturnDate());
        assertEquals(LoanStatus.RETURNED, result.getLoanStatus());
        assertEquals(0, result.getFineAmount());

        verify(loanRepository).save(loan);
    }

    @Test
    void returnLoan_shouldCalculateFine_whenLoanIsOverdue() {
        // Arrange
        Long loanId = 20L;
        LocalDate today = LocalDate.now();

        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setLoanDate(today.minusDays(20));
        loan.setDueDate(today.minusDays(4));
        loan.setReturnDate(null);
        loan.setLoanStatus(LoanStatus.ACTIVE);
        loan.setFineAmount(0);

        when(loanRepository.findById(loanId))
            .thenReturn(Optional.of(loan));

        when(loanRepository.save(any(Loan.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Loan result = loanService.returnLoan(loanId);

        // Assert
        assertEquals(today, result.getReturnDate());
        assertEquals(LoanStatus.RETURNED, result.getLoanStatus());
        assertEquals(4, result.getFineAmount());

        verify(loanRepository).save(loan);
    }

    @Test
    void returnLoan_shouldThrowException_whenLoanAlreadyReturned() {
        // Arrange
        Long loanId = 20L;
        LocalDate today = LocalDate.now();

        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setDueDate(today.minusDays(5));
        loan.setReturnDate(today.minusDays(2));
        loan.setLoanStatus(LoanStatus.RETURNED);
        loan.setFineAmount(3);

        when(loanRepository.findById(loanId))
            .thenReturn(Optional.of(loan));

        // Act & Assert
        assertThrows(
            LoanAlreadyReturnedException.class,
            () -> loanService.returnLoan(loanId)
        );

        verify(loanRepository, never())
            .save(any(Loan.class));
    }

    @Test
    void returnLoan_shouldThrowException_whenLoanDoesNotExist() {
        // Arrange
        Long loanId = 999L;

        when(loanRepository.findById(loanId))
            .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
            LoanNotFoundException.class,
            () -> loanService.returnLoan(loanId)
        );

        verify(loanRepository, never())
            .save(any(Loan.class));
    }

    @Test
    void returnMyLoan_shouldThrowException_whenLoanDoesNotBelongToUser() {
        // Arrange
        Long loanId = 20L;
        String email = "member@example.com";

        when(loanRepository.findByIdAndUserEmail(loanId, email))
            .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
            LoanNotFoundException.class,
            () -> loanService.returnMyLoan(loanId, email)
        );

        verify(loanRepository, never())
            .save(any(Loan.class));
    }

    @Test
    void returnMyLoan_shouldReturnLoan_whenLoanBelongsToUser() {
        // Arrange
        Long loanId = 20L;
        String email = "member@example.com";
        LocalDate today = LocalDate.now();

        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setDueDate(today.plusDays(5));
        loan.setReturnDate(null);
        loan.setLoanStatus(LoanStatus.ACTIVE);
        loan.setFineAmount(0);

        when(loanRepository.findByIdAndUserEmail(loanId, email))
            .thenReturn(Optional.of(loan));

        when(loanRepository.save(any(Loan.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Loan result = loanService.returnMyLoan(loanId, email);

        // Assert
        assertEquals(today, result.getReturnDate());
        assertEquals(LoanStatus.RETURNED, result.getLoanStatus());
        assertEquals(0, result.getFineAmount());

        verify(loanRepository).save(loan);
    }
}

