package com.gabrielarcanjo.securewallet.transaction;

import com.gabrielarcanjo.securewallet.auth.CurrentUser;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final CurrentUser currentUser;

    public TransactionController(TransactionService transactionService, CurrentUser currentUser) {
        this.transactionService = transactionService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public ResponseEntity<TransactionHistoryResponse> findAll(
            Authentication authentication,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size
    ) {
        TransactionHistoryResponse response = transactionService.findByUser(
                currentUser.id(authentication),
                page,
                size
        );
        return ResponseEntity.ok(response);
    }
}
