package com.example.LibraryManagementSystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.LibraryManagementSystem.entity.Loan;


@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    Long countByBookIdAndReturnDateIsNull(long bookId);

    List<Loan> findByUserId(Long userId);

    List<Loan> findByReturnDateIsNull();

    List<Loan> findByUserIdAndReturnDateIsNull(Long userId);
}
