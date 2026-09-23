package com.example.LibraryManagementSystem.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.context.SecurityContextHolder;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @AfterEach
    void cleanUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_shouldContinueWithoutAuthentication_whenHeaderIsMissing()
            throws Exception {

        jwtAuthenticationFilter.doFilter(
            request,
            response,
            filterChain
        );

        assertNull(
            SecurityContextHolder
                .getContext()
                .getAuthentication()
        );

        verifyNoInteractions(
            jwtService,
            userDetailsService
        );

        verify(filterChain)
            .doFilter(request, response);
    }

    @Test
    void doFilter_shouldAuthenticateUser_whenTokenIsValid()
            throws Exception {

        // Arrange
        String email = "member@example.com";

        request.addHeader(
            "Authorization",
            "Bearer valid-token"
        );

        UserDetails userDetails =
            org.springframework.security.core.userdetails.User
                .withUsername(email)
                .password("encoded-password")
                .roles("MEMBER")
                .build();

        when(jwtService.extractUsername("valid-token"))
            .thenReturn(email);

        when(userDetailsService.loadUserByUsername(email))
            .thenReturn(userDetails);

        when(jwtService.isTokenValid(
            "valid-token",
            userDetails
        )).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilter(
            request,
            response,
            filterChain
        );

        // Assert
        var authentication =
            SecurityContextHolder
                .getContext()
                .getAuthentication();

        assertNotNull(authentication);
        assertEquals(email, authentication.getName());
        assertEquals(
            "ROLE_MEMBER",
            authentication.getAuthorities()
                .iterator()
                .next()
                .getAuthority()
        );

        verify(filterChain)
            .doFilter(request, response);
    }

    @Test
    void doFilter_shouldNotAuthenticateUser_whenTokenIsInvalid()
            throws Exception {

        // Arrange
        String email = "member@example.com";

        request.addHeader(
            "Authorization",
            "Bearer invalid-token"
        );

        UserDetails userDetails =
            org.springframework.security.core.userdetails.User
                .withUsername(email)
                .password("encoded-password")
                .roles("MEMBER")
                .build();

        when(jwtService.extractUsername("invalid-token"))
            .thenReturn(email);

        when(userDetailsService.loadUserByUsername(email))
            .thenReturn(userDetails);

        when(jwtService.isTokenValid(
            "invalid-token",
            userDetails
        )).thenReturn(false);

        // Act
        jwtAuthenticationFilter.doFilter(
            request,
            response,
            filterChain
        );

        // Assert
        assertNull(
            SecurityContextHolder
                .getContext()
                .getAuthentication()
        );

        verify(filterChain)
            .doFilter(request, response);
    }

    @Test
    void doFilter_shouldContinueWithoutAuthentication_whenTokenIsMalformed()
            throws Exception {

        // Arrange
        request.addHeader(
            "Authorization",
            "Bearer malformed-token"
        );

        when(jwtService.extractUsername("malformed-token"))
            .thenThrow(new JwtException("Malformed token"));

        // Act
        jwtAuthenticationFilter.doFilter(
            request,
            response,
            filterChain
        );

        // Assert
        assertNull(
            SecurityContextHolder
                .getContext()
                .getAuthentication()
        );

        verifyNoInteractions(userDetailsService);

        verify(filterChain)
            .doFilter(request, response);
    }
}