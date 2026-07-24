package com.example.LibraryManagementSystem.dto.LoanDtos;

import java.time.LocalDate;

import com.example.LibraryManagementSystem.entity.Loan;
import com.example.LibraryManagementSystem.entity.LoanStatus;

public record LoanResponse(
    Long id,
    LoanStatus status,
    Integer fineAmount,
    LocalDate loanDate,
    LocalDate dueDate,
    LocalDate returnDate,
    Long bookId,
    String bookTitle,
    Long userId,
    String userFullName
) {
    
    public static LoanResponse from(Loan loan){
        return new LoanResponse(
                        loan.getId(),
                        loan.getLoanStatus(),
                        loan.getFineAmount(), 
                        loan.getLoanDate(), 
                        loan.getDueDate(), 
                        loan.getReturnDate(), 
                        loan.getBook().getId(), 
                        loan.getBook().getTitle(), 
                        loan.getUser().getId(), 
                        loan.getUser().getFullName()
        );
    }
}
