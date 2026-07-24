package com.example.LibraryManagementSystem.service;

import java.util.Locale;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.LibraryManagementSystem.dto.UserDtos.AuthResponse;
import com.example.LibraryManagementSystem.dto.UserDtos.LoginRequest;
import com.example.LibraryManagementSystem.dto.UserDtos.RegisterRequest;
import com.example.LibraryManagementSystem.entity.Role;
import com.example.LibraryManagementSystem.entity.User;
import com.example.LibraryManagementSystem.exception.EmailAlreadyExistsException;
import com.example.LibraryManagementSystem.repository.UserRepository;
import com.example.LibraryManagementSystem.security.JwtService;

@Service
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, 
                    PasswordEncoder passwordEncoder, 
                    AuthenticationManager authenticationManager,
                    JwtService jwtService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request){
        String email = request.email()
            .trim()
            .toLowerCase(Locale.ROOT);
        
        if (userRepository.existsByEmail(email)){
            throw new EmailAlreadyExistsException("Email is already registered!");
        }

        User user = new User();
        user.setFullName(request.fullName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.MEMBER);

        userRepository.save(user);

        return login(new LoginRequest(email, request.password()));
    }

    public AuthResponse login(LoginRequest request){
        String email = request.email()
            .trim()
            .toLowerCase(Locale.ROOT);

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, request.password())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token);
        
    }
}
