package com.kinandcarta.ecommerce.exceptions;

public class OrderModelNotPersistedException extends RuntimeException {
    public OrderModelNotPersistedException(String message) {
        super(message);
    }
}
