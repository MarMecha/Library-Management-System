package com.example.LibraryManagementSystem.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.LibraryManagementSystem.dto.loan.LoanResponse;
import com.example.LibraryManagementSystem.dto.common.PageResponse;
import com.example.LibraryManagementSystem.service.LoanService;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Loans", description = "Book loan and return management")
@RestController
@RequestMapping("/loans")
public class LoanController {
    
    private final LoanService loanService;

    public LoanController(LoanService loanService){
        this.loanService = loanService;
    }

    @Operation(summary = "Borrow a book", description = "Creates a loan for the authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Loan created successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "404", description = "User or book not found"),
        @ApiResponse(responseCode = "409", description = "Book is unavailable or user already has an active loan")
    })
    @PostMapping //POST /loans?bookId=2
    @ResponseStatus(HttpStatus.CREATED)
    public LoanResponse createLoan(@RequestParam Long bookId, Authentication authentication) {

        return LoanResponse.from(
                    loanService.createLoan(authentication.getName(), bookId)
                );
    }
    
    @Operation(summary = "Get all loans", description = "Returns all loans. Requires ADMIN role.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Loans returned successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "ADMIN role required")
    })
    @GetMapping  //GET /loans
    public PageResponse<LoanResponse> getAllLoans(
        @ParameterObject
        @PageableDefault(
            size = 10,
            sort = "loanDate",
            direction = Sort.Direction.DESC
        )
        Pageable pageable
    ){
        return PageResponse.from(
            loanService.findAll(pageable)
                .map(LoanResponse::from)
        );
    }

    @Operation(summary = "Get loan by ID", description = "Returns a specific loan. Requires ADMIN role.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Loan returned successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "Loan not found")
    })
    @GetMapping("/{loanId}")   //GET /loans/1
    public LoanResponse getLoanById(@PathVariable Long loanId){
        return LoanResponse.from(
                    loanService.findById(loanId)
                );
    }

    @Operation(summary = "Get loans by user", description = "Returns all loans for a user. Requires ADMIN role.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Loans returned successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/user/{userId}") //GET /loans/user/1
    public List<LoanResponse> getLoansByUserId(@PathVariable Long userId){
        return loanService.findByUserId(userId)
            .stream()
            .map(LoanResponse::from)
            .toList();
    }

    @Operation(summary = "Get active loans", description = "Returns every active loan. Requires ADMIN role.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Active loans returned successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "ADMIN role required")
    })
    @GetMapping("/active") //GET /loans/active
    public List<LoanResponse> getActiveLoans(){
        return loanService.findActiveLoans()
            .stream()
            .map(LoanResponse::from)
            .toList();
    }

    @Operation(summary = "Get active loans by user", description = "Returns active loans for a user. Requires ADMIN role.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Active loans returned successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/active/user/{userId}") //GET /loans/active/user/1
    public List<LoanResponse> getActiveLoansByUserId(@PathVariable Long userId){
        return loanService.findActiveLoansByUserId(userId)
            .stream()
            .map(LoanResponse::from)
            .toList();
    }

    @Operation(summary = "Return a loan", description = "Marks a loan as returned. Requires ADMIN role.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Loan returned successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "Loan not found"),
        @ApiResponse(responseCode = "409", description = "Loan has already been returned")
    })
    @PatchMapping("/{loanId}/return")  //PATCH /loans/1/return
    public LoanResponse returnLoan(@PathVariable Long loanId){
        return LoanResponse.from(
                    loanService.returnLoan(loanId)
                );
    }
    
    @Operation(summary = "Get my loans", description = "Returns all loans belonging to the authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Loans returned successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/me")
    public List<LoanResponse> getMyLoans(Authentication authentication){

        String email = authentication.getName();

        return loanService.findMyLoans(email)
            .stream()
            .map(LoanResponse::from)
            .toList();
    }

    @Operation(summary = "Get my active loans", description = "Returns active loans belonging to the authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Active loans returned successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/me/active")
    public List<LoanResponse> getMyActiveLoans(Authentication authentication){

        return loanService.findMyActiveLoans(authentication.getName())
            .stream()
            .map(LoanResponse::from)
            .toList();
    }

    @Operation(summary = "Get one of my loans", description = "Returns a loan only when it belongs to the authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Loan returned successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Loan belongs to another user"),
        @ApiResponse(responseCode = "404", description = "Loan not found")
    })
    @GetMapping("/me/{loanId}")
    public LoanResponse getMyLoanById(
        @PathVariable Long loanId,
        Authentication authentication
    ){
        return LoanResponse.from(
            loanService.findMyLoanById(loanId, authentication.getName())
        );
    }

    @Operation(summary = "Return one of my loans", description = "Returns a loan only when it belongs to the authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Loan returned successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Loan belongs to another user"),
        @ApiResponse(responseCode = "404", description = "Loan not found"),
        @ApiResponse(responseCode = "409", description = "Loan has already been returned")
    })
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
