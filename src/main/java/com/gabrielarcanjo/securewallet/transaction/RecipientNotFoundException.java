package com.gabrielarcanjo.securewallet.transaction;

public class RecipientNotFoundException extends RuntimeException {

    public RecipientNotFoundException() {
        super("Destinatário não encontrado");
    }
}
