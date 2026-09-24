package com.example.LibraryManagementSystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.LibraryManagementSystem.entity.User;
import com.example.LibraryManagementSystem.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void findAll_shouldReturnRequestedPage() {
        Pageable pageable = PageRequest.of(0, 2);

        User firstUser = new User();
        firstUser.setId(1L);
        firstUser.setFullName("Alice Example");

        User secondUser = new User();
        secondUser.setId(2L);
        secondUser.setFullName("Bob Example");

        Page<User> repositoryPage = new PageImpl<>(
            List.of(firstUser, secondUser),
            pageable,
            3
        );

        when(userRepository.findAll(pageable))
            .thenReturn(repositoryPage);

        Page<User> result = userService.findAll(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(3, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertSame(firstUser, result.getContent().get(0));

        verify(userRepository).findAll(pageable);
    }
}
