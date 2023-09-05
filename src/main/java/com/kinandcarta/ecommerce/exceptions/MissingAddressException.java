package com.kinandcarta.ecommerce.exceptions;

public class MissingAddressException extends RuntimeException {
    public MissingAddressException(String message) {
        super(message);
    }
}

