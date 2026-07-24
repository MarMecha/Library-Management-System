package com.example.LibraryManagementSystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.LibraryManagementSystem.entity.Loan;


@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    
    Long countByBookIdAndReturnDateIsNull(long bookId);

    List<Loan> findByUserId(Long userId);

    List<Loan> findByReturnDateIsNull();

    List<Loan> findByUserIdAndReturnDateIsNull(Long userId);

    List<Loan> findByUserEmail(String email);

    List<Loan> findByUserEmailAndReturnDateIsNull(String email);

    Optional<Loan> findByIdAndUserEmail(Long loanId, String email);
}
