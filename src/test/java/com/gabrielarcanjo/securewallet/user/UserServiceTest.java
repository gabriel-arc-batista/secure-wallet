package com.gabrielarcanjo.securewallet.user;

import com.gabrielarcanjo.securewallet.user.dto.CreateUserRequest;
import com.gabrielarcanjo.securewallet.user.exception.EmailAlreadyRegisteredException;
import com.gabrielarcanjo.securewallet.wallet.Wallet;
import com.gabrielarcanjo.securewallet.wallet.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUserWithEncodedPasswordAndWallet() {
        CreateUserRequest request = new CreateUserRequest(
                "  Gabriel Batista  ",
                "  GABRIEL@EMAIL.COM  ",
                "password123"
        );
        when(userRepository.existsByEmailIgnoreCase("gabriel@email.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.create(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("Gabriel Batista", userCaptor.getValue().getFullName());
        assertEquals("gabriel@email.com", userCaptor.getValue().getEmail());
        assertEquals("encoded-password", userCaptor.getValue().getPasswordHash());
        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    void shouldRejectEmailAlreadyRegistered() {
        CreateUserRequest request = new CreateUserRequest(
                "Gabriel Batista",
                "gabriel@email.com",
                "password123"
        );
        when(userRepository.existsByEmailIgnoreCase("gabriel@email.com")).thenReturn(true);

        assertThrows(EmailAlreadyRegisteredException.class, () -> userService.create(request));

        verify(userRepository, never()).save(any(User.class));
        verify(walletRepository, never()).save(any(Wallet.class));
    }
}
