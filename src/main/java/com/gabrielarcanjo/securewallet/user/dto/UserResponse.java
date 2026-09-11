package com.gabrielarcanjo.securewallet.user.dto;

import com.gabrielarcanjo.securewallet.user.User;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String fullName,
        String email,
        OffsetDateTime createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}
