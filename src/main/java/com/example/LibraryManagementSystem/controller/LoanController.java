package com.example.LibraryManagementSystem.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.LibraryManagementSystem.dto.loan.LoanResponse;
import com.example.LibraryManagementSystem.service.LoanService;

import java.util.List;

import org.springframework.http.HttpStatus;
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

    @PostMapping //POST /loans?bookId=2
    @ResponseStatus(HttpStatus.CREATED)
    public LoanResponse createLoan(@RequestParam Long bookId, Authentication authentication) {

        return LoanResponse.from(
                    loanService.createLoan(authentication.getName(), bookId)
                );
    }
    
    @GetMapping  //GET /loans
    public List<LoanResponse> getAllLoans(){
        return loanService.findAll()
            .stream()
            .map(LoanResponse::from)
            .toList();
    }

    @GetMapping("/{loanId}")   //GET /loans/1
    public LoanResponse getLoanById(@PathVariable Long loanId){
        return LoanResponse.from(
                    loanService.findById(loanId)
                );
    }

    @GetMapping("/user/{userId}") //GET /loans/user/1
    public List<LoanResponse> getLoansByUserId(@PathVariable Long userId){
        return loanService.findByUserId(userId)
            .stream()
            .map(LoanResponse::from)
            .toList();
    }

    @GetMapping("/active") //GET /loans/active
    public List<LoanResponse> getActiveLoans(){
        return loanService.findActiveLoans()
            .stream()
            .map(LoanResponse::from)
            .toList();
    }

    @GetMapping("/active/user/{userId}") //GET /loans/active/user/1
    public List<LoanResponse> getActiveLoansByUserId(@PathVariable Long userId){
        return loanService.findActiveLoansByUserId(userId)
            .stream()
            .map(LoanResponse::from)
            .toList();
    }

    @PatchMapping("/{loanId}/return")  //PATCH /loans/1/return
    public LoanResponse returnLoan(@PathVariable Long loanId){
        return LoanResponse.from(
                    loanService.returnLoan(loanId)
                );
    }
    
    @GetMapping("/me")
    public List<LoanResponse> getMyLoans(Authentication authentication){

        String email = authentication.getName();

        return loanService.findMyLoans(email)
            .stream()
            .map(LoanResponse::from)
            .toList();
    }

    @GetMapping("/me/active")
    public List<LoanResponse> getMyActiveLoans(Authentication authentication){

        return loanService.findMyActiveLoans(authentication.getName())
            .stream()
            .map(LoanResponse::from)
            .toList();
    }

    @GetMapping("/me/{loanId}")
    public LoanResponse getMyLoanById(
        @PathVariable Long loanId,
        Authentication authentication
    ){
        return LoanResponse.from(
            loanService.findMyLoanById(loanId, authentication.getName())
        );
    }

    @PatchMapping("/me/{loanId}/return")
    public LoanResponse returnMyLoan(
        @PathVariable Long loanId,
        Authentication authentication
    ) {
        return LoanResponse.from(
            loanService.returnMyLoan(
                loanId,
                authentication.getName()
            )
        );
    }
}
