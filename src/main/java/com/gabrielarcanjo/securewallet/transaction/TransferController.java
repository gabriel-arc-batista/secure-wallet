package com.gabrielarcanjo.securewallet.transaction;

import com.gabrielarcanjo.securewallet.auth.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService transferService;
    private final CurrentUser currentUser;

    public TransferController(TransferService transferService, CurrentUser currentUser) {
        this.transferService = transferService;
        this.currentUser = currentUser;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> transfer(
            Authentication authentication,
            @Valid @RequestBody TransferRequest request
    ) {
        TransactionResponse response = transferService.transfer(currentUser.id(authentication), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
