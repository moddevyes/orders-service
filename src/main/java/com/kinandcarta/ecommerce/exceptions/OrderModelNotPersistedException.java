package com.kinandcarta.ecommerce.exceptions;

import java.util.function.Supplier;

public class OrderModelNotPersistedException extends RuntimeException implements Supplier<OrderModelNotPersistedException> {
    public OrderModelNotPersistedException(String message) {
        super(message);
    }

    @Override
    public OrderModelNotPersistedException get() {
        return new OrderModelNotPersistedException(getMessage());
    }
}
