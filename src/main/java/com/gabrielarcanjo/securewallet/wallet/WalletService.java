package com.gabrielarcanjo.securewallet.wallet;

import com.gabrielarcanjo.securewallet.transaction.TransactionResponse;
import com.gabrielarcanjo.securewallet.transaction.TransactionType;
import com.gabrielarcanjo.securewallet.transaction.WalletTransaction;
import com.gabrielarcanjo.securewallet.transaction.WalletTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;

    public WalletService(
            WalletRepository walletRepository,
            WalletTransactionRepository transactionRepository
    ) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public WalletResponse findByUserId(UUID userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(WalletNotFoundException::new);
        return WalletResponse.from(wallet);
    }

    @Transactional
    public TransactionResponse deposit(UUID userId, DepositRequest request) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(WalletNotFoundException::new);
        BigDecimal amount = request.amount().setScale(2, RoundingMode.UNNECESSARY);

        wallet.add(amount);
        WalletTransaction transaction = new WalletTransaction(
                wallet,
                null,
                TransactionType.DEPOSIT,
                amount,
                cleanDescription(request.description())
        );

        WalletTransaction savedTransaction = transactionRepository.saveAndFlush(transaction);
        return TransactionResponse.from(savedTransaction);
    }

    private String cleanDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }
        return description.trim();
    }
}
