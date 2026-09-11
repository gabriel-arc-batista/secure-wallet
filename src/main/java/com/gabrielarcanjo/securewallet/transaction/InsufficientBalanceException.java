package com.gabrielarcanjo.securewallet.transaction;

public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException() {
        super("Saldo insuficiente");
    }
}
