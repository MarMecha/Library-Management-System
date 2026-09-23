package com.example.LibraryManagementSystem.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.LibraryManagementSystem.repository.UserRepository;
import com.example.LibraryManagementSystem.security.JwtService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.LibraryManagementSystem.dto.user.AuthResponse;
import com.example.LibraryManagementSystem.dto.user.LoginRequest;
import com.example.LibraryManagementSystem.dto.user.RegisterRequest;
import com.example.LibraryManagementSystem.entity.Role;
import com.example.LibraryManagementSystem.entity.User;
import com.example.LibraryManagementSystem.exception.EmailAlreadyExistsException;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_shouldCreateMemberAndReturnToken_whenRequestIsValid() {
        // Arrange
        RegisterRequest request = new RegisterRequest(
            "  Mario Example  ",
            "  MEMBER@EXAMPLE.COM  ",
            "password123"
        );
    
        UserDetails userDetails =
            org.springframework.security.core.userdetails.User
                .withUsername("member@example.com")
                .password("encoded-password")
                .roles("MEMBER")
                .build();
    
        Authentication authentication = mock(Authentication.class);
    
        when(userRepository.existsByEmail("member@example.com"))
            .thenReturn(false);
    
        when(passwordEncoder.encode("password123"))
            .thenReturn("encoded-password");
    
        when(authenticationManager.authenticate(
            any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(authentication);
    
        when(authentication.getPrincipal())
            .thenReturn(userDetails);
    
        when(jwtService.generateToken(userDetails))
            .thenReturn("jwt-token");
    
        // Act
        AuthResponse result = authService.register(request);
    
        // Assert
        assertEquals("jwt-token", result.token());
    
        ArgumentCaptor<User> userCaptor =
            ArgumentCaptor.forClass(User.class);
    
        verify(userRepository).save(userCaptor.capture());
    
        User savedUser = userCaptor.getValue();
    
        assertEquals("Mario Example", savedUser.getFullName());
        assertEquals("member@example.com", savedUser.getEmail());
        assertEquals("encoded-password", savedUser.getPassword());
        assertEquals(Role.MEMBER, savedUser.getRole());
    
        verify(passwordEncoder).encode("password123");
        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void register_shouldThrowException_whenEmailAlreadyExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest(
            "Mario Example",
            " MEMBER@EXAMPLE.COM ",
            "password123"
        );

        when(userRepository.existsByEmail("member@example.com"))
            .thenReturn(true);

        // Act & Assert
        assertThrows(
            EmailAlreadyExistsException.class,
            () -> authService.register(request)
        );

        verify(userRepository, never())
            .save(any(User.class));

        verifyNoInteractions(
            passwordEncoder,
            authenticationManager,
            jwtService
        );
    }

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() {
        // Arrange
        LoginRequest request = new LoginRequest(
            " MEMBER@EXAMPLE.COM ",
            "password123"
        );

        UserDetails userDetails =
            org.springframework.security.core.userdetails.User
                .withUsername("member@example.com")
                .password("encoded-password")
                .roles("MEMBER")
                .build();

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(
            any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(authentication);

        when(authentication.getPrincipal())
            .thenReturn(userDetails);

        when(jwtService.generateToken(userDetails))
            .thenReturn("jwt-token");

        // Act
        AuthResponse result = authService.login(request);

        // Assert
        assertEquals("jwt-token", result.token());

        ArgumentCaptor<UsernamePasswordAuthenticationToken>
            authenticationCaptor =
                ArgumentCaptor.forClass(
                    UsernamePasswordAuthenticationToken.class
                );

        verify(authenticationManager)
            .authenticate(authenticationCaptor.capture());

        UsernamePasswordAuthenticationToken submittedAuthentication =
            authenticationCaptor.getValue();

        assertEquals(
            "member@example.com",
            submittedAuthentication.getPrincipal()
        );

        assertEquals(
            "password123",
            submittedAuthentication.getCredentials()
        );

        verify(jwtService).generateToken(userDetails);
    }
    @Test
    void login_shouldThrowException_whenCredentialsAreInvalid() {
        // Arrange
        LoginRequest request = new LoginRequest(
            "member@example.com",
            "wrong-password"
        );

        when(authenticationManager.authenticate(
            any(UsernamePasswordAuthenticationToken.class)
        )).thenThrow(
            new BadCredentialsException("Bad credentials")
        );

        // Act & Assert
        assertThrows(
            BadCredentialsException.class,
            () -> authService.login(request)
        );

        verify(jwtService, never())
            .generateToken(any(UserDetails.class));
    }
}