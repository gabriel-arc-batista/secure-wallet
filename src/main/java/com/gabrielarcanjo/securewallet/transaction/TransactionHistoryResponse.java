package com.gabrielarcanjo.securewallet.transaction;

import org.springframework.data.domain.Page;

import java.util.List;

public record TransactionHistoryResponse(
        List<TransactionResponse> transactions,
        int page,
        int totalPages,
        long totalItems
) {
    public static TransactionHistoryResponse from(Page<WalletTransaction> result) {
        List<TransactionResponse> transactions = result.getContent().stream()
                .map(TransactionResponse::from)
                .toList();

        return new TransactionHistoryResponse(
                transactions,
                result.getNumber(),
                result.getTotalPages(),
                result.getTotalElements()
        );
    }
}
