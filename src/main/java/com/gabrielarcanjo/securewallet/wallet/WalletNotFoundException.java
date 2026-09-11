package com.gabrielarcanjo.securewallet.wallet;

public class WalletNotFoundException extends RuntimeException {

    public WalletNotFoundException() {
        super("Carteira não encontrada");
    }
}
