package com.gabrielarcanjo.securewallet.auth;

public record LoginResponse(
        String token,
        long expiresIn
) {
}
