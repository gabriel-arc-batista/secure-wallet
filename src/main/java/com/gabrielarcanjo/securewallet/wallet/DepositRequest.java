package com.gabrielarcanjo.securewallet.wallet;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record DepositRequest(
        @NotNull
        @Digits(integer = 17, fraction = 2)
        BigDecimal amount,
        @Size(max = 120)
        String description
) {
}
