package com.example.LibraryManagementSystem.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;

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

@Service
public class LoanService {
    
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public LoanService(LoanRepository loanRepository,
                    UserRepository userRepository,
                    BookRepository bookRepository){
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public Loan createLoan(String email, Long bookId){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->
                new RuntimeException("User not found!")
            );

        Book book = bookRepository.findById(bookId)
                .orElseThrow(()->
                new RuntimeException("Book not Found!")
            );

        long activeLoans = loanRepository.countByBookIdAndReturnDateIsNull(bookId);

        if (activeLoans >= book.getTotalCopies()) {
            throw new NoAvailableCopiesException(  "No book copies available!");
        }

        LocalDate today = LocalDate.now();

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBook(book);
        loan.setLoanDate(today);
        loan.setDueDate(LocalDate.now().plusDays(14));
        loan.setReturnDate(null);
        loan.setLoanStatus(LoanStatus.ACTIVE);
        loan.setFineAmount(0);

        return loanRepository.save(loan);
    }

    public Loan returnLoan(Long loanId){
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(()->
                new LoanNotFoundException("Loan not found!")
            );

        return completeReturn(loan);
    }
    
    public Loan returnMyLoan(Long loanId, String email) {
        Loan loan = loanRepository
            .findByIdAndUserEmail(loanId, email)
            .orElseThrow(() ->
                new LoanNotFoundException("Loan not found!")
            );

        return completeReturn(loan);
    }

    private Loan completeReturn(Loan loan) {
        if (loan.getReturnDate() != null) {
            throw new LoanAlreadyReturnedException(
                "Loan has already been returned!"
            );
        }

        LocalDate returnDate = LocalDate.now();

        if (returnDate.isAfter(loan.getDueDate())) {
            long overdueDays = ChronoUnit.DAYS.between(
                loan.getDueDate(),
                returnDate
            );

            int finePerDay = 1;

            int fineAmount = Math.toIntExact(
                overdueDays * finePerDay
            );

            loan.setFineAmount(fineAmount);
        } else {
            loan.setFineAmount(0);
        }

        loan.setReturnDate(returnDate);
        loan.setLoanStatus(LoanStatus.RETURNED);

        return loanRepository.save(loan);
    }

    public List<Loan> findAll(){
        return loanRepository.findAll();
    }

    public Loan findById(Long loanId){
        return loanRepository.findById(loanId)
            .orElseThrow(() ->
                    new LoanNotFoundException("Loan not found!")
            );
    }

    public List<Loan> findByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found!");
        }

        return loanRepository.findByUserId(userId);
    }

    public List<Loan> findActiveLoans(){
        return loanRepository.findByReturnDateIsNull();
    }

    public List<Loan> findActiveLoansByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found!");
        }

        return loanRepository.findByUserIdAndReturnDateIsNull(userId);
    }

    public List<Loan> findMyLoans(String email) {
        return loanRepository.findByUserEmail(email);
    }

    public List<Loan> findMyActiveLoans(String email) {
        return loanRepository
            .findByUserEmailAndReturnDateIsNull(email);
    }

    public Loan findMyLoanById(Long loanId, String email){
        return loanRepository.findByIdAndUserEmail(loanId, email)
            .orElseThrow(() ->
                new LoanNotFoundException("Loan not found!")
        );
    }
}
