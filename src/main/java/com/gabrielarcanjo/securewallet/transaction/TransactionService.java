package com.gabrielarcanjo.securewallet.transaction;

import com.gabrielarcanjo.securewallet.wallet.Wallet;
import com.gabrielarcanjo.securewallet.wallet.WalletNotFoundException;
import com.gabrielarcanjo.securewallet.wallet.WalletRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TransactionService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;

    public TransactionService(
            WalletRepository walletRepository,
            WalletTransactionRepository transactionRepository
    ) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public TransactionHistoryResponse findByUser(UUID userId, int page, int size) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(WalletNotFoundException::new);
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<WalletTransaction> result = transactionRepository
                .findByWalletIdOrderByCreatedAtDesc(wallet.getId(), pageRequest);

        return TransactionHistoryResponse.from(result);
    }
}
