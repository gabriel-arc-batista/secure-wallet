package com.gabrielarcanjo.securewallet.transaction;

import com.gabrielarcanjo.securewallet.wallet.InvalidAmountException;
import com.gabrielarcanjo.securewallet.wallet.Wallet;
import com.gabrielarcanjo.securewallet.wallet.WalletNotFoundException;
import com.gabrielarcanjo.securewallet.wallet.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class TransferService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;

    public TransferService(
            WalletRepository walletRepository,
            WalletTransactionRepository transactionRepository
    ) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransactionResponse transfer(UUID senderId, TransferRequest request) {
        Wallet senderWallet = walletRepository.findByUserId(senderId)
                .orElseThrow(WalletNotFoundException::new);
        Wallet recipientWallet = walletRepository.findByUserEmailIgnoreCase(request.recipientEmail().trim())
                .orElseThrow(RecipientNotFoundException::new);
        BigDecimal amount = request.amount().setScale(2, RoundingMode.UNNECESSARY);

        if (amount.signum() <= 0) {
            throw new InvalidAmountException();
        }
        if (senderWallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }

        senderWallet.subtract(amount);
        recipientWallet.add(amount);
        String description = cleanDescription(request.description());

        WalletTransaction outgoing = new WalletTransaction(
                senderWallet,
                recipientWallet,
                TransactionType.TRANSFER_OUT,
                amount,
                description
        );
        WalletTransaction incoming = new WalletTransaction(
                recipientWallet,
                senderWallet,
                TransactionType.TRANSFER_IN,
                amount,
                description
        );

        transactionRepository.saveAllAndFlush(List.of(outgoing, incoming));
        return TransactionResponse.from(outgoing);
    }

    private String cleanDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }
        return description.trim();
    }
}
