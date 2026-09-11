package com.gabrielarcanjo.securewallet.wallet;

import com.gabrielarcanjo.securewallet.auth.CurrentUser;
import com.gabrielarcanjo.securewallet.transaction.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;
    private final CurrentUser currentUser;

    public WalletController(WalletService walletService, CurrentUser currentUser) {
        this.walletService = walletService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public ResponseEntity<WalletResponse> find(Authentication authentication) {
        WalletResponse response = walletService.findByUserId(currentUser.id(authentication));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/deposits")
    public ResponseEntity<TransactionResponse> deposit(
            Authentication authentication,
            @Valid @RequestBody DepositRequest request
    ) {
        TransactionResponse response = walletService.deposit(currentUser.id(authentication), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
