package com.example.LibraryManagementSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.LibraryManagementSystem.entity.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    
}
