package com.kinandcarta.ecommerce.exceptions;

/**
 * MissingAccountException
 * Occurs when account is null in an Order for create or update.
 */
public class MissingAccountException extends RuntimeException {
    public MissingAccountException(String message) {
        super(message);
    }
}
