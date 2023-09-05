package com.kinandcarta.ecommerce.exceptions;

/**
 * InvalidAccountException
 * Occurs when there is an Account, but it could not be located by id from the Accounts Service.
 */
public class InvalidAccountException extends RuntimeException {
    public InvalidAccountException(String message) {
        super(message);
    }
}
