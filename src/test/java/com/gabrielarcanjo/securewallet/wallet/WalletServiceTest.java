package com.gabrielarcanjo.securewallet.wallet;

import com.gabrielarcanjo.securewallet.transaction.WalletTransaction;
import com.gabrielarcanjo.securewallet.transaction.WalletTransactionRepository;
import com.gabrielarcanjo.securewallet.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletTransactionRepository transactionRepository;

    @InjectMocks
    private WalletService walletService;

    @Test
    void shouldAddDepositToBalance() {
        UUID userId = UUID.randomUUID();
        Wallet wallet = new Wallet(new User("Gabriel", "gabriel@email.com", "hash"));
        DepositRequest request = new DepositRequest(new BigDecimal("150.00"), "Primeiro depósito");
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.saveAndFlush(any(WalletTransaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        walletService.deposit(userId, request);

        assertEquals(new BigDecimal("150.00"), wallet.getBalance());
        verify(transactionRepository).saveAndFlush(any(WalletTransaction.class));
    }

    @Test
    void shouldRejectNegativeDeposit() {
        UUID userId = UUID.randomUUID();
        Wallet wallet = new Wallet(new User("Gabriel", "gabriel@email.com", "hash"));
        DepositRequest request = new DepositRequest(new BigDecimal("-10.00"), null);
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        assertThrows(InvalidAmountException.class, () -> walletService.deposit(userId, request));

        assertEquals(BigDecimal.ZERO, wallet.getBalance());
        verify(transactionRepository, never()).saveAndFlush(any(WalletTransaction.class));
    }
}
