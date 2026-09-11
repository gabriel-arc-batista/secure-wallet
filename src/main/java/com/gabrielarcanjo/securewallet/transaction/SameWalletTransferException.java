package com.gabrielarcanjo.securewallet.transaction;

public class SameWalletTransferException extends RuntimeException {

    public SameWalletTransferException() {
        super("Não é possível transferir para a própria carteira");
    }
}
