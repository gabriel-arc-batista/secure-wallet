package com.gabrielarcanjo.securewallet.wallet;

public class InvalidAmountException extends RuntimeException {

    public InvalidAmountException() {
        super("O valor deve ser maior que zero");
    }
}
