package com.example.LibraryManagementSystem.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.LibraryManagementSystem.entity.Loan;
import com.example.LibraryManagementSystem.service.LoanService;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/loans")
public class LoanController {
    
    private final LoanService loanService;

    public LoanController(LoanService loanService){
        this.loanService = loanService;
    }

    @PostMapping //POST /loans?userId=1&bookId=2
    public Loan createLoan(@RequestParam Long bookId, Authentication authentication) {

        return loanService.createLoan(authentication.getName(), bookId);
    }
    
    @GetMapping  //GET /loans
    public List<Loan> getAllLoans(){
        return loanService.findAll();
    }

    @GetMapping("/{loanId}")   //GET /loans/1
    public Loan getLoanById(@PathVariable Long loanId){
        return loanService.findById(loanId);
    }

    @GetMapping("/user/{userId}") //GET /loans/user/1
    public List<Loan> getLoansByUserId(@PathVariable Long userId){
        return loanService.findByUserId(userId);
    }

    @GetMapping("/active") //GET /loans/active
    public List<Loan> getActiveLoans(){
        return loanService.findActiveLoans();
    }

    @GetMapping("/active/user/{userId}") //GET /loans/active/user/1
    public List<Loan> getActiveLoansByUserId(@PathVariable Long userId){
        return loanService.findActiveLoansByUserId(userId);
    }

    @PatchMapping("/{loanId}/return")  //PATCH /loans/1/return
    public Loan returnLoan(@PathVariable Long loanId){
        return loanService.returnLoan(loanId);
    }
    
}
