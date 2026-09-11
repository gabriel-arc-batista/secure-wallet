package com.gabrielarcanjo.securewallet.auth;

import com.gabrielarcanjo.securewallet.user.User;
import com.gabrielarcanjo.securewallet.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldReturnTokenWhenCredentialsAreValid() {
        User user = new User("Gabriel Batista", "gabriel@email.com", "encoded-password");
        LoginRequest request = new LoginRequest("gabriel@email.com", "password123");
        LoginResponse expectedResponse = new LoginResponse("jwt-token", 7200);
        when(userRepository.findByEmailIgnoreCase("gabriel@email.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encoded-password")).thenReturn(true);
        when(tokenService.createToken(user)).thenReturn(expectedResponse);

        LoginResponse response = authService.login(request);

        assertEquals(expectedResponse, response);
    }

    @Test
    void shouldRejectInvalidPassword() {
        User user = new User("Gabriel Batista", "gabriel@email.com", "encoded-password");
        LoginRequest request = new LoginRequest("gabriel@email.com", "wrong-password");
        when(userRepository.findByEmailIgnoreCase("gabriel@email.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));

        verify(tokenService, never()).createToken(user);
    }

    @Test
    void shouldUseSameErrorWhenEmailDoesNotExist() {
        LoginRequest request = new LoginRequest("missing@email.com", "password123");
        when(userRepository.findByEmailIgnoreCase("missing@email.com")).thenReturn(Optional.empty());

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        assertEquals("E-mail ou senha inválidos", exception.getMessage());
    }
}
