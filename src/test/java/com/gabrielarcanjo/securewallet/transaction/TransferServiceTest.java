package com.gabrielarcanjo.securewallet.transaction;

import com.gabrielarcanjo.securewallet.user.User;
import com.gabrielarcanjo.securewallet.wallet.Wallet;
import com.gabrielarcanjo.securewallet.wallet.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

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
class TransferServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletTransactionRepository transactionRepository;

    @InjectMocks
    private TransferService transferService;

    @Test
    void shouldTransferBalanceBetweenWallets() {
        UUID senderId = UUID.randomUUID();
        Wallet senderWallet = wallet("sender@email.com");
        Wallet recipientWallet = wallet("recipient@email.com");
        senderWallet.add(new BigDecimal("100.00"));
        when(walletRepository.findByUserId(senderId)).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findByUserEmailIgnoreCase("recipient@email.com"))
                .thenReturn(Optional.of(recipientWallet));
        TransferRequest request = new TransferRequest(
                "recipient@email.com",
                new BigDecimal("35.00"),
                "Pagamento"
        );

        transferService.transfer(senderId, request);

        assertEquals(new BigDecimal("65.00"), senderWallet.getBalance());
        assertEquals(new BigDecimal("35.00"), recipientWallet.getBalance());
        verify(transactionRepository).saveAllAndFlush(any());
    }

    @Test
    void shouldRejectTransferToSameWallet() {
        UUID senderId = UUID.randomUUID();
        Wallet wallet = wallet("sender@email.com");
        wallet.add(new BigDecimal("100.00"));
        when(walletRepository.findByUserId(senderId)).thenReturn(Optional.of(wallet));
        when(walletRepository.findByUserEmailIgnoreCase("sender@email.com")).thenReturn(Optional.of(wallet));
        TransferRequest request = new TransferRequest(
                "sender@email.com",
                new BigDecimal("10.00"),
                null
        );

        assertThrows(SameWalletTransferException.class, () -> transferService.transfer(senderId, request));

        assertEquals(new BigDecimal("100.00"), wallet.getBalance());
        verify(transactionRepository, never()).saveAllAndFlush(any());
    }

    @Test
    void shouldRejectTransferWithInsufficientBalance() {
        UUID senderId = UUID.randomUUID();
        Wallet senderWallet = wallet("sender@email.com");
        Wallet recipientWallet = wallet("recipient@email.com");
        when(walletRepository.findByUserId(senderId)).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findByUserEmailIgnoreCase("recipient@email.com"))
                .thenReturn(Optional.of(recipientWallet));
        TransferRequest request = new TransferRequest(
                "recipient@email.com",
                new BigDecimal("10.00"),
                null
        );

        assertThrows(InsufficientBalanceException.class, () -> transferService.transfer(senderId, request));

        verify(transactionRepository, never()).saveAllAndFlush(any());
    }

    private Wallet wallet(String email) {
        Wallet wallet = new Wallet(new User("User", email, "hash"));
        ReflectionTestUtils.setField(wallet, "id", UUID.randomUUID());
        return wallet;
    }
}
