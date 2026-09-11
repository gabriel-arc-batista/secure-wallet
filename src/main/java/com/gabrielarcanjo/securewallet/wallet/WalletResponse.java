package com.gabrielarcanjo.securewallet.wallet;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record WalletResponse(
        UUID id,
        BigDecimal balance,
        OffsetDateTime updatedAt
) {
    public static WalletResponse from(Wallet wallet) {
        return new WalletResponse(wallet.getId(), wallet.getBalance(), wallet.getUpdatedAt());
    }
}
