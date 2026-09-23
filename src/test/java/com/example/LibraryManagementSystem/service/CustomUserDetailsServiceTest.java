package com.example.LibraryManagementSystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.LibraryManagementSystem.entity.Role;
import com.example.LibraryManagementSystem.entity.User;
import com.example.LibraryManagementSystem.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        User user = new User();
        user.setEmail("admin@example.com");
        user.setPassword("encoded-password");
        user.setRole(Role.ADMIN);

        when(userRepository.findByEmail("admin@example.com"))
            .thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService
            .loadUserByUsername("admin@example.com");

        assertEquals("admin@example.com", result.getUsername());
        assertEquals("encoded-password", result.getPassword());
        assertEquals(
            "ROLE_ADMIN",
            result.getAuthorities().iterator().next().getAuthority()
        );
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUserDoesNotExist() {
        when(userRepository.findByEmail("missing@example.com"))
            .thenReturn(Optional.empty());

        assertThrows(
            UsernameNotFoundException.class,
            () -> customUserDetailsService
                .loadUserByUsername("missing@example.com")
        );
    }
}
