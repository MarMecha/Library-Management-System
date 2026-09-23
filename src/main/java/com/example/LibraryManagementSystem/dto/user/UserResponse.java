package com.example.LibraryManagementSystem.dto.user;

import com.example.LibraryManagementSystem.entity.Role;
import com.example.LibraryManagementSystem.entity.User;

public record UserResponse(
    Long id,
    String fullName,
    String email,
    Role role
) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(),
                                user.getFullName(),
                                user.getEmail(),
                                user.getRole()
        );
    }
}
