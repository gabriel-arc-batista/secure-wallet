package com.gabrielarcanjo.securewallet.transaction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        TransactionType type,
        BigDecimal amount,
        String description,
        String relatedEmail,
        OffsetDateTime createdAt
) {
    public static TransactionResponse from(WalletTransaction transaction) {
        String relatedEmail = transaction.getRelatedWallet() == null
                ? null
                : transaction.getRelatedWallet().getUser().getEmail();

        return new TransactionResponse(
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getDescription(),
                relatedEmail,
                transaction.getCreatedAt()
        );
    }
}
