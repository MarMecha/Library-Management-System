package com.example.LibraryManagementSystem.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.LibraryManagementSystem.config.SecurityConfig;
import com.example.LibraryManagementSystem.dto.user.AuthResponse;
import com.example.LibraryManagementSystem.dto.user.LoginRequest;
import com.example.LibraryManagementSystem.dto.user.RegisterRequest;
import com.example.LibraryManagementSystem.security.JwtAuthenticationFilter;
import com.example.LibraryManagementSystem.security.JwtService;
import com.example.LibraryManagementSystem.service.AuthService;

@WebMvcTest(AuthController.class)
@Import({
    SecurityConfig.class,
    JwtAuthenticationFilter.class
})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void register_shouldReturnCreatedAndToken_whenBodyIsValid()
            throws Exception {
        when(authService.register(any(RegisterRequest.class)))
            .thenReturn(new AuthResponse("jwt-token"));

        String body = """
            {
              "fullName": "Mario Example",
              "email": "member@example.com",
              "password": "password123"
            }
            """;

        mockMvc.perform(
                post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.token").value("jwt-token"));

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void register_shouldReturnBadRequest_whenBodyIsInvalid()
            throws Exception {
        String body = """
            {
              "fullName": "",
              "email": "not-an-email",
              "password": "short"
            }
            """;

        mockMvc.perform(
                post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Validation failed"));

        verify(authService, never())
            .register(any(RegisterRequest.class));
    }

    @Test
    void login_shouldReturnOkAndToken_whenCredentialsAreValid()
            throws Exception {
        when(authService.login(any(LoginRequest.class)))
            .thenReturn(new AuthResponse("jwt-token"));

        String body = """
            {
              "email": "member@example.com",
              "password": "password123"
            }
            """;

        mockMvc.perform(
                post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void login_shouldReturnUnauthorized_whenCredentialsAreInvalid()
            throws Exception {
        when(authService.login(any(LoginRequest.class)))
            .thenThrow(new BadCredentialsException("Bad credentials"));

        String body = """
            {
              "email": "member@example.com",
              "password": "wrong-password"
            }
            """;

        mockMvc.perform(
                post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
            )
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error")
                .value("Invalid credentials"));
    }
}
