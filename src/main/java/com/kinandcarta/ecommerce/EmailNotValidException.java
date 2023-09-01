package com.kinandcarta.ecommerce;

public class EmailNotValidException extends RuntimeException {
    public EmailNotValidException(String message) {
        super(message);
    }
}
