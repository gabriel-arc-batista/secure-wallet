package com.gabrielarcanjo.securewallet.common;

import com.gabrielarcanjo.securewallet.auth.InvalidCredentialsException;
import com.gabrielarcanjo.securewallet.transaction.InsufficientBalanceException;
import com.gabrielarcanjo.securewallet.transaction.RecipientNotFoundException;
import com.gabrielarcanjo.securewallet.transaction.SameWalletTransferException;
import com.gabrielarcanjo.securewallet.user.exception.EmailAlreadyRegisteredException;
import com.gabrielarcanjo.securewallet.wallet.WalletNotFoundException;
import com.gabrielarcanjo.securewallet.wallet.InvalidAmountException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    public ResponseEntity<ApiError> handleEmailAlreadyRegistered(EmailAlreadyRegisteredException exception) {
        return buildResponse(HttpStatus.CONFLICT, exception.getMessage(), Map.of());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(InvalidCredentialsException exception) {
        return buildResponse(HttpStatus.UNAUTHORIZED, exception.getMessage(), Map.of());
    }

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<ApiError> handleWalletNotFound(WalletNotFoundException exception) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), Map.of());
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<ApiError> handleInvalidAmount(InvalidAmountException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), Map.of());
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ApiError> handleInsufficientBalance(InsufficientBalanceException exception) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage(), Map.of());
    }

    @ExceptionHandler(RecipientNotFoundException.class)
    public ResponseEntity<ApiError> handleRecipientNotFound(RecipientNotFoundException exception) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), Map.of());
    }

    @ExceptionHandler(SameWalletTransferException.class)
    public ResponseEntity<ApiError> handleSameWalletTransfer(SameWalletTransferException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), Map.of());
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiError> handleConcurrentUpdate(ObjectOptimisticLockingFailureException exception) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "A carteira foi atualizada por outra operação. Tente novamente.",
                Map.of()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataConflict(DataIntegrityViolationException exception) {
        return buildResponse(HttpStatus.CONFLICT, "Conflito ao salvar os dados", Map.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedError(Exception exception) {
        logger.error("Unexpected API error", exception);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor", Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> fields = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                fields.putIfAbsent(error.getField(), error.getDefaultMessage())
        );

        return buildResponse(HttpStatus.BAD_REQUEST, "Dados inválidos", fields);
    }

    private ResponseEntity<ApiError> buildResponse(
            HttpStatus status,
            String message,
            Map<String, String> fields
    ) {
        ApiError error = new ApiError(OffsetDateTime.now(), status.value(), message, fields);
        return ResponseEntity.status(status).body(error);
    }
}
